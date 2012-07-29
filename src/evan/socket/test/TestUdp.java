package evan.socket.test;

import java.net.DatagramSocket;

import bb.common.network.ProtocolStrategy;
import bb.common.network.SocketWrapper;
import bb.common.network.UdpStrategy;
import bb.common.network.message.RegistrationMessage;
import bb.common.util.Utility;

public class TestUdp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			// Setup listener first
			DatagramSocket inSock = new DatagramSocket(9009);
			System.out.println("DEBUG: receiving buffer size is " + inSock.getReceiveBufferSize());
			SocketWrapper inWrap = new SocketWrapper(inSock);
			
			// Setup sender
			DatagramSocket outSock = new DatagramSocket();
			System.out.println("DEBUG: sending buffer size is " + outSock.getSendBufferSize());
			SocketWrapper outWrap = new SocketWrapper(outSock, "127.0.0.1", 9009);
			ProtocolStrategy strategy = new UdpStrategy();

			// Test send a Registration Message over the socket
			RegistrationMessage outMsg = new RegistrationMessage();
			/* Fix the outMsg before sending again */
			System.out.println("Size of message in bytes is " + Utility.marshal(outMsg).length);
			strategy.send(outWrap, outMsg);
			System.out.println("Sent");
			
			// Test receive
			RegistrationMessage inMsg = (RegistrationMessage) strategy.recv(inWrap);
			System.out.println("Received:\n" + inMsg.getClientUdpIp() + ", " + inMsg.getClientUdpPort());
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
