package Exceptions;

@SuppressWarnings("serial")
public class RaceException extends Exception
{
	// setting a default constructor will take a message when it reports to the console
	public RaceException(String message){ super(message);}
	//lol documentation
	public RaceException(Throwable cause){ super(cause);}
	//why even description
	public RaceException(String message, Throwable cause){ super(message, cause);}
	//something something Matt Damon
}

