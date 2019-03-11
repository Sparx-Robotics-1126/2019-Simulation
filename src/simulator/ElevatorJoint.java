package simulator;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class ElevatorJoint extends AbstractControl{
	private VehicleControl baseControl;
	private RigidBodyControl ctrl;
	private float height;
	private float force;
	private float mtrPwr;

	public ElevatorJoint(VehicleControl base) {
		this.baseControl = base;
		height = 0f;
		mtrPwr = 0f;
		force = 0f;
	}

	public ElevatorJoint(VehicleControl base, float height) {
		this.baseControl = base;
		this.height = height;
		mtrPwr = 0f;
		force = 0f;
	}

	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		ctrl = spatial.getControl(RigidBodyControl.class);
		ctrl.setPhysicsRotation(baseControl.getPhysicsRotation());
		ctrl.setMass(0f);
		ctrl.setAngularDamping(100000000f);
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public void setMotor(float motorPower) {
		mtrPwr = motorPower;
	}

	@Override
	protected void controlRender(RenderManager arg0, ViewPort arg1) {
	}

	@Override
	protected void controlUpdate(float tpf) {
		if(height <= 2) {
			force -= mtrPwr;
			if(mtrPwr < 0) {
				baseControl.applyForce(new Vector3f(0f, 0f, force), SimUtilities.getPointAtAngleAndOffsetOfObject(baseControl.getPhysicsLocation(), baseControl.getPhysicsRotation(), 2f, 1.5f, 0f));
				baseControl.applyForce(new Vector3f(0f, 0f, force), SimUtilities.getPointAtAngleAndOffsetOfObject(baseControl.getPhysicsLocation(), baseControl.getPhysicsRotation(), -2f, 1.5f, 0f));
			} else if(mtrPwr == 0) {
				baseControl.applyForce(new Vector3f(0f, 0f, 58f), SimUtilities.getPointAtAngleAndOffsetOfObject(baseControl.getPhysicsLocation(), baseControl.getPhysicsRotation(), 2f, -1.5f, 0f));
				baseControl.applyForce(new Vector3f(0f, 0f, 58f), SimUtilities.getPointAtAngleAndOffsetOfObject(baseControl.getPhysicsLocation(), baseControl.getPhysicsRotation(), -2f, -1.5f, 0f));
			} else {
				System.out.println(force);
				height += mtrPwr/100;
			}
		} else {
			height += mtrPwr/100;
		}
		Vector3f basePos = baseControl.getPhysicsLocation();
		ctrl.setPhysicsLocation(new Vector3f(basePos.x, basePos.y, height));
	}
}