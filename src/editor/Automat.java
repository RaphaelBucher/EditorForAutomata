/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Automat {
  private ArrayList<State> states;
  private ArrayList<Transition> transitions;
  // The unfinished Transition that is currently being constructed by the user
  // when he operates with the Transition tool. 
  private Transition constructingTransition; 
  // A reference to the currently selected Shape. Null if none is selected.
  private Shape selectedShape; 
  
  // If the move-tool is selected and the mouseClick hit a state, save the original mouseEvent for positioning
  private MouseEvent moveToolOrigMouseEvent;
  private State movedState;
  private Point movedStateOriginalLocation;

  public Automat() {
    this.states = new ArrayList<State>();
    this.transitions = new ArrayList<Transition>();
  }

  /** Iterates over all added states, transitions and their symbols and draws them. */
  public void paint(Graphics2D graphics2D) {
    // states
    for (int i = 0; i < states.size(); i++) {
      states.get(i).paint(graphics2D);
    }
    
    // transitions
    for (int i = 0; i < transitions.size(); i++) {
      transitions.get(i).paint(graphics2D);
    }
  }

  public void handleMousePressed(MouseEvent evt) {
    // Put the new state exactly where the cursor-image is
    int stateX = evt.getX();
    int stateY = evt.getY() - 1; // little adjustment
    
    ToggleButton selectedButton = Editor.getToolBar().getSelectedButton();
    ToolBar toolBar = Editor.getToolBar();
    
    if (selectedButton.equals(toolBar.getArrowButton())) {
      // Default arrow-cursor. 
      checkShapeSelection(evt);
    } else if (selectedButton.equals(toolBar.getMoveCursorButton())) {
      // Move tool
      handleMoveToolMousePressed(evt);
    } else if (selectedButton.equals(toolBar.getStateButton())) {
      // Add state Cursor
      addState(new State(findNewStateIndex(), stateX, stateY));
    } else if (selectedButton.equals(toolBar.getStartStateButton())) {
      // Add start state cursor
      addState(new StartState(0, stateX, stateY));
    } else if (selectedButton.equals(toolBar.getEndStateButton())) {
      // Add end state cursor
      addState(new EndState(findNewStateIndex(), stateX, stateY));
    } else if (selectedButton.equals(toolBar.getStartEndStateButton())) {
      // Add start-end state cursor
      addState(new StartEndState(0, stateX, stateY));
    } else if (selectedButton.equals(toolBar.getTransitionButton())) {
      // Transition cursor
      handleTransitionConstruction(evt);
    }
  }
  
  /** Is Invoked if the move tool was selected and the mouse pressed inside the drawable panel.
   * This method checks if a state was selected by the mouse. */
  private void handleMoveToolMousePressed(MouseEvent evt) {
    // check if the click hit a state
    Shape clickedShape = getClickedShape(evt);
    if (clickedShape instanceof State) {
      // mouseClick hit a state
      this.movedState = (State)clickedShape;
      this.movedState.setSelected(true);
      this.movedStateOriginalLocation = new Point(movedState.getX(), movedState.getY());
      this.moveToolOrigMouseEvent = evt;
    }
  }
  
  /** In case a state was hit by the original mousePressed-Event, move this state around. */
  public void handleMoveToolMouseDragged(MouseEvent evt) {
    if (this.moveToolOrigMouseEvent != null) {
      int stateOffsetX = evt.getX() - moveToolOrigMouseEvent.getX();
      int stateOffsetY = evt.getY() - moveToolOrigMouseEvent.getY();
      
      // Move the state
      this.movedState.moveTo(movedStateOriginalLocation.x + stateOffsetX,
          movedStateOriginalLocation.y + stateOffsetY);
      
      // Update the painting coordinates of all transitions coming from or going to the state
      updateStateTransitions(movedState);
    }
  }
  
  /** Updates the painting coordinates of all transitions coming from or going to the state. */
  private void updateStateTransitions(State state) {
    ArrayList<Transition> movedStateTransitions = state.getTransitions(transitions);
    for (int i = 0; i < movedStateTransitions.size(); i++) {
      movedStateTransitions.get(i).computePaintingCoordinates(transitions);
    }
  }
  
  /** Invoked if the move-tool is selected, the mouse pressed inside the drawable Panel 
   * and then released somewhere (not nescessarily inside the drawable panel too!) */
  public void handleMoveToolMouseReleased(MouseEvent evt) {
    // Don't allow the use to move the state outside of the drawable Panel area
    if (movedState.x <= 0 || movedState.y <= 0 || movedState.x >= Editor.getDrawablePanel().getWidth() ||
        movedState.y >= Editor.getDrawablePanel().getHeight()) {
      // Reset the moved State back to its original position before the dragging
      movedState.moveTo(movedStateOriginalLocation.x, movedStateOriginalLocation.y);
      
      // Reset his transitions
      updateStateTransitions(movedState);
    }
    
    // Reset the trigger for the mouseDragged-handling
    this.moveToolOrigMouseEvent = null;
    
    this.movedState.setSelected(false);
  }
  
  /** Handles the construction of the constructingTransition */
  private void handleTransitionConstruction(MouseEvent evt) {
    // Distinguish the construction phases
    
    // 1. Phase: Did the user clicked a transitionStartState?
    // constructingTransition = null
    if (this.constructingTransition == null) {
      Shape clickedShape = getClickedShape(evt);
      if (clickedShape instanceof State) {
        // The user clicked a State, save it and set it selected for visual feedback
        this.constructingTransition = new Transition((State)clickedShape);
        clickedShape.setSelected(true);
        
        // Display the tooltip to click the second state (ending state)
        Tooltip.setMessage(Config.Tooltips.transitionSelectEndingState);
      } else {
        ErrorMessage.setMessage(Config.ErrorMessages.transitionStartNotClicked);
      }
      
      return;
    }
    
    // 2. Phase: Did the user clicked a transitionEndState?
    // constructingTransition != null, transitionEnd = null
    if (this.constructingTransition.getTransitionEnd() == null) {
      Shape clickedShape = getClickedShape(evt);
      if (clickedShape instanceof State) {
        // The user clicked a State, save it and set it selected for visual feedback
        this.constructingTransition.setTransitionEnd((State)clickedShape);
        clickedShape.setSelected(true);
        
        // Display the tooltop to enter a symbol
        Tooltip.setMessage(Config.Tooltips.transitionEnterSymbol);
      } else {
        // Set an ErrorMessage and return to phase 1
        ErrorMessage.setMessage(Config.ErrorMessages.transitionEndNotClicked);
        this.resetConstructingTransition();
      }
      
      return;
    }
    
    // 3. Phase. Transition-symbol entered by keyboard expected. If the user clicks
    // the mouse, the transition construction process is being resetted.
    this.resetConstructingTransition();
    ErrorMessage.setMessage(Config.ErrorMessages.transitionNoSymbolEntered);
  }
  
  /** Invoked by the KeyboardAdapter in case a key has been pressed. This method is needed
   * for the third stadium of the transitions creation process when the user wants
   * to add a symbol to the transition. */
  public void handleKeyPressed(KeyEvent keyEvent) {
    int key = keyEvent.getKeyCode();
    
    // Is the selection tool active
    if (Editor.getToolBar().getArrowButton().isSelected()) {
      if (key == KeyEvent.VK_BACK_SPACE) {
        deleteShape();
      }
    }
    
    // Is the transition tool selected?
    if (Editor.getToolBar().getTransitionButton().isSelected()) {
      // Since the user has to select states with the transition tool, he might think
      // he can delete states with this tool too. Display a hint.
      if (key == KeyEvent.VK_BACK_SPACE) {
        ErrorMessage.setMessage(Config.ErrorMessages.transitionStateDeletionProhibited);
      }
      
      // Are we in the 3. phase of the transition-construction?
      if (this.constructingTransition != null) {
        if (this.constructingTransition.getTransitionEnd() != null) {
          // We are in the 3. phase of the transition construction
          if (Transition.isTransitionSymbolValid(keyEvent.getKeyChar())) {
            // Entered symbol is valid and being added to the transition
            this.constructingTransition.addSymbol(keyEvent.getKeyChar());
            
            Transition transition = constructingTransition.isInArrayList(transitions);
            // Add the transition to the automats transitions.
            if (transition == null) {
              // The automat has no such transition yet, add the whole transition
              constructingTransition.computePaintingCoordinates(transitions);
              transitions.add(constructingTransition);
            } else {
              // The automat has already a transition with these start- and end-indices.
              // Add only the symbol of the constructingTransition to the existing transition.
              transition.addSymbol(constructingTransition.getSymbols().get(0));
            }
            
            // Return to phase 1 for transition construction again
            this.resetConstructingTransition();
          } else
            ErrorMessage.setMessage(Config.ErrorMessages.transitionInvalidSymbolEntered);
        }
      }
    }
  }
  
  /** Did the mouseClick hit a Shape of the automat? E.g. a state, a transition etc. */
  private void checkShapeSelection(MouseEvent evt) {
    // Deselect the previously selected Shape if any was selected
    if (selectedShape != null)
      selectedShape.setSelected(false);
    
    // Get the newly clicked Shape if any was clicked. Gets null otherwise.
    selectedShape = getClickedShape(evt);
    
    if (selectedShape != null) {
      selectedShape.setSelected(true);
      selectedShape.displaySelectedShapeTooltip();
    }
  }
  
  /** Returns the clicked Shape, or null if no Shape was hit by the mouse-click. Cannot return
   * several Shapes. */
  private Shape getClickedShape(MouseEvent evt) {
    Shape hitShape = null;
    
    // Traverse the automats states. Makes sure that only one or zero Shapes gets selected.
    for (int i = 0; i < states.size(); i++) {
      if (states.get(i).mouseClickHit(evt.getX(), evt.getY())) {
        // Save the new Shape that reported a mouse-collision
        hitShape = states.get(i);
      }
    }
    
    // Todo: Traverse the automats transitions
    
    return hitShape;
  }

  private void addState(State state) {
    if (addingStateAllowed(state))
      states.add(state);
  }

  /**
   * Checks whether it's allowed to add the passed state-object to the Automat's
   * ArrayList states.
   */
  private boolean addingStateAllowed(State state) {
    // checking for class-dependent rules
    if (state instanceof StartState || state instanceof StartEndState) {
      if (!addingStartStateAllowed()) {
        return false;
      }
    }

    // if all checks passed, return true
    return true;
  }

  /** Returns true if the automat doesn't have a startState or startEndState yet, false otherwise. */
  private boolean addingStartStateAllowed() {
    if (hasStartState()) {
      ErrorMessage.setMessage(Config.ErrorMessages.cannotAddStartState);
      return false;
    }

    return true;
  }

  /** Returns true if the Automat already has a start-state, false instead. */
  private boolean hasStartState() {
    for (int i = 0; i < states.size(); i++) {
      if (states.get(i) instanceof StartState || states.get(i) instanceof StartEndState)
        return true;
    }

    return false;
  }

  /** Starts at 1, 0 is reserved for the start-state */
  private int findNewStateIndex() {
    int i = 1;
    while (getStateByStateIndex(i) != null) {
      i++;
    }

    return i;
  }

  /** Returns the found state, or null otherwise. */
  private State getStateByStateIndex(int stateIndex) {
    for (int i = 0; i < states.size(); i++) {
      if (stateIndex == states.get(i).getStateIndex())
        return states.get(i);
    }

    return null;
  }
  
  /** Deletes a Shape (states, transitions...) from the automat. */
  public void deleteShape() {
    if (selectedShape == null)
      return;
    
    // Selected Shape is a State
    if (selectedShape instanceof State) {
      // Remove all transitions going to and coming from that State
      ((State)selectedShape).deleteTransitions(transitions);
      
      // Removes the State from the automat. The states own stateIndex is freed and
      // can be retaken by newly added states again.
      states.remove(selectedShape);
      selectedShape = null;
    }
    // todo: if (selectedShape instanceof Transition) ...
  }
  
  /** Deselect the currently selected shape if there is any. */
  public void deselectSelectedShape() {
    if (selectedShape != null) {
      selectedShape.setSelected(false);
      selectedShape = null;
    }
  }
  
  // Setters and Getters
  /** Returns the currently selected shape. Returns null if none is selected. */
  public Shape getSelectedShape() {
    return this.selectedShape;
  }
  
  /** Resets the constructingTransition object. Called when the user deselects the transition
   * Button, the user enters invalid transition-Symbols etc. */
  public void resetConstructingTransition() {
    // Deselect states
    if (this.constructingTransition != null) {
      constructingTransition.getTransitionStart().setSelected(false);
      
      if (constructingTransition.getTransitionEnd() != null)
        constructingTransition.getTransitionEnd().setSelected(false);
    }
    
    this.constructingTransition = null;
  }

  // Currently only used for testing. Called in the DrawablePanel Constructor. TODO: delete later
  public void createExampleAutomat() {
    // Discard all current states.
    this.states = new ArrayList<State>();

    // add some states
    states.add(new State(findNewStateIndex(), 100, 100));
    // states.add(new StartState(200, 200));
    states.add(new EndState(findNewStateIndex(), 300, 300));
    states.add(new StartEndState(findNewStateIndex(), 400, 400));

    // states.remove(1);

    states.add(new State(findNewStateIndex(), 600, 600));
    states.add(new State(findNewStateIndex(), 600, 600));
    // states.remove(2);
  }
  
  // Currently used for debuging purpose. TODO: delete later
  @SuppressWarnings("unused")
  private void printTransitions() {
    if (transitions.size() == 0)
      System.out.println("no transitions yet.");
    
    for (int i = 0; i < transitions.size(); i++) {
      System.out.print(transitions.get(i).getTransitionStart().getStateIndex() + " to " +
          transitions.get(i).getTransitionEnd().getStateIndex() + " with ");
      for (int j = 0; j < transitions.get(i).getSymbols().size(); j++) {
        System.out.print(transitions.get(i).getSymbols().get(j) + " ");
      }
      System.out.println("");
    }
    System.out.println("");
  }
}
