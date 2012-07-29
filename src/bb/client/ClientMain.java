package bb.client;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import bb.client.entities.OptionsEntity;
import bb.client.network.ClientNetworkingWrapper;
import bb.client.painter.Painter;
import bb.common.ActionType;
import bb.common.EntityIterator;
import bb.common.EntityVector;
import bb.common.GameProperties;
import bb.common.exception.NetworkingException;
import bb.common.exception.PropertiesException;
import bb.client.input.InputManager;
import bb.client.input.InputManager.Button;
import bb.client.input.InputMemento;
import bb.common.network.NetworkPair;
import bb.common.network.message.ActionMessage;
import bb.common.network.message.EntityBroadcastMessage;
import bb.common.network.message.Message;
import bb.common.network.message.Message.MsgType;
import bb.common.network.message.RegistrationMessage;
import bb.common.network.message.RegistrationResultMessage;
import bb.common.network.message.StartGameMessage;
import bb.common.network.message.TerminateMessage;
import bb.common.util.Utility;

/*
 * Client Main
 * This file is used to start the client side application
 * 
 * An entityVector is created for the type optionsEntity (which is a class derived from paintEntity)
 * And then an iterator is retrieved for the vector
 * 
 * The properties file is checked which will initialize addresses and ports needed for connecting to the server
 * this is designed so that the server and port can be changed without having to recompile
 * 
 * A Painter is created so that we can use all of its functions
 * (bb.client.painter.Painter.java)
 * An input manager is also created to handle inputs some checks are done here in case the client previously bound keys to a controller
 * (bb.common.input.InputManager.java)
 * A ClickTests is created to detect and handle for clicks on the splash screen and options (key binding screen)
 * (bb.client.ClickTests.java)
 * 
 * From here the client will enter a loop checking for which state the client is in (splash screen, options screen, playing the game, etc)
 * When in the game state the client will 
 * -poll for user input
 * -listen for broadcast messages
 * -repaint
 * 
 */

public class ClientMain {
	public static EntityVector<OptionsEntity> optionEntities = new EntityVector<OptionsEntity>();
	public static EntityIterator<OptionsEntity> optionsIterator = optionEntities.createIterator();
	public static InputManager input = null;
	private static InputMemento m = null;

	enum State{
		SPLASH, OPTIONS, GAME, WIN, LOSE, CONNECTING, WAIT, EXIT
	};
	public enum BoxColor{
		GREEN, RED, ORANGE, BLUE;
	};

	public static Painter myPainter;

