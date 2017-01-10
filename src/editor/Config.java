/* 
 * Editor for Automata
 * Bachelor Thesis
 * Raphael Bucher 2016 / 2017
 * */
package editor;

import java.awt.Color;

public interface Config {
  //How many times the main loop calls update() and the repaint() functions.
  int FPS = 30; 

  // Frame will be initialized with these sizes. Can be enlarged.
  int FRAME_PANEL_MIN_WIDTH = 1000; // TODO change back to 700

  // Needs to be a bit bigger than the ToolBar needs. It also contains the
  // frames menu-bar.
  // The height initialization of everything depends on only this value.
  int FRAME_PANEL_MIN_HEIGHT = 630; // TODO change back to 500

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
  int STATE_DIAMETER = 50 - 8; // Should be even for more accurate visuals
  int END_STATE_INNER_DIAMETER = STATE_DIAMETER - 6;
  Color SELECTED_STATE_COLOR = new Color(60, 120, 255);
  
  // For the animation
  Color HIGHLIGHTED_COLOR_WORD_ACCEPTED = new Color(85, 200, 85);
  Color HIGHLIGHTED_COLOR_WORD_DENIED = new Color(255, 102, 102);
  Color HIGHLIGHTED_COLOR_SYMBOL_BACKGROUND_ACCEPTED = new Color(180, 240, 180);
  Color HIGHLIGHTED_COLOR_SYMBOL_BACKGROUND_DENIED = new Color(255, 190, 190);

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
  long ERROR_MESSAGE_DURATION_MILLIS = 2400;
  int ERROR_MESSAGE_FONT_SIZE = 20;
  
  // The Tooltips in the top left corner. E.g. hotkeys like backspace to delete states
  long TOOLTIP_DURATION_MILLIS = 3900;
  int TOOLTIP_FONT_SIZE = 16;
  int TOOLTIP_TOOLBAR_DISPLAY_AMOUNT = 2;
  int TOOLTIP_DRAWABLE_PANEL_DISPLAY_AMOUNT = 3;
  
  /** Displayed tooltips of the Toolbars ToggleButtons */
  interface ToolbarTooltips {
    String selectTool = "Please select States, Transitions and their Symbols";
    String moveTool = "Please drag & drop states";
    String transition = "Please select a start-state for the Transition";
  }
  
  /** Displayed Tooltips */
  interface Tooltips {
    String stateSelected = "Please press Backspace to delete the state";
    String transitionSelected = "Please press Backspace to delete the transition";
    String symbolSelected = "Please press Backspace to delete the symbol";
    
    // Transition
    String transitionSelectStartingState = "Please select an initial state for the Transition";
    String transitionSelectEndingState = "Please select the same or another destination state";
    String transitionEnterSymbol = "Please enter one or more symbols";
    
    // Transformation
    String transformIsNEAAlready = "Automat is already a NEA";
    String transformIsDEAAlready = "Automat is already a DEA";
  }
  
  /** Displayed ErrorMessages */
  interface ErrorMessages {
    String cannotAddStartState = "Has already a start state";
    
    // Transition
    String transitionStartNotClicked = "Please select a starting state";
    String transitionEndNotClicked = "Please select an ending state";
    String transitionInvalidSymbolEntered = "Valid symbols are 0,...,9 and a,...,z";
    String transitionStateDeletionProhibited = "Please use the selection tool to delete states";
    
    // XML-parsing errors
    String xmlParsingError = "Invalid XML-File";
    
    // Transformation
    String startStateMissing = "Please add a start state";
  }
  
  // In case the Transition has a reverseTransition, how far it is away from
  // the direct angle to its other state
  double TRANSITION_PAINT_ANGLE_OFFSET = 18 / 180.0d * Math.PI;
  // A transition below this length will not be painted
  int TRANSITION_MIN_LENGTH = 20;
}