/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;

public class StartEndState extends State {
  private Image startArrowImage;
  
  public StartEndState(int stateIndex, int x, int y) {
    super(stateIndex, x, y);
    
    startArrowImage = new ImageIcon(new File("").getAbsolutePath() + Config.START_STATE_ARROW_ICON_PATH).getImage();
  }

  // custom paint method which overrides the one from class State
  public void paint(Graphics2D graphics2D) {
    super.paint(graphics2D);

    // draw the second inner oval of an end-state
    graphics2D.drawOval(x - (Config.END_STATE_INNER_DIAMETER / 2), y - (Config.END_STATE_INNER_DIAMETER / 2),
        Config.END_STATE_INNER_DIAMETER, Config.END_STATE_INNER_DIAMETER);
    
    // Start arrow image
    graphics2D.drawImage(startArrowImage, x - 49, y - 11, null);
  }
}
