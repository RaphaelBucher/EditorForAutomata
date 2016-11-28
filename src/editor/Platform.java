package editor;

/** A Utility class to determine the platform the application is running on. */
public class Platform {
  private static boolean initialized;
  private static boolean isMac;
  private static boolean isWindows;
  
  private static void init() {
    isMac = System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0;
    isWindows = System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
    initialized = true;
  }
  
  public static boolean isMac() {
    if (!initialized)
      init();
    
    return isMac;
  }
  
  public static boolean isWindows() {
    if (!initialized)
      init();
    
    return isWindows;
  }
}
