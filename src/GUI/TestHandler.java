package GUI;

import javax.swing.JTextArea;

public class TestHandler extends Handler {
	public TestHandler(JTextArea d, JTextArea p) {
	super(d, p);
	}
	
	public String getDisplay()
	{
		return displayArea.getText();
	}
	
	public String getPrinter()
	{
		return printArea.getText();
	}

}
