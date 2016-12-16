/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package editor;

import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/** Listens for keyboard events. An instance of this class is added to the DrawablePanel instance. 
 * It can only be added to a component that has called setFocusable(true). If two components called
 * setFocusable(true), e.g. ToolBar instance and DrawablePanel instance, it doesn't work. */
public class KeyboardAdapter extends KeyAdapter {
  public void keyPressed(KeyEvent keyEvent) {
    // Does the automat need to take action?
    if (!isTakenHotkey(keyEvent))
      Editor.getDrawablePanel().getAutomat().handleKeyPressed(keyEvent);
  }
  
  /** Returns true if its a taken hotkey such as cmd + z / ctrl + z, false otherwise. */
  private boolean isTakenHotkey(KeyEvent keyEvent) {
    // TODO if (keyEvent == Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())
    
    return false;
  }
}
