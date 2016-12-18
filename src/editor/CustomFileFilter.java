package editor;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class CustomFileFilter extends FileFilter {
  private String extension;
  
  /** @param The extension the FileFilter should filter for (pass in lowerCase), e.g. xml, png */
  public CustomFileFilter(String extension) {
    super();
    
    this.extension = extension;
  }

  @Override
  public boolean accept(File file) {
    if (file.isDirectory())
      return true;

    return getExtension(file).equals(extension);
  }

  @Override
  public String getDescription() {
    return extension.toUpperCase();
  }

  /**
   * Get the extension of a file. In case the file has no extension, it returns an empty String "".
   * @return the extension, e.g. xml
   */  
  public String getExtension(File file) {
      String extension = "";
      String fileName = file.getName();
      int i = fileName.lastIndexOf('.');

      if (i > 0 && i < fileName.length() - 1) {
          extension = fileName.substring(i + 1).toLowerCase();
      }
      return extension;
  }
  
  // Setters and Getters
  public String getExtension() {
    return this.extension;
  }
}
