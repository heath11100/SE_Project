package ChronoTimer;

public abstract class UIPrint{
	private String textArea;
	
	
	public void setText(String write)
	{
		textArea = write;
	}
	
	public void append(String write)
	{
		textArea += write;
	}
	
	public String getText()
	{
		return textArea;
	}
	
	public abstract String writeTo();
	public abstract void up();
	public abstract void down();
}
