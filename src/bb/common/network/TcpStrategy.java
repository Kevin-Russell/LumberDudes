package bb.common.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import bb.common.exception.ConnectionClosedException;
import bb.common.exception.MarshalUnmarshalException;
import bb.common.exception.NetworkingException;
import bb.common.network.message.Message;
import bb.common.util.Utility;

public class TcpStrategy implements ProtocolStrategy {

	public void send(SocketWrapper sock, Message msg) throws NetworkingException, IOException {
		/*
		 * Marshal the message into bytes
		 * Send the length
		 * Send the bytes
		 * Throw exception if connection is down or something
		 */
		try {
			byte[] bytes = Utility.marshal(msg);
			Socket tcpSocket = sock.getTcpSocket();
			OutputStream outStream = tcpSocket.getOutputStream();
			byte[] lenBytes = Utility.intToBytes(bytes.length);
			outStream.write(lenBytes);
			outStream.write(bytes);
		} catch (MarshalUnmarshalException e) {
			e.printStackTrace();
			throw new NetworkingException(e.toString());
		}
		
	}

	public Message recv(SocketWrapper sock) throws NetworkingException, IOException {
		/*
		 * Read the length
		 * Loop-read until all bytes have been read (rely on offset)
		 * Unmarshal into Message
		 */
		final int SIZE_LEN = 4;
		try {
			Socket tcpSocket = sock.getTcpSocket();
			InputStream inStream = tcpSocket.getInputStream();
			byte[] msgLenBytes = new byte[SIZE_LEN];
			int msgLenOffset = 0;
			while (msgLenOffset < SIZE_LEN) {
				int count = inStream.read(msgLenBytes, msgLenOffset, SIZE_LEN-msgLenOffset);
				if (count == -1) {
					throw new ConnectionClosedException("Connection close detected during recv()");
				}
				msgLenOffset += count;
			}
			int msgLen = Utility.bytesToInt(msgLenBytes);
			byte[] bytes = new byte[msgLen];
			int offset = 0;
			while (offset < msgLen) {
				int count = inStream.read(bytes, offset, msgLen-offset);
				if (count == -1) {
					throw new ConnectionClosedException("Connection close detected during recv()");
				}
				offset += count;
			}
			return (Message)Utility.unmarshal(bytes);
		} catch (MarshalUnmarshalException e) {
			e.printStackTrace();
			throw new NetworkingException(e.toString());
		}
	}

}