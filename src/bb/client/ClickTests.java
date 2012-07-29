package bb.client;

import bb.client.ClientMain.BoxColor;
import bb.client.ClientMain.State;
import bb.client.entities.OptionsEntity;
import bb.client.input.InputManager.Button;

/*
 * The ClickTests class is used to determine if a click hits an option on the screen
 */

public class ClickTests {
	int screenHeight = 600;
	int screenWidth = 800;
	public State anythingClickedSplash(int x, int y){
		State returnState = null;
		if(x > screenWidth/3 && x < screenWidth/3*2){
			if(y > 1*screenHeight/7 && y < 2*screenHeight/7){
				//Hit Exit
				returnState = State.EXIT;
			} else if(y > 3*screenHeight/7 && y < 4*screenHeight/7){
				//Hit Key Bindings
				returnState = State.OPTIONS;
			} else if(y > 4*screenHeight/7 && y < 6*screenHeight/7){
				//Hit start game
				returnState = State.CONNECTING;
			}
		}
		return returnState;
	}
	public State anythingClickedOptions(int x, int y, OptionsEntity myEntity){
		State returnState = null;
		y = screenHeight - y;
		boolean r = false;
		if(myEntity.Contains(x, y)){
			myEntity.setColour(BoxColor.ORANGE);
			ClientMain.paintOptions();
			for (Button b : Button.values()){
				if (b.toString() == myEntity.GetText()){
					r = ClientMain.input.SetBinding(b);
					break;
				}
			}
			if (r){
				myEntity.setColour(BoxColor.GREEN);
			} else {
				myEntity.setColour(BoxColor.RED);
			}
		}
		return returnState;
	}
}
