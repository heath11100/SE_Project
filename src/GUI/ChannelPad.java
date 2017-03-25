package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ChannelPad extends JPanel{
	private static final long serialVersionUID = 1L;
	JButton[] channelTrigger;
	JCheckBox[] channelEnable;
	JPanel sub1, sub2;
	GridBagConstraints c;
	
	
	protected ChannelPad(){
		setLayout(new GridBagLayout());
		createLeftPanel();
		createRightPanel();
		c=new GridBagConstraints();
		c.gridx=c.gridy=0;c.fill=GridBagConstraints.BOTH;c.insets = new Insets(0,20,0,10);
		add(sub1,c);
		c.gridx=1;c.gridwidth=8;c.weightx=c.weighty=1;c.insets = new Insets(0,0,0,0);
		add(sub2,c);
	}
	
	private void createLeftPanel(){
		sub1 = new JPanel();
		//sub1.setBackground(new Color(0,0,200));
		sub1.setLayout(new GridLayout(6,1));
		
		JLabel[] labels = new JLabel[6];
		for (int i=0;i<6;i++){
			labels[i] = new JLabel();
			labels[i].setHorizontalAlignment(JLabel.RIGHT);}
		
		labels[0].setText(" ");					sub1.add(labels[0]);
		labels[1].setText("Start");				sub1.add(labels[1]);
		labels[2].setText("Enable/Disable");	sub1.add(labels[2]);
		labels[3].setText(" ");					sub1.add(labels[3]);
		labels[4].setText("Finish");			sub1.add(labels[4]);
		labels[5].setText("Enable/Disable");	sub1.add(labels[5]);
	}
	
	private void createRightPanel(){
		sub2 = new JPanel();
		//sub2.setBackground(new Color(200,0,0));
		sub2.setLayout(new GridLayout(6,4));
		
		JLabel[] labels = new JLabel[8];
		channelTrigger = new JButton[8];
		channelEnable = new JCheckBox[8];
		
		for (int i=0;i<8;i++){
			labels[i] = new JLabel(""+(i+1));
			labels[i].setHorizontalAlignment(JLabel.CENTER);
			channelTrigger[i] = new JButton();
			channelEnable[i] = new JCheckBox();
			channelEnable[i].setHorizontalAlignment(JCheckBox.CENTER);
		}
		
		
		sub2.add(labels[0]);sub2.add(labels[2]);sub2.add(labels[4]);sub2.add(labels[6]);
		sub2.add(channelTrigger[0]);sub2.add(channelTrigger[2]);sub2.add(channelTrigger[4]);sub2.add(channelTrigger[6]);
		sub2.add(channelEnable[0]);sub2.add(channelEnable[2]);sub2.add(channelEnable[4]);sub2.add(channelEnable[6]);
		
		sub2.add(labels[1]);sub2.add(labels[3]);sub2.add(labels[5]);sub2.add(labels[7]);
		sub2.add(channelTrigger[1]);sub2.add(channelTrigger[3]);sub2.add(channelTrigger[5]);sub2.add(channelTrigger[7]);
		sub2.add(channelEnable[1]);sub2.add(channelEnable[3]);sub2.add(channelEnable[5]);sub2.add(channelEnable[7]);
		
		
		
		
		
		
		
		
		
	}
}
