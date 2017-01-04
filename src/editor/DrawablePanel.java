/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package editor;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/** The main-panel where the Editor is drawn into. */
public class DrawablePanel extends JPanel implements MouseMotionListener {
  private static final long serialVersionUID = 1L;
  private Automat automat;
  private KeyboardAdapter keyboardAdapter;
  
  // Always set on true, hide them only for the image-export
  private boolean paintMessages;

  public DrawablePanel(ToolBar toolBarCopy) {
    // Initialized with minimal dimensions. This Panel is extended in both
    // directions to the full available space via the Editors GridBagLayout.
    this.setPreferredSize(new Dimension(1, 1));
    this.setBackground(Config.BACKGROUND_COLOR);
    this.setDoubleBuffered(true);
    this.setFocusable(true); // needed for the added KeyListener
    
    addMouseListener();
    addMouseMotionListener(this);
    
    automat = new Automat();
    paintMessages = true;
    
    /* Listens for keyboard events. An instance of this class is added to the DrawablePanel instance. 
     * It can only be added to a component that has called setFocusable(true). If two components called
     * setFocusable(true), e.g. ToolBar instance and DrawablePanel instance, it doesn't work. */
    keyboardAdapter = new KeyboardAdapter();
    this.addKeyListener(keyboardAdapter);
  }

  private void addMouseListener() {
    this.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent evt) {
        handleMousePressed(evt);
      }
      
      public void mouseReleased(MouseEvent evt) {
        if (Editor.getToolBar().getMoveCursorButton().isSelected()) {
          automat.handleMoveToolMouseReleased();
        }
      }
    });
  }
  
  @Override
  public void mouseDragged(MouseEvent evt) {
    if (Editor.getToolBar().getMoveCursorButton().isSelected()) {
      automat.handleMoveToolMouseDragged(evt);
    }
    
  }

  @Override
  public void mouseMoved(MouseEvent evt) {
  }

  private void handleMousePressed(MouseEvent evt) {
    Editor.stopWordAcceptedAnimation();
    
    automat.handleMousePressed(evt);
  }
  
  public void paint(Graphics graphics) {
    super.paint(graphics);
    Graphics2D graphics2D = (Graphics2D) graphics;
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    automat.paint(graphics2D);

    if (paintMessages) {
      ErrorMessage.paint(graphics2D);
      Tooltip.paint(graphics2D);
    }
    
    graphics2D.dispose();
  }

  // called once per frame
  public void update(ToolBar toolBar) {
    ErrorMessage.update();
    Tooltip.update();
  }

  // Changes the cursor according to what the user has selected in the toolBar
  public void setCustomCursor(Cursor cursor) {
    this.setCursor(cursor);
  }
  
  /** Perform logic when a button is clicked, e.g. deselect a selected state when the user switches
   * away from the selection tool, abort transition building if the user switches away from the transition
   * button and the transition is not done building etc. */
  public void toolBarButtonClicked(ToggleButton clickedButton) {
    // Set the custom cursor of the selected ToggleButton
    this.setCustomCursor(clickedButton.getCustomCursor());
    
    // Was something else than the selection tool (first button, the arrow) being clicked?
    if (!clickedButton.equals(Editor.getToolBar().getArrowButton())) {
      // Deselect a currently selected state / transition
      this.automat.deselectSelectedShape();
    }
    
    // Was something else than the transition Button being clicked?
    if (!clickedButton.equals(Editor.getToolBar().getTransitionButton())) {
      this.automat.resetConstructingTransition();
    } else {
      this.automat.resetConstructingTransition();
    }
  }
  
  /** Changes the automat. */
  public void changeAutomat(Automat automat) {
    this.automat = automat;
  }
  
  /** Exports this JPanel into an image and saves it on the file-system. */
  public void imageExport(String filePath) {
    int scale = 2; // Needed for image-quality...
    BufferedImage image = new BufferedImage(getWidth() * scale, getHeight() * scale, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    
    g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
    g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    
    g.scale(scale, scale); // Needed for image quality...
    
    // Hide ErrorMessages and Tooltips if some are currently displayed
    paintMessages = false;
    printAll(g);
    g.dispose();
    paintMessages = true; // restore the default-value
    
    // Write the image into the file
    try { 
        ImageIO.write(image, "png", new File(filePath)); 
    } catch (IOException e) {
        e.printStackTrace();
    }
  }
  
  // Getters
  public Automat getAutomat() {
    return this.automat;
  }
}