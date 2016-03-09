package common;

import java.rmi.RemoteException;

public class FailException extends RemoteException {

	public FailException(String string) {
		super(string);
	}
}
