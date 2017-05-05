package GUI;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.JTextArea;

import ChronoTimer.Printer;

public class TestHandler extends Handler {
	
	
	static final boolean _reportOn = true;
	static final int RUN = 0, END = 1;
	static final String DELIMITERS = "\\n";
	
	private static void warning(String message)
	{
		System.out.println("Error encountered during argument read:"+message);
		System.out.println("Expected: <filename>");
	}
	
	private static void report(String message)
	{
		if(_reportOn)
			System.out.println(message);
	}
	
	public static void main(String args[])
	{
		boolean f_read = false;
		Scanner input;
		int state = RUN;
		String tokens[] = {""};
		String cCmd = "";
		int cToken = 0;
		if(args.length > 1)
		{
			warning("Improper number of arguments.");
			report("Using console commands...");
			input = new Scanner(System.in);
		}
		else if(args.length == 1)
		{
			try
			{
				input = new Scanner(new FileReader(args[0]));
				f_read = true;
			}
			catch (FileNotFoundException e) 
			{
				warning("File not found: " + args[0]);
				report("Using console commands...");
				input = new Scanner(System.in);
			}
		}
		else
		{
			report("Using console commands...");
			input = new Scanner(System.in);
		}
		
		TestHandler main = new TestHandler(new JTextArea(), new JTextArea());
		input.useDelimiter(DELIMITERS);
		main.main.setPrinter(new Printer(System.out));
		while(state != END)
		{
			cToken = 0;
			try
			{
				//cCmd = fparse ? tokens[tokens.length - 1] : input.next().trim().toUpperCase();
				cCmd = input.next().trim().toUpperCase();
			}
			catch (NoSuchElementException ex)
			{
				if(f_read && !input.hasNext())//have we reached end of file?
					state = END;
				else
					warning("Unhandled IOException.");
			}
			main.issue(cCmd);
			main.issue("UPDATE");
			System.out.println(main.getDisplay());
			System.out.println(main.getPrinter());
			System.out.println("---------------------------------------------------");
			/*try {
				main.wait(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		main.main.flush();
		
		//main.main.setPrinter(new Printer(System.out));
		System.exit(0);
	}
	
	public TestHandler(JTextArea d, JTextArea p) {
	super(d, p);
	STARTDELAY = 0;
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
