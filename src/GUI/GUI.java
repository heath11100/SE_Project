package GUI;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;

public class GUI {

	private final int FRAME_WIDTH = 1200, FRAME_HEIGHT = 700, BACK_WIDTH=400,BACK_HEIGHT = 120;

	// Containers
	JFrame frame, backFrame;
	JPanel[] panels;
	GridBagConstraints c;

	// Buttons
	JButton power, printerPower, function, up, down, left, right, swap;
	JComboBox<String>[] channelPlugs;

	// Pads
	NumPad numPad;
	ChannelPad channelPad;

	// Displays
	JTextArea displayText,printerText;

	//Handles commands issued from GUI
	Handler handler;
	
	public GUI() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyTrigger());
		displayText = new JTextArea();
		printerText = new JTextArea();
		handler = new Handler(displayText,printerText);
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
					c.insets = new Insets(0, 0, 0, FRAME_WIDTH/4);
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

		printerText.setEditable(false);
		printerText.setPreferredSize(new Dimension(250,250));
		//printerScroll = new JScrollPane(printerText,
				//JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//printerScroll.setPreferredSize(new Dimension(250,250));
		c = new GridBagConstraints();
		c.gridx = 0;c.gridy = 1;
		c.weightx = c.weighty = 1;
		panels[2].add(printerText,c);
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
		displayText.setEditable(false);
		displayText.setPreferredSize(new Dimension(600,300));
		//displayScroll = new JScrollPane(displayText,
			//	JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//displayScroll.setPreferredSize(new Dimension(600,300));
		c = new GridBagConstraints();
		c.gridx = 0;c.gridy = 0;
		c.weightx = c.weighty = 1;
		panels[4].add(displayText,c);
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
		
		channelPlugs = new JComboBox[8];
		for (int i=0;i<8;i++){
			channelPlugs[i] = new JComboBox<String>(new String[]{" NONE"," EYE"," GATE"," PAD"});
			channelPlugs[i].setSelectedIndex(0);
			channelPlugs[i].addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							@SuppressWarnings("unchecked")
							JComboBox<String> b = (JComboBox<String>) e.getSource();
							String type = (String) b.getSelectedItem();
							int i;
							for (i=0 ;i<8;i++)
								if (b == channelPlugs[i]) break;
							handler.issue("PLUG "+(i+1)+type);
						}});
		}
		
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

	private class KeyTrigger implements KeyEventDispatcher{
		@Override
		public boolean dispatchKeyEvent(KeyEvent k) {
			if (k.getID() != KeyEvent.KEY_PRESSED) return false;
			switch (k.getKeyCode()){
			case KeyEvent.VK_ESCAPE: if (!k.isShiftDown())power.doClick(); else printerPower.doClick(); break;
			case KeyEvent.VK_F: function.doClick();break;
			case KeyEvent.VK_UP: up.doClick();break;
			case KeyEvent.VK_DOWN: down.doClick();break;
			case KeyEvent.VK_LEFT: left.doClick();break;
			case KeyEvent.VK_RIGHT: right.doClick();break;
			case KeyEvent.VK_TAB: swap.doClick();break;
			case KeyEvent.VK_NUMPAD0: numPad.press(0);break;
			case KeyEvent.VK_NUMPAD1: numPad.press(1);break;
			case KeyEvent.VK_NUMPAD2: numPad.press(2);break;
			case KeyEvent.VK_NUMPAD3: numPad.press(3);break;
			case KeyEvent.VK_NUMPAD4: numPad.press(4);break;
			case KeyEvent.VK_NUMPAD5: numPad.press(5);break;
			case KeyEvent.VK_NUMPAD6: numPad.press(6);break;
			case KeyEvent.VK_NUMPAD7: numPad.press(7);break;
			case KeyEvent.VK_NUMPAD8: numPad.press(8);break;
			case KeyEvent.VK_NUMPAD9: numPad.press(9);break;
			case KeyEvent.VK_BACK_SPACE: numPad.press(10);break;
			case KeyEvent.VK_ENTER: numPad.press(11);break;
			case KeyEvent.VK_1: channelPad.press(!k.isShiftDown()? 1: 9);break;
			case KeyEvent.VK_2: channelPad.press(!k.isShiftDown()? 2: 10);break;
			case KeyEvent.VK_3: channelPad.press(!k.isShiftDown()? 3: 11);break;
			case KeyEvent.VK_4: channelPad.press(!k.isShiftDown()? 4: 12);break;
			case KeyEvent.VK_5: channelPad.press(!k.isShiftDown()? 5: 13);break;
			case KeyEvent.VK_6: channelPad.press(!k.isShiftDown()? 6: 14);break;
			case KeyEvent.VK_7: channelPad.press(!k.isShiftDown()? 7: 15);break;
			case KeyEvent.VK_8: channelPad.press(!k.isShiftDown()? 8: 16);break;
		}return true;
	}}
	
	public static void main(String[] args) {new GUI();}
}
