//package simulator;
//
//import com.jme3.bullet.control.RigidBodyControl;
//import com.jme3.bullet.control.VehicleControl;
//import com.jme3.bullet.joints.SixDofJoint;
//import com.jme3.bullet.joints.SliderJoint;
//import com.jme3.bullet.objects.PhysicsRigidBody;
//import com.jme3.math.Matrix3f;
//import com.jme3.math.Vector3f;
//import com.jme3.renderer.RenderManager;
//import com.jme3.renderer.ViewPort;
//import com.jme3.scene.Spatial;
//import com.jme3.scene.control.AbstractControl;
//
//public class ElevatorJoint extends AbstractControl{
//	private VehicleControl baseControl;
//	private RigidBodyControl ctrl;
//	private double mtrPwr;
//	
//	public ElevatorJoint(VehicleControl base) {
//		this.baseControl = base;
//		mtrPwr = 0;
//	}
//
//	public void setSpatial(Spatial spatial) {
//		super.setSpatial(spatial);
//		ctrl = spatial.getControl(RigidBodyControl.class);
//		SliderJoint sliderJoint = new SliderJoint((PhysicsRigidBody)baseControl, (PhysicsRigidBody)ctrl, new Vector3f(0f, 0f, -1f), new Vector3f(0f, -1f, 0f), new Matrix3f(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f), 
//				new Matrix3f(-1.0000000f,  0.0000000f,  0.0000000f,
//							  0.0000000f,  1.0000000f,  0.0000000f,
//							 -0.0000000f,  0.0000000f, -1.0000000f), false);
//		
//	}
//
//	public void setMotor(float motorPower) {
//		mtrPwr = motorPower;
//		mtrPwr = 0;
//	}
//
//	@Override
//	protected void controlRender(RenderManager arg0, ViewPort arg1) {
//	}
//
//	@Override
//	protected void controlUpdate(float tpf) {
//		
//	}
//}