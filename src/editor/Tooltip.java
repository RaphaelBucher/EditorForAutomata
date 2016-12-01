/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/** Atooltip message which is displayed in the top left corner of the drawable Panel. */
public class Tooltip {
  // Only one Tooltip possible at a time. New invokations will override old ones.
  private static Message tooltip = new Message("", Config.TOOLTIP_DURATION_MILLIS);

  public static void setMessage(String message) {
    tooltip.setMessage(message);
  }

  // paints the message with a fading effect
  public static void paint(Graphics2D graphics2D) {
    if (!tooltip.getMessage().equals("")) {
      // Calculate the middle of the drawable Area
      Dimension drawableArea = graphics2D.getClipBounds().getSize();
      int middleX = (int)drawableArea.getWidth() / 2;
      
      // Font metric calculations
      Font messageFont = new Font("Arial", Font.PLAIN, Config.TOOLTIP_FONT_SIZE);
      graphics2D.setFont(messageFont);
      FontMetrics fontMetrics = graphics2D.getFontMetrics();
      int messageWidth = fontMetrics.stringWidth(tooltip.getMessage()); // Render the String centered
      int messageHeight = fontMetrics.getAscent();
      int whiteSpace = 6; // 10 pixels of white space around the message between the border
      
      // The white round filled rectangle to "clear" up the area
      graphics2D.setColor(new Color(255, 255, 255, tooltip.getFadeAlpha()));
      graphics2D.fillRoundRect(middleX - messageWidth / 2 - whiteSpace,
          0, messageWidth + 2 * whiteSpace, messageHeight + whiteSpace * 2, 9, 9);
      
      // The black round Border around the filled white rectangle
      graphics2D.setColor(new Color(0, 0, 0, tooltip.getFadeAlpha()));
      graphics2D.drawRoundRect(middleX - messageWidth / 2 - whiteSpace,
          0, messageWidth + 2 * whiteSpace, messageHeight + whiteSpace * 2, 9, 9);
      
      // The passed y-coordinate is the BOTTOM of the first Letter, not the top.
      graphics2D.drawString(tooltip.getMessage(), middleX - messageWidth / 2,
          messageHeight + fontMetrics.getDescent());
      
      graphics2D.setColor(Color.BLACK); // restore the default color for further rendering
    }
  }

  public static void update() {
    tooltip.update();
  }
}
