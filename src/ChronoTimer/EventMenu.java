package ChronoTimer;

public class EventMenu extends UIPrint {

	final String TYPES[] = 
		{
			"IND",
			"PARIND",
			"GRP",
			"PARGRP"
		};
	
	String display;
	int position;
	int newPosition;
	
	public EventMenu()
	{
		position = 0;
		updateDisplay();
	}
	
	@Override
	public String writeTo() {
		if(newPosition != position)
			updateDisplay();
		return TYPES[position];
	}
	
	private void updateDisplay()
	{
		display = "";
		position = newPosition;
		for(int i = 0; i < TYPES.length; i++)
		{
			if(i == position)
				display += "> ";
			display += TYPES[i] + "\n";//not sure how to start new line?
		}
		this.setText(display);
	}

	@Override
	public void up() {
		// TODO Auto-generated method stub
		newPosition--;
		if(newPosition < 0)
			newPosition = TYPES.length - 1;
	}

	@Override
	public void down() {
		// TODO Auto-generated method stub
		newPosition = (newPosition + 1) % TYPES.length;
	}

}
