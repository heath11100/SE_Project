package GUI;

public class Handler {
	GUI master;
	public Handler(GUI master){
		this.master=master;
	}
	
	protected boolean issue(String command){
		master.print(command);
		return true;
	}
}
