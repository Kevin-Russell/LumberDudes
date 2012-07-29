package bb.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import bb.common.exception.PropertiesException;
import bb.common.network.ProtocolStrategy.NetworkingStrategyEnum;


/** GameProperties allows us to load from the properties file into this system's apps. This is a Singleton.
 */
public class GameProperties extends Properties {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1289680464538468263L;
	
	/** The "cache" for saved value from the .properties file, so we don't need to call getProperty() twice. */
	private static Map<String, String> cache_;
	
	/** Singleton reference to this class. */
	private static GameProperties singleton_ = null;
	
	// Keys in the .properties file
	/** The Constant SERVER_TCP_ADDR. */
	private final static String SERVER_TCP_ADDR = "server.tcp.addr";
	
	/** The Constant SERVER_TCP_PORT. */
	private final static String SERVER_TCP_PORT = "server.tcp.port";
	
	/** The Constant NUM_PLAYERS. */
	private final static String NUM_PLAYERS = "num.players";
	
	/** The Constant SOCKET_STRATEGY. */
	private final static String SOCKET_STRATEGY = "socket.strategy";
	
	/**
	 * Private constructor; only use static class methods.
	 */
	private GameProperties() {
		cache_ = new HashMap<String, String>();
	}
	
	/**
	 * Gets the single instance of GameProperties.
	 *
	 * @return single instance of GameProperties
	 */
	public static GameProperties getInstance() {
		if (singleton_ == null) {
			singleton_ = new GameProperties();
		}
		return singleton_;
	}
	
	/**
	 * Inits the GameProperties.
	 *
	 * @param filepath the filepath
	 * @throws IOException 
	 */
	public void init(String filepath) throws IOException {
		FileInputStream in = new FileInputStream(filepath);
		this.load(in);
		in.close();
	}
	
	
	/**
	 * Gets the server tcp addr.
	 *
	 * @return the server tcp addr
	 * @throws PropertiesException the properties exception
	 */
	public String getServerTcpAddr() throws PropertiesException {
		return cacher(SERVER_TCP_ADDR);
	}
	
	/**
	 * Gets the server tcp port.
	 *
	 * @return the server tcp port
	 * @throws PropertiesException the properties exception
	 */
	public int getServerTcpPort() throws PropertiesException {
		try {
			return Integer.parseInt(cacher(SERVER_TCP_PORT));
		} catch (NumberFormatException nfe) {
			throw new PropertiesException("Non numeric data found for property that expects one: " + SERVER_TCP_PORT);
		}
	}
	
	/**
	 * Gets the num players.
	 *
	 * @return the num players
	 * @throws PropertiesException the properties exception
	 */
	public int getNumPlayers() throws PropertiesException {
		try {
			return Integer.parseInt(cacher(NUM_PLAYERS));
		} catch (NumberFormatException nfe) {
			throw new PropertiesException("Non numeric data found for property that expects one: " + NUM_PLAYERS);
		}
	}
	
	/**
	 * Gets the socket strategy.
	 *
	 * @return the socket strategy
	 * @throws PropertiesException the properties exception
	 */
	public NetworkingStrategyEnum getSocketStrategy() throws PropertiesException {
		final String TCP = "tcp";
		final String UDP = "udp";
		String strat = cacher(SOCKET_STRATEGY);
		if (strat.equals(TCP)) {
			return NetworkingStrategyEnum.TCP_S;
		} else if (strat.equals(UDP)) {
			return NetworkingStrategyEnum.UDP_S;
		} else {
			throw new PropertiesException("Null or invalid socket strategy set in the properties file for " + SOCKET_STRATEGY);
		}
	}
	
	/**
	 * Query the cache for a property, or set the property (accessed from the properties file) if not found yet.
	 *
	 * @param prop the prop
	 * @return the string
	 * @throws PropertiesException 
	 */
	private String cacher(String prop) throws PropertiesException {
		String cachedObj = cache_.get(prop);
		if (cachedObj == null) {
			String propObj = myGetProperty(prop);
			cache_.put(prop, propObj);
			return propObj;
		} else {
			return cachedObj;
		}
	}
	
	/**
	 * My get property.
	 *
	 * @param p the p
	 * @return the string
	 * @throws PropertiesException the properties exception
	 */
	private String myGetProperty(String p) throws PropertiesException {
		if (getProperty(p) == null) {
			throw new PropertiesException("No data found for property: " + p);
		}
		return getProperty(p);
	}
	
}
