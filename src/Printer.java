import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/*
 __    __     ______     ______   ______      _____     ______     __    __     ______     __   __     ______                     
/\ "-./  \   /\  __ \   /\__  _\ /\__  _\    /\  __-.  /\  __ \   /\ "-./  \   /\  __ \   /\ "-.\ \   /\  ___\                    
\ \ \-./\ \  \ \  __ \  \/_/\ \/ \/_/\ \/    \ \ \/\ \ \ \  __ \  \ \ \-./\ \  \ \ \/\ \  \ \ \-.  \  \ \___  \                   
 \ \_\ \ \_\  \ \_\ \_\    \ \_\    \ \_\     \ \____-  \ \_\ \_\  \ \_\ \ \_\  \ \_____\  \ \_\\"\_\  \/\_____\                  
  \/_/  \/_/   \/_/\/_/     \/_/     \/_/      \/____/   \/_/\/_/   \/_/  \/_/   \/_____/   \/_/ \/_/   \/_____/                  
																																
 ______     ______     ______     __   __     ______        ______   ______     __     ______     ______     ______     ______    
/\  ___\   /\  == \   /\  __ \   /\ "-.\ \   /\  __ \      /\__  _\ /\  == \   /\ \   /\  ___\   /\  ___\   /\  ___\   /\  == \   
\ \ \____  \ \  __<   \ \ \/\ \  \ \ \-.  \  \ \ \/\ \     \/_/\ \/ \ \  __<   \ \ \  \ \ \__ \  \ \ \__ \  \ \  __\   \ \  __<   
 \ \_____\  \ \_\ \_\  \ \_____\  \ \_\\"\_\  \ \_____\       \ \_\  \ \_\ \_\  \ \_\  \ \_____\  \ \_____\  \ \_____\  \ \_\ \_\ 
  \/_____/   \/_/ /_/   \/_____/   \/_/ \/_/   \/_____/        \/_/   \/_/ /_/   \/_/   \/_____/   \/_____/   \/_____/   \/_/ /_/ 
*/

//Ryan Thorne
//3/2/2017
//ver 1.0

public class Printer 
{
	//stores all of the commands added to the printer 
	ArrayList<String> list;
	//holds all commands that havent been written to the output stream yet
	Queue<String> buffer;
	//the place where the printer prints to
	PrintStream output;
	
	/**
	 * initializes a Printer with the default printstream (System.out) 
	 */
	public Printer()
	{
		list = new ArrayList<String>();
		buffer = new LinkedList<String>();
		output = System.out;
	}
	
	/**
	 * initializes a Printer with the specified printstream 
	 * @param p
	 * the specified PrintStream
	 */
	public Printer(PrintStream p)
	{
		list = new ArrayList<String>();
		buffer = new LinkedList<String>();
		output = p;
	}
	
	/**
	 * creates a new Printer with a printstream different from an existing Printer
	 * @param p
	 * the specified new PrintStream
	 * @param oldPrinter
	 * the old Printer from which the stream will copy
	 */
	public Printer(PrintStream p, Printer oldPrinter)
	{
		list = new ArrayList<String>();
		list.addAll(oldPrinter.list);
		buffer = new LinkedList<String>();
		buffer.addAll(oldPrinter.buffer);
		output = p;
	}
	/**
	 * changes the printstream to a specified stream
	 * @param p
	 * the specified new PrintStream
	 */
	public void changeStream(PrintStream p)
	{
		output = p;
	}
	
	/**
	 * clones the Printer
	 * not as useful as i originally thought
	 */
	@Override
	public Printer clone()
	{
		Printer np = new Printer(output, this);
		return np;
	}
	
	/**
	 * add a formatted string to the Printer
	 * @param s
	 * the item to be added to the print stream
	 */
	public void add(String s)
	{
		buffer.add(s);
		list.add(s);
	}
	
	/**
	 * writes all strings to the current output that have not yet been written
	 */
	public void flush()
	{
		if(buffer.isEmpty())
			output.println("buffer up to date");
		
		while(!buffer.isEmpty())
			output.println(buffer.remove());
	}
	
	/**
	 * writes all strings to the output, reguardless of whether they have been written or not, also clears flush
	 */
	public void write()
	{
		for(String s : list)
			output.println(s);
		buffer.clear();
	}
}
