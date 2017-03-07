package ChronoTimer;
import java.io.PrintStream;

/**
 * The Printer class.
 * @author Casey Van Groll
 */
public class Printer 
{
	private PrintStream output;
	
	/** Initializes a Printer with System.out PrintStream. */
	public Printer(){output = System.out;}
	
	/** Initializes a Printer with the specified PrintStream. 
	 * @param stream the specified PrintStream */
	public Printer(PrintStream stream){output = stream;}
	
	/** Changes the PrintStream to a specified stream
	 * @param stream the new PrintStream */
	public void changeStream(PrintStream stream){output = stream;}
	
	/** Flushes the parameter log's pending data to the output. */
	public void flush(Log l){output.print(l.flush());}
	
	/** Prints the entire parameter log to the output. */
	public void print(Log l){output.print(l.readAll());}
}
