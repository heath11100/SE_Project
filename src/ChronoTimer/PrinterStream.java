package ChronoTimer;

import java.io.PrintStream;

import javax.swing.JTextArea;

public class PrinterStream extends PrintStream 
{

	JTextArea textArea;
	
	public PrinterStream(JTextArea outputArea) 
	{
		super(System.out);
		textArea = outputArea;
		
		// TODO Auto-generated constructor stub
	}
	
	public void println(String string) 
	{
		textArea.append(string+"\n");
		textArea.validate();
	}
	 
	public void print(String string) 
	{
		textArea.append(string);
		textArea.validate();
	}

}
