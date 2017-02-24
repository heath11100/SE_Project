package ChronoTimer;
@SuppressWarnings("serial")
class InvalidCommandException extends Exception
{
	// setting a default constructor will take a message when it reports to the console
	public InvalidCommandException(String message){ super(message);}
	//lol documentation
	public InvalidCommandException(Throwable cause){ super(cause);}
	//why even description
	public InvalidCommandException(String message, Throwable cause){ super(message, cause);}
	//something something Matt Damon
}
