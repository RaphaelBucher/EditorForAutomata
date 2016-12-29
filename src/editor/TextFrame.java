package editor;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.MatteBorder;

public class TextFrame extends JFrame {
  private static final long serialVersionUID = 1L;

  public TextFrame(String title, Dimension size, String text) {
    //FRAME
    setTitle(title);
    setResizable(true);
    setMinimumSize(new Dimension(240, 100));

    //TEXT AREA
    JTextArea textArea = new JTextArea(text);
    textArea.setPreferredSize(size);
    textArea.setLineWrap(true);
    textArea.setEditable(false);
    textArea.setBorder(new MatteBorder(12, 12, 12, 12, Color.WHITE));

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    add(scrollPane);
    pack();
    setVisible(true);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
  }
}
