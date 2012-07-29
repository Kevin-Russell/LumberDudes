package bb.client.painter;

import bb.common.BaseEntity;

/*
 * The paintEntity is used by the painter class to paint
 * we were planning on having entities animate but that was commented out
 */

public class PaintEntity extends BaseEntity {
	//private int animation;
	private int guid;
	public PaintEntity(int guid, int id, float x, float y, float width, float height) {
		super(x, y, width, height);
		//animation = 0;
		this.guid = guid;
		this.id = id;
	}
	/*public int getAnimation(){
		return animation;
	}
	*/
	public int getGuid(){
		return guid;
	}

}
