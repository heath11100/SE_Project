package Exceptions;

@SuppressWarnings("serial")
public class RaceException extends Exception
{
	public RaceException(String message){ super(message);}
	public RaceException(Throwable cause){ super(cause);}
	public RaceException(String message, Throwable cause){ super(message, cause);}
}

