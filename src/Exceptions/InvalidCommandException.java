package Exceptions;
@SuppressWarnings("serial")
public class InvalidCommandException extends Exception
{
	public InvalidCommandException(String message){ super(message);}
	public InvalidCommandException(Throwable cause){ super(cause);}
	public InvalidCommandException(String message, Throwable cause){ super(message, cause);}
}
