/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JToolBar;

public class ToolBar extends JToolBar {
	private static final long serialVersionUID = 1L;
	private ToggleButton addStateButton;
	private ToggleButton addStartStateButton;
	private ToggleButton addEndStateButton;
	private ToggleButton addTransitionButton;
	
	public ToolBar() {
		this.setPreferredSize(new Dimension(Config.TOOLBAR_X, Config.TOOLBAR_Y));
		this.setDoubleBuffered(true);
		this.setFocusable(true);
		this.setLayout(new FlowLayout());
		
		// Removes the weird standard-borders with 2 vertical lines on the left
		this.setBorder(BorderFactory.createRaisedBevelBorder());
		
		String absolutePath = new File("").getAbsolutePath();
		
		// Normal state
		addStateButton = new ToggleButton(absolutePath + Config.ADD_STATE_ICON_PATH, true, this);
		this.add(addStateButton);
		
		// Start state
		addStartStateButton = new ToggleButton(absolutePath + Config.ADD_START_STATE_ICON_PATH, false, this);
		this.add(addStartStateButton);
		
		// End state
		addEndStateButton = new ToggleButton(absolutePath + Config.ADD_END_STATE_ICON_PATH, false, this);
		this.add(addEndStateButton);
		
		// Transition
		addTransitionButton = new ToggleButton(absolutePath + Config.ADD_TRANSITION_ICON_PATH, false, this);
		this.add(addTransitionButton);
	}
	
	public void paint(Graphics graphics) {
		super.paint(graphics);
		Graphics2D graphics2D = (Graphics2D) graphics;
		
		graphics2D.dispose();
	}
	
	// manages the logic constraints of the toggle-buttons. E.g. when addTransition is clicked,
	// deselect all other toggle-buttons
	protected void toggleButtonEventHandler(ToggleButton clickedButton) {
		if (clickedButton.equals(addStateButton) || clickedButton.equals(addStartStateButton) || 
				clickedButton.equals(addEndStateButton)) {
			// Handle the three state buttons separately
			this.handleStateButtons(clickedButton);
		} else
			this.handleDefaultButtons(clickedButton);
	}
	
	// clickedButton is one of the three stateButtons
	private void handleStateButtons(ToggleButton clickedButton) {
		// Swing called setSelected() already. So e.g. when button was not selected and clicked,
		// isSelected() returns true already.
		
		// deselecting
		if (!clickedButton.isSelected()) {
			// In case of addStateButton was deselected, desect StartStateButton and EndStateButton too
			if (clickedButton.equals(addStateButton)) {
				addStartStateButton.setSelected(false);
				addEndStateButton.setSelected(false);
			}
		} else { // selecting
			deselectNonStateButtons();
			
			// select addStateButton in case the user clicked on StartButton or EndButton
			if (clickedButton.equals(addStartStateButton) || clickedButton.equals(addEndStateButton)) {
				addStateButton.setSelected(true);
			}
		}
	}
	
	private void deselectNonStateButtons() {
		addTransitionButton.setSelected(false);
	}
	
	// clickedButton is not one of the three state Buttons
	private void handleDefaultButtons(ToggleButton clickedButton) {
		// Swing called setSelected() already. So e.g. when button was not selected and clicked,
		// isSelected() returns true already.
		
		// Deselect all other buttons if click made deselected -> selected. In case of
		// selected -> deselected, do nothing.
		if (clickedButton.isSelected()) {
			// not selected -> selected
			deselectAllButtons(clickedButton);
		}
	}
	
	// helper method to deselect all ToggleButton instances except the one passed
	private void deselectAllButtons(ToggleButton clickedButton) {
		addStateButton.setSelected(false);
		addStartStateButton.setSelected(false);
		addEndStateButton.setSelected(false);
		addTransitionButton.setSelected(false);
		
		// reselect the passed ToggleButton
		clickedButton.setSelected(true);
	}
	
	// Getters
	public ToggleButton getAddStateButton() {
		return this.addStateButton;
	}
	
	public ToggleButton getAddStartStateButton() {
		return this.addStartStateButton;
	}
	
	public ToggleButton getAddEndStateButton() {
		return this.addEndStateButton;
	}
	
	public ToggleButton getAddTransitionButton() {
		return this.addTransitionButton;
	}
}