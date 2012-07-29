package bb.client.entities;



import bb.client.painter.PaintEntity;
import bb.client.input.InputManager;
import bb.client.ClientMain.BoxColor;

/*
 * Extends the paintEntity and is used for the keybindings page
 */

public class OptionsEntity extends PaintEntity {

    BoxColor myColor;
    boolean state;
    int colour;
    InputManager.Button button;

    public OptionsEntity(InputManager.Button b, int id, float x, float y, float width, float height) {
        super(200, id, x, y, width, height);
        button = b;
        myColor = BoxColor.BLUE;
    }

    public boolean Contains(float px, float py){
        if ((px >= x) && (px <= x+width) && (py >= y) && py <= y+height) {
            return true;
        } else {
            return false;
        }
    }

    public String GetText(){
        return button.toString();
    }
    public void setColour(BoxColor newColor){
    	myColor = newColor;
    }
    public BoxColor getColor(){
    	return myColor;
    }
}
