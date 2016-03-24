package common;

import java.rmi.RemoteException;

public class ServerException extends RemoteException {

	/**
	 * Version ID required b/c serializable
	 */
	private static final long serialVersionUID = 1L;

	public ServerException(String string) {
		super(string);
	}
}
