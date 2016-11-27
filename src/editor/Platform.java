package editor;

/** A Utility class to determine the platform the application is running on. */
public class Platform {
  public static boolean isMac() {
    return System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0;
  }
  
  public static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
  }
}
