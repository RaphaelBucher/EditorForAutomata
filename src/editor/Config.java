/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.Color;

public interface Config {
  int FPS = 30; // How many times the main loop calls update() and the repaint()
                // functions.

  // Frame will be initialized with these sizes. Can be enlarged.
  int FRAME_PANEL_MIN_WIDTH = 600;

  // Needs to be a bit bigger than the ToolBar needs. It also contains the
  // frames menu-bar.
  // The height initialization of everything depends on only this value.
  int FRAME_PANEL_MIN_HEIGHT = 400;

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

  // Background-color of the drawablePanel
  Color BACKGROUND_COLOR = Color.WHITE;

  /** Icons-paths for ToggleButtons and cursor on mac. */
  interface Icon_paths {
    String STATE = "/resources/Icons/state.png";
    String START_STATE = "/resources/Icons/startState.png";
    String END_STATE = "/resources/Icons/endState.png";
    String START_END_STATE = "/resources/Icons/startEndState.png";
    String TRANSITION = "/resources/Icons/transition.png";
  }
  
  /** Icons-paths for ToggleButtons and cursor on mac. */
  interface Windows_cursor_paths {
    String STATE = "/resources/Icons/stateWindowsCursor.png";
    String START_STATE = "/resources/Icons/startStateWindowsCursor.png";
    String END_STATE = "/resources/Icons/endStateWindowsCursor.png";
    String START_END_STATE = "/resources/Icons/startEndStateWindowsCursor.png";
    String TRANSITION = "/resources/Icons/transitionWindowsCursor.png";
  }

   /** The same for mac and windows since only one cursor is set per ToggleButton. */
  interface Cursor_names {
    String STATE_CURSOR = "stateCursor";
    String START_STATE_CURSOR = "startStateCursor";
    String END_STATE_CURSOR = "endStateCursor";
    final String START_END_STATE_CURSOR = "startEndStateCursor";
    final String TRANSITION_CURSOR = "transitionCursor";
    final String WINDOWS_CURSOR = "stateWindowsCursor";
  }
  
  // The start-state arrow that is drawn inside the drawablePanel
  String START_STATE_ARROW_ICON_PATH = "/resources/Icons/startStateArrow.png";

  // How many millis until the Error-message fades away completely
  long ERROR_MESSAGE_DURATION_MILLIS = 3000;
  int ERROR_MESSAGE_FONT_SIZE = 20;
}