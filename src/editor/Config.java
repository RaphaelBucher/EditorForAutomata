/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.Color;

public interface Config {
	/** Drawable pixels */
	int DRAWABLE_PANEL_X = 1200, DRAWABLE_PANEL_Y = 800;
	
	int TOOLBAR_X = 60, TOOLBAR_Y = DRAWABLE_PANEL_Y;
	int TOOLBAR_ICON_XY = 50;
	int CURSOR_XY = TOOLBAR_ICON_XY; // The custom cursor width and height of DrawablePanel
	// In case of an end-state, the second circle is within the default one, the diameter for normal
	// and end-states is the same. This eases the rendering.
	int STATE_DIAMETER = CURSOR_XY - 4; // make it slightly smaller
	int END_STATE_INNER_DIAMETER = STATE_DIAMETER - 6;
	
	Color BACKGROUND_COLOR = Color.WHITE;
	
	String ADD_STATE_ICON_PATH = "/resources/Icons/addState.png";
	//String ADD_STATE_HOVERED_ICON_PATH = "/resources/Icons/addStateHovered.png";
	String ADD_START_STATE_ICON_PATH = "/resources/Icons/addStartState.png";
	//String ADD_START_STATE_HOVERED_ICON_PATH = "/resources/Icons/addStartStateHovered.png";
	String ADD_END_STATE_ICON_PATH = "/resources/Icons/addEndState.png";
	//String ADD_END_STATE_HOVERED_ICON_PATH = "/resources/Icons/addEndStateHovered.png";
	String ADD_TRANSITION_ICON_PATH = "/resources/Icons/addTransition.png";
	//String ADD_TRANSITION_HOVERED_ICON_PATH = "/resources/Icons/addTransitionHovered.png";
	
	String START_END_STATE_CURSOR_PATH = "/resources/Icons/startEndStateCursor.png";
	
	int FPS = 30;

	interface Cursor_names {
		final String STATE_CURSOR = "stateCursor";
		final String START_STATE_CURSOR = "startStateCursor";
		final String END_STATE_CURSOR = "endStateCursor";
		final String START_END_STATE_CURSOR = "startEndStateCursor";
		final String TRANSITION_CURSOR = "transitionCursor";
	}
	
	// ErrorMessage
	long ERROR_MESSAGE_DURATION_MILLIS = 3000; // How many millis until the message faded away completely
	int ERROR_MESSAGE_FONT_SIZE = 20;
}