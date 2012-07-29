package bb.common.network;

import java.io.Serializable;

public class NetworkPair implements Serializable {
	private static final long serialVersionUID = -463636864667690513L;
	
	private String ipAddr;
	private int port;
	public NetworkPair(String ipAddr, int port) {
		this.ipAddr = ipAddr;
		this.port = port;
	}
	public String getIpAddr() {
		return ipAddr;
	}
	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	@Override
	public String toString() {
		return new StringBuffer("NetworkPair [ipAddr=").append(ipAddr)
				.append(", port=").append(port).append("]").toString();
	}
	
	
}
