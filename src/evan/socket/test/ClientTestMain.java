package evan.socket.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ClientTestMain {
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			System.out.println("Now let's test Datagram. Creating socket...");
			DatagramSocket sock = new DatagramSocket();
			System.out.println("My local port is " + sock.getLocalPort());
			System.out.println("UDP create done.");
			System.out.println("Sending my name...");
			byte[] data = (InetAddress.getLocalHost().getHostAddress() + ":" + Integer.toString(sock.getLocalPort())).getBytes();
			
			InetAddress addr = InetAddress.getByName("127.0.0.1");
			DatagramPacket packet = new DatagramPacket(data, data.length, addr, 9087);
			sock.send(packet);
			System.out.println("DONE. Check the server to see if it was received.");
			
			System.out.println("clientSocket connected: " + sock.isConnected());
			
			DatagramSocket anotherSock = new DatagramSocket(11001);
			System.out.println("sock connected to: " + sock.getRemoteSocketAddress());
			System.out.println("anotherSock connected to: " + anotherSock.getRemoteSocketAddress());
			byte[] buf = new byte[256];
			packet = new DatagramPacket(buf, buf.length);
			System.out.println("Listening for UDP response...");
			sock.receive(packet);
			System.out.println("Response from server received: " + new String(buf));
			sock.close();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}