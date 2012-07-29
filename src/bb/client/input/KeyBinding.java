package bb.client.input;

import java.io.IOException;
import java.io.Serializable;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import bb.client.input.InputManager.Button;

public class KeyBinding implements Serializable {
    private static final long serialVersionUID = -2081353696456741186L;
	
    transient Controller controller;
    transient Component component;
    int controllerIndex;
    Button button;
    float value;
    KeyBinding(Controller x, Component y, int index, Button z, float v){
        controller = x;
        component = y;
        controllerIndex = index;
        button = z;
        value = v;

    }

    private synchronized void writeObject(java.io.ObjectOutputStream out) throws IOException{
        // note, here we don't need out.defaultWriteObject(); because
        // MyClass has no other state to serialize
        out.defaultWriteObject();

        if (controller == null){
            out.writeInt(-1);
        } else {
            out.writeInt(controllerIndex);
        }

        if (component == null){
            out.writeObject(null);

        } else {
            out.writeObject(component.getName());
        }


    }

    private synchronized void readObject(java.io.ObjectInputStream in) throws IOException {
        // note, here we don't need in.defaultReadObject();
        // because MyClass has no other state to deserialize
        try {
            in.defaultReadObject();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ControllerEnvironment env = ControllerEnvironment.getDefaultEnvironment();

        int y = in.readInt();

        if (y == -1){
            controller = null;
        } else {
            Controller controllers[] = env.getControllers();

            if (y < controllers.length){
                controller = controllers[y];
            } else {
                controller = null;
            }
        }

        String x = null;
        try {
            x = (String) in.readObject();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (controller == null || x == null){
            component = null;
        } else {
            for (Component comp : controller.getComponents()){
                if (comp.getName().equals(x)){
                    component = comp;
                    break;
                }
            }
        }
    }
}