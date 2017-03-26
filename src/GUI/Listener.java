package GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Listener implements ActionListener{
	private Handler handler;
	private int cur;
	private String[] commands;
	
	public Listener(Handler handler, String command){
		super();
		this.handler=handler;
		cur=0;
		this.commands=new String[]{command};}
	
	public Listener(Handler handler,String[] commands){
		super();
		this.handler=handler;
		cur=0;
		this.commands=commands;}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (handler.issue(commands[cur]) && ++cur >= commands.length)
			cur=0;}
}
