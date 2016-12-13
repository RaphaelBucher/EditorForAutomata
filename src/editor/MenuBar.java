package editor;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class MenuBar extends JMenuBar {
  private static final long serialVersionUID = 1L;
  
  // file-menu
  private JMenu fileMenu;
  private MenuItem newAutomat;
  private MenuItem saveAutomat;
  
  // automat-menu
  private JMenu automatMenu;
  
  public MenuBar() {
    super();
    
    // file-menu
    fileMenu = new JMenu("file");
    newAutomat = new MenuItem("new");
    saveAutomat = new MenuItem("save");
    
    fileMenu.add(newAutomat);
    fileMenu.add(saveAutomat);
    this.add(fileMenu);
    
    // automat-menu
    automatMenu = new JMenu("automat");
    this.add(automatMenu);
    
    this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
  }
}
