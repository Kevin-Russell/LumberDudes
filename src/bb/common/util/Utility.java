package bb.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.List;

import bb.common.EntityInfo;
import bb.common.exception.MarshalUnmarshalException;

/**
 * Static class that contains general, utility functions
 */
public class Utility {
    public static long getTick() {
        return System.currentTimeMillis();
    }

	public static byte[] marshal(Serializable obj) throws MarshalUnmarshalException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw new MarshalUnmarshalException();
		}
	}

	public static Object unmarshal(byte[] bytes) throws MarshalUnmarshalException {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
			throw new MarshalUnmarshalException();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new MarshalUnmarshalException();
		}
	}

	/*
	 * intToBytes() and bytesToInt() taken/influenced from:
	 * http://stackoverflow.com/questions/2183240/java-integer-to-byte-array
	 */
	public static byte[] intToBytes(int value) {
		ByteBuffer b = ByteBuffer.allocate(4);
		//b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
		b.putInt(value);

		return b.array();
	}

	public static int bytesToInt(byte[] bytes) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.put(bytes);
		return bb.getInt(0);
	}
	
	// Determine if an entity info is inside a list based on id
	public static boolean contains(List<EntityInfo> list, EntityInfo searchInfo) {
		for (EntityInfo info : list) {
			if (info.getId() == searchInfo.getId()) {
				return true;
			}
		}
		return false;
	}
}
