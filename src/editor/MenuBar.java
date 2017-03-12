package editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import controlFlow.UserAction;
import transformation.Language;
import transformation.Layout;
import transformation.ReadSymbol;
import transformation.Transformation;
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
  private MenuItem grammar;
  private MenuItem layout;
  private MenuItem removeUnreachableStates;
  private MenuItem toNEA;
  private MenuItem toDEA;
  private MenuItem toMinimalDEA;
  private MenuItem wordAccepted;
  
  // file-choosers
  private final CustomFileChooser xmlFileChooser;
  private final CustomFileChooser pngFileChooser;
  
  public MenuBar() {
    super();
    
    // Create the file choosers
    xmlFileChooser = new CustomFileChooser("xml", new File("").getAbsolutePath() + "/savedAutomata/");
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
        // TODO Dieser Aufruf muss bei allen MenuItems zuerst stehen!!!!!
        Editor.stopWordAcceptedAnimation();
        
        UserAction.resetActions();
        Editor.changeAutonat(new Automat(), false, "");
      }
    });
    fileMenu.add(newAutomat);
    
    // open
    openAutomat = new MenuItem("Open");
    openAutomat.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        Editor.stopWordAcceptedAnimation();
        
        UserAction.resetActions();
        loadAutomat();
      }
    });
    fileMenu.add(openAutomat);
    
    // save
    saveAutomat = new MenuItem("Save");
    saveAutomat.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        Editor.stopWordAcceptedAnimation();
        
        saveAutomat();
      }
    });
    fileMenu.add(saveAutomat);
    
    fileMenu.addSeparator();
    
    // image export
    imageExport = new MenuItem("Image Export");
    imageExport.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        Editor.stopWordAcceptedAnimation();
        
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
        Editor.stopWordAcceptedAnimation();
        
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
        Editor.stopWordAcceptedAnimation();
        
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
    automatMenu = new JMenu("Automaton");
    
    // info
    info = new MenuItem("Info");
    info.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        Editor.stopWordAcceptedAnimation();
        
        String info = Util.automatInfo(Editor.getDrawablePanel().getAutomat());
        new TextFrame("Automaton Info", new Dimension(500, 300), info);
      }
    });
    automatMenu.add(info);
    
    // Typ-3 Grammatik
    grammar = new MenuItem("To Type-3 Grammar");
    grammar.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        Editor.stopWordAcceptedAnimation();
        Automat automatCopy = Editor.getDrawablePanel().getAutomat().copy();
        
        if (automatCopy.getStateByStateIndex(0) != null) {
          String grammar = Util.toGrammar(automatCopy);
          new TextFrame("Type-3 Grammar", new Dimension(620, 360), grammar);
        } else
          ErrorMessage.setMessage(Config.ErrorMessages.startStateMissing);
      }
    });
    automatMenu.add(grammar);
    automatMenu.addSeparator();
    
    // layout
    layout = new MenuItem("Layout");
    layout.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        Editor.stopWordAcceptedAnimation();
        
        Automat automatDeepCopy = Editor.getDrawablePanel().getAutomat().copy();
        Layout.layoutAutomat(automatDeepCopy);
        Editor.changeAutonat(automatDeepCopy, true, "Layout");
      }
    });
    automatMenu.add(layout);
    automatMenu.addSeparator();
    
    // remove unreachable states
    removeUnreachableStates = new MenuItem("Remove unreachable States");
    removeUnreachableStates.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        Editor.stopWordAcceptedAnimation();
        
        Automat automatDeepCopy = Editor.getDrawablePanel().getAutomat().copy();
        Util.deleteUnreachableStates(automatDeepCopy, automatDeepCopy.getStateByStateIndex(0));
        
        // Don't apply the Layout-algorithm, just remove the unreachable states and compute the
        // painting information again
        automatDeepCopy.updatePainting();
        Editor.changeAutonat(automatDeepCopy, true, "Remove unreachable States");
      }
    });
    automatMenu.add(removeUnreachableStates);
    automatMenu.addSeparator();
    
    // toNEA
    toNEA = new MenuItem("Transform to NFA (NEA)");
    toNEA.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        Editor.stopWordAcceptedAnimation();
        
        Automat automat = Editor.getDrawablePanel().getAutomat();
        if (automat.getStateByStateIndex(0) == null) {
          ErrorMessage.setMessage(Config.ErrorMessages.startStateMissing);
          return;
        }
        
        if (Util.isNEA(automat)) {
          Tooltip.setMessage(Config.Tooltips.transformIsNEAAlready, 0);
          return;
        }
        
        // Automat is a real Epsilon-Automat (has at least one Epsilon-Transition)
        Automat automatDeepCopy = automat.copy();
        Transformation.transformToNEA(automatDeepCopy);
        
        automatDeepCopy.updatePainting();
        Editor.changeAutonat(automatDeepCopy, true, "Transform to NFA (NEA)");
      }
    });
    automatMenu.add(toNEA);
    
    // toDEA
    toDEA = new MenuItem("Transform to DFA (DEA)");
    toDEA.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        Editor.stopWordAcceptedAnimation();
        
        Automat automat = Editor.getDrawablePanel().getAutomat();
        if (automat.getStateByStateIndex(0) == null) {
          ErrorMessage.setMessage(Config.ErrorMessages.startStateMissing);
          return;
        }
        
        if (Util.isDEA(automat)) {
          Tooltip.setMessage(Config.Tooltips.transformIsDEAAlready, 0);
          return;
        }
        
        // Automat is not a DEA, just a NEA or Epsilon-Automat
        Automat automatDeepCopy = automat.copy();
        automatDeepCopy = Transformation.transformToDEA(automatDeepCopy);
        
        Layout.layoutAutomat(automatDeepCopy);
        Editor.changeAutonat(automatDeepCopy, true, "Transform to DFA (DEA)");
      }
    });
    automatMenu.add(toDEA);
    
    // toMinimalDEA
    toMinimalDEA = new MenuItem("Transform to minimal DFA (DEA)");
    toMinimalDEA.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        Editor.stopWordAcceptedAnimation();
        
        Automat automat = Editor.getDrawablePanel().getAutomat();
        if (automat.getStateByStateIndex(0) == null) {
          ErrorMessage.setMessage(Config.ErrorMessages.startStateMissing);
          return;
        }
        
        // Get a minimal DEA, the Editors automat is untouched
        Automat minDEA = Transformation.transformToMinimalDEA(Editor.getDrawablePanel().getAutomat());
        if (minDEA != null) {
          // The Editors current automat wasn't a minimal DEA already
          Layout.layoutAutomat(minDEA);
          Editor.changeAutonat(minDEA, true, "Transform to minimal DFA (DEA)");
        } else
          Tooltip.setMessage(Config.Tooltips.transformIsMinimalDEAAlready, 0);
      }
    });
    automatMenu.add(toMinimalDEA);
    
    automatMenu.addSeparator();
    
    // wordAccepted
    wordAccepted = new MenuItem("Word accepted");
    wordAccepted.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        Editor.stopWordAcceptedAnimation();
        
        // Open an input dialog
        String word = (String)JOptionPane.showInputDialog(Editor.getEditor(),
            "Enter the word the automat should test for acceptance", "Word accepted",
            JOptionPane.PLAIN_MESSAGE);
        
        if (word == null) 
          return; // the user aborted the dialog
        
        Automat automat = Editor.getDrawablePanel().getAutomat();
        
        // Store the traveled transitions in this list when the word is tested for acceptance
        ArrayList<ReadSymbol> readTransitionsSymbols = new ArrayList<ReadSymbol>();
        boolean wordAccepted = Language.wordAccepted(word, automat, readTransitionsSymbols);
        
        // Start the animation how the word was accepted / denied
        Editor.startWordAcceptedAnimation(word, wordAccepted, readTransitionsSymbols);
      }
    });
    automatMenu.add(wordAccepted);
    
    // Add the built menu to the MenuBar
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
        Editor.changeAutonat(testAutomat, false, "");
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
