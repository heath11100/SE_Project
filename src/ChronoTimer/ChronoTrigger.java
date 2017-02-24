package ChronoTimer;
	 /**__    __     ______     ______   ______      _____     ______     __    __     ______     __   __     ______                     
	/\ "-./  \   /\  __ \   /\__  _\ /\__  _\    /\  __-.  /\  __ \   /\ "-./  \   /\  __ \   /\ "-.\ \   /\  ___\                    
	\ \ \-./\ \  \ \  __ \  \/_/\ \/ \/_/\ \/    \ \ \/\ \ \ \  __ \  \ \ \-./\ \  \ \ \/\ \  \ \ \-.  \  \ \___  \                   
	 \ \_\ \ \_\  \ \_\ \_\    \ \_\    \ \_\     \ \____-  \ \_\ \_\  \ \_\ \ \_\  \ \_____\  \ \_\\"\_\  \/\_____\                  
	  \/_/  \/_/   \/_/\/_/     \/_/     \/_/      \/____/   \/_/\/_/   \/_/  \/_/   \/_____/   \/_/ \/_/   \/_____/                  
																																	
	 ______     ______     ______     __   __     ______        ______   ______     __     ______     ______     ______     ______    
	/\  ___\   /\  == \   /\  __ \   /\ "-.\ \   /\  __ \      /\__  _\ /\  == \   /\ \   /\  ___\   /\  ___\   /\  ___\   /\  == \   
	\ \ \____  \ \  __<   \ \ \/\ \  \ \ \-.  \  \ \ \/\ \     \/_/\ \/ \ \  __<   \ \ \  \ \ \__ \  \ \ \__ \  \ \  __\   \ \  __<   
	 \ \_____\  \ \_\ \_\  \ \_____\  \ \_\\"\_\  \ \_____\       \ \_\  \ \_\ \_\  \ \_\  \ \_____\  \ \_____\  \ \_____\  \ \_\ \_\ 
	  \/_____/   \/_/ /_/   \/_____/   \/_/ \/_/   \/_____/        \/_/   \/_/ /_/   \/_/   \/_____/   \/_____/   \/_____/   \/_/ /_/ 
	*/
import java.util.ArrayList;

public class ChronoTrigger 
{
	private ArrayList raceList;
	private Time officialTime;
	private ArrayList channelList;
	
	public ChronoTrigger()
	{
		
	}
	//setup that allows you to set the Official Time
	public ChronoTrigger(Time officialTime)
	{
		
	}
	//sets time
	public void setTime(Time newTime)
	{
		
	}
	//toggles channel
	public void toggle(int channel)
	{
		
	}
	//connects sensor to channel
	public void connectSensor(int eye, int gate, int pad, int channel)
	{
		
	}
	//disconnects sensor
	public void disSensor(int channel)
	{
		
	}
	public void start(int channel)
	{
		
	}
	public void finish(int channel)
	{
		
	}
	//competitor did not finish
	public void didNotFinish(int competitor)
	{
		
	}
	//cancels run
	public void cancelRun(int channel, int runner)
	{
		
	}
	//returns officialTime
	public Time getTime()
	{
		return this.officialTime;
	}
	//resets the time, racelist, and channel list
	public void reset()
	{
		
	}
	//ends chronotrigger
	public void exit()
	{
		
	}
	
}
