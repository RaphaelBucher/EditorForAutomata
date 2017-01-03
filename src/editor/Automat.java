/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package editor;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import controlFlow.AddedState;
import controlFlow.AddedTransition;
import controlFlow.RemovedState;
import controlFlow.RemovedTransition;
import controlFlow.StateMoved;
import controlFlow.UserAction;
import transformation.Language;
import transformation.ReadSymbol;

public class Automat {
  private ArrayList<State> states;
  private ArrayList<Transition> transitions;
  // The unfinished Transitions states that is currently being constructed by the user
  // when he operates with the Transition tool.
  private State constructingTransitionStartState;
  private State constructingTransitionEndState;
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
    
    ToolBar toolBar = Editor.getToolBar();
    ToggleButton selectedButton = toolBar.getSelectedButton();
    
    /* In very rare cases, the mouseReleased-Event of DrawablePanel is NOT fired, which means
     * that I end up with a toolbar with ZERO buttons selected, getting null here, leading
     * to a nullPointerException aprox. 5 lines further. If no button is selected, just return
     * and do nothing, the user needs to select a button again and we're fine again. */
    if (selectedButton == null) {
      return;
    }
    
    if (selectedButton.equals(toolBar.getArrowButton())) {
      // Default arrow-cursor. 
      checkShapeSelection(evt);
    } else if (selectedButton.equals(toolBar.getMoveCursorButton())) {
      // Move tool
      handleMoveToolMousePressed(evt);
    } else if (selectedButton.equals(toolBar.getStateButton())) {
      // Add state Cursor
      addState(new State(findNewStateIndex(), stateX, stateY), true);
    } else if (selectedButton.equals(toolBar.getStartStateButton())) {
      // Add start state cursor
      addState(new StartState(0, stateX, stateY), true);
    } else if (selectedButton.equals(toolBar.getEndStateButton())) {
      // Add end state cursor
      addState(new EndState(findNewStateIndex(), stateX, stateY), true);
    } else if (selectedButton.equals(toolBar.getStartEndStateButton())) {
      // Add start-end state cursor
      addState(new StartEndState(0, stateX, stateY), true);
    } else if (selectedButton.equals(toolBar.getTransitionButton())) {
      // Transition cursor
      handleTransitionConstruction(evt);
    }
  }
  
  /** Is Invoked if the move tool was selected and the mouse pressed inside the drawable panel.
   * This method checks if a state was selected by the mouse. */
  private void handleMoveToolMousePressed(MouseEvent evt) {
    // check if the click hit a state
    State clickedState = getClickedState(evt);
    if (clickedState == null)
      return;
    
    this.movedState = clickedState;
    this.movedState.setSelected(true);
    this.movedStateOriginalLocation = new Point(movedState.getX(), movedState.getY());
    this.moveToolOrigMouseEvent = evt;
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
  public void updateStateTransitions(State state) {
    ArrayList<Transition> movedStateTransitions = state.getTransitions(transitions);
    for (int i = 0; i < movedStateTransitions.size(); i++) {
      movedStateTransitions.get(i).computePaintingCoordinates(transitions);
    }
    
    /* Need to update all ArcTransitions of states that can be reached by only one
     * transition FROM the state being moved around. This is because updating LineTransitions
     * affect arcTransitions at BOTH ends. Even if an ArcTransition of the movedState
     * itself was updated already, we update it here again to make sure it's updated
     * AFTER all LineTransitions have been updated. This is to avoid visual artifacts. */
    ArrayList<Transition> arcTransitions = getMovedStateNeighboursArcTransitions(state);
    for (int i = 0; i < arcTransitions.size(); i++) {
      arcTransitions.get(i).computePaintingCoordinates(transitions);
    }
  }
  
  /** Retrieves all ArcTransitions of the moved State and its neighbour-states which
   * can be reached by one transition. */
  private ArrayList<Transition> getMovedStateNeighboursArcTransitions(State movedState) {
    ArrayList<Transition> arcTransitions = new ArrayList<Transition>();
    
    // Get all transitions that have the movedState as a Start-State. Gets an ArcTransition
    // of the movedState itself too if it has one
    ArrayList<Transition> movedStateTransitions = Transition.getTransitionsByStartState(movedState,
        transitions);
    
    // Iterate over end-states of the retrieved list and check if it has an ArcTransition
    Transition arcTransition;
    for (int i = 0; i < movedStateTransitions.size(); i++) {
      // Add found ArcTransitions to the list. If the passed EndState has no arcTransition, we 
      // get null and add this to the arrayList which has no effect.
      arcTransition = Transition.isInArrayList(movedStateTransitions.get(i).getTransitionEnd().stateIndex,
          movedStateTransitions.get(i).getTransitionEnd().stateIndex, transitions);
      if (arcTransition != null)
        arcTransitions.add(arcTransition);
    }
    
    return arcTransitions;
  }
  
  /** Invoked if the move-tool is selected, the mouse pressed inside the drawable Panel 
   * and then released somewhere (not nescessarily inside the drawable panel too!) */
  public void handleMoveToolMouseReleased() {
    // Was the state moved at all?
    if (this.moveToolOrigMouseEvent != null) {
      // Don't allow the user to move the state outside of the drawable Panel area
      if (movedState.x <= 0 || movedState.y <= 0 || movedState.x >= Editor.getDrawablePanel().getWidth() ||
          movedState.y >= Editor.getDrawablePanel().getHeight()) {
        // Reset the moved State back to its original position before the dragging
        movedState.moveTo(movedStateOriginalLocation.x, movedStateOriginalLocation.y);
        
        // Reset his transitions
        updateStateTransitions(movedState);
      } else {
        // The state was moved to a valid location, therefore register the action to the Control-flow
        UserAction.addAction(new StateMoved(movedState.stateIndex, 
            new Point(movedStateOriginalLocation.x, movedStateOriginalLocation.y),
            new Point(movedState.x, movedState.y)));
      }
      
      // Reset the trigger for the mouseDragged-handling
      this.moveToolOrigMouseEvent = null;
      this.movedState.setSelected(false);
    }
  }
  
  /** Handles the construction of the constructingTransition. Is invoked by mouseClicks. */
  private void handleTransitionConstruction(MouseEvent evt) {
    // Did the mouseClick hit a Shape?
    Shape clickedShape = getClickedShape(evt);
    
    // --- Distinguish the construction phases ---
    
    // 1. Phase: Did the user click a transitionStartState?
    // constructingTransition = null
    if (this.constructingTransitionStartState == null) {
      if (clickedShape instanceof State) {
        // The user clicked a State, save it and set it selected for visual feedback
        this.constructingTransitionStartState = ((State)clickedShape);
        clickedShape.setSelected(true);
        
        // Display the tooltip to click the second state (ending state)
        Tooltip.setMessage(Config.Tooltips.transitionSelectEndingState,
            Config.TOOLTIP_DRAWABLE_PANEL_DISPLAY_AMOUNT);
      } else {
        Tooltip.setMessage(Config.Tooltips.transitionSelectStartingState, 0);
      }
      
      return;
    }
    
    // 2. Phase: Did the user click a transitionEndState?
    // constructingTransitioinStartState != null, constructingTransitioinEndState = null
    if (this.constructingTransitionEndState == null) {
      if (clickedShape instanceof State) {
        // The user clicked a State, save it and set it selected for visual feedback
        this.constructingTransitionEndState = ((State)clickedShape);
        this.constructingTransition = new Transition(constructingTransitionStartState,
            constructingTransitionEndState);
        clickedShape.setSelected(true);
        
        addTransition(constructingTransition, true);
        
        // Display the tooltop to enter a symbol
        Tooltip.setMessage(Config.Tooltips.transitionEnterSymbol, Config.TOOLTIP_DRAWABLE_PANEL_DISPLAY_AMOUNT);
      } else {
        // Set an ErrorMessage and return to phase 1
        ErrorMessage.setMessage(Config.ErrorMessages.transitionEndNotClicked);
        this.resetConstructingTransition();
      }
      
      return;
    }
    
    // 3. Phase. Transition-symbol entered by keyboard expected. 
    
    // If the user clicks the mouse somewhere, the transition construction process is being resetted.
    this.resetConstructingTransition();
    
    // This code is just reached if the constructing-Transition had both start- and end-state
    // at the start of this method, phase 3. This click just reseted the constructed state.
    // If he also hit a state with click, enter phase 1 directly again.
    if (clickedShape instanceof State) {
      // The user clicked a State, save it and set it selected for visual feedback
      this.constructingTransitionStartState = ((State)clickedShape);
      clickedShape.setSelected(true);
      
      // Display the tooltip to click the second state (ending state)
      Tooltip.setMessage(Config.Tooltips.transitionSelectEndingState,
          Config.TOOLTIP_DRAWABLE_PANEL_DISPLAY_AMOUNT);
    }
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
          // Add the key pressed as a Symbol to the built transition
          if (!constructingTransition.addSymbol(keyEvent.getKeyChar(), false))
            ErrorMessage.setMessage(Config.ErrorMessages.transitionInvalidSymbolEntered);
          
          // Add the Transition to the automat. The method decides itself what 
          // still needs to be added, e.g. the whole transition or only a symbol
          addTransition(constructingTransition, true);
        }
      }
    }
    
    // Debug TODO: start of removing
    if (key == KeyEvent.VK_RIGHT) {
      Debug.printAutomat(this);
    }
    if (key == KeyEvent.VK_LEFT) {
      ArrayList<ReadSymbol> readTransitionsSymbols = new ArrayList<ReadSymbol>();
      
      boolean wordAccepted = Language.wordAccepted("abc", this, readTransitionsSymbols);
      System.out.println("Word accepted: " + wordAccepted);
      for (int i = 0; i < readTransitionsSymbols.size(); i++) {
        Transition transition = readTransitionsSymbols.get(i).getTraveledTransition();
        System.out.println("Transition state " + transition.getTransitionStart().stateIndex + 
            " to state " + transition.getTransitionEnd().stateIndex + " with read Symbol " +
            readTransitionsSymbols.get(i).getReadSymbol());
      }
      System.out.println();
    }
    // TODO: end of removing
  }
  
  /** Do not directly add Transitions to the transitions-ArrayList. Call this method instead,
   * there is updating to perform when adding a Transition. Pass a Transition with a start-
   * and end-state and with one or more symbols. They can be invalid, this method checks 
   * itself for validation. Will add a COPY of the Transition to the arrayList to prevent
   * direct manipulation with the passed reference.
   * @param addActionToControlFlow Pass true when the user is adding states himself, pass false if
   * this method is just called from a redo / undo order. */
  public void addTransition(Transition newTransition, boolean addActionToControlFlow) {
    // Is the Transition invalid?
    if (newTransition == null || newTransition.getTransitionStart() == null ||
        newTransition.getTransitionEnd() == null)
      return;
    
    // Does the automat already have such a transition with such start-state and 
    // end-state indices?
    Transition transition = newTransition.isInArrayList(transitions);
    if (transition == null) {
      // Instantiate a new Transition with start- and end-state and no symbols yet
      transition = new Transition(newTransition.getTransitionStart(),
          newTransition.getTransitionEnd());
      
      // Add the transition to the automats transitions.
      transitions.add(transition);
      
      if (addActionToControlFlow) {
        UserAction.addAction(new AddedTransition(newTransition));
        addActionToControlFlow = false;
      }
    }
    
    // Add all symbols
    for (int i = 0; i < newTransition.getSymbols().size(); i++) {
      transition.addSymbol(newTransition.getSymbols().get(i).getSymbol(), addActionToControlFlow);
    }
      
    // Update the transitions painting information. Called every time, even if only a symbol has
    // been added.
    transition.computePaintingCoordinates(transitions);
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
    hitShape = getClickedState(evt);
    
    // Traverse the Transitions
    for (int i = 0; i < transitions.size(); i++) {
      if (transitions.get(i).mouseClickHit(evt.getX(), evt.getY())) {
        // Save the new Transition that reported a mouse-collision
        hitShape = transitions.get(i);
      }
    }
    
    // Traverse the Transitions symbols
    ArrayList<Symbol> symbols;
    for (int i = 0; i < transitions.size(); i++) {
      symbols = transitions.get(i).getSymbols();
      
      // Don't allow selecting symbols on a LineTransition which is not painted
      if (transitions.get(i).getTransitionPaint() instanceof TransitionPaintLine) {
        if ( !((TransitionPaintLine) transitions.get(i).getTransitionPaint()).isPainted() ) {
          continue;
        }
      }
      
      for (int j = 0; j < symbols.size(); j++) {
        if (symbols.get(j).mouseClickHit(evt.getX(), evt.getY()))
          // Save the new Symbol that reported a mouse-collision
          hitShape = symbols.get(j);
      }
    }
    
    return hitShape;
  }
  
  /** Returns the clicked State, or null if no State was hit by the mouse-click. Cannot return
   * several States. */
  private State getClickedState(MouseEvent evt) {
    State hitState = null;
    
    // Traverse the Automats states. Makes sure that only one or zero States gets selected.
    for (int i = 0; i < states.size(); i++) {
      if (states.get(i).mouseClickHit(evt.getX(), evt.getY())) {
        // Save the new State that reported a mouse-collision
        hitState = states.get(i);
      }
    }
    
    return hitState;
  }

  /** Adds a state to the autmats states-ArrayList.
   * @param addActionToControlFlow Pass true when the user is adding states himself, pass false if
   * this method is just called from a redo / undo order. */
  public void addState(State state, boolean addActionToControlFlow) {
    if (addingStateAllowed(state)) {
      states.add(state);
      
      // Add this action to the controlFlow if the flag is set
      if (addActionToControlFlow)
        UserAction.addAction(new AddedState(state));
    }
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
  public State getStateByStateIndex(int stateIndex) {
    return State.getStateByStateIndex(stateIndex, states);
  }
  
  /** Deletes a Shape (states, transitions...) from the automat. */
  public void deleteShape() {
    if (selectedShape == null)
      return;
    
    // Selected Shape is a State
    if (selectedShape instanceof State) {
      // Removes the State from the automat. The states own stateIndex is freed and
      // can be retaken by newly added states again.
      deleteState((State) selectedShape, true);
    }
    
    // Selected Shape is a Transition
    if (selectedShape instanceof Transition) {
      deleteTransition((Transition) selectedShape, true);
    }
    
    // Selected Shape is a Transition-Symbol
    if (selectedShape instanceof Symbol) {
      Transition hostTransition = ((Symbol) selectedShape).getHostTransition();
     
      // Remove the selected symbol
      hostTransition.removeSymbol(((Symbol) selectedShape).getSymbol(), true);
    }
    
    selectedShape = null;
  }
  
  /** Deletes the Transition from the automat and performs the necessary painting updataes. */
  public void deleteTransition(Transition transition, boolean addActionToControlFlow) {
    // Is the transition an ArcTransition?
    if (transition.isArcTransition()) {
      // Delete the transition. No further painting updating needed
      transitions.remove(transition);
      
      // Add this action to the controlFlow if the flag is set
      if (addActionToControlFlow)
        UserAction.addAction(new RemovedTransition(transition));
      
      return;
    }
      
    // Transition is a LineTransition. Save neighbor-Transitions that need to be updated
    // after the deletion.
    Transition reverseTransition = transition.gotReverseTransition(transitions);
    Transition startStateArcTransition = transition.getTransitionStart().gotArcTransition(transitions);
    Transition endStateArcTransition = transition.getTransitionEnd().gotArcTransition(transitions);
    
    // Delete the transition
    transitions.remove(transition);
    
    // Add this action to the controlFlow if the flag is set
    if (addActionToControlFlow)
      UserAction.addAction(new RemovedTransition(transition));
    
    // After the deleting the transition, update the neighbors painting stats
    if (reverseTransition != null)
      reverseTransition.computePaintingCoordinates(transitions);
    if (startStateArcTransition != null)
      startStateArcTransition.computePaintingCoordinates(transitions);
    if (endStateArcTransition != null)
      endStateArcTransition.computePaintingCoordinates(transitions);
  }
  
  /** Removes all transitions of the automat. */
  public void deleteAllTransitions() {
    transitions = new ArrayList<Transition>();
  }
  
  /** Deletes a state from the automats list and performs the necessary painting updating. 
   * @param addActionToControlFlow Pass true when the user is removing states himself, pass
   * false if this method is just called from a redo / undo order. */
  public void deleteState(State state, boolean addActionToControlFlow) {
    // Remove all transitions going to and coming from that State
    ArrayList<Transition> deletedTransitions = state.deleteTransitions(this);
    
    // Remove the state from the automat
    states.remove(state);
    
    // Add this action to the controlFlow if the flag is set
    if (addActionToControlFlow)
      UserAction.addAction(new RemovedState(state, deletedTransitions));
  }
  
  /** Deletes all passed states from the automats list and performs the necessary painting updating. 
   * @param addActionToControlFlow Pass true when the user is removing states himself, pass
   * false if this method is just called from a redo / undo order. */
  public void deleteStates(ArrayList<State> statesToRemove, boolean addActionToControlFlow) {
    for (int i = 0; i < statesToRemove.size(); i++) {
      deleteState(getStateByStateIndex(statesToRemove.get(i).getStateIndex()), addActionToControlFlow);
    }
  }
  
  /** Deselect the currently selected shape if there is any. */
  public void deselectSelectedShape() {
    if (selectedShape != null) {
      selectedShape.setSelected(false);
      selectedShape = null;
    }
  }
  
  /** Returns the currently selected shape. Returns null if none is selected. */
  public Shape getSelectedShape() {
    return this.selectedShape;
  }
  
  /** Resets the constructingTransition object. Called when the user deselects the transition
   * Button, the user enters invalid transition-Symbols etc. */
  public void resetConstructingTransition() {
    // Deselect states
    if (this.constructingTransitionStartState != null)
      constructingTransitionStartState.setSelected(false);
    
    if (this.constructingTransitionEndState != null)
      constructingTransitionEndState.setSelected(false);
    
    // Reset the references
    this.constructingTransitionStartState = null;
    this.constructingTransitionEndState = null;
    this.constructingTransition = null;
  }
  
  /** @return An ArrayList of all EndStates (EndStates and StartEndStates) */
  public ArrayList<State> getEndStates() {
    ArrayList<State> endStates = new ArrayList<State>();
    
    for (int i = 0; i < states.size(); i++) {
      if (states.get(i) instanceof EndState || states.get(i) instanceof StartEndState)
        endStates.add(states.get(i));
    }
    
    return endStates;
  }
  
  /** Returns a deep copy of the automat. Doesn't perform painting computation of the transitions. 
   * @return a deep copy of the automat with all new references on the automat itself,
   * its states, transitions etc. */
  public Automat copy() {
    Automat automat = new Automat();
    
    // States
    for (int i = 0; i < states.size(); i++) {
      automat.states.add(states.get(i).copy());
    }
    
    // Transitions
    for (int i = 0; i < transitions.size(); i++) {
      automat.transitions.add(transitions.get(i).copy(automat));
    }
    
    return automat;
  }
  
  /** Updates all painting coordinates of all the transitions the automat has. */
  public void updatePainting() {
    for (int i = 0; i < transitions.size(); i++) {
      transitions.get(i).computePaintingCoordinates(transitions);
    }
  }
  
  /** Changes the type of an automats state. Cannot change the stateIndex. Index 0 can only
   * be changed to StartState or StartEndState, Indices >= 1 only to State or EndState.
   * Pass one of the States types, e.g. STATE.START_STATE */
  public void changeStateType(State oldState, int newType) {
    if (oldState == null)
      return;

    // Is the new state type valid?
    if (!changeStateValidType(oldState, newType))
      return;
    
    State newState = new State(oldState.stateIndex, oldState.x, oldState.y);

    switch (newType) {
      case State.START_STATE:
        newState = new StartState(oldState.stateIndex, oldState.x, oldState.y);
        break;
      case State.END_STATE:
        newState = new EndState(oldState.stateIndex, oldState.x, oldState.y);
        break;
      case State.START_END_STATE:
        newState = new StartEndState(oldState.stateIndex, oldState.x, oldState.y);
        break;
    }
    
    // Replace the State
    for (int i = 0; i < states.size(); i++) {
      if (states.get(i).equals(oldState)) {
        Transition.replaceState(oldState, newState, transitions);
        
        states.set(i, newState);
      }
    }
  }
  
  /** Computes whether the new state type is valid and needed. */
  private boolean changeStateValidType(State oldState, int newType) {
    // same type already?
    if (oldState.getType() == newType)
      return false;
    
    // -- invalid type --
    // a StartState / StartEndState can only be changed into one of those
    if (oldState.getStateIndex() == 0 && (newType == State.STATE || newType == State.END_STATE))
      return false;
    
    // Cannot transform a state with index >= 1 into a StartState / StartEndState
    if (oldState.getStateIndex() >= 1 && (newType == State.START_STATE || newType == State.START_END_STATE))
      return false;
      
    return true;
  }
  
  
  // Setters and Getters
  public ArrayList<Transition> getTransitions() {
    return this.transitions;
  }
  
  public ArrayList<State> getStates() {
    return this.states;
  }
}
