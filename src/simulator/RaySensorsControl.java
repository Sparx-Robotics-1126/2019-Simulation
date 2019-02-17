package simulator;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Line;

import simulator.PairedDoubleFactory.PairedDouble;

public class RaySensorsControl extends AbstractControl{

	private VehicleControl robotControl;
	private SimpleApplication app;

	private Ray centerLeftSensor;
	private PairedDouble isCenterLeftSensingTape = PairedDoubleFactory.getInstance().createPairedDouble("centerLeftSensor", false, 1.0);
	private Ray centerRightSensor; 
	private PairedDouble isCenterRightSensingTape = PairedDoubleFactory.getInstance().createPairedDouble("centerRightSensor", false, 1.0);
	private Ray leftSensor;
	private PairedDouble isLeftSensingTape = PairedDoubleFactory.getInstance().createPairedDouble("leftTapeSensor", false, 1.0);
	private Ray rightSensor;
	private PairedDouble isRightSensingTape = PairedDoubleFactory.getInstance().createPairedDouble("rightTapeSensor", false, 1.0);
	private Ray upperLeftPerpendicularSensor;
	private PairedDouble isForwardLeftPerpendicular = PairedDoubleFactory.getInstance().createPairedDouble("forwardLeftPerpendicularSensor", false, 0.0);
	private Ray upperRightPerpendicularSensor;
	private PairedDouble isForwardRightPerpendicular = PairedDoubleFactory.getInstance().createPairedDouble("forwardRightPerpendicularSensor", false, 0.0);
	private Ray rearLeftPerpendicularSensor;
	private PairedDouble isRearPerpendicularLeft = PairedDoubleFactory.getInstance().createPairedDouble("rearLeftPerpendicularSensor", false, 0.0);
	private Ray rearRightPerpendicularSensor;
	private PairedDouble isRearPerpendicularRight = PairedDoubleFactory.getInstance().createPairedDouble("rearRightPerpendicularSensor", false, 0.0);
	private Geometry debugCenterLeft;
	private Geometry debugCenterRight;
	
	public RaySensorsControl(SimpleApplication app, VehicleControl robotControl) {
		this.app = app;
		this.robotControl = robotControl;
		centerLeftSensor = new Ray(SimUtilities.Griebel_DeweyMethod(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), .11f, .25f, 0), new Vector3f(0, 0, -1));
		centerRightSensor = new Ray(SimUtilities.Griebel_DeweyMethod(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), -.11f, .25f, 0), new Vector3f(0, 0, -1));
		leftSensor = new Ray(SimUtilities.Griebel_DeweyMethod(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), 1.57f, .25f, 0), new Vector3f(0, 0, -1));
		rightSensor = new Ray(SimUtilities.Griebel_DeweyMethod(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), -1.57f, .25f, 0), new Vector3f(0, 0, -1));
		upperLeftPerpendicularSensor = new Ray(SimUtilities.Griebel_DeweyMethod(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), .7854f, .25f, 0), SimUtilities.getPerpendicularDirection(robotControl, true));
		upperRightPerpendicularSensor = new Ray(SimUtilities.Griebel_DeweyMethod(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), -.7854f, .25f, 0), SimUtilities.getPerpendicularDirection(robotControl, false));		
		rearLeftPerpendicularSensor = new Ray(SimUtilities.Griebel_DeweyMethod(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), 2.356f, .25f, 0), SimUtilities.getPerpendicularDirection(robotControl, true));
		rearRightPerpendicularSensor = new Ray(SimUtilities.Griebel_DeweyMethod(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), -2.356f, .25f, 0), SimUtilities.getPerpendicularDirection(robotControl, false));


	}

	@Override
	protected void controlUpdate(float arg0) {
		upperLeftPerpendicularSensor = new Ray(SimUtilities.Griebel_DeweyMethod(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), .7854f, .25f, 0), SimUtilities.getPerpendicularDirection(robotControl, true));
		upperRightPerpendicularSensor = new Ray(SimUtilities.Griebel_DeweyMethod(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), -.7854f, .25f, 0), SimUtilities.getPerpendicularDirection(robotControl, false));		
		rearLeftPerpendicularSensor = new Ray(SimUtilities.Griebel_DeweyMethod(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), 2.356f, .25f, 0), SimUtilities.getPerpendicularDirection(robotControl, true));
		rearRightPerpendicularSensor = new Ray(SimUtilities.Griebel_DeweyMethod(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), -2.356f, .25f, 0), SimUtilities.getPerpendicularDirection(robotControl, false));		
		centerLeftSensor.setOrigin(SimUtilities.Griebel_DeweyMethod(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), .11f, .25f, 0));
		centerRightSensor.setOrigin(SimUtilities.Griebel_DeweyMethod(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), -.11f, .25f, 0));
		leftSensor.setOrigin(SimUtilities.Griebel_DeweyMethod(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), 1.57f, .25f, 0));
		rightSensor.setOrigin(SimUtilities.Griebel_DeweyMethod(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), -1.57f, .25f, 0));
		FieldAppState field = app.getStateManager().getState(FieldAppState.class);
		Node tapeTargets = field.getTapeMarks();

		CollisionResults collisionResult = new CollisionResults();
		if(tapeTargets.collideWith(centerLeftSensor, collisionResult) > 0) {
			isCenterLeftSensingTape.value = 0.0;
		} else{
			isCenterLeftSensingTape.value = 1.0;
		}
		if(tapeTargets.collideWith(centerRightSensor, collisionResult) > 0) {
			isCenterRightSensingTape.value = 0.0;
		} else{
			isCenterRightSensingTape.value = 1.0;
		}
		if(tapeTargets.collideWith(leftSensor, collisionResult) > 0) {
			isLeftSensingTape.value = 0.0;
		} else{
			isLeftSensingTape.value = 1.0;
		}
		if(tapeTargets.collideWith(rightSensor, collisionResult) > 0) {
			isRightSensingTape.value = 0.0f;
		} else{
			isRightSensingTape.value = 1.0f;
		}

