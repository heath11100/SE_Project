package ChronoTimer;

import java.util.ArrayList;

import javax.swing.JTextArea;

import GUI.Handler.guis;

public abstract class UIPrint extends JTextArea{
	public abstract String writeTo();
	public abstract void up();
	public abstract void down();
}
