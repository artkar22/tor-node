package main;
import javax.swing.SwingUtilities;
/**
* Klasa g³ówna programu
**/
public class Starter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable(){public void run(){
			 new Menu(args[0]);}});
	}

}
