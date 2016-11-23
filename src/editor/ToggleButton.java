/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

public class ToggleButton extends JToggleButton {
  private static final long serialVersionUID = 1L;
  private boolean isHovered; // to simulate the hover-effect, see
                             // this.paintComponent()

  // parentObject passes down the address of the parent-object. It's used to
  // call the toggleButtonEventHandler(). The instances of this class itself
  // lack the information of each others selected property.
  public ToggleButton(String iconPath, boolean selected, ToolBar parentObject) {
    super(new ImageIcon(iconPath), selected);
    this.setFocusable(false);

    /** Changes background color if the user hovers the mouse over the button */
    // Inside the block below, this refers to the MouseAdapter instance
    ToggleButton tmpThis = this;
    this.addMouseListener(new MouseAdapter() {
      public void mouseEntered(MouseEvent evt) {
        setHovered(true);
      }

      public void mouseExited(MouseEvent evt) {
        setHovered(false);
      }

      public void mouseClicked(MouseEvent evt) {
        if (isHovered)
          isHovered = false;

        parentObject.toggleButtonEventHandler(tmpThis);
      }
    });
  }

  @Override
  public void paintComponent(Graphics g) {
    if (isHovered) {
      boolean saveSelection = this.isSelected();

      this.setSelected(true);
      super.paintComponent(g);
      this.setSelected(saveSelection);
    } else
      super.paintComponent(g);
  }

  public boolean isHovered() {
    return isHovered;
  }

  public void setHovered(boolean isHovered) {
    this.isHovered = isHovered;
  }
}
