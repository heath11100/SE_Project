package ChronoTimer;
import java.util.Queue;

public class Card extends UIPrint {
	private String header, body, footer;
	
	public Card(){
		header = "";
		footer = "";
		body="";
		this.setText("");
	}
	
	@Override
	public String writeTo()
	{
		refresh();
		return "RUN";
	}

	public void setHeader(String string) {
		header = string;
		refresh();
	}
	
	public void setHeader(Queue<Racer> newHeader) {
		header = "";
		for (Racer r: newHeader)
			header = r.toString() + "\n" + header;
		refresh();
	}

	public void setBody(String newBody) {
		body = newBody;
		refresh();
	}

	public void setFooter(String string) {
		footer = string;
		refresh();
	}

	private void refresh() {
		this.setText(header);
		this.append("\n\n" + body);
		this.append("\n\n" + footer);
	}

	@Override
	public void up() { }

	@Override
	public void down() { }
}
