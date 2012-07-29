package bb.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bb.common.ActionType;
import bb.common.EntityInfo;
import bb.common.exception.NetworkingException;
import bb.common.network.NetworkPair;
import bb.common.network.NetworkingManager;
import bb.common.network.message.ActionMessage;
import bb.common.network.message.DeadClientMessage;
import bb.common.network.message.EntityBroadcastMessage;
import bb.common.network.message.Message;
import bb.common.network.message.Message.MsgType;
import bb.common.network.message.RegistrationMessage;
import bb.common.network.message.RegistrationResultMessage;
import bb.common.network.message.StartGameMessage;
import bb.common.network.message.TerminateMessage;
import bb.common.util.Utility;
import bb.server.entities.PhysEntity;
import bb.server.entities.Platform;
import bb.server.entities.Player;
import bb.server.entities.items.Bomb;
import bb.server.entities.items.Coin;
import bb.server.entities.items.GravityGum;
import bb.server.entities.items.SpeedSoup;
import bb.server.network.ServerNetworkingWrapper;

public class Game {
	private final float FPS = 60;
	private final float SECONDS_TO_MILLISEC_RATE = 1000;
	private final float playerDimension = 24;
	
	private int curNumPlayers;
	private int maxNumPlayers;
	private int serverPort;
	private int levelEndingXPosition;

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	private boolean gameActive;

	public static ServerEntityManager entityManager;
	private PhysicsEngine physicsEngine;
	private ScoreKeeper scoreKeeper;
	private ItemCreator itemCreator;
	public static Camera camera;

	private NetworkingManager serverNetworkingManager;

	public Game() throws IOException {
		curNumPlayers = 0;
		gameActive = true;
		entityManager = ServerEntityManager.getInstance();
		serverNetworkingManager = new ServerNetworkingWrapper();
		LevelCreator levelCreator = new LevelCreator("resources/Level1.txt", entityManager);
		levelCreator.readLevel();
		levelEndingXPosition = levelCreator.getLeveEndingXPosition();
	}
	
	public void init(int serverPort, int maxNumPlayers) {
		this.serverPort = serverPort;
		this.maxNumPlayers = maxNumPlayers;
	}

	public void start() throws InterruptedException, IOException, NetworkingException {
		physicsEngine = PhysicsEngine.getInstance();
		itemCreator = ItemCreator.getInstance();
		camera = Camera.getInstance();
		scoreKeeper = ScoreKeeper.getInstance();

		// Initialize server networking manager
		serverNetworkingManager = new ServerNetworkingWrapper();
		NetworkPair pair = new NetworkPair(null, serverPort);
		serverNetworkingManager.init(pair);

        waitForPlayers();
		gameLoop();
	}

	private int registerPlayer() {
		Player p = new Player(curNumPlayers * (playerDimension + 1), 0, playerDimension, playerDimension);
		entityManager.add(p);
		scoreKeeper.addPlayer(p);
		curNumPlayers++;
		return p.getId();
	}

	private void waitForPlayers() throws NetworkingException {
		Message message;
		Message resultMessage;
		MsgType type;

		while (curNumPlayers < maxNumPlayers) {
			message = serverNetworkingManager.recvMsg();
			if (message == null) continue;
			type = message.getType();

			if (type == MsgType.REG_TYPE) {
				RegistrationMessage registrationMessage = (RegistrationMessage)message;
				int playerId = registerPlayer();

				System.out.println("Game - Player registered");
				resultMessage = new RegistrationResultMessage(playerId, registrationMessage.getClientId());
				System.out.println("Game - Send registration result");
				serverNetworkingManager.sendMsg(resultMessage);
			}
		}

		Message startMessage = new StartGameMessage();
		serverNetworkingManager.sendMsg(startMessage);

		System.out.println("Registration complete!");
	}

