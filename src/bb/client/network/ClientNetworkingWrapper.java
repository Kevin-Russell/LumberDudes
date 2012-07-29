package bb.client.network;

import bb.common.exception.NetworkingException;
import bb.common.network.BaseWrapper;
import bb.common.network.NetworkPair;

public class ClientNetworkingWrapper extends BaseWrapper {
	
	public void init(NetworkPair ipAndPort) throws NetworkingException {
		// We are given the credentials for the server's TCP conection. Attempt to connect.
		manager_ = ClientNetworkingManager.getInstance();
		ClientNetworkingManager cnm = (ClientNetworkingManager) manager_;
		cnm.setServerTcp(ipAndPort);
		if (!cnm.setupSockets()) {
			// Popup some sort of error splash screen, because the server connection ain't working
			throw new NetworkingException("TCP connection to server failed");
		}
		cnm.init();
	}

}
