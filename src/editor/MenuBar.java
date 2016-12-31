package editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;

import controlFlow.UserAction;
import transformation.Layout;
import transformation.Util;

public class MenuBar extends JMenuBar {
  private static final long serialVersionUID = 1L;
  
  // file-menu
  private JMenu fileMenu;
  private MenuItem newAutomat;
  private MenuItem openAutomat;
  private MenuItem saveAutomat;
  private MenuItem imageExport;
  
  // edit-menu
  private JMenu editMenu;
  private MenuItem undo;
  private MenuItem redo;
  
  // automat-menu
  private JMenu automatMenu;
  private MenuItem info;
  private MenuItem layout;
  
  // file-choosers
  private final CustomFileChooser xmlFileChooser;
  private final CustomFileChooser pngFileChooser;
  
  public MenuBar() {
    super();
    
    // Create the file choosers
    xmlFileChooser = new CustomFileChooser("xml", new File("").getAbsolutePath() + "/savedAutomats/");
    pngFileChooser = new CustomFileChooser("png", new File("").getAbsolutePath() + "/exportedImages/");
    
    // Init the Menus
    initFileMenu();
    initEditMenu();
    initAutomatMenu();

    this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
  }
  
  /** Helper method for the constructor */
  private void initFileMenu() {
    // --- file-menu ---
    fileMenu = new JMenu("File");
    
    // new
    newAutomat = new MenuItem("New");
    newAutomat.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        UserAction.resetActions();
        Editor.changeAutonat(new Automat());
      }
    });
    fileMenu.add(newAutomat);
    
    // open
    openAutomat = new MenuItem("Open");
    openAutomat.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        UserAction.resetActions();
        loadAutomat();
      }
    });
    fileMenu.add(openAutomat);
    
    // save
    saveAutomat = new MenuItem("Save");
    saveAutomat.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        saveAutomat();
      }
    });
    fileMenu.add(saveAutomat);
    
    fileMenu.addSeparator();
    
    // image export
    imageExport = new MenuItem("Image Export");
    imageExport.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        exportImage();
      }
    });
    fileMenu.add(imageExport);

    this.add(fileMenu);
  }
  
  /** Helper method for the constructor */
  private void initEditMenu() {
    // --- edit-menu ---
    editMenu = new JMenu("Edit");
    
    // undo
    undo = new MenuItem("Undo");
    
    Action undoAction = new AbstractAction("undoAction") {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent e) {
        Automat automat = Editor.getDrawablePanel().getAutomat();
        automat.resetConstructingTransition();
        automat.handleMoveToolMouseReleased();
        automat.deselectSelectedShape();
        
        // perform the action
        UserAction.undoAction();
      }
    };
    
    undoAction.putValue(Action.ACCELERATOR_KEY,
        KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    undo.setAction(undoAction);
    editMenu.add(undo);
    
    // redo
    redo = new MenuItem("Redo");
    
    Action redoAction = new AbstractAction("redoAction") {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent e) {
        // perform the action
        UserAction.redoAction();
      }
    };
    
    redoAction.putValue(Action.ACCELERATOR_KEY,
        KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    redo.setAction(redoAction);
    editMenu.add(redo);
    
    // Update texts and hotkeys
    updateUndoRedo(false, false, "Undo", "Redo");

    this.add(editMenu);
  }
  
  /** Update Menus undo and redo (normal or unclickable greyed out) */
  public void updateUndoRedo(boolean undoEnabled, boolean redoEnabled, String undoText, String redoText) {
    undo.setEnabled(undoEnabled);
    redo.setEnabled(redoEnabled);
      
    undo.setText(undoText);
    redo.setText(redoText);
  }
  
  /** Helper method for the constructor */
  private void initAutomatMenu() {
    // --- Automat-menu ---
    automatMenu = new JMenu("Automat");
    
    // info
    info = new MenuItem("Info");
    info.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        String info = Util.automatInfo(Editor.getDrawablePanel().getAutomat());
        new TextFrame("Automat Info", new Dimension(500, 300), info);
      }
    });
    automatMenu.add(info);
    
    automatMenu.addSeparator();
    
    // layout
    layout = new MenuItem("Layout");
    layout.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        // TODO control flow (save references of old and new automat)
        Automat automatDeepCopy = Editor.getDrawablePanel().getAutomat().copy();
        Layout.layoutAutomat(automatDeepCopy);
        Editor.changeAutonat(automatDeepCopy);
      }
    });
    automatMenu.add(layout);
    
    this.add(automatMenu);
  }
  
  /** Saves an Automat to as an XML-File. */
  private void saveAutomat() {
    int returnVal = xmlFileChooser.showSaveDialog(Editor.getEditor());
    
    // Did the user press the "save"-Button?
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = xmlFileChooser.getSelectedFile();
      String filePath = file.getAbsolutePath();
      
      // Add .xml extension in case the file doesn't end with this extension
      CustomFileFilter xmlFileFilter = xmlFileChooser.getCustomFileFilter();
      if (!xmlFileFilter.getExtension(file).equals(xmlFileFilter.getExtension()))
        filePath += "." + xmlFileFilter.getExtension();
      
      XMLFileParser.writeAutomatToXMLFile(Editor.getDrawablePanel().getAutomat(), filePath);
    }
  }
  
  /** Loads an Automat from a chosen XML-File. */
  private void loadAutomat() {
    int returnVal = xmlFileChooser.showOpenDialog(Editor.getEditor());
    
    // Did the user open a file?
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      String filePath = xmlFileChooser.getSelectedFile().getAbsolutePath();

      Automat testAutomat = XMLFileParser.readAutomatFromXMLFile(filePath);
      
      if (testAutomat != null) {
        // Debug.printAutomat(testAutomat);
        Editor.changeAutonat(testAutomat);
      } else
        ErrorMessage.setMessage(Config.ErrorMessages.xmlParsingError);
    }
  }
  
  /** Exports the Automat as a png-file. */
  private void exportImage() {
    int returnVal = pngFileChooser.showSaveDialog(Editor.getEditor());
    
    // Did the user press the "save"-Button?
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = pngFileChooser.getSelectedFile();
      String filePath = file.getAbsolutePath();
      
      // Add .png extension in case the file doesn't end with this extension
      CustomFileFilter pngFileFilter = pngFileChooser.getCustomFileFilter();
      if (!pngFileFilter.getExtension(file).equals(pngFileFilter.getExtension()))
        filePath += "." + pngFileFilter.getExtension();
      
      Editor.getDrawablePanel().imageExport(filePath);
    }
  }
}
