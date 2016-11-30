/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;

public class StartEndState extends State {
  private Image startArrowImage;
  private Image startArrowSelectedImage;
  
  public StartEndState(int stateIndex, int x, int y) {
    super(stateIndex, x, y);
    
    String absolutePath = new File("").getAbsolutePath();
    startArrowImage = new ImageIcon(absolutePath + Config.START_STATE_ARROW_ICON_PATH).getImage();
    startArrowSelectedImage = new ImageIcon(absolutePath + Config.START_STATE_ARROW_SELECTED_ICON_PATH).getImage();
  }

  // custom paint method which overrides the one from class State
  public void paint(Graphics2D graphics2D) {
    super.paint(graphics2D);
    
    if (this.isSelected) {
      graphics2D.setColor(Config.SELECTED_STATE_COLOR);
      graphics2D.drawImage(startArrowSelectedImage, x - 49, y - 11, null);
    } else
      // Start arrow image
      graphics2D.drawImage(startArrowImage, x - 49, y - 11, null);
    

    // draw the second inner oval of an end-state
    graphics2D.drawOval(x - (Config.END_STATE_INNER_DIAMETER / 2), y - (Config.END_STATE_INNER_DIAMETER / 2),
        Config.END_STATE_INNER_DIAMETER, Config.END_STATE_INNER_DIAMETER);
    
    graphics2D.setColor(Color.BLACK);
  }
}
