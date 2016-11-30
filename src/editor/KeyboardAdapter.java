package editor;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/** Listens for keyboard events. An instance of this class is added to the DrawablePanel instance. 
 * It can only be added to a component that has called setFocusable(true). If two components called
 * setFocusable(true), e.g. ToolBar instance and DrawablePanel instance, it doesn't work. */
public class KeyboardAdapter extends KeyAdapter {
  public void keyPressed(KeyEvent keyEvent) {
    int key = keyEvent.getKeyCode();
    
    if (key == KeyEvent.VK_BACK_SPACE) {
      Editor.getDrawablePanel().getAutomat().deleteShape();
    }
  }
}
