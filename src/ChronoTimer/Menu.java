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
	
	int position;
	ArrayList<String> curList; 
	
	final String RACEOFF = "NEWRUN";
	
	public Menu(boolean race)
	{
		curList = new ArrayList();
		if(race)
			for(String s: RACEON)
				curList.add(s);
		else
			curList.add(RACEOFF);
		for(String s: ALWAYSFUNCTION)
			curList.add(s);
		position = 0;
	}
	
	@Override
	public String writeTo() 
	{
		String display = "";
		String toReturn = "FAIL";
		for(int i = 0; i < curList.size(); i++)
		{
			if(i == position)
			{
				display += ">";
				toReturn = curList.get(i);
			}
			display += curList.get(i) + "\n";//not sure how to start new line?				
		}
		return toReturn;
	}


	@Override
	public void up() {
		// TODO Auto-generated method stub
		position = (position + 1) % curList.size();
	}


	@Override
	public void down() {
		// TODO Auto-generated method stub
		position--;
		if(position < 0)
			position = curList.size() - 1;
	}

}
