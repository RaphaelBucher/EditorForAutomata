/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.Color;

public interface Config {
  //How many times the main loop calls update() and the repaint() functions.
  int FPS = 30; 

  // Frame will be initialized with these sizes. Can be enlarged.
  int FRAME_PANEL_MIN_WIDTH = 700;

  // Needs to be a bit bigger than the ToolBar needs. It also contains the
  // frames menu-bar.
  // The height initialization of everything depends on only this value.
  int FRAME_PANEL_MIN_HEIGHT = 500;

  // ToolBar
  int TOOLBAR_ICON_WIDTH = 60;
  int TOOLBAR_ICON_HEIGHT = 60;

  // The custom cursor width and height of DrawablePanel for mac and linux
  int CURSOR_WIDTH = TOOLBAR_ICON_WIDTH;
  int CURSOR_HEIGHT = TOOLBAR_ICON_HEIGHT;
  
  // Windows cursor width and height
  int WINDOWS_CURSOR_WIDTH = 32;
  int WINDOWS_CURSOR_HEIGHT = 32;

  // In case of an end-state, the second circle is within the default one, the
  // diameter for normal and end-states is the same. This eases the rendering.
  int STATE_DIAMETER = 50 - 8;
  int END_STATE_INNER_DIAMETER = STATE_DIAMETER - 6;
  Color SELECTED_STATE_COLOR = Color.BLUE;

  // Background-color of the drawablePanel
  Color BACKGROUND_COLOR = Color.WHITE;

  /** Icons-paths for ToggleButtons and cursor on mac. */
  interface IconPaths {
    String ARROW = "/resources/Icons/arrowCursorButton.png";
    String MOVE_CURSOR = "/resources/Icons/moveCursorButton.png";
    String STATE = "/resources/Icons/state.png";
    String START_STATE = "/resources/Icons/startState.png";
    String END_STATE = "/resources/Icons/endState.png";
    String START_END_STATE = "/resources/Icons/startEndState.png";
    String TRANSITION = "/resources/Icons/transition.png";
  }
  
  /** Icons-paths for ToggleButtons and cursor on mac. */
  interface WindowsCursorPaths {
    String STATE = "/resources/Icons/stateWindowsCursor.png";
    String START_STATE = "/resources/Icons/startStateWindowsCursor.png";
    String END_STATE = "/resources/Icons/endStateWindowsCursor.png";
    String START_END_STATE = "/resources/Icons/startEndStateWindowsCursor.png";
    String TRANSITION = "/resources/Icons/transitionWindowsCursor.png";
  }

   /** The same for mac and windows since only one cursor is set per ToggleButton. */
  interface CursorNames {
    String STATE_CURSOR = "stateCursor";
    String START_STATE_CURSOR = "startStateCursor";
    String END_STATE_CURSOR = "endStateCursor";
    final String START_END_STATE_CURSOR = "startEndStateCursor";
    final String TRANSITION_CURSOR = "transitionCursor";
    final String WINDOWS_CURSOR = "stateWindowsCursor";
  }
  
  // The start-state arrow that is drawn inside the drawablePanel
  String START_STATE_ARROW_ICON_PATH = "/resources/Icons/startStateArrow.png";
  String START_STATE_ARROW_SELECTED_ICON_PATH = "/resources/Icons/startStateArrowSelected.png";
  

  // How many millis until the Error-message fades away completely
  long ERROR_MESSAGE_DURATION_MILLIS = 3000;
  int ERROR_MESSAGE_FONT_SIZE = 20;
  
  // The Tooltips in the top left corner. E.g. hotkeys like backspace to delete states
  long TOOLTIP_DURATION_MILLIS = 3000;
  int TOOLTIP_FONT_SIZE = 16;
  
  /** Displayed Tooltips */
  interface Tooltips {
    String stateSelected = "Press Backspace to delete state";
    String moveToolSelected = "Drag and Drop states";
    
    // Transition
    String transitionSelectStartingState = "Please select a starting state";
    String transitionSelectEndingState = "Please select an ending state";
    String transitionEnterSymbol = "Please enter a symbol";
  }
  
  /** Displayed ErrorMessages */
  interface ErrorMessages {
    String cannotAddStartState = "Has already a start state";
    
    // Transition
    String transitionStartNotClicked = "Please select a starting state";
    String transitionEndNotClicked = "Please select an ending state";
    String transitionNoSymbolEntered = "Please enter a symbol";
    String transitionInvalidSymbolEntered = "Valid symbols are 0,...,9 and a,...,z";
    String transitionStateDeletionProhibited = "Please use the selection tool to delete states";
  }
  
  // In case the Transition has a reverseTransition, how far it is away from
  // the direct angle to its other state
  double TRANSITION_PAINT_ANGLE_OFFSET = 18 / 180.0d * Math.PI;
  // A transition below this length will not be painted
  int TRANSITION_MIN_LENGTH = 20;
}