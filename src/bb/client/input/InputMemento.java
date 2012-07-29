package bb.client.input;

import java.io.Serializable;

public class InputMemento implements Serializable {
	private static final long serialVersionUID = 3789997576477420533L;
	
	KeyBinding keybindings[];

    public InputMemento(KeyBinding k[]){
        keybindings = k;
    }

    public KeyBinding[] GetState(){
        return keybindings;
    }

    public void SetState(KeyBinding k[]){
        keybindings = k;
    }

}
