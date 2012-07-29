package bb.common.network;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import bb.common.exception.NetworkingException;

/**
 * Because Java's Socket and DatagramSocket are two completely different objects
 * (not sure why, it's a bad language design), then we need some sort of abstraction
 * in order to represent either-or in one single object.
 */
public class SocketWrapper {
	
	// Only one of these sockets will be non-null. Up to the programmer to use the wrapper correctly.
	private Socket tcpSocket_ = null;
	private DatagramSocket udpSocket_ = null;
	// We need to store the credentials to the other machine for UDP
	private String ipAddr_ = null;
	private Integer port_ = null;
	private boolean isClosed = false;
	
	/**
	 * Constructor for TCP
	 *
	 * @param sock the socket
	 */
	public SocketWrapper(Socket sock) {
		tcpSocket_ = sock;
	}
	
	/**
	 * Constructor for UDP
	 *
	 * @param sock the socket
	 */
	public SocketWrapper(DatagramSocket sock) {
		udpSocket_ = sock;
	}
	
	/**
	 * Constructor for UDP, when we also need to add in the credentials for the other machine
	 *
	 * @param dSock the datagram sock
	 * @param i the ip
	 * @param p the port
	 */
	public SocketWrapper(DatagramSocket dSock, String i, int p) {
		udpSocket_ = dSock; 
		this.ipAddr_ = i;
		this.port_ = p;
	}
	
	// Both of the following getters will always return a value. It is up to the coder to ensure that he is getting the correct socket.
	// NOTE: Design decision is to disallow the returning of null values, via an exception
	public Socket getTcpSocket() throws NetworkingException {
		if (tcpSocket_ == null) {
			throw new NetworkingException("Attempted to use a null TCP socket");
		}
		return tcpSocket_;
	}
	
	public DatagramSocket getUdpSocket() throws NetworkingException {
		if (udpSocket_ == null) {
			throw new NetworkingException("Attempted to use a null UDP socket");
		}
		return udpSocket_;
	}
	
	/**
	 * Closes the underlying socket and all its resources.
	 */
	public synchronized void closeSocket() {
		if (!isClosed) {
			isClosed = true;
			try {
				if (tcpSocket_ != null) tcpSocket_.close();
			} catch (IOException e) {
				// Ignore exceptions
			}
			if (udpSocket_ != null) udpSocket_.close();
		}
	}

	public String getIpAddr() {
		return ipAddr_;
	}
	
	public InetAddress getIpInet() throws UnknownHostException {
		return InetAddress.getByName(ipAddr_);
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr_ = ipAddr;
	}

	public Integer getPort() {
		return port_;
	}

	public void setPort(Integer port) {
		this.port_ = port;
	}
}
