package ChronoTimer;

import java.io.PrintStream;
import java.io.PrintWriter;


import com.google.gson.Gson;


/**
 * The Printer class.
 * @author Casey Van Grolls
 */
public class Printer 
{
	private PrintStream output = System.out;
	
	
	/** Initializes a Printer with System.out PrintStream. */
	public Printer(){output =null;}
	
	/** Initializes a Printer with the specified PrintStream. 
	 * @param stream the specified PrintStream */
	public Printer(PrintStream stream){output = stream;}
	
	/** Changes the PrintStream to a specified stream
	 * @param stream the new PrintStream */
	public void changeStream(PrintStream stream){output = stream;}
	
	/** Flushes the parameter log's pending data to the output. */
	public void flush(Log l){output.print(l.flush());}
	
	/** Prints the entire parameter log to the output. */
	public void print(Log l){ output.print(l.readAll()); }
	
	/** Exports the parameter run to a file. */
	public void export(int runNumber, Run r){
		String fileName = "run"+runNumber+".txt";
		
		try(PrintWriter p = new PrintWriter(fileName)){p.write(new Gson().toJson(r));}
		catch (Exception e) {System.out.println("Could not print to file: "+fileName);}
	}
}
