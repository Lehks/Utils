package exception;

@SuppressWarnings("serial")
public class IllegalDepthException extends Exception
{
	public IllegalDepthException()
	{
		super();
	}

	public IllegalDepthException(String arg0)
	{
		super(arg0);
	}

	public IllegalDepthException(Throwable arg0)
	{
		super(arg0);
	}

	public IllegalDepthException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

	public IllegalDepthException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3)
	{
		super(arg0, arg1, arg2, arg3);
	}

}
