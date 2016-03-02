package common;

import java.rmi.RemoteException;

public class ServerException extends RemoteException {

	public ServerException(String string) {
		super(string);
	}
}
