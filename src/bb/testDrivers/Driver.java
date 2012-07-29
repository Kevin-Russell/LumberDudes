package bb.testDrivers;

import bb.common.EntityVector;
import bb.server.entities.*;
import bb.common.*;

public class Driver {
	public static void main(String[] argv) {
        EntityCollection<PhysEntity> v = new EntityVector<PhysEntity>();
        v.createIterator();
        Player p = new Player(100, 0, 0, 0);
        
        v.add(p);
        
        System.out.println(v.fetch(0).getX());
    }
}
