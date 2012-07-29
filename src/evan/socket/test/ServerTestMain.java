package evan.socket.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServerTestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			DatagramSocket sock = new DatagramSocket(9087);
			byte[] buf = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			System.out.println("UDP packet created. Listening for UDP...");
			sock.receive(packet);
			String response = new String(buf);
			System.out.println("Received: " + response);
			
			String clientAddr = response.substring(0, response.indexOf(':'));
			int clientPort = Integer.parseInt(response.substring(response.indexOf(':') + 1).trim());
			InetAddress packetAddr = packet.getAddress();
			InetAddress returnAddr = InetAddress.getByName(clientAddr);
			System.out.println("Return address is " + returnAddr.getCanonicalHostName());
			System.out.println("Packet address is " + packetAddr.getCanonicalHostName());
			System.out.println("Return port is " + clientPort);
			System.out.println("Packet port is " + packet.getPort());
			System.out.println("Old sock remote addr is " + sock.getRemoteSocketAddress());
			
			DatagramSocket anotherSock = new DatagramSocket();
			anotherSock.connect(packetAddr, clientPort);
			System.out.println("sock connected to: " + sock.getRemoteSocketAddress());
			System.out.println("anotherSock connected to: " + anotherSock.getRemoteSocketAddress());
			String newMsg = "ACK: " + response;
			buf = newMsg.getBytes();
			DatagramPacket newPacket = new DatagramPacket(buf, buf.length, packet.getAddress(), clientPort);
			System.out.print("Sending response to " + returnAddr.getHostAddress() + "...");
			anotherSock.send(newPacket);
			System.out.println("DONE");
			
			sock.close();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}