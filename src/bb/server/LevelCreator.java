package bb.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import bb.server.entities.Platform;

public class LevelCreator {
	/*
	 * The LevelCreator class is called by the server to initialize the terrain
	 * This is read from a resource file
	 * A G represents ground and -G represents two ground 
	 */
	
	String levelFile;
	ServerEntityManager entityManager;
	int levelEndingXPosition;

	public LevelCreator(String levelFile, ServerEntityManager entityManager) {
		this.levelFile = levelFile;
		this.entityManager = entityManager;
	}
	
	public void readLevel() throws IOException {
		BufferedReader myReader;
		float xPos;
		float yPos;
		int boxSize = 32;
		float height;
		float width = 0;
		yPos = -boxSize;
		myReader = new BufferedReader(new FileReader(levelFile));
		String line;
		height = boxSize;
		
		int maxLength = 0;
		
		while ((line = myReader.readLine()) != null){
			maxLength = Math.max(maxLength, line.length());
			yPos += boxSize;
			xPos = -boxSize;
			char[] cArray = line.toCharArray();
			for(int i = 0; i < line.length(); i++){
				xPos += boxSize;
				if(cArray[i] == 'G'){
					width += boxSize;
					height = boxSize;
					entityManager.add(new Platform(xPos, yPos, width, height));
					xPos += width - boxSize;
					width = 0;
				} 
				if(cArray[i] == '-'){
					width += boxSize;
					xPos -= boxSize;
				}
			}
		}
		myReader.close();
		
		levelEndingXPosition = maxLength * boxSize;
	}
	
	public int getLeveEndingXPosition() {
		return levelEndingXPosition;
	}
}
