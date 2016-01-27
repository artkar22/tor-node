package utils;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Wyświetlanie komunikatów
 *
 */
public class MessagePrompt extends JFrame{

	public MessagePrompt(String labelText){
		JLabel label = new JLabel(labelText);
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        getContentPane().add(label);
        pack();
	}
	
}
