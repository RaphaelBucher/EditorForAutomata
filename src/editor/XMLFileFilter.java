package editor;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class XMLFileFilter extends FileFilter {
  public final static String xml = "xml";

  @Override
  public boolean accept(File file) {
    if (file.isDirectory())
      return true;

    return getExtension(file).equals(xml);
  }

  @Override
  public String getDescription() {
    return "XML";
  }

  /**
   * Get the extension of a file. In case the file has no extension, it returns an empty String "".
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
}
