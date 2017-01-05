/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package editor;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;

public class StartState extends State {
  private Image startArrowImage;
  private Image startArrowSelectedImage;
  
  public StartState(int stateIndex, int x, int y) {
    super(stateIndex, x, y);
    
    String absolutePath = new File("").getAbsolutePath();
    startArrowImage = new ImageIcon(absolutePath + Config.START_STATE_ARROW_ICON_PATH).getImage();
    startArrowSelectedImage = new ImageIcon(absolutePath + Config.START_STATE_ARROW_SELECTED_ICON_PATH).getImage();
  }

  // custom paint method which overrides the one from class State
  public void paint(Graphics2D graphics2D) {
    super.paint(graphics2D);

    if (this.isSelected || this.wordAcceptedPath) {
      graphics2D.drawImage(startArrowSelectedImage, x - 49, y - 11, null);
    } else      
      // Start arrow image
      graphics2D.drawImage(startArrowImage, x - 49, y - 11, null);
  }
  
  /** Returns a deep copy of the State. */
  public State copy() {
    State state = new StartState(stateIndex, x, y);
    return state;
  }
  
  /** Returns the type of the state, e.g. State.STATE or State.START_STATE. */
  public int getType() {
    return State.START_STATE;
  }
}
