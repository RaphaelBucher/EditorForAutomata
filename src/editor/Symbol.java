/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package editor;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

public class Symbol extends Shape {
  /** A reference on the Transition the Symbol belongs to. */
  private Transition hostTransition;
  private char symbol;
  private Point symbolPaintingMiddle;
  /** positive distance from symbolPaintingMiddle to the left border of the bounding box. Right
   * side is the same. */
  private int boundingBoxLeft;
  /** Box goes down for the same amount. */
  private static int boundingBoxUp; 
  
  public Symbol(Transition hostTransition, char symbol) {
    this.hostTransition = hostTransition;
    this.symbol = symbol;
    symbolPaintingMiddle = new Point();
  }
  
  @Override
  public boolean mouseClickHit(int mouseX, int mouseY) {
    // Simple Box collision. boundingBoxLeft is the same for the right side. Same with Up.
    // Tolerate 1 pixel more for the horizontal collision, it's a better user experience.
    if (mouseX >= symbolPaintingMiddle.x - boundingBoxLeft -1 &&
        mouseX <= symbolPaintingMiddle.x + boundingBoxLeft + 1 &&
        mouseY >= symbolPaintingMiddle.y - boundingBoxUp &&
        mouseY <= symbolPaintingMiddle.y + boundingBoxUp) {
      return true;
    }
    
    return false;
  }

  @Override
  public void displaySelectedShapeTooltip() {
    Tooltip.setMessage(Config.Tooltips.symbolSelected, Config.TOOLTIP_DRAWABLE_PANEL_DISPLAY_AMOUNT);
  }
  
  /** Computes the middle of the symbols and paints them. direction = 1 mean the symbols
   * expand to the right of the transitionDockingPoint, -1 is to the left, 0 centered.
   * This method ensures that the elements of the passed list get positioned from left to
   * right, index 0 is left, last index is right. */
  public static void paint(Graphics2D graphics2D, ArrayList<Symbol> symbols,
      Point symbolDockingPoint, int direction) {
    FontMetrics fontMetrics = graphics2D.getFontMetrics();
    boundingBoxUp = fontMetrics.getAscent() / 2 + 1;
    
    // Compute the bounding boxes
    boundingBoxes(fontMetrics, symbols);
    
    // Compute all middlePoints
    paintingMiddlePoints(fontMetrics, symbols, symbolDockingPoint, direction);
    
    paint(graphics2D, symbols, fontMetrics, symbolDockingPoint);
  }
  
  /** The actual painting. */
  private static void paint(Graphics2D graphics2D, ArrayList<Symbol> symbols, FontMetrics fontMetrics,
      Point symbolDockingPoint) {
    int fontOffsetY = fontMetrics.getAscent() / 3;
    
    // In case its an Epsilon-Transition, paint only the epsilon-sign at the docking-point
    if (symbols.size() <= 0) {
      graphics2D.drawString("\u03B5", symbolDockingPoint.x - 3, symbolDockingPoint.y + fontOffsetY);
    }
    
    Symbol currentSymbol;
    for (int i = 0; i < symbols.size(); i++) {
      currentSymbol = symbols.get(i);
      // If the element is selected, paint a background-roundRect to hightlight it
      if (currentSymbol.isSelected || symbols.get(i).hostTransition.isSelected ||
          symbols.get(i).wordAcceptedPath) {
        graphics2D.setColor(new Color(190, 240, 255));
        
        graphics2D.fillRoundRect(currentSymbol.symbolPaintingMiddle.x - currentSymbol.boundingBoxLeft - 2,
            currentSymbol.symbolPaintingMiddle.y - boundingBoxUp - 2, currentSymbol.boundingBoxLeft * 2 + 4,
            boundingBoxUp * 2 + 4, 7, 7);
        graphics2D.setColor(Color.BLACK);
      }
      
      String drawnString = "" + currentSymbol.getSymbol();
      // Append a comma if its not the last symbol of the chain
      if (i < symbols.size() - 1)
        drawnString += ",";
      
      graphics2D.drawString(drawnString, currentSymbol.symbolPaintingMiddle.x -
          currentSymbol.boundingBoxLeft, currentSymbol.symbolPaintingMiddle.y + fontOffsetY);
    }
  }
  
