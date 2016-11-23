/* 
 * Bachelor Thesis
 * Raphael Bucher
 * November 2016
 * */
package editor;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Editor extends JFrame {
	private static final long serialVersionUID = 1L;
	private static JPanel container;
	private static ToolBar toolBar;
	private static DrawablePanel drawablePanel;
	
	
	public Editor(DrawablePanel editor) {
		this.setTitle("Editor for Automata");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // close Window => System.exit
		this.setLayout(new BorderLayout());
		
		container = new JPanel();
		toolBar = new ToolBar();
		drawablePanel = new DrawablePanel();
		
		container.setLayout(new FlowLayout());
		container.add(toolBar);
		container.add(drawablePanel);
		
		this.add(container);
		
		this.pack();
		
		// The order of these calls is important for the positioning through setLocationRelativeTo()
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setResizable(false);
	}

	public static void main(String[] args) {
		Editor editor = new Editor(drawablePanel);
		
		editor.run();
	}
	
	// The main loop of the program
	private void run() {
		long startTime;
		long millisPerFrame = 1000 / Config.FPS;
		long timeout;
		while (true) {
			// Frame-rate like in games to not permanently repaint() everything when not needed.
			// Doesn't eat too much unneeded system resources
			startTime = System.currentTimeMillis();
			
			// Updating
			drawablePanel.update(toolBar);

			// Painting
			toolBar.repaint();
			drawablePanel.repaint();
			
			timeout = millisPerFrame - (System.currentTimeMillis() - startTime);
			sleep(timeout);
		}
	}
	
	/**
	 * @param millis negative values allowed, method will deal with it (set to 0)
	 */
	private void sleep(long millis) {
		if (millis < 0)
			millis = 0;
		
		try {
			Thread.sleep(millis);
		} catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
			System.err.println("Thread.sleep() failed.");
		}
	}
}

