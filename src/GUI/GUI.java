package GUI;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.*;

public class GUI {

	private final int FRAME_WIDTH = 1200, FRAME_HEIGHT = 700, BACK_HEIGHT = 200;

	// Containers
	JFrame frame, backFrame;
	JPanel[] panels;
	GridBagConstraints c;

	// Buttons
	JButton power, printerPower, function, swap;
	JButton[] directionals;

	// Pads
	NumPad numPad;
	ChannelPad channelPad;

	// Displays
	JTextArea displayText,printerText;
	JScrollPane displayScroll, printerScroll;

	public GUI() {
		createContainers();
		createContents();
		frame.setVisible(true);
		backFrame.setVisible(true);
	}

	private void createContainers() {
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

		// Create back view frame.
		backFrame = new JFrame("Back View");
		backFrame.setLayout(new GridLayout(4, 4));
		backFrame.setSize(FRAME_WIDTH, BACK_HEIGHT);
		backFrame.setResizable(false);
		backFrame.setLocation(frame.getX(), frame.getY() + FRAME_HEIGHT);
		backFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void createContents() {
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
		panels[0].add(power, c);
	}

	/**
	 * North panel- Channel Trigger/Toggle
	 */
	private void createPanel1() {
		channelPad = new ChannelPad();
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
		panels[3].add(new JLabel("panel 3"));
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
		numPad = new NumPad();
		numPad.setPreferredSize(new Dimension(200, 200));
		panels[5].add(numPad);
	}

	public static void main(String[] args) {
		new GUI();
	}
}
