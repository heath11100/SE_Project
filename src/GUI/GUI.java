package GUI;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import ChronoTimer.ChronoTrigger;

public class GUI {

	private final int FRAME_WIDTH = 1200, FRAME_HEIGHT = 700, BACK_WIDTH=400,BACK_HEIGHT = 200;

	// Containers
	JFrame frame, backFrame;
	JPanel[] panels;
	GridBagConstraints c;

	// Buttons
	JButton power, printerPower, function, up, down, left, right, swap;
	JCheckBox[] channelPlugs;

	// Pads
	NumPad numPad;
	ChannelPad channelPad;

	// Displays
	JTextArea displayText,printerText;
	JScrollPane displayScroll, printerScroll;

	//Handles commands issued from GUI
	Handler handler;
	
	public GUI() {
		handler = new Handler(this);
		createTopView();
		createBackView();
		frame.setVisible(true);
		backFrame.setVisible(true);
	}

	private void createTopView() {
		// Create main frame.
		frame = new JFrame("Top View");
		frame.setLayout(new GridBagLayout());
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setLocation(frame.getX(), frame.getY() - 100);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create panels.
		panels = new JPanel[6];
		for (int i = 0; i < 6; i++) {
			panels[i] = new JPanel(new GridBagLayout());
			// Adding color can make layout easier
			// panels[i].setBackground(new Color(i*20,i*20,i*20));
		}

		// Add panels to frame using GridBagLayout.
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		int i = 0;
		for (int y = 0; y < 2; y++) {
			for (int x = 0; x < 4; x++) {
				c.gridx = x;
				c.gridy = y;
				c.gridwidth = (x == 1) ? 2 : 1;
				c.weightx = 1;
				c.weighty = 1;

				// must use a dummy panel to give extra space to middle column
				if (x == 2) {
					c.insets = new Insets(0, 0, 0, 300);
					frame.add(new JPanel(), c);
					c.insets = new Insets(0, 0, 0, 0);}
				else
					frame.add(panels[i++], c);
			}
		}
		
		//create the contents of each subpanel
		createPanel0();
		createPanel1();
		createPanel2();
		createPanel3();
		createPanel4();
		createPanel5();
	}

	/**
	 * Northwest panel- Power button
	 */
	private void createPanel0() {
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.insets = new Insets(20, 30, 0, 0);
		c.weightx = c.weighty = 1;
		power = new JButton("Power");
		power.setPreferredSize(new Dimension(100, 40));
		power.addActionListener(new Listener(handler,"POWER"));
		panels[0].add(power, c);
	}

	/**
	 * North panel- Channel Trigger/Toggle
	 */
	private void createPanel1() {
		channelPad = new ChannelPad(handler);
		channelPad.setPreferredSize(new Dimension(600, 300));
		panels[1].add(channelPad);
	}

	/**
	 * Northeast panel- Printer power and printer tape
	 */
	private void createPanel2() {
		c = new GridBagConstraints();
		c.insets= new Insets(0, 0, 0,0);
		c.gridx = c.gridy = 0;
		c.weightx = c.weighty = 1;
		printerPower = new JButton("Printer Power");
		printerPower.addActionListener(new Listener(handler,"PRINTER POWER"));
		panels[2].add(printerPower,c);

		printerText = new JTextArea();
		printerText.setEditable(false);
		printerScroll = new JScrollPane(printerText,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		printerScroll.setPreferredSize(new Dimension(250,250));
		c = new GridBagConstraints();
		c.gridx = 0;c.gridy = 1;
		c.weightx = c.weighty = 1;
		panels[2].add(printerScroll,c);
	}

	/**
	 * Southwest panel- Function, directional buttons, swap
	 */
	private void createPanel3() {
		function = new JButton("Function");
		up = new JButton("^");
		down = new JButton("v");
		left = new JButton("<");
		right = new JButton(">");
		swap = new JButton("Swap");
		function.addActionListener(new Listener(handler,"FUNCTION"));
		up.addActionListener(new Listener(handler,"UP"));
		down.addActionListener(new Listener(handler,"DOWN"));
		left.addActionListener(new Listener(handler,"LEFT"));
		right.addActionListener(new Listener(handler,"RIGHT"));
		swap.addActionListener(new Listener(handler,"SWAP"));
		
		c = new GridBagConstraints();
		c.weightx = c.weighty = 1;
		
		c.gridx=0;c.gridy=0;c.gridwidth=3;
		panels[3].add(function,c);
		
		c.gridx=1;c.gridy=1;c.gridwidth=1;
		panels[3].add(up,c);
		
		c.gridx=0;c.gridy=2;
		panels[3].add(left,c);
		c.gridx=2;
		panels[3].add(right,c);
		c.gridx=1;c.gridy=3;
		panels[3].add(down,c);

		c.gridy=4;c.gridx=0;c.gridwidth=4;
		panels[3].add(swap,c);
	}

	/**
	 * South panel- Display
	 */
	private void createPanel4() {
		displayText = new JTextArea();
		displayText.setEditable(false);
		displayScroll = new JScrollPane(displayText,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		displayScroll.setPreferredSize(new Dimension(600,300));
		c = new GridBagConstraints();
		c.gridx = 0;c.gridy = 0;
		c.weightx = c.weighty = 1;
		panels[4].add(displayScroll,c);
		c.gridx = 0;c.gridy = 1;
		panels[4].add(new JLabel("Queue / Running / Final Time"),c);
	}

	/**
	 * Southeast panel- Number Pad
	 */
	private void createPanel5() {
		numPad = new NumPad(handler);
		numPad.setPreferredSize(new Dimension(200, 200));
		panels[5].add(numPad);
	}
	
	/**
	 * Back view panel
	 */
	private void createBackView(){
		// Create back view frame.
		backFrame = new JFrame("Back View (Channel Plugs)");
		backFrame.setLayout(new GridLayout(4, 4));
		backFrame.setSize(BACK_WIDTH, BACK_HEIGHT);
		backFrame.setResizable(false);
		backFrame.setLocation(frame.getX(), frame.getY() + FRAME_HEIGHT);
		backFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		JLabel[] labels = new JLabel[8];
		for (int i=0;i<8;i++){
			labels[i] = new JLabel(""+(i+1));
			labels[i].setHorizontalAlignment(JLabel.CENTER);}
		
		channelPlugs = new JCheckBox[8];
		for (int i=0;i<8;i++){
			channelPlugs[i] = new JCheckBox();
			channelPlugs[i].setHorizontalAlignment(JCheckBox.CENTER);
			channelPlugs[i].addActionListener(new Listener(handler, new String[] {"PLUG "+(i+1),"UNPLUG "+(i+1)} ));}
		
		backFrame.add(labels[0]);
		backFrame.add(labels[2]);
		backFrame.add(labels[4]);
		backFrame.add(labels[6]);
		backFrame.add(channelPlugs[0]);
		backFrame.add(channelPlugs[2]);
		backFrame.add(channelPlugs[4]);
		backFrame.add(channelPlugs[6]);
		backFrame.add(labels[1]);
		backFrame.add(labels[3]);
		backFrame.add(labels[5]);
		backFrame.add(labels[7]);
		backFrame.add(channelPlugs[1]);
		backFrame.add(channelPlugs[3]);
		backFrame.add(channelPlugs[5]);
		backFrame.add(channelPlugs[7]);
	}
	
	public void print(String s){
		displayText.append(s+"\n");
	}

	public static void main(String[] args) {new GUI();}
}
