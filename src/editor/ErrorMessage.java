/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/** An error message which is displayed in the middle of the drawable Panel. */
public class ErrorMessage {
  // Only one error message possible at a time. New invokations will override old ones.
  private static Message errorMessage = new Message("", Config.ERROR_MESSAGE_DURATION_MILLIS);

  public static void setMessage(String message) {
    errorMessage.setMessage(message);
  }

  // paints the message with a fading effect
  public static void paint(Graphics2D graphics2D) {
    if (!errorMessage.getMessage().equals("")) {
      // Calculate the middle of the drawable Area
      Dimension drawableArea = graphics2D.getClipBounds().getSize();
      int middleX = (int)drawableArea.getWidth() / 2;
      int middleY = (int)drawableArea.getHeight() / 2;
      
      // Font metric calculations
      Font messageFont = new Font("Arial", Font.PLAIN, Config.ERROR_MESSAGE_FONT_SIZE);
      graphics2D.setFont(messageFont);
      FontMetrics fontMetrics = graphics2D.getFontMetrics();
      int messageWidth = fontMetrics.stringWidth(errorMessage.getMessage()); // Render the String centered
      int messageHeight = fontMetrics.getAscent();
      int whiteSpace = 10; // 10 pixels of white space around the message between the border
      
      // The white round filled rectangle to "clear" up the area
      graphics2D.setColor(new Color(255, 255, 255, errorMessage.getFadeAlpha()));
      graphics2D.fillRoundRect(middleX - messageWidth / 2 - whiteSpace,
          middleY - messageHeight / 2 - whiteSpace, messageWidth + 2 * whiteSpace,
          messageHeight + whiteSpace * 2, 12, 12);
      
      // The red round Border around the filled white rectangle
      graphics2D.setColor(new Color(255, 0, 0, errorMessage.getFadeAlpha()));
      graphics2D.drawRoundRect(middleX - messageWidth / 2 - whiteSpace,
          middleY - messageHeight / 2 - whiteSpace, messageWidth + 2 * whiteSpace,
          messageHeight + whiteSpace * 2, 12, 12);
      
      // The passed y-coordinate is the BOTTOM of the first Letter, not the top.
      graphics2D.drawString(errorMessage.getMessage(), middleX - messageWidth / 2,
          middleY + messageHeight / 2 - fontMetrics.getDescent() / 2);
      
      graphics2D.setColor(Color.BLACK); // restore the default color for further rendering
    }
  }

  public static void update() {
    errorMessage.update();
  }
}
