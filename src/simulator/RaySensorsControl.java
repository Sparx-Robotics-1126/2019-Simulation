package simulator;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
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
	private SimMain app;
	private VehicleControl robotControl;
	private FieldAppState field;

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
	private PairedDouble isRearLeftPerpendicular = PairedDoubleFactory.getInstance().createPairedDouble("rearLeftPerpendicularSensor", false, 0.0);
	private Ray rearRightPerpendicularSensor;
	private PairedDouble isRearRightPerpendicular = PairedDoubleFactory.getInstance().createPairedDouble("rearRightPerpendicularSensor", false, 0.0);
	private boolean debugLinesOn = false;
	private Geometry debugCenterLeft;
	private Geometry debugCenterRight;
	private Geometry debugLeft;
	private Geometry debugRight;
	private Geometry debugUpperRightPerpendicular;
	private Geometry debugUpperLeftPerpendicular;
	private Geometry debugRearRightPerpendicular;
	private Geometry debugRearLeftPerpendicular;

	public RaySensorsControl(SimpleApplication app, VehicleControl robotControl) {
		this.app = (SimMain)app;
		this.robotControl = robotControl;
		createRays();
		field = app.getStateManager().getState(FieldAppState.class);

	}

	@Override
	protected void controlUpdate(float arg0) {
		Node tapeTargets = field.getTapeMarks();
		createRays();
		
		CollisionResults collisionList = new CollisionResults();
		if(tapeTargets.collideWith(centerLeftSensor, collisionList) > 0) {
			isCenterLeftSensingTape.value = 0.0;
		} else{
			isCenterLeftSensingTape.value = 1.0;
		}
		if(tapeTargets.collideWith(centerRightSensor, collisionList) > 0) {
			isCenterRightSensingTape.value = 0.0;
		} else{
			isCenterRightSensingTape.value = 1.0;
		}
		if(tapeTargets.collideWith(leftSensor, collisionList) > 0) {
			isLeftSensingTape.value = 0.0;
		} else{
			isLeftSensingTape.value = 1.0;
		}
		if(tapeTargets.collideWith(rightSensor, collisionList) > 0) {
			isRightSensingTape.value = 0.0f;
		} else{
			isRightSensingTape.value = 1.0f;
		}
		if(debugLinesOn) {
			((Line)debugCenterRight.getMesh()).updatePoints(centerRightSensor.getOrigin(), centerRightSensor.getDirection().add(centerRightSensor.getOrigin()));
			((Line)debugCenterLeft.getMesh()).updatePoints(centerLeftSensor.getOrigin(), centerLeftSensor.getDirection().add(centerLeftSensor.getOrigin()));
			((Line)debugRight.getMesh()).updatePoints(rightSensor.getOrigin(), rightSensor.getDirection().add(rightSensor.getOrigin()));
			((Line)debugLeft.getMesh()).updatePoints(leftSensor.getOrigin(), leftSensor.getDirection().add(leftSensor.getOrigin()));
			((Line)debugUpperRightPerpendicular.getMesh()).updatePoints(upperRightPerpendicularSensor.getOrigin(), upperRightPerpendicularSensor.getDirection().add(upperRightPerpendicularSensor.getOrigin()));
			((Line)debugUpperLeftPerpendicular.getMesh()).updatePoints(upperLeftPerpendicularSensor.getOrigin(), upperLeftPerpendicularSensor.getDirection().add(upperLeftPerpendicularSensor.getOrigin()));
			((Line)debugRearRightPerpendicular.getMesh()).updatePoints(rearRightPerpendicularSensor.getOrigin(), rearRightPerpendicularSensor.getDirection().add(rearRightPerpendicularSensor.getOrigin()));
			((Line)debugRearLeftPerpendicular.getMesh()).updatePoints(rearLeftPerpendicularSensor.getOrigin(), rearLeftPerpendicularSensor.getDirection().add(rearLeftPerpendicularSensor.getOrigin()));
		}
		
		updateRayDistanceToWall(upperRightPerpendicularSensor, isForwardRightPerpendicular);
		updateRayDistanceToWall(upperLeftPerpendicularSensor, isForwardLeftPerpendicular);
		updateRayDistanceToWall(rearRightPerpendicularSensor, isRearRightPerpendicular);
		updateRayDistanceToWall(rearLeftPerpendicularSensor, isRearLeftPerpendicular);

	}

	private void updateRayDistanceToWall(Ray testRay, PairedDouble valueStore) {
		CollisionResults collisionList = new CollisionResults();
		CollisionResult closestCollision;		
		
		field.getField().collideWith(testRay, collisionList);
		closestCollision = collisionList.getClosestCollision();
		if(closestCollision != null) {
			valueStore.value = closestCollision.getDistance() * 39.3701;
		} else{
			valueStore.value = -1.0;
		}
		
	}
	
	public void toggleDebugLines(boolean linesOn) {
		debugLinesOn = linesOn;
		if(debugLinesOn) {
			Material rayMaterial = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
			rayMaterial.setBoolean("UseMaterialColors", true);
			rayMaterial.setColor("Diffuse", ColorRGBA.Green);

			debugCenterLeft = new Geometry("Center Left Sensor debug", new Line(centerRightSensor.getOrigin(), centerRightSensor.getDirection().add(centerRightSensor.getOrigin())));
			debugCenterLeft.setMaterial(rayMaterial);
			app.getRootNode().attachChild(debugCenterLeft);

			debugCenterRight = new Geometry("Center Right Sensor debug", new Line(centerLeftSensor.getOrigin(), centerLeftSensor.getDirection().add(centerLeftSensor.getOrigin())));
			debugCenterRight.setMaterial(rayMaterial);
			app.getRootNode().attachChild(debugCenterRight);

			debugRight = new Geometry("Right Sensor debug", new Line(rightSensor.getOrigin(), rightSensor.getDirection().add(rightSensor.getOrigin())));
			debugRight.setMaterial(rayMaterial);
			app.getRootNode().attachChild(debugRight);

			debugLeft = new Geometry("Left Sensor debug", new Line(leftSensor.getOrigin(), leftSensor.getDirection().add(leftSensor.getOrigin())));
			debugLeft.setMaterial(rayMaterial);
			app.getRootNode().attachChild(debugLeft);

			debugUpperLeftPerpendicular = new Geometry("Upper Left Perpendicular Sensor debug", new Line(upperLeftPerpendicularSensor.getOrigin(), upperLeftPerpendicularSensor.getDirection().add(upperLeftPerpendicularSensor.getOrigin())));
			debugUpperLeftPerpendicular.setMaterial(rayMaterial);
			app.getRootNode().attachChild(debugUpperLeftPerpendicular);

			debugUpperRightPerpendicular = new Geometry("Upper Right Perpendicular Sensor debug", new Line(upperRightPerpendicularSensor.getOrigin(), upperRightPerpendicularSensor.getDirection().add(upperRightPerpendicularSensor.getOrigin())));
			debugUpperRightPerpendicular.setMaterial(rayMaterial);
			app.getRootNode().attachChild(debugUpperRightPerpendicular);

			debugRearLeftPerpendicular = new Geometry("Rear Left Perpendicular Sensor debug", new Line(rearLeftPerpendicularSensor.getOrigin(), rearLeftPerpendicularSensor.getDirection().add(rearLeftPerpendicularSensor.getOrigin())));
			debugRearLeftPerpendicular.setMaterial(rayMaterial);
			app.getRootNode().attachChild(debugRearLeftPerpendicular);

			debugRearRightPerpendicular = new Geometry("Rear Right Perpendicular Sensor debug", new Line(rearRightPerpendicularSensor.getOrigin(), rearRightPerpendicularSensor.getDirection().add(rearRightPerpendicularSensor.getOrigin())));
			debugRearRightPerpendicular.setMaterial(rayMaterial);
			app.getRootNode().attachChild(debugRearRightPerpendicular);

		}
	}

	private void createRays(){
		Vector3f rayOrigin = getRayOrigin(.11f);
		centerLeftSensor = new Ray(rayOrigin, new Vector3f(0, 0, -1));
		
		rayOrigin = getRayOrigin(-.11f);
		centerRightSensor = new Ray(rayOrigin, new Vector3f(0, 0, -1));
		
		rayOrigin = getRayOrigin(FastMath.HALF_PI);
		leftSensor = new Ray(rayOrigin, new Vector3f(0, 0, -1));
		
		rayOrigin = getRayOrigin(-FastMath.HALF_PI);
		rightSensor = new Ray(rayOrigin, new Vector3f(0, 0, -1));
		
		rayOrigin = getRayOrigin(.7854f);
		upperLeftPerpendicularSensor = new Ray(rayOrigin, SimUtilities.getPerpendicularDirection(robotControl, true));
		
		rayOrigin = getRayOrigin(-.7854f);
		upperRightPerpendicularSensor = new Ray(rayOrigin, SimUtilities.getPerpendicularDirection(robotControl, false));		
		
		rayOrigin = getRayOrigin(2.356f);	
		rearLeftPerpendicularSensor = new Ray(rayOrigin, SimUtilities.getPerpendicularDirection(robotControl, true));
		
		rayOrigin = getRayOrigin(-2.356f);
		rearRightPerpendicularSensor = new Ray(rayOrigin, SimUtilities.getPerpendicularDirection(robotControl, false));
	}

	private Vector3f getRayOrigin(float angle) {
		return SimUtilities.Griebel_DeweyMethod(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), angle, .25f, -.15f);
	}

	@Override
	protected void controlRender(RenderManager arg0, ViewPort arg1) {

	}


}
