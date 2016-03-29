package common;

import java.rmi.RemoteException;

/**
 * Class for non fatal exceptions in the server, such as
 * incorrect login. These errors are printed by the client,
 * but the client continues.
 */
public class ServerException extends RemoteException {

	/**
	 * Version ID required b/c serializable
	 */
	private static final long serialVersionUID = 1L;

	public ServerException(String string) {
		super(string);
	}
}
