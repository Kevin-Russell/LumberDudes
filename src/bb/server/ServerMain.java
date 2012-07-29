package bb.server;

import java.io.File;
import java.io.IOException;

import bb.common.GameProperties;
import bb.common.exception.PropertiesException;

public class ServerMain {
	
	public static void main(String[] args) throws IOException {
		// First, setup system properties.
		String propertiesFilepath = null;
    	final String DEFAULT_PROP = "resources/startup.properties";
    	if (args.length != 1) {
    		if (args.length == 0) {
    			System.out.println("Using default properties file of \"" + DEFAULT_PROP + "\"");
    			propertiesFilepath = DEFAULT_PROP;
    		} else {
    			System.err.println("ERROR: Illegal number of startup arguments.");
    			System.err.println("Expected one argument: [filepath to .properties file]");
    			System.exit(1);
    		}
    	} else {
    		propertiesFilepath = args[0];
    	}
    	File propertiesFile = new File(propertiesFilepath);
    	if (!propertiesFile.exists()) {
            System.err.println("Properties file does not exist.");
            System.exit(1);
        }

    	GameProperties props = GameProperties.getInstance();
    	props.init(propertiesFilepath);

        int serverTcpPort = -1;
        int numPlayers = -1;
        try {
        	serverTcpPort = props.getServerTcpPort();
        	numPlayers = props.getNumPlayers();
        	if (serverTcpPort <= 0 || numPlayers <= 0) {
        		throw new PropertiesException("Illegal value for a property");
        	}
        } catch (PropertiesException pe) {
            System.err.println("Error setting properties. Retry setting the properties file and try running the client again.");
            pe.printStackTrace();
            System.exit(1);
        }
		
        // Now let's play!
		try {
			Game g = new Game();
			g.init(serverTcpPort, numPlayers);
			g.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error! Uncaught exception");
		}
	}
	
}
