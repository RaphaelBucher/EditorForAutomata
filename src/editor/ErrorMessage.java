/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;

/** An error message which is displayed in the middle of the drawable Panel. */
public class ErrorMessage {
  // Only one error message possible at a time. New invokations will override
  // old ones.
  private static String message = "";
  private static long messageCreationTimeStamp = 0L;

  public static void setMessage(String message) {
    ErrorMessage.message = message;
    messageCreationTimeStamp = System.currentTimeMillis();
  }

  public static void paint(Graphics2D graphics2D) {
    if (!message.equals("")) {
      // fade effect
      graphics2D.setColor(new Color(255, 0, 0, getFadeAlpha()));

      graphics2D.setFont(new Font("Arial", Font.PLAIN, Config.ERROR_MESSAGE_FONT_SIZE));
      Dimension drawableArea = graphics2D.getClipBounds().getSize();
      // Render the String centered
      graphics2D.drawString(message,
          (int) drawableArea.getWidth() / 2 - message.length() * Config.ERROR_MESSAGE_FONT_SIZE / 5,
          (int) drawableArea.getHeight() / 2 - Config.ERROR_MESSAGE_FONT_SIZE / 2);
    }
  }

  /**
   * Simulates the fade effect. The first half of the message duration, full red
   * Color is shown. During the second half, the message fades away by reducing
   * the alpha from 255 to 0.
   */
  private static int getFadeAlpha() {
    // For e.g. ERROR_MESSAGE_DURATION_MILLIS = 3000, goes down from 3000 to 0
    // (negative
    // values possible, will be dealt with later
    long remainingMillis = Config.ERROR_MESSAGE_DURATION_MILLIS
        - (System.currentTimeMillis() - messageCreationTimeStamp);

    // fade effect starts at halftime. So for e.g. ERROR_MESSAGE_DURATION_MILLIS
    // = 3000,
    // it goes down from 255 * 2 to 0 and limits result to 255
    int alpha = (int) ((double) remainingMillis / Config.ERROR_MESSAGE_DURATION_MILLIS * 255 * 2);

    // limit the value to 255 and ensure its not negative
    alpha = Math.min(alpha, 255);
    alpha = Math.max(alpha, 0);

    return alpha;
  }

  public static void update() {
    if (System.currentTimeMillis() - messageCreationTimeStamp > Config.ERROR_MESSAGE_DURATION_MILLIS) {
      message = "";
    }
  }
}
