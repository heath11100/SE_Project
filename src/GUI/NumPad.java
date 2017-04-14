package GUI;

import java.awt.GridLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

public class NumPad extends JPanel{
	private static final long serialVersionUID = 1L;
	private Handler handler;
	private JButton[] numbers; //index corresponds directly to number (0-9)
	private JButton star,pound;
	
	protected NumPad(Handler handler){
		this.handler=handler;
		createButtons();
		
		//organize buttons in pad
		setLayout(new GridLayout(4,3));
		for (int i=1;i<10;i++){
			add(numbers[i]);}
		add(star);
		add(numbers[0]);
		add(pound);
	}
	
	protected void press(int i){
		if (i == 10) star.doClick();
		else if (i == 11) pound.doClick();
		else	numbers[i].doClick();
	}
	
	private void createButtons(){
		numbers = new JButton[10];
		for (int i=0;i<10;i++){
			numbers[i] = new JButton(""+i);
			GUI.stylize(numbers[i]);
			numbers[i].addActionListener(new Listener(handler, "NUM "+i));}
		star = new JButton("*");
		GUI.stylize(star);
		star.addActionListener(new Listener(handler, "STAR"));
		pound = new JButton("#");
		GUI.stylize(pound);
		pound.addActionListener(new Listener(handler, "POUND"));
	}
}
