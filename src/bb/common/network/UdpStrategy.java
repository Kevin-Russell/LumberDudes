package bb.common.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import bb.common.exception.MarshalUnmarshalException;
import bb.common.exception.NetworkingException;
import bb.common.network.message.Message;
import bb.common.util.Utility;

public class UdpStrategy implements ProtocolStrategy {
	
	private static int MAX_MESSAGE_SIZE = 65000;
	
	public void send(SocketWrapper sock, Message msg) throws NetworkingException {
		try {
			DatagramSocket udpSocket = sock.getUdpSocket();
			// TODO: Maybe insert the UDP sequence number here??? Or somewhere...probably depends on the entity id
			
			// Append the bytes for the length to the beginning of the bytes for the message
			byte[] msgBytes = Utility.marshal(msg);
			// Now send it all at once in one packet. Hopefully we can split up the packet into two later on when we receive. This needs to be tested.
			DatagramPacket outPacket = new DatagramPacket(msgBytes, msgBytes.length,
					sock.getIpInet(), sock.getPort());
			udpSocket.send(outPacket);
		} catch (MarshalUnmarshalException e) {
			e.printStackTrace();
			throw new NetworkingException(e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			throw new NetworkingException(e.toString());
		}
	}

	public Message recv(SocketWrapper sock) throws NetworkingException, IOException {
		try {
			DatagramSocket udpSocket = sock.getUdpSocket();
			byte[] msgBytes = new byte[MAX_MESSAGE_SIZE];
			DatagramPacket msgPacket = new DatagramPacket(msgBytes, msgBytes.length);
			udpSocket.receive(msgPacket);
			return (Message)Utility.unmarshal(msgBytes);			
		} catch (MarshalUnmarshalException e) {
			e.printStackTrace();
			throw new NetworkingException(e.toString());
		}
	}

}
