package editor;

public class Debug {
  /** Prints all states, Transitions and their symbols of the passed automat. */
  public static void printAutomat(Automat automat) {
    printStates(automat);
    printTransitions(automat);
  }
  
  private static void printStates(Automat automat) {
    System.out.println("--------- States ---------");
    
    for (int i = 0; i < automat.getStates().size(); i++) {
      System.out.println("ArrayList Index " + i + " holds stateIndex " + automat.getStates().get(i).stateIndex
          + " at Position (" + automat.getStates().get(i).x + ", " + automat.getStates().get(i).y + ")");
    }
    
    System.out.println("");
  }
  
  private static void printTransitions(Automat automat) {
    System.out.println("--------- Transitions ---------");
    
    for (int i = 0; i < automat.getTransitions().size(); i++) {
      Transition transition = automat.getTransitions().get(i);
      
      System.out.print("ArrayList Index " + i + " holds stateIndices " +
          transition.getTransitionStart().stateIndex + " to " + transition.getTransitionEnd().stateIndex
          + " with Symbols ");
      
      for (int j = 0; j < transition.getSymbols().size(); j++) {
        System.out.print(transition.getSymbols().get(j).getSymbol() + " ");
      }
      System.out.println("");
    }
    
    System.out.println("");
  }
}
