package ChronoTimer;

import java.util.ArrayList;

import GUI.Handler.guis;

public class Menu extends UIPrint {

	final String RACEON[] = 
		{
			"NUM",
			"CLEAR",
			"CANCEL",
			"DNF",
			"ENDRUN"
		};  
	
	final String ALWAYSFUNCTION[] = 
		{
			"EVENT",
			"PRINT",
			"EXPORT",
			"RESET"
		};
	
	final String RACEOFF = "NEWRUN";
	

	int position;
	ArrayList<String> curList; 
	
	
	public Menu(boolean race)
	{
		curList = new ArrayList<String>();
		if(race)
			for(String s: RACEON)
				curList.add(s);
		else
			curList.add(RACEOFF);
		for(String s: ALWAYSFUNCTION)
			curList.add(s);
		position = 0;
		this.setText("");
		this.setRows(20);
	}
	
	//TODO make this more efficient, try not to rebuild the string every time
	@Override
	public String writeTo() 
	{
		String display = "";
		String toReturn = "FAIL";
		for(int i = 0; i < curList.size(); i++)
		{
			if(i == position)
			{
				display += "> ";
				toReturn = curList.get(i);
			}
			display += curList.get(i) + "\n";//not sure how to start new line?				
		}
		this.setText(display);
		//System.out.println(display + "trialtrialtrial ");
		return toReturn;
	}


	@Override
	public void up() {
		// TODO Auto-generated method stub
		position--;
		if(position < 0)
			position = curList.size() - 1;
	}


	@Override
	public void down() {
		// TODO Auto-generated method stub
		position = (position + 1) % curList.size();
	}

}
