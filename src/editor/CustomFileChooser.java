package editor;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class CustomFileChooser extends JFileChooser {
  private static final long serialVersionUID = 1L;
  private CustomFileFilter fileFilter;

  /** @param The file-extension to be set in lowerCase, e.g. xml, png */
  public CustomFileChooser(String extension, String currentDirectoryPath) {
    super(currentDirectoryPath);
    
    fileFilter = new CustomFileFilter(extension);
    
    this.removeChoosableFileFilter(this.getFileFilter());
    this.setFileFilter(fileFilter);
  }
  
  @Override
  public void approveSelection() {
    if (getDialogType() == SAVE_DIALOG) {
      File selectedFile = getSelectedFile();
      
      if (selectedFile == null)
        return;
      
      String fileName = selectedFile.getName();
      // Check also if the user omitted the extension
      File noExtension = new File(selectedFile.getAbsolutePath() + "." + fileFilter.getExtension());
      
      if (selectedFile.exists() || noExtension.exists()) {
        if (noExtension.exists())
          fileName += "." + fileFilter.getExtension();
        
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
  
  // Setters and Getters
  public CustomFileFilter getCustomFileFilter() {
    return this.fileFilter;
  }
}
