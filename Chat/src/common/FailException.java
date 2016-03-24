package common;

import java.rmi.RemoteException;

public class FailException extends RemoteException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FailException(String string) {
		super(string);
	}
}
