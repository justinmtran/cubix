package GameEngine;

import javax.swing.JOptionPane;

import sage.input.action.AbstractInputAction;

public class QuitGameAction extends AbstractInputAction
{	 
	 public void performAction(float time, net.java.games.input.Event e)
	 {
		 int result = JOptionPane.showConfirmDialog(null,  "Are you sure you want to exit?","Confirm Exit",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(result == JOptionPane.YES_OPTION)
			{
				System.exit(0);
			}
	 }


}
