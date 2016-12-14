package editor;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class MenuBar extends JMenuBar {
  private static final long serialVersionUID = 1L;
  
  // file-menu
  private JMenu fileMenu;
  private MenuItem newAutomat;
  private MenuItem openAutomat;
  private MenuItem saveAutomat;
  
  // automat-menu
  private JMenu automatMenu;
  
  public MenuBar() {
    super();
    
    // --- file-menu ---
    fileMenu = new JMenu("file");
    
    // new
    newAutomat = new MenuItem("new");
    newAutomat.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        Editor.changeAutonat(new Automat());
      }
    });
    fileMenu.add(newAutomat);
    
    // open
    openAutomat = new MenuItem("open");
    openAutomat.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        Automat testAutomat = XMLFileParser.readAutomatFromXMLFile(new File("").getAbsolutePath()
            + "/savedAutomats/automat1.xml");
        if (testAutomat != null) {
          Debug.printAutomat(testAutomat);
          Editor.changeAutonat(testAutomat);
        } else
          ErrorMessage.setMessage(Config.ErrorMessages.xmlParsingError);
      }
    });
    fileMenu.add(openAutomat);
    
    // save
    saveAutomat = new MenuItem("save");
    saveAutomat.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        XMLFileParser.writeAutomatToXMLFile(Editor.getDrawablePanel().getAutomat(),
            new File("").getAbsolutePath() + "/savedAutomats/automat1.xml");
      }
    });
    fileMenu.add(saveAutomat);

    this.add(fileMenu);
    
    // --- Automat-menu ---
    automatMenu = new JMenu("automat");
    this.add(automatMenu);
    
    this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
  }
}
