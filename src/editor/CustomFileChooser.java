package editor;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class CustomFileChooser extends JFileChooser {
  private static final long serialVersionUID = 1L;

  public CustomFileChooser(String currentDirectoryPath) {
    super(currentDirectoryPath);
  }
  
  @Override
  public void approveSelection() {
    if (getDialogType() == SAVE_DIALOG) {
      File selectedFile = getSelectedFile();
      
      if (selectedFile == null)
        return;
      
      String fileName = selectedFile.getName();
      // Check also if the user omitted the xml-extension
      File noExtension = new File(selectedFile.getAbsolutePath() + ".xml");
      
      if (selectedFile.exists() || noExtension.exists()) {
        if (noExtension.exists())
          fileName += ".xml";
        
        int response = JOptionPane.showConfirmDialog(this,
          "'" + fileName + "' already exists. Do you want to replace it?",
          "Overwrite file", JOptionPane.YES_NO_OPTION,
          JOptionPane.WARNING_MESSAGE);
        if (response != JOptionPane.YES_OPTION)
          return; 
      }
    }

    super.approveSelection();
  }
}
