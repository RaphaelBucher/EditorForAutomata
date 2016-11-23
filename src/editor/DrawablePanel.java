/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/** The main-panel where the Editor is drawn into. */
public class DrawablePanel extends JPanel {
  private static final long serialVersionUID = 1L;
  private Cursor stateCursor;
  private Cursor startStateCursor;
  private Cursor endStateCursor;
  private Cursor startEndStateCursor;
  private Cursor transitionCursor;

  private Automat automat;

  public DrawablePanel() {
    this.setPreferredSize(new Dimension(Config.DRAWABLE_PANEL_X, Config.DRAWABLE_PANEL_Y));
    this.setBackground(Config.BACKGROUND_COLOR);
    this.setDoubleBuffered(true);
    this.setFocusable(true);

    initCursors();
    addMouseListener();

    automat = new Automat();

    // del. used for testing only
    automat.createExampleAutomat();
  }

  private void addMouseListener() {
    this.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        handleMouseClicked(evt);
      }
    });
  }

  private void handleMouseClicked(MouseEvent evt) {
    automat.handleMouseClicked(evt, this.getCursor().getName());
  }

  private void initCursors() {
    String absolutePath = new File("").getAbsolutePath();
    Toolkit toolkit = Toolkit.getDefaultToolkit();

    // add State Cursor
    ImageIcon cursorImg = new ImageIcon(absolutePath + Config.ADD_STATE_ICON_PATH);
    // The second parameter of createCustomCursor is the cursors hotspot. This
    // specifies where in
    // the image the actual cursor potition is. (1, 1) would be top left, (25,
    // 25) in the middle.
    stateCursor = toolkit.createCustomCursor(cursorImg.getImage(),
        new Point(Config.CURSOR_XY / 2, Config.CURSOR_XY / 2), Config.Cursor_names.STATE_CURSOR);

    // add start State Cursor
    cursorImg = new ImageIcon(absolutePath + Config.ADD_START_STATE_ICON_PATH);
    startStateCursor = toolkit.createCustomCursor(cursorImg.getImage(),
        new Point(Config.CURSOR_XY / 2, Config.CURSOR_XY / 2), Config.Cursor_names.START_STATE_CURSOR);

    // add end State Cursor
    cursorImg = new ImageIcon(absolutePath + Config.ADD_END_STATE_ICON_PATH);
    endStateCursor = toolkit.createCustomCursor(cursorImg.getImage(),
        new Point(Config.CURSOR_XY / 2, Config.CURSOR_XY / 2), Config.Cursor_names.END_STATE_CURSOR);

    // add start-end State Cursor
    cursorImg = new ImageIcon(absolutePath + Config.START_END_STATE_CURSOR_PATH);
    startEndStateCursor = toolkit.createCustomCursor(cursorImg.getImage(),
        new Point(Config.CURSOR_XY / 2, Config.CURSOR_XY / 2), Config.Cursor_names.START_END_STATE_CURSOR);

    // add start-end State Cursor
    cursorImg = new ImageIcon(absolutePath + Config.ADD_TRANSITION_ICON_PATH);
    transitionCursor = toolkit.createCustomCursor(cursorImg.getImage(),
        new Point(Config.CURSOR_XY / 2, Config.CURSOR_XY / 2), Config.Cursor_names.TRANSITION_CURSOR);
  }

  public void paint(Graphics graphics) {
    super.paint(graphics);
    Graphics2D graphics2D = (Graphics2D) graphics;

    automat.paint(graphics2D);

    ErrorMessage.paint(graphics2D);

    // Del
    // graphics2D.drawRect(0, 0, 1, 1);
    // 1200, 800
    // graphics2D.drawRect(Config.DRAWABLE_PANEL_X - 1, Config.DRAWABLE_PANEL_Y
    // - 1, 1, 1);
    // => drawable area is: (0, 0) bis (1199, 799)

    graphics2D.dispose();
  }

  // called once per frame
  public void update(ToolBar toolBar) {
    updateCursor(toolBar);
    ErrorMessage.update();
  }

  // changes the cursor according to what the user has selected in the toolBar
  private void updateCursor(ToolBar toolBar) {
    if (toolBar.getAddStateButton().isSelected()) {
      if (toolBar.getAddStartStateButton().isSelected()) {
        if (toolBar.getAddEndStateButton().isSelected()) {
          // all three
          this.setCursor(startEndStateCursor);
          return;
        } else {
          // state, start, end not
          this.setCursor(startStateCursor);
          return;
        }
      } else if (toolBar.getAddEndStateButton().isSelected()) {
        // addState, addStartState not, addEndState
        this.setCursor(endStateCursor);
        return;
      }
      // only start state is selected. Other combos are excluded at
      // handleStateButtons of class
      // ToolBar.
      this.setCursor(stateCursor);
      return;
    }

    // transition cursor
    if (toolBar.getAddTransitionButton().isSelected()) {
      this.setCursor(transitionCursor);
      return;
    }

    // nothing is selected, restore default cursor
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }
}
