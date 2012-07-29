package bb.server.entities.items;

import bb.server.entities.Player;

public abstract class Effect {
	protected Player target;
	protected int id;
	protected float currentDuration;
	protected float expireDuration;
	protected boolean expired;
	protected static int idGenerator = 0;
	
	protected Effect() {
		id = idGenerator++;
	}
	
	public void performTimeStep(float elapsedTime) {
		currentDuration += elapsedTime;
		if (currentDuration >= expireDuration) {
			expire();
		}
	}
	
	public int getId() {
		return id;
	}
	
	public boolean isExpired() {
		return expired;
	}
	
	// Method is called on the first application of an Effect
	public void apply(Player target, float expireDuration) {
		this.target = target;
		this.expireDuration = expireDuration;
		currentDuration = 0;
		expired = false;
		target.applyEffect(this);
	}
	
	// Method is called when an Effect expires 
	public abstract void expire();
}
