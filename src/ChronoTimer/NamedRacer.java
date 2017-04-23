package ChronoTimer;

import Exceptions.InvalidTimeException;

public class NamedRacer implements Comparable<NamedRacer>{
	private Racer myRacer;
	private String lastName, firstInitial;
	
	public NamedRacer(Racer r, String l, String f){
		myRacer = r;
		lastName = l;
		firstInitial = f;}

	public Racer getMyRacer() {return myRacer;}
	public String getLastName() {return lastName;}
	public String getFirstInitial() {return firstInitial;}

	@Override
	public int compareTo(NamedRacer o) {
		if (o instanceof NamedRacer) {
			NamedRacer other = (NamedRacer) o;
			try {
				return (myRacer.getElapsedTime().isBefore(other.getMyRacer().getElapsedTime()))?-1:1;
			} catch (InvalidTimeException e) {}
		}
		return 0;
	}
}