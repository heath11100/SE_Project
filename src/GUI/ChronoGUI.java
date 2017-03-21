package GUI;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.*;

public class ChronoGUI {
	
	//Containers
	JFrame frame, backFrame;
	JPanel[] panels;
	
	//Components
	JButton power, printerPower, function, swap;
	JTextField display;
	JScrollPane printerTape;
	
	JButton[] channelTrigger;
	JCheckBox[] channelEnable;
	
	JButton[] numPad, directionals;
	
	public ChronoGUI(){
		createContainers();
		createDisplays();
		createButtons();
		frame.setVisible(true);
		backFrame.setVisible(true);
	}
	
	private void createContainers(){
		//create main frames
		frame = new JFrame("Top View");
		frame.setLayout(new GridLayout(2,3));
		frame.setSize(1000, 600);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//create/add panels
		panels = new JPanel[6];
		for (int i=0;i<6;i++){
			panels[i] = new JPanel(new GridBagLayout());
			panels[i].setBackground(new Color(i*20,i*20,i*20));
			frame.add(panels[i]);}

		//create back frame
		backFrame = new JFrame("Back View");
		backFrame.setLayout(new GridLayout(4,4));
		backFrame.setSize(400, 200);
		backFrame.setResizable(false);
		backFrame.setLocation(frame.getLocation().x,frame.getLocation().y-backFrame.getHeight());
		backFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void createButtons(){
		//Use this to set preferred layout for each component
		GridBagConstraints c;
		
		//panel 0 (upper left)
		c = new GridBagConstraints();
		c.anchor=GridBagConstraints.PAGE_START;
		c.ipadx=15;c.ipady=5;
		c.insets= new Insets(20,0,0,70);
		c.weightx=c.weighty=1;
		power = new JButton("Power");
		panels[0].add(power,c);
		
		//JButton power, printerPower, function, swap;
		//JTextField display;
		//JScrollPane printerTape;
		
		//JButton[] channelTrigger;
		//JCheckBox[] channelEnable;
		
		//JButton[] numPad, directionals;
		
		
	}
	
	private void createDisplays(){
		
	}
	
	
	public static void main(String[] args){new ChronoGUI();}
}