//		Material rayMaterial = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
//		rayMaterial.setBoolean("UseMaterialColors", true);
//		rayMaterial.setColor("Diffuse", ColorRGBA.Green);
//
//		debugCenterLeft = new Geometry("Center Left Sensor debug", new Line(centerRightSensor.origin, upperRightPerpendicularSensor.getDirection().normalize().mult(5)));
//		debugCenterLeft.setMaterial(rayMaterial);
//		app.getRootNode().attachChild(debugCenterLeft);
//		
//		debugCenterRight = new Geometry("Center Right Sensor debug", new Line(centerLeftSensor.origin, upperRightPerpendicularSensor.getDirection().normalize().mult(5)));
//		debugCenterRight.setMaterial(rayMaterial);
//		app.getRootNode().attachChild(debugCenterRight);
		
		collisionResult = new CollisionResults();
		CollisionResult collision;		
		field.getField().collideWith(upperLeftPerpendicularSensor, collisionResult);
		collision = collisionResult.getClosestCollision();
		if(collision != null) {
			isForwardLeftPerpendicular.value = collision.getDistance() < 10 ? (10 - collision.getDistance())/10 : 0;
			collision = collisionResult.getClosestCollision();
		}
		field.getField().collideWith(upperRightPerpendicularSensor, collisionResult);
		if(collision != null) {
			collision = collisionResult.getClosestCollision();
			isForwardRightPerpendicular.value = collision.getDistance() < 10 ? (10 - collision.getDistance())/10 : 0;
		}
		collision = collisionResult.getClosestCollision();
		if(collision != null) {
			field.getField().collideWith(rearLeftPerpendicularSensor, collisionResult);
			isRearPerpendicularLeft.value = collision.getDistance() < 10 ? (10 - collision.getDistance())/10 : 0;
		}
		collision = collisionResult.getClosestCollision();
		if(collision != null) {
			field.getField().collideWith(rearRightPerpendicularSensor, collisionResult);
			isRearPerpendicularRight.value = collision.getDistance() < 10 ? (10 - collision.getDistance())/10 : 0;
		}
	}

	@Override
	protected void controlRender(RenderManager arg0, ViewPort arg1) {
	}


}
