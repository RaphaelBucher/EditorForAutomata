/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

public class ToggleButton extends JToggleButton {
  private static final long serialVersionUID = 1L;
  private Cursor customCursor;
  private ImageIcon image; // The Buttons image. There's no text displayed.

  // To simulate the hover-effect, see this.paintComponent()
  private boolean isHovered;

  // parentObject passes down the address of the parent-object. It's used to
  // call the toggleButtonEventHandler(). The instances of this class itself
  // lack the information of each others selected property.
  public ToggleButton(String iconPath, String windowsCursorIconPath,
      String cursorName, ToolBar parentObject) {
    super(new ImageIcon(iconPath), false);
    this.setBorder(null);
    this.setFocusable(false);

    // The image of the button
    image = new ImageIcon(iconPath);
    
    // Cursor
    createCustomCursor(iconPath, windowsCursorIconPath, cursorName);

    /** Changes background color if the user hovers the mouse over the button. */
    ToggleButton tmpThis = this;
    this.addMouseListener(new MouseAdapter() {
      public void mouseEntered(MouseEvent evt) {
        isHovered = true;
      }

      public void mouseExited(MouseEvent evt) {
        isHovered = false;
      }

      public void mousePressed(MouseEvent evt) {
        parentObject.toggleButtonEventHandler(tmpThis);
      }
    });
  }
  
  /** Sets the drawableAreas cursor if the button is pressed. Mac and Windows have their own Icons
   * due to Windows cursor size limitations of 32x32 pixels. */
  private void createCustomCursor(String iconPath, String windowsCursorIconPath, String cursorName) {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    
    if (!Platform.isWindows()) {
      // Mac or Linux
      customCursor = toolkit.createCustomCursor(image.getImage(),
          new Point(Config.CURSOR_WIDTH / 2, Config.CURSOR_HEIGHT / 2), cursorName);
    } else {
      // Windows
      customCursor = toolkit.createCustomCursor(new ImageIcon(windowsCursorIconPath).getImage(),
          new Point(Config.WINDOWS_CURSOR_WIDTH / 2, Config.WINDOWS_CURSOR_HEIGHT / 2), cursorName);
    }
  }

  @Override
  public void paintComponent(Graphics graphics) {
    Graphics2D graphics2D = (Graphics2D) graphics;
    
    // Windows has a native hover and selection effect... mac in my current use not.
    if (!Platform.isWindows()) {
      // hover effect
      if (this.isHovered) {
        graphics2D.setColor(new Color(220, 220, 220));
        graphics2D.fillRoundRect(0, 0, Config.TOOLBAR_ICON_WIDTH, Config.TOOLBAR_ICON_HEIGHT, 15, 15);
      }
      
      // darken the area if selected
      if (this.isSelected()) {
        graphics2D.setColor(new Color(196, 196, 196));
        graphics2D.fillRoundRect(0, 0, Config.TOOLBAR_ICON_WIDTH, Config.TOOLBAR_ICON_HEIGHT, 15, 15);
      }
    }
    
    super.paintComponent(graphics);
  }
  
  public Cursor getCustomCursor() {
    return this.customCursor;
  }
  
  public ImageIcon getImageIcon() {
    return this.image;
  }
}
