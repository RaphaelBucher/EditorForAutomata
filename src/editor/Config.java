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

  // The custom cursor width and height of DrawablePanel
  int CURSOR_XY = TOOLBAR_ICON_WIDTH;

  // In case of an end-state, the second circle is within the default one, the
  // diameter for normal
  // and end-states is the same. This eases the rendering.
  int STATE_DIAMETER = CURSOR_XY - 4; // make it slightly smaller
  int END_STATE_INNER_DIAMETER = STATE_DIAMETER - 6;

  // Background-color of the drawablePanel
  Color BACKGROUND_COLOR = Color.WHITE;

  String STATE_ICON_PATH = "/resources/Icons/state.png";
  String START_STATE_ICON_PATH = "/resources/Icons/startState.png";
  String END_STATE_ICON_PATH = "/resources/Icons/endState.png";
  String START_END_STATE_ICON_PATH = "/resources/Icons/startEndState.png";
  String TRANSITION_ICON_PATH = "/resources/Icons/transition.png";

  interface Cursor_names {
    final String STATE_CURSOR = "stateCursor";
    final String START_STATE_CURSOR = "startStateCursor";
    final String END_STATE_CURSOR = "endStateCursor";
    final String START_END_STATE_CURSOR = "startEndStateCursor";
    final String TRANSITION_CURSOR = "transitionCursor";
  }

  // How many millis until the Error-message fades away completely
  long ERROR_MESSAGE_DURATION_MILLIS = 3000;
  int ERROR_MESSAGE_FONT_SIZE = 20;
}