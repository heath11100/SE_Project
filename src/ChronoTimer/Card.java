package ChronoTimer;
import java.util.Queue;

import Exceptions.InvalidTimeException;

public class Card extends UIPrint {
	private final int TOTAL_LINES=20;
	private int headerSize, bodySize, footerSize;
	private String header, body, footer;
	
	public Card(int h, int f){
		headerSize=h;header = "";
		footerSize=f;footer = "";
		bodySize = TOTAL_LINES-h-f-2;
		body="";
		this.setText("");
		this.setRows(20);
	}
	
	@Override
	public String writeTo()
	{
		this.setText(header + body + footer);
		System.out.println(header + body + footer);
		return "RACE";
	}
	
	public void setHeader(Queue<Racer> newHeader) {
		//don't care about times for queue, right?
		header = "";
		for (Racer r: newHeader)
			header = "Racer["+r.getNumber()+"]\n" +header;
		
		this.replaceRange(header, 0, headerSize-1);
	}

	public void setBody(Queue<Racer> newBody) throws InvalidTimeException{
		body = "";
		for (Racer r: newBody)
			body = "Racer["+r.getNumber()+"] "+r.getElapsedTime()+"\n" +body;
		
		this.replaceRange(body, headerSize+1, headerSize+bodySize);
	}

	public void setFooter(Queue<Racer> newFooter) throws InvalidTimeException{
		footer = "";
		for (Racer r: newFooter)
			footer = "Racer["+r.getNumber()+"] "+r.getElapsedTime()+"\n" +footer;
		
		this.replaceRange(footer, headerSize+bodySize+2, headerSize+bodySize+footerSize+1);
	}

	@Override
	public void up() {
		//until we decide to do something with these, these are really just for the menu class
	}

	@Override
	public void down() {
		//until we decide to do something with these, these are really just for the menu class
	}
}
