package GUI;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

public class NumPad extends JPanel{
	private static final long serialVersionUID = 1L;

	JButton[] numbers; //index corresponds directly to number (0-9)
	JButton star,pound;
	
	protected NumPad(){
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
		for (int i=0;i<10;i++)
			numbers[i] = new JButton(""+i);
		star = new JButton("*");
		pound = new JButton("#");
	}
	
	
}
