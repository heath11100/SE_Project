package ChronoTimer;

import java.io.PrintWriter;

import javax.swing.JTextArea;

import com.google.gson.Gson;


/**
 * The Printer class.
 * @author Casey Van Grolls
 */
public class Printer 
{
	private JTextArea output;
	
	/** Initializes a Printer with System.out JTextArea. */
	public Printer(){output =null;}
	
	/** Initializes a Printer with the specified JTextArea. 
	 * @param stream the specified JTextArea */
	public Printer(JTextArea stream){output = stream;}
	
	/** Changes the JTextArea to a specified stream
	 * @param stream the new JTextArea */
	public void changeStream(JTextArea stream){output = stream;}
	
	/** Flushes the parameter log's pending data to the output. */
	public void flush(Log l){output.append(l.flush());}
	
	/** Prints the entire parameter log to the output. */
	public void print(Log l){ output.append(l.readAll()); }
	
	/** Exports the parameter run to a file. */
	public void export(int runNumber, Run r){
		String fileName = "run"+runNumber+".txt";
		
		try(PrintWriter p = new PrintWriter(fileName)){p.write(new Gson().toJson(r));}
		catch (Exception e) {System.out.println("Could not print to file: "+fileName);}
	}
}
