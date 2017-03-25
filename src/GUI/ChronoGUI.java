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
	GridBagConstraints c;
	
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
		frame.setLayout(new GridBagLayout());
		frame.setSize(1000, 600);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//create panels
		panels = new JPanel[6];
		for (int i=0;i<6;i++){
			panels[i] = new JPanel(new GridBagLayout());
			panels[i].setBackground(new Color(i*20,i*20,i*20));}
		
		//add to frame using gridBagLayout
		c = new GridBagConstraints();
		c.fill=GridBagConstraints.BOTH;
		int i=0;
		for (int y=0;y<2;y++){
			for (int x=0;x<4;x++){
				c.gridx=x;
				c.gridy=y;
				c.gridwidth=(x==1)?2:1;
				c.weightx=1;c.weighty=1;
				
				//must use a dummy panel to give extra space to middle column
				if (x==2){
					c.insets = new Insets(0,0,0,250);
					frame.add(new JPanel(),c);
					c.insets = new Insets(0,0,0,0);}
				else
					frame.add(panels[i++],c);
				//System.out.println("added panel "+(x+(3*y)));
			}
		}
		
		//create back frame
		backFrame = new JFrame("Back View");
		backFrame.setLayout(new GridLayout(4,4));
		backFrame.setSize(400, 200);
		backFrame.setResizable(false);
		backFrame.setLocation(frame.getLocation().x,frame.getLocation().y-backFrame.getHeight());
		backFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void createButtons(){
		//Use GridBagConstraints to set preferred layout for each component
		
		//panel 0 (upper left)
		/*
		c = new GridBagConstraints();
		c.anchor=GridBagConstraints.FIRST_LINE_START;
		c.ipadx=15;c.ipady=5;
		c.insets= new Insets(20,20,0,0);
		c.weightx=c.weighty=1;
		power = new JButton("Power");
		panels[0].add(power,c);*/
		
		panels[0].add(new JLabel("panel 0"));
		//JButton power, printerPower, function, swap;
		panels[1].add(new JLabel("panel 1"));
		panels[2].add(new JLabel("panel 2"));
		panels[3].add(new JLabel("panel 3"));
		panels[4].add(new JLabel("panel 4"));
		panels[5].add(new JLabel("panel 5"));
		
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
