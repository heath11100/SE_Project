package ChronoTimer;

public class NamedRacer{
	private Racer myRacer;
	private String lastName, firstInitial;
	
	public NamedRacer(Racer r, String l, String f){
		myRacer = r;
		lastName = l;
		firstInitial = f;}

	public Racer getMyRacer() {return myRacer;}
	public String getLastName() {return lastName;}
	public String getFirstInitial() {return firstInitial;}
}