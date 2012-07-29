package bb.server.network;

import bb.common.exception.NetworkingException;
import bb.common.network.BaseWrapper;
import bb.common.network.NetworkPair;

public class ServerNetworkingWrapper extends BaseWrapper {

	@Override
	public void init(NetworkPair ipAndPort) throws NetworkingException {
		// We are given the credentials for the server's TCP conection. Attempt to connect.
		manager_ = ServerNetworkingManager.getInstance();
		ServerNetworkingManager snm = (ServerNetworkingManager) manager_;
		// Ignore the IP from the argument, but the port is the listening port
		snm.setListeningPort(ipAndPort.getPort());
		if (!snm.setupSockets()) {
			// Popup some sort of error splash screen, because the server connection ain't working
			throw new NetworkingException("Setup of listening socket failed");
		}
		snm.init();
	}
}
