package editor;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyboardAdapter extends KeyAdapter {
  public void keyPressed(KeyEvent keyEvent) {
    int key = keyEvent.getKeyCode();

    System.out.println("a key pressed");
    
    if (key == KeyEvent.VK_DOWN) {
      System.out.println("vk down");
    }
  }
  
  public void keyReleased(KeyEvent keyEvent) {
    int key = keyEvent.getKeyCode();

    System.out.println("a key released");
    
    if (key == KeyEvent.VK_DOWN) {
      System.out.println("vk down");
    }
  }
}
