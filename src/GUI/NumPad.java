package GUI;

import java.awt.Dimension;
import java.awt.GridLayout;

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
	
	private void createButtons(){
		numbers = new JButton[10];
		for (int i=0;i<10;i++){
			numbers[i] = new JButton(""+i);
			numbers[i].addActionListener(new Listener(handler, "NUM "+i));}
		star = new JButton("*");
		star.addActionListener(new Listener(handler, "STAR"));
		pound = new JButton("#");
		pound.addActionListener(new Listener(handler, "POUND"));
	}
	
	
}
