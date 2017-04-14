package Exceptions;

@SuppressWarnings("serial")
public class InvalidTimeException extends Exception{
	public InvalidTimeException(String message){ super(message);}
	public InvalidTimeException(Throwable cause){ super(cause);}
	public InvalidTimeException(String message, Throwable cause){ super(message, cause);}
}