	public static void main(String[] args) throws IOException, LWJGLException, NetworkingException, InterruptedException {
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

		String serverTcpAddr = null;
		int serverTcpPort = -1;
		try {
			serverTcpAddr = props.getServerTcpAddr();
			serverTcpPort = props.getServerTcpPort();
			if (serverTcpAddr == null || serverTcpAddr.isEmpty() || serverTcpPort <= 0) {
				throw new PropertiesException("Illegal value for a property");
			}
			// We also need to query for the socket strategy, to ensure that a legal value was set.
			props.getSocketStrategy();
		} catch (PropertiesException pe) {
			System.err.println("Error setting properties. Retry setting the properties file and try running the client again.");
			pe.printStackTrace();
			System.exit(1);
		}


		/* GAME TIME! */
		State state = State.SPLASH;
		ClickTests clickTester = new ClickTests();
		myPainter = new Painter();
		myPainter.initialize();
		input = new InputManager();

		int playerEntityId = 0;

		// Try to load the memento from file.
		File f = new File("resources/Memento.dat");
		if (f.exists() && f.canRead()){ // Load the memento from file..
			System.out.println("Found Memento, trying to load from file");
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			try {
				m = (InputMemento) ois.readObject();
				input.SetMemento(m);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				ois.close();
			}
		}
		// if the file doesnt exist, then InputManager uses default settings
		// ie: keyboard keybindings.

		// Build the entities for the options screen
		int x = 0;
		float xpos = 0, ypos = 0;
		for (Button b : Button.values()){
			if (b == Button.NUM_BUTTONS) break;
			if ((x%4 == 0) && (x !=0)) {
				ypos += 203f;
				xpos = 0f;
			}
			OptionsEntity entity = new OptionsEntity(b, x, xpos, ypos, 193f,193f);
			optionEntities.add(entity);
			xpos+= 203f;
			x++;
		}
		
		ClientNetworkingWrapper cnw = null;
		boolean singleConnect = true;

		long startTick = 0, lastTick = 0;
		State returnState = null;
		boolean mousePause = false;
		while(state != State.EXIT && !myPainter.isClosed()){
			switch (state){
				case CONNECTING:
					myPainter.paintWaiting();
					if(singleConnect){
						try {
							singleConnect = false;
							cnw = new ClientNetworkingWrapper();
							cnw.init(new NetworkPair(serverTcpAddr, serverTcpPort));
							@SuppressWarnings("unused")
							Message clientIdMessage = cnw.recvMsg();
							cnw.sendMsg(new RegistrationMessage());
						} catch (NetworkingException e1) {
							e1.printStackTrace();
						}
					} else {
						try {
							RegistrationResultMessage r = (RegistrationResultMessage) cnw.recvMsg();
							playerEntityId = r.getEntityId();
							if (playerEntityId > 0) {
								// registration success
							} else {
								// TODO: WE FAILED
							}
						} catch (NetworkingException e1) {
							e1.printStackTrace();
						}
						state = State.WAIT;
					}
					break;
				case WAIT:
					myPainter.paintWaiting();
					StartGameMessage recvStart = (StartGameMessage) cnw.recvMsg();
					state = State.GAME;
					break;
				case SPLASH:
					if(Mouse.isButtonDown(0)){
						returnState = clickTester.anythingClickedSplash(Mouse.getX(), Mouse.getY());
						if(returnState != null){
							state = returnState;
							mousePause = true;
						}
					}
					myPainter.paintSplashScreen();
					break;
				case GAME:
					startTick = Utility.getTick();
					if ((startTick - lastTick) > 16){
						lastTick = startTick;
						input.Poll();
						//get inputs
						if (input.IsPressed(Button.UP) || input.IsPressed(Button.B)){
							cnw.sendMsg(new ActionMessage(playerEntityId, ActionType.JUMP));
						}
						if (input.IsPressed(Button.RIGHT)){
							cnw.sendMsg(new ActionMessage(playerEntityId, ActionType.RIGHT));
						}
						if (input.IsPressed(Button.LEFT)){
							cnw.sendMsg(new ActionMessage(playerEntityId, ActionType.LEFT));
						}
						if (!input.IsPressed(Button.LEFT) && !input.IsPressed(Button.RIGHT)){
							cnw.sendMsg(new ActionMessage(playerEntityId, ActionType.NONE));
						}
					}
					Message update = cnw.recvMsg();
					if (update.getType() == MsgType.BROADCAST_TYPE) {
						EntityBroadcastMessage broadcastMessage = (EntityBroadcastMessage)update;
						broadcastMessage.getEntities();
					}
					if (update.getType() == MsgType.TERMINATE_TYPE){
						TerminateMessage terminateMessage = (TerminateMessage)update;
						if (terminateMessage.isWinner()) {
							state = State.WIN;
						} else {
							state = State.LOSE;
						}
						continue;
					}
					EntityBroadcastMessage ebm = (EntityBroadcastMessage)update;
					myPainter.paintEntities(ebm.getEntities());
					myPainter.paintSpikeStrip();
					myPainter.updateDisplay();
					break;
				case OPTIONS:
					if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
						state = State.SPLASH;
					}
					if(Mouse.isButtonDown(0) && !mousePause){
						optionsIterator.reset();
						while(optionsIterator.hasNext()){
							returnState = clickTester.anythingClickedOptions(Mouse.getX(), Mouse.getY(), optionsIterator.next());
							if(returnState != null){
								state = returnState;
								mousePause = true;
							}
						}
					}
					if(!Mouse.isButtonDown(0) && mousePause){
						mousePause = false;
					}
					optionsIterator.reset();
					myPainter.paintOptions(optionsIterator);
					myPainter.updateDisplay();
					break;
				case WIN:
					myPainter.paintWin();
					break;
				case LOSE:
					myPainter.paintLose();
					break;
				case EXIT:
					myPainter.closeDisplay();
					break;
			}
		}
		cnw.shutdown();
		// Close requested, save the input memento to file.
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("resources/Memento.dat"));
			oos.writeObject(input.CreateMemento());
			oos.close();
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	//needed to keep the painter on clientmain
	public static void paintOptions(){
		optionsIterator.reset();
		myPainter.paintOptions(optionsIterator);
		myPainter.updateDisplay();
	}
}
