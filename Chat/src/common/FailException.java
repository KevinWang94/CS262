package common;

import java.rmi.RemoteException;

/**
 * Class for fatal exceptions, such as user submitting an invalid 
 * session ID. These exceptions cause the client to exit.
 */
public class FailException extends RemoteException {

	/**
	 * Version ID required b/c serializable
	 */
	private static final long serialVersionUID = 1L;

	public FailException(String string) {
		super(string);
	}
}
