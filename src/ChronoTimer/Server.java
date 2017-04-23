/**
 * Simple HTTP handler for testing ChronoTimer
 */
package ChronoTimer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import Exceptions.InvalidTimeException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {

    // a shared area where we get the POST data and then use it in the other handler
    static String sharedResponse = "";
    static boolean gotMessageFlag = false;
    public static ArrayList<NamedRacer> racers = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        // set up a simple HTTP server on our local host
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // create a context to get the request to display the results
        server.createContext("/displayresults", new DisplayHandler());
        
        // create a context to get the request to display the results
        server.createContext("/css/style.css", new CSSHandler());

        // create a context to get the request for the POST
        server.createContext("/sendresults",new PostHandler());
        server.setExecutor(null); // creates a default executor

        // get it going
        System.out.println("Starting Server...");
        server.start();
    }

    static class DisplayHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {

            String response = "<!DOCTYPE html><html><head><title>Race Results</title><link rel=\"stylesheet\" href=\"/css/style.css\"></head>";
			Gson g = new Gson();
			// set up the header
            System.out.println(response);
            
			try {
				if (!sharedResponse.isEmpty()) {
					Collections.sort(racers);
					response += generateTable(); 
		}
			} catch (JsonSyntaxException | InvalidTimeException e) {
				e.printStackTrace();
			}
            response += "</html>";
            System.out.println(response);
            // write out the response
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class PostHandler implements HttpHandler {
        public void handle(HttpExchange transmission) throws IOException {

            //  shared data that is used with other handlers
            sharedResponse = "";

            // set up a stream to read the body of the request
            InputStream inputStr = transmission.getRequestBody();

            // set up a stream to write out the body of the response
            OutputStream outputStream = transmission.getResponseBody();

            // string to hold the result of reading in the request
            StringBuilder sb = new StringBuilder();

            // read the characters from the request byte by byte and build up the sharedResponse
            int nextChar = inputStr.read();
            while (nextChar > -1) {
                sb=sb.append((char)nextChar);
                nextChar=inputStr.read();}
            
            //parse and handle the command
            parseAndHandle(sb.toString());

            // create our response String to use in other handler
            sharedResponse = sharedResponse+sb.toString();

            // respond to the POST with ROGER
            String postResponse = "ROGER JSON RECEIVED";

            System.out.println("response: " + sharedResponse);
            

            //Desktop dt = Desktop.getDesktop();
            //dt.open(new File("raceresults.html"));

            // assume that stuff works all the time
            transmission.sendResponseHeaders(300, postResponse.length());

            // write it and return it
            outputStream.write(postResponse.getBytes());

            outputStream.close();
        }
    }
    
    static class CSSHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
        	
        	String css = "table {border-collapse: collapse; width:75%;}"
        			+ "th, td { border: 1px solid #dddddd; text-align:left; padding:8px; }"
        			+ "tr:nth-child(even) { background-color:#dddddd; }";
        	
            // write out the response
            t.sendResponseHeaders(200, css.length());
            OutputStream os = t.getResponseBody();
            os.write(css.getBytes());
            os.close();
        }
    }
    
    private static void parseAndHandle(String s){
    	System.out.println(s);
    	if (s.startsWith("ADD")){
    		s = s.substring(4);
    		NamedRacer r = new Gson().fromJson(s, NamedRacer.class);
    		racers.add(r);
    		System.out.println("Successfully added employee.");
    	}
    	else if (s.startsWith("CLEAR")){
    		racers.clear();
    		System.out.println("Successfully cleared all employees.");
    	}
    	else if (s.startsWith("PRINT")){
    		System.out.println("Employees:"+((racers.isEmpty())?"NONE":""));
    		System.out.flush();
    		for (int i=0;i<racers.size();i++)
    			System.out.println(racers.get(i).toString());
    	}
    	else
    		System.out.println("Failed command.");
    }
    
    private static String generateTable() throws InvalidTimeException{
    	String result = "<table><tr><th>Bib</th><th>Last Name</th> <th>First Initial</th><th>Time</th></tr>";
    	for (int i=0;i<racers.size();i++){
    		NamedRacer cur = racers.get(i);
    		result +="<tr><td>"+cur.getMyRacer().getNumber()+"</td><td>"+cur.getLastName()+", "+cur.getFirstInitial()+"</td><td>"+
    				((cur.getMyRacer().getStatus() == Racer.Status.DNF)? "DNF" : cur.getMyRacer().getElapsedTime())
    		+"</td></tr>";}
    	return result+"</table>";
    }
}
