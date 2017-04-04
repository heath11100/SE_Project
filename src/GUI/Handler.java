package GUI;

import javax.swing.JTextArea;

public class Handler {
	
	JTextArea displayArea, printArea;
	public Handler(JTextArea d, JTextArea p){displayArea=d;printArea=p;}
	
	protected boolean issue(String command){
		
		//right now just feeds to display text area
		//will need to handle area being full
		
		displayArea.append(command+"\n");
		return true;
	}
}
