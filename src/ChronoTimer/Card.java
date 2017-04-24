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
		//this.setRows(20);
		//comment this cus no longer needed?
	}
	
	@Override
	public String writeTo()
	{
		refresh();
		//this.setText(header + body + footer);
		//System.out.println(header + body + footer);
		return "RACE";
	}

	public void setHeader(String string) {
		header = string;
		refresh();
		//this.replaceRange(header, 0, headerSize-1);
	}
	
	public void setHeader(Queue<Racer> newHeader) {
		//don't care about times for queue, right?
		header = "";
		for (Racer r: newHeader)
			header = r.toString() + "\n" + header;
		refresh();
		//this.replaceRange(header, 0, headerSize-1);
	}

	public void setBody(Queue<Racer> newBody){
		body = "";

		for (Racer r : newBody) {
			try {
				ChronoTime elapsedTime = r.getElapsedTime();

				body = r.toString() + elapsedTime + "\n" + body;
			} catch (InvalidTimeException e) {
				//Then the Racer will not be added.
				//We should never reach this point anyways.
			}
		}
		refresh();
		//this.replaceRange(body, headerSize+1, headerSize+bodySize);
	}

	public void setFooter(String string) {
		footer = string;
		refresh();
		//this.replaceRange(footer, headerSize+bodySize+2, headerSize+bodySize+footerSize+1);
	}

	private void refresh() {
		this.setText(header);
		this.append("\n\n" + body);
		this.append("\n\n" + footer);
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
