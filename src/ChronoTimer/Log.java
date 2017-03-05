package ChronoTimer;

/**
 * The Log class.
 * @author Casey Van Groll
 */
public class Log 
{
	private String master, buffer;
	
	/** Constructor */
	public Log() {master = buffer = "";}
	
	/** Add a string to the log
	 * @param str the string to be added */
	public void add(String str){
		master += str+"\n";
		buffer += str+"\n";}
	
	/** Returns the buffer, then empties it. */
	public String flush(){
		String result = new String(buffer);
		buffer = "";
		return result;}
	
	/** Returns the entire log. */
	public String readAll(){return master;}
}
