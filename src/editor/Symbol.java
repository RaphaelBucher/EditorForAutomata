package editor;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

public class Symbol extends Shape {
  private char symbol;
  private Point symbolPaintingMiddle;
  /** positive distance from symbolPaintingMiddle to the left border of the bounding box. Right
   * side is the same. */
  private int boundingBoxLeft;
  /** Box goes down for the same amount. */
  private static int boundingBoxUp; 
  
  public Symbol(char symbol) {
    this.symbol = symbol;
    symbolPaintingMiddle = new Point();
  }
  
  @Override
  public boolean mouseClickHit(int mouseX, int mouseY) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void displaySelectedShapeTooltip() {
    // TODO Auto-generated method stub
    
  }
  
  /** Computes the middle of the symbols and paints them. direction = 1 mean the symbols
   * expand to the right of the transitionDockingPoint, -1 is to the left, 0 centered.
   * This method ensures that the elements of the passed list get positioned from left to
   * right, index 0 is left, last index is right. */
  public static void paint(Graphics2D graphics2D, ArrayList<Symbol> symbols,
      Point symbolDockingPoint, int direction) {
    FontMetrics fontMetrics = graphics2D.getFontMetrics();
    int fontOffsetY = fontMetrics.getAscent() / 3;
    boundingBoxUp = fontMetrics.getAscent() / 2 + 1;
    
    // Compute the bounding boxes
    boundingBoxes(fontMetrics, symbols);
    
    // Compute all middlePoints
    paintingMiddlePoints(fontMetrics, symbols, symbolDockingPoint, direction);
    
    // TODO: remove
    graphics2D.drawRect(symbolDockingPoint.x, symbolDockingPoint.y, 1, 1);
    
    for (int i = 0; i < symbols.size(); i++) {
      String drawnString = "" + symbols.get(i).getSymbol();
      // Append a comma if its not the last symbol of the chain
      if (i < symbols.size() - 1)
        drawnString += ",";
      
      graphics2D.drawString(drawnString, symbols.get(i).symbolPaintingMiddle.x -
          symbols.get(i).boundingBoxLeft, symbols.get(i).symbolPaintingMiddle.y + fontOffsetY);
      
      // TODO: remove
      graphics2D.setColor(Color.GREEN);
      graphics2D.drawRect(symbols.get(i).symbolPaintingMiddle.x - symbols.get(i).boundingBoxLeft,
          symbols.get(i).symbolPaintingMiddle.y - boundingBoxUp, symbols.get(i).boundingBoxLeft * 2,
          boundingBoxUp * 2);
      graphics2D.setColor(Color.BLACK);
    }
  }
  
  /** Computes the symbols middlePoints according to the bounding boxes. */
  private static void paintingMiddlePoints(FontMetrics fontMetrics, ArrayList<Symbol> symbols,
      Point symbolDockingPoint, int direction) {
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
  
  // Setters and Getters
  public char getSymbol() {
    return symbol;
  }
  
  public Point getSymbolPaintingMiddle() {
    return this.symbolPaintingMiddle;
  }
}
