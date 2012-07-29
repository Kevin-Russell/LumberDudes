package bb.server;

import java.util.HashMap;
import java.util.Map;

import bb.server.entities.Player;

public class ScoreKeeper {
	private static ScoreKeeper instance;
	private HashMap<Integer, Integer> playerScores;
	
	private ScoreKeeper() {
		playerScores = new HashMap<Integer, Integer>();
	}
	
	public static ScoreKeeper getInstance() {
		if (instance == null) {
			instance = new ScoreKeeper();
		}
		return instance;
	}
	
	public int getScore(int id) {
		if (!playerScores.containsKey(id)) return -1;
		return playerScores.get(id);
	}
	
	public void addPlayer(Player p) {
		playerScores.put(p.getId(), 0);
	}
	
	public void modifyScore(int id, int amount) {
		int newScore = playerScores.get(id) + amount;
		playerScores.put(id, newScore);
	}
	
	public int getWinnerId() {
		int maxScore = Integer.MIN_VALUE;
		Integer highScorePlayerId = -1;
		
		// initialize
		for (Map.Entry<Integer, Integer> entry : playerScores.entrySet()) {
			highScorePlayerId = entry.getKey();
			maxScore = entry.getValue();
			break;
		}
		
		for (Map.Entry<Integer, Integer> entry : playerScores.entrySet()) {
			Integer playerId = entry.getKey();
			Integer score = entry.getValue();
			
			if (score > maxScore) {
				maxScore = score;
				highScorePlayerId = playerId;
			}
		}
		
		return highScorePlayerId;
	}
}
