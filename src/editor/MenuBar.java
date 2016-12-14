package editor;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
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
  
  // file-chooser
  private final CustomFileChooser fileChooser;
  
  // XML File-Filter
  private final XMLFileFilter xmlFileFilter;
  
  public MenuBar() {
    super();
    
    // Create a file chooser
    fileChooser = new CustomFileChooser(new File("").getAbsolutePath() + "/savedAutomats/");
    
    // File-Filter
    xmlFileFilter = new XMLFileFilter();
    fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());
    fileChooser.setFileFilter(xmlFileFilter);
    
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
        loadAutomat();
      }
    });
    fileMenu.add(openAutomat);
    
    // save
    saveAutomat = new MenuItem("save");
    saveAutomat.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        saveAutomat();
      }
    });
    fileMenu.add(saveAutomat);

    this.add(fileMenu);
    
    // --- Automat-menu ---
    automatMenu = new JMenu("automat");
    this.add(automatMenu);
    
    this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
  }
  
  /** Saves an Automat to as an XML-File. */
  private void saveAutomat() {
    int returnVal = fileChooser.showSaveDialog(Editor.getEditor());
    
    // Did the user press the "save"-Button?
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = fileChooser.getSelectedFile();
      String filePath = file.getAbsolutePath();
      
      // Add .xml extension in case the file doesn't end with this extension
      if (!xmlFileFilter.getExtension(file).equals(XMLFileFilter.xml))
        filePath += ".xml";
      
      XMLFileParser.writeAutomatToXMLFile(Editor.getDrawablePanel().getAutomat(), filePath);
    }
  }
  
  /** Loads an Automat from a chosen XML-File. */
  private void loadAutomat() {
    int returnVal = fileChooser.showOpenDialog(Editor.getEditor());
    
    // Did the user open a file?
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      String filePath = fileChooser.getSelectedFile().getAbsolutePath();

      Automat testAutomat = XMLFileParser.readAutomatFromXMLFile(filePath);
      
      if (testAutomat != null) {
        // Debug.printAutomat(testAutomat);
        Editor.changeAutonat(testAutomat);
      } else
        ErrorMessage.setMessage(Config.ErrorMessages.xmlParsingError);
    }
  }
}
