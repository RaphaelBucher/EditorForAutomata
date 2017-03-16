package controlFlow;

import java.util.LinkedList;

import editor.Editor;

/** Represents a reversible user-action. Used for the undo/redo functionality. */
public abstract class UserAction {
  /** How many actions will be stored for undoing / redoing. */
  private static final int listSize = 30; //TODO increase for final build?
  /** An index for the list iteration. Undoing decrements, redoing increments. */
  private static int currentListIndex = -1;
  private static LinkedList<UserAction> userActions = new LinkedList<UserAction>();
  
  protected abstract void undo();
  protected abstract void redo();
  
  /** Adds a UserAction to the list. */
  public static void addAction(UserAction userAction) {
    // If the user is not in an undo-process, currentListIndex equals userActions.size() - 1
    while (currentListIndex < userActions.size() - 1) {
      // The user is in an undo-process, the currentListIndex doesn't point at the
      // last element of the list. Adding an Action here will delete all undone actions
      // from the list, and by adding this current action the user will start a new
      // history branch from here.
      userActions.removeLast();
    }
    
    userActions.add(userAction);
    
    if (userActions.size() > listSize)
      userActions.removeFirst();
    
    currentListIndex = userActions.size() - 1; // put the index on the last element
    
    // Update the MenuBar
    updateUndoRedo();
  }
  
  /** Reverts the last action. The action itself will not be deleted from this list yet
   * in case the user wants to redo it. As soon as the user performs a NEW action,
   * all currently undon actions are unstored. */
  public static void undoAction() {
    if (canUndo()) {
      userActions.get(currentListIndex).undo();
      currentListIndex--; // needs to be AFTER the redo-call. Wanna undo the CURRENT pointed action
      
      // Update the MenuBar
      updateUndoRedo();
    }
  }
  
  /** Tell whether there is an action that can be undone. */
  public static boolean canUndo() {
    return currentListIndex >= 0;
  }
  
  /** Tell whether there is an action that can be redone. */
  public static boolean canRedo() {
    return currentListIndex + 1 <= userActions.size() - 1;
  }
  
  /** Repeats the last action. The action itself will not be deleted from this list yet
   * in case the user wants to redo it. As soon as the user performs a NEW action,
   * all currently undon actions are unstored. */
  public static void redoAction() {
    if (canRedo()) {
      // needs to be BEFORE the redo-call! Wanna redo the CURRENT + 1 pointed action (the next action)
      currentListIndex++; 
      userActions.get(currentListIndex).redo();
      
      // Update the MenuBar
      updateUndoRedo();
    }
  }
  
  /** Updates the MenuBars Undo / Redo MenuItems with texts and enable-flags. */
  private static void updateUndoRedo() {
    String undoText = "Undo";
    String redoText = "Redo";
    
    if (canUndo())
      undoText += " " + userActions.get(currentListIndex).toString();
    
    if (canRedo())
      redoText += " " + userActions.get(currentListIndex + 1).toString();
    
    // Update the MenuBar
    Editor.getCustomMenuBar().updateUndoRedo(canUndo(), canRedo(), undoText, redoText);
  }
  
  /** Deletes all added Actions from the list and sets the currentListIndex to the initial value. 
   * This method is called when loading an automat / starting a new automat. */
  public static void resetActions() {
    userActions.clear();
    currentListIndex = -1;
    
    // Update the MenuBar
    updateUndoRedo();
  }
}
