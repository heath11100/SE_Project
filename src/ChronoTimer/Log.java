package ChronoTimer;
import java.util.PriorityQueue;

/**
 * The Log class.
 * @author Casey Van Groll
 */
public class Log 
{
	private PriorityQueue<String> master, buffer;
	
	/** Constructor */
	public Log(){
		master = new PriorityQueue<>();
		buffer = new PriorityQueue<>();}
	
	/** Add a string to the log
	 * @param str the string to be added */
	public void add(String str){
		master.add(str);
		buffer.add(str);}
	
	/** Returns the buffer, then empties it. */
	public PriorityQueue<String> flush(){
		PriorityQueue<String> result = buffer;
		buffer.clear();
		return result;}
	
	/** Returns the entire log. */
	public PriorityQueue<String> readAll(){
		return master;}
}