  /** Computes the symbols middlePoints according to the bounding boxes. */
  private static void paintingMiddlePoints(FontMetrics fontMetrics, ArrayList<Symbol> symbols,
      Point symbolDockingPoint, int direction) {
    // Epsilon-transitions need to compute nothing here
    if (symbols.size() <= 0)
      return;
    
    // total length of the symbol-chain
    int totalLength = 0;
    int commaWidth = fontMetrics.stringWidth(",");
    int whiteSpace = 1;
    
    // First symbol is at the dockingPoint
    symbols.get(0).symbolPaintingMiddle.x = symbolDockingPoint.x;
    symbols.get(0).symbolPaintingMiddle.y = symbolDockingPoint.y;
    
    for (int i = 1; i < symbols.size(); i++) {
      int distance = symbols.get(i - 1).boundingBoxLeft + commaWidth + whiteSpace +
          symbols.get(i).boundingBoxLeft;
      
      symbols.get(i).symbolPaintingMiddle.x = symbols.get(i - 1).symbolPaintingMiddle.x +
          distance;
      symbols.get(i).symbolPaintingMiddle.y = symbolDockingPoint.y;
      
      totalLength += distance;
    }
    
    // Shift all middlePoints left by the whole length in case the direction was -1
    if (direction == -1)
      shiftMiddlePointsX(symbols, - totalLength);
    else if (direction == 0)
      shiftMiddlePointsX(symbols, - totalLength / 2);
  }
  
  /** Shifts all middlePoints x attribute by a passed offset. */
  private static void shiftMiddlePointsX(ArrayList<Symbol> symbols, int offsetX) {
    for (int i = 0; i < symbols.size(); i++) {
      symbols.get(i).symbolPaintingMiddle.x += offsetX;
    }
  }

  /** Computes the bounding boxes for the symbols. A wide letter like m has a bigger one than
   * an i.*/
  private static void boundingBoxes(FontMetrics fontMetrics, ArrayList<Symbol> symbols) {
    for (int i = 0; i < symbols.size(); i++) {
      symbols.get(i).boundingBoxLeft = fontMetrics.stringWidth("" + symbols.get(i).getSymbol()) / 2;
      
      // A little correction for thin letters like i,l etc.
      if (symbols.get(i).boundingBoxLeft <= 1)
        symbols.get(i).boundingBoxLeft++;
    }
  }
  
  /** Checks if the user entered a valid character for the transition. Valid symbols are single 
   * digits and small letters. */
  public static boolean isSymbolValid(char symbol) {
    // Is the symbol a single digit?
    if (symbol >= new Character('0') && symbol <= new Character('9'))
      return true;
    
    if (symbol >= 'a' && symbol <= 'z')
      return true;
    
    return false;
  }
  
  /** Returns a deep copy of the Symbol. */
  public Symbol copy(Transition hostTransition) {
    Symbol newSymbol = new Symbol(hostTransition, symbol);
    return newSymbol;
  }
  
  /** Returns the Symbol-object in the list that has the same symbol-char as the passed one. Returns
   * null if none is found in the list. */
  public static Symbol getSymbol(ArrayList<Symbol> symbols, char searchedSymbol) {
    for (int i = 0; i < symbols.size(); i++) {
      if (symbols.get(i).symbol == searchedSymbol)
        return symbols.get(i);
    }
    
    return null;
  }
  
  // Setters and Getters
  public char getSymbol() {
    return symbol;
  }
  
  public Point getSymbolPaintingMiddle() {
    return this.symbolPaintingMiddle;
  }
  
  public Transition getHostTransition() {
    return this.hostTransition;
  }
}
