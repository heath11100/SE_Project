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
	
	/** Prints the parameter queue to the output.
	 *  NB: doesn't empty the queue, so Log.readAll() can return actual copy of master queue. */
	public void print(String str){output.print(str);}
}
