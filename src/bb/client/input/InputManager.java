package bb.client.input;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

import org.lwjgl.input.Keyboard;

/*
 * Input manager, Has A list of keybindings, Bind each button to a callback
 */

public class InputManager {
    public enum Button {
        UP, DOWN, LEFT, RIGHT, L, R,
        START, SELECT, A, B, X, Y, NUM_BUTTONS
    }


    KeyBinding keybindings[];
    Controller controllers[];
    ControllerEnvironment env;
    boolean ButtonStates[];
    int keys[] = {Keyboard.KEY_UP,
            Keyboard.KEY_DOWN,
            Keyboard.KEY_LEFT,
            Keyboard.KEY_RIGHT,
            Keyboard.KEY_L,
            Keyboard.KEY_R,
            Keyboard.KEY_RETURN,
            Keyboard.KEY_RSHIFT,
            Keyboard.KEY_A,
            Keyboard.KEY_B,
            Keyboard.KEY_X,
            Keyboard.KEY_Y,
            };

    public InputManager(){
        keybindings = new KeyBinding[Button.NUM_BUTTONS.ordinal()];
        env = ControllerEnvironment.getDefaultEnvironment();
        controllers = env.getControllers();
        ButtonStates = new boolean[Button.NUM_BUTTONS.ordinal()];

    }

    public boolean SetBinding(Button b) {
        Controller.Type type;
        Controller c;
        for (;;){
            for (int i = 0; i < controllers.length; i++){
                c = controllers[i];
                type = c.getType();
                if (type != Controller.Type.KEYBOARD &&
                        type != Controller.Type.GAMEPAD &&
                        type != Controller.Type.STICK) continue;
                c.poll();
                for (Component comp : c.getComponents()){
                    float pd = comp.getPollData();
                    if (pd == 1 || pd == -1){
                        if (comp.getIdentifier() != Identifier.Key.ESCAPE){
                            keybindings[b.ordinal()] = new KeyBinding(c, comp, i, b, pd);
                            return true; // key was successfully set
                        } else {
                            return false; // binding cancelled
                        }
                    }
                }
            }
        }
    }

    public boolean IsPressed(Button b){
        KeyBinding kb = keybindings[b.ordinal()];
        if (kb == null || kb.controller == null){
            return Keyboard.isKeyDown(keys[b.ordinal()]);
        }
        return ButtonStates[b.ordinal()];
    }
    public void Poll(){
        for (KeyBinding kb : keybindings){
            if (kb == null) continue;
            try {
                kb.controller.poll();
                float a = kb.component.getPollData();
                if (a == kb.value){
                    ButtonStates[kb.button.ordinal()] = true;
                } else {
                    ButtonStates[kb.button.ordinal()] = false;
                }
            } catch (Exception e) {
                kb = null;
            }


        }
    }

    public InputMemento CreateMemento(){
        return new InputMemento(keybindings);
    }

    public void SetMemento(InputMemento m){
        try {
            keybindings = m.GetState();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
