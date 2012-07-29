package bb.client.painter;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;

import java.awt.Font;
import java.io.IOException;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import bb.client.ClientMain.BoxColor;
import bb.client.entities.OptionsEntity;
import bb.common.EntityInfo;
import bb.common.EntityIterator;

/*
 * The Painter class handles painting for the client
 * The client will call the initialize() class which initializes the display, textures and fonts
 * 
 * Based on which state the client is in it will ask the painter to paint different screens
 * 
 */

public class Painter {
	private  UnicodeFont uniFont;
	private Texture lumberTexture;
	private Texture lumberTexture2;
	private Texture lumberTexture3;
	private Texture lumberTexture4;
	private Texture bombTexture;
	private Texture coinTexture;
	private Texture speedTexture;

	private Texture gummyUpTexture;

	int screenHeight = 600;
	int screenWidth = 800;

	public void initialize() throws LWJGLException, IOException{
		Display.setDisplayMode(new DisplayMode(800,600));
        Display.setTitle("LUMBER DUDES!");
        Display.create();
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, 800, 600, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        lumberTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("resources/lumber-dude.png"));
        lumberTexture2 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("resources/lumber-dude2.png"));
        lumberTexture3 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("resources/lumber-dude3.png"));
        lumberTexture4 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("resources/lumber-dude4.png"));
        bombTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("resources/spikey_16.png"));
        coinTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("resources/coin.png"));
        gummyUpTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("resources/gummy_up.png"));
        speedTexture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("resources/lightning.png"));
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Font awtFont = new Font("", Font.PLAIN,55);
        uniFont = new UnicodeFont(awtFont, 24, false, false);
        uniFont.addAsciiGlyphs();
        uniFont.addGlyphs(400,600);           // Setting the unicode Range
        uniFont.getEffects().add(new ColorEffect(java.awt.Color.white));

        try {
            uniFont.loadGlyphs();
        } catch (SlickException e) {};
	}

	public boolean isClosed(){
		if(Display.isCloseRequested()){
			return true;
		} else {
			return false;
		}
	}
	public void closeDisplay(){
		Display.destroy();
	}
	public void updateDisplay(){
		Display.update();
	}
	public void paintOptions(EntityIterator<OptionsEntity> it){
		paintBorder();
	    while (it.hasNext()){
	        paintOptionBlock(it.next());
	    }
	    return;
	}


	public void paintEntities(List<EntityInfo> entityInfos){

		glClear(GL_COLOR_BUFFER_BIT);
		for (EntityInfo e : entityInfos) {
			if(e.getSpriteId() == 100){ //ground
				paintGround(e.getX(), e.getY(), e.getHeight(), e.getWidth());
			} else if (e.getSpriteId() == 0) {
                paintPNG(e.getX(), e.getY(), e.getHeight(), e.getWidth(), lumberTexture);
			} else if (e.getSpriteId() == 1) {
                paintPNG(e.getX(), e.getY(), e.getHeight(), e.getWidth(), lumberTexture2);
			} else if (e.getSpriteId() == 2) {
                paintPNG(e.getX(), e.getY(), e.getHeight(), e.getWidth(), lumberTexture3);
			}  else if (e.getSpriteId() == 3) {
				paintPNG(e.getX(), e.getY(), e.getHeight(), e.getWidth(), lumberTexture4);
			}  else if (e.getSpriteId() == 101){ //coin
				paintPNG(e.getX(), e.getY(), e.getHeight(), e.getWidth(), coinTexture);
			} else if (e.getSpriteId() == 102){
				paintPNG(e.getX(), e.getY(), e.getHeight(), e.getWidth(), bombTexture);
			} else if (e.getSpriteId() == 103){
				paintPNG(e.getX(), e.getY(), e.getHeight(), e.getWidth(), gummyUpTexture);
			} else if (e.getSpriteId() == 104){
				paintPNG(e.getX(), e.getY(), e.getHeight(), e.getWidth(), speedTexture);
			}
			if(e.isPlayer()){
				paintScore(e.getSpriteId(), e.getScore());
			}
		}
	}

	private void paintOptionBlock(OptionsEntity e){
		
		drawMenuItem(e.getX(), e.getY(), e.getHeight(), e.getWidth(), e.GetText(), e.getColor());
	    return;
	}

	private void paintGround(float xPos, float yPos, float height, float width){
		GL11.glColor3f(0f, 1f, 0f);
		glBegin(GL_QUADS);
			GL11.glVertex2f(xPos, yPos);
			GL11.glVertex2f(xPos + width, yPos);
			GL11.glVertex2f(xPos + width, yPos + (height/8));
			GL11.glVertex2f(xPos, yPos + (height/8));
		glEnd();
		GL11.glColor3f(0.8f, 0.8f, 0.2f);
		glBegin(GL_QUADS);
			GL11.glVertex2f(xPos, yPos + (height/8));
			GL11.glVertex2f(xPos + width, yPos + (height/8));
			GL11.glVertex2f(xPos + width, yPos + height);
			GL11.glVertex2f(xPos, yPos + height);
		glEnd();
	}
	public void paintSpikeStrip(){
		GL11.glColor3f(0.9f, 0.1f, 0.1f);
		glBegin(GL_TRIANGLE_STRIP);
			for(float i = 0; i < 800/20; i++){
				GL11.glVertex2f(0, i*20);
				GL11.glVertex2f(20, i*20 + 10);
				GL11.glVertex2f(0, i*20 + 20);
			}
	    glEnd();
	}
	private void paintScore(int player, int playerScore) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		uniFont.drawString(650 , 0+ player*30, "P" + Integer.toString(player+1) + ": " + Integer.toString(playerScore));
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

	private void paintPNG(float xPos, float yPos, float height, float width, Texture png){
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		png.bind();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
		glBegin(GL_QUADS);
			GL11.glTexCoord2f(0,0);
			GL11.glVertex2f(xPos, yPos);
			GL11.glTexCoord2f(1,0);
			GL11.glVertex2f(xPos + width, yPos);
			GL11.glTexCoord2f(1,1);
			GL11.glVertex2f(xPos + width, yPos + height);
			GL11.glTexCoord2f(0,1);
			GL11.glVertex2f(xPos, yPos + height);
		glEnd();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

	public void paintSplashScreen(){
		paintBorder();
		drawMenuItem(screenWidth/3, 1*screenHeight/7, screenWidth/3*2 - screenWidth/3, 2*screenHeight/7- 1*screenHeight/7, "Start game", BoxColor.GREEN);
		drawMenuItem(screenWidth/3, 3*screenHeight/7, screenWidth/3*2 - screenWidth/3, 4*screenHeight/7- 3*screenHeight/7, "Key Bindings", BoxColor.GREEN);
		drawMenuItem(screenWidth/3, 5*screenHeight/7, screenWidth/3*2 - screenWidth/3, 6*screenHeight/7- 5*screenHeight/7, "Exit", BoxColor.GREEN);

		Display.update();
	}
	private void drawMenuItem(float x, float y, float width, float height, String item, BoxColor myColor){
		int border = 10;
		switch(myColor){
			case GREEN:
				GL11.glColor3f(0.1f, 0.9f, 0.1f);
				break;
			case RED:
				GL11.glColor3f(0.9f, 0.1f, 0.1f);
				break;
			case BLUE:
				GL11.glColor3f(0.1f, 0.1f, 0.9f);
				break;
			case ORANGE:
				GL11.glColor3f(0.9f, 0.4f, 0.1f);
				break;
		}
		glBegin(GL_QUADS);
			GL11.glVertex2f(x, y);
			GL11.glVertex2f(x + width, y);
			GL11.glVertex2f(x + width, y + height);
			GL11.glVertex2f(x, y + height);
		glEnd();

		GL11.glColor3f(0,0,0);
		glBegin(GL_QUADS);
			GL11.glVertex2f(x + border, y + border);
			GL11.glVertex2f(x + width - border, y + border);
			GL11.glVertex2f(x + width  - border, y + height - border);
			GL11.glVertex2f(x + border, y + height - border);
		glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		uniFont.drawString(x + 2*border, y + 2*border, item, Color.white);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	public void paintWin(){
		paintBorder();
		GL11.glColor3f(0,0,0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor3f(1,1,1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		uniFont.drawString(screenWidth/2 - 50, screenHeight/2, "You win.", Color.black);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Display.update();
	}
	public void paintLose(){
		paintBorder();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor3f(0,0,0);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		uniFont.drawString(screenWidth/2 - 50, screenHeight/2, "You lose!", Color.black);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Display.update();
	}
	
	public void paintWaiting(){
		paintBorder();
		paintPNG(screenWidth/5, screenHeight/4, 64, 64, lumberTexture);
		paintPNG(2*screenWidth/5, screenHeight/4, 64, 64, lumberTexture2);
		paintPNG(3*screenWidth/5, screenHeight/4, 64, 64, lumberTexture3);
		paintPNG(4*screenWidth/5, screenHeight/4, 64, 64, lumberTexture4);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor3f(0,0,0);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		uniFont.drawString(screenWidth/2 - 50, screenHeight/2, "Waiting! ", Color.black);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Display.update();
	}
	private void paintBorder(){
		glClear(GL_COLOR_BUFFER_BIT);
		int border = 10;
		GL11.glColor3f(0.5f, 0.5f, 0.5f);
		glBegin(GL_QUADS);
			GL11.glVertex2f(0, 0);
			GL11.glVertex2f(screenWidth, 0);
			GL11.glVertex2f(screenWidth, screenHeight);
			GL11.glVertex2f(0, screenHeight);
		glEnd();
		GL11.glColor3f(0.8f, 0.8f, 0.8f);
		glBegin(GL_QUADS);
			GL11.glVertex2f(0 + border, 0 + border);
			GL11.glVertex2f(screenWidth - border, 0 + border);
			GL11.glVertex2f(screenWidth - border, screenHeight - border);
			GL11.glVertex2f(0 + border, screenHeight - border);
		glEnd();
	}
}