	private void gameLoop() throws InterruptedException, NetworkingException {
		System.out.println("Game - Start game loop");
		float milliSecPerFrame = 1 / FPS * SECONDS_TO_MILLISEC_RATE;

		long startTick;
		long elapsedTick;
		long cameraStartTimer = 0;
		float elapsedTimeInSeconds = (float)milliSecPerFrame / 1000;
		List<PhysEntity> entitiesInCameraFrame = new ArrayList<PhysEntity>();
		List<EntityInfo> entityInfos;
		List<EntityInfo> prevEntityInfos = new ArrayList<EntityInfo>();

		while (gameActive) {
			startTick = Utility.getTick();
			for (int i = 0; i < curNumPlayers; i++) {
				handleClientEvents();
			}

	        itemCreator.pollItem(elapsedTimeInSeconds);
			physicsEngine.performTimeStep(elapsedTimeInSeconds);
			camera.performTimeStep(elapsedTimeInSeconds);
			
			if (camera.getX() + camera.getWidth() > levelEndingXPosition) {
				int winnerId = scoreKeeper.getWinnerId();
				serverNetworkingManager.sendMsg(new TerminateMessage(winnerId));
				break;
			}

			entitiesInCameraFrame = camera.getEntitiesInFrame();
			entityInfos = createEntityInfoList(entitiesInCameraFrame);

			// Mark entities that existed before but does not exist now as "removed"
			for (EntityInfo prevEntityInfo : prevEntityInfos) {
				if (!Utility.contains(entityInfos, prevEntityInfo)) {
					prevEntityInfo.setIsRemoved(true);
					entityInfos.add(prevEntityInfo);
				}
			}
			prevEntityInfos = entityInfos;

			Message broadcastMessage = new EntityBroadcastMessage(entityInfos);
			serverNetworkingManager.sendMsg(broadcastMessage);
			
			// Remove entities that are no longer within the camera screen
			int i = 0;
			while (i < entityInfos.size()) {
				EntityInfo entityInfo = entityInfos.get(i);
				if (entityInfo.isRemoved()) {
					entityInfos.remove(i);
				} else {
					i++;
				}
			}
			
			prevEntityInfos = new ArrayList<EntityInfo>();
			for (EntityInfo entityInfo : entityInfos) {
				prevEntityInfos.add(entityInfo);
			}

			elapsedTick = Utility.getTick() - startTick;
			
			cameraStartTimer += milliSecPerFrame;
			if (cameraStartTimer >= 10000) {
				camera.startMovement();
			}
			
			if (elapsedTick < milliSecPerFrame) {
				Thread.sleep(Math.round(milliSecPerFrame) - elapsedTick);
			}
		}
	}

	private void handleClientEvents() throws NetworkingException {
		Message message = serverNetworkingManager.recvMsg();
		if (message == null) return;

		if (message.getType() == MsgType.ACTION_TYPE) {
			ActionMessage actionMessage = (ActionMessage)message;
			ActionType actionType = actionMessage.getAction();
			int playerId = actionMessage.getEntityId();
			
			PhysEntity entity = entityManager.getEntityById(playerId);
			if (entity.isPlayer()) {
				Player player = (Player)entity;
				switch (actionType) {
				case LEFT:
					player.moveLeft();
					break;
				case JUMP:
					player.jump();
					break;
				case RIGHT:
					player.moveRight();
					break;
				case NONE:
					player.stop();
					break;
				}
			} else {
				System.out.println("Error - received an action message from a non player entity");
			}
		}
		else if (message.getType() == MsgType.DEAD_TYPE) {
			DeadClientMessage deadMsg = (DeadClientMessage)message;
			entityManager.remove(deadMsg.getEntityId());
		}
	}

	private List<EntityInfo> createEntityInfoList(List<PhysEntity> entities) {
		List<EntityInfo> results = new ArrayList<EntityInfo>();
		int spriteId = 0;
		int playerSpriteId = 0;
		for(PhysEntity entity : entities) {
			if (entity.isPlayer()) {
				spriteId = playerSpriteId++;
			} else if (entity instanceof Platform) {
				spriteId = 100;
			} else if (entity instanceof Coin){
				spriteId = 101;
			} else if (entity instanceof Bomb){
				spriteId = 102;
			} else if (entity instanceof GravityGum){
				spriteId = 103;
			} else if (entity instanceof SpeedSoup){
				spriteId = 104;
			}
			results.add(new EntityInfo(entity.getId(), entity.getX() - camera.getX(), entity.getY(), entity.getWidth(), entity.getHeight(), spriteId, entity.isPlayer(), scoreKeeper.getScore(entity.getId())));
		}

		return results;
	}
}

