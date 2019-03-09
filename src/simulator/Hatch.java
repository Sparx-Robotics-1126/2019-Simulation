package simulator;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

public class Hatch {

	private RigidBodyControl control;
	private Spatial spatial;
	private boolean pickupable;

	
	public Hatch(RigidBodyControl control, Spatial spatial) {
		this.control = control; 
		this.spatial = spatial;
	}
	
	public void setPickupable(boolean pickupable) {
		this.pickupable = pickupable;
	}
	public boolean isPickupable() {
		return pickupable;
	}	
	public Vector3f getLocation() {
		return control.getPhysicsLocation();
	}
	public void setLocation(Vector3f newLocation) {
		control.setPhysicsLocation(newLocation);
	}
	public Quaternion getRotation() {
		return control.getPhysicsRotation();
	}
	public void setRotation(Quaternion newRotation) {
		control.setPhysicsRotation(newRotation);
	}
	public RigidBodyControl getControl() {
		return control;
	}
	public void setControl(RigidBodyControl control) {
		this.control = control;
	}
	public Spatial getSpatial() {
		return spatial;
	}
	public void setSpatial(Spatial spatial) {
		this.spatial = spatial;
	}
	public void setKinematic(boolean freeMoving) {
		control.setKinematic(freeMoving);
	}
}
