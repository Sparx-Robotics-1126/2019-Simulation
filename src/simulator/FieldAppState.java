package simulator;


import java.util.ArrayList;
import java.util.Random;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.Collidable;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

public class FieldAppState extends BaseAppState {
	private SimMain app;
	private AssetManager assetManager;
	private Node rootNode;
	private RigidBodyControl hatchCtrl;
	private Spatial hatch;
	private ArrayList<RigidBodyControl> cargoCtrlList = new ArrayList<RigidBodyControl>();
	private ArrayList<RigidBodyControl> hatchCtrlList = new ArrayList<RigidBodyControl>();
	public ArrayList<RigidBodyControl> getHatchCtrlList() {
		return hatchCtrlList;
	}
	private final float CARGO_VARIANCE = 0.0254f; // +- .5 inch for a total of 1 inch.
	private final float CARGO_RADIUS  = 0.3302f/2f - CARGO_VARIANCE; // 
	private final float CARGO_SPACING = 0.35f;
	private final float CARGO_SET_X_POS = 7.35f;
	private final float CARGO_SET_Y_POS = 2.0f;
	private final float[] HAB_HEIGHTS = {.04f, .115f, .28f};
	private final float HAB_WIDTH = 1.625f;
	private Spatial field;
	private Node tapeTargets;
	
	@Override
	protected void initialize(Application _app) {
		app = (SimMain) _app;
		assetManager = app.getAssetManager();
		rootNode = app.getRootNode();

		createField();
		createFloor();
		createVisionTargets();
		createHabs();
		createCargoPieces();
		createHatch(1f, -2f, 0.5f);
		addLight();
	}


	@Override
	public void update(float tpf) {
		for (RigidBodyControl cargoCtrl : cargoCtrlList)
		{ 
			boolean change = false;
			Vector3f temp = new Vector3f();
			cargoCtrl.getPhysicsLocation(temp);
			Float xMax = 8.2296f;
			Float yMax = 4.1148f;

			if (temp.x > xMax) {
				temp.x = 7.7500f;
				change = true;
			} 
			if (temp.x < -xMax) {
				temp.x = -7.7500f;
				change = true;
			} 
			if (temp.y > yMax) {
				temp.y =3.8500f;
				change = true;
			} 
			if (temp.y < -yMax) {
				temp.y = -3.8500f;
				change = true;
			} 
			if (change) {
				cargoCtrl.setPhysicsLocation(temp);   	
			}
		}
	}

	private void createVisionTargets() {
		Material tapeMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		tapeMaterial.setBoolean("UseMaterialColors", true);
		tapeMaterial.setColor("Diffuse", ColorRGBA.White);
		tapeTargets = new Node();
		
		for(int i = 0; i < 3; i++) { //Left and right in name scheme of geometry are of perspective of blue alliance wall
			Geometry visionTarget = new Geometry("Vision Target Blue Left " + i, new Box(.05f, .4064f, .0001f));
			RigidBodyControl visionCtrl = new RigidBodyControl(new BoxCollisionShape(new Vector3f(.05f, .4064f, .0001f)), 0);
			visionTarget.addControl(visionCtrl);
			visionTarget.setMaterial(tapeMaterial);
			visionCtrl.setPhysicsLocation(new Vector3f((float)(.535*i-1.54), 1.2f, .06f));
			app.getPhysicsSpace().add(visionCtrl);
			tapeTargets.attachChild(visionTarget);
		}
		
		for(int i = 0; i < 3; i++) {
			Geometry visionTarget = new Geometry("Vision Target Blue Right " + i, new Box(.05f, .4064f, .0001f));
			RigidBodyControl visionCtrl = new RigidBodyControl(new BoxCollisionShape(new Vector3f(.05f, .4064f, .0001f)), 0);
			visionTarget.addControl(visionCtrl);
			visionTarget.setMaterial(tapeMaterial);
			visionCtrl.setPhysicsLocation(new Vector3f((float)(.535*i-1.54), -.93f, .06f));
			app.getPhysicsSpace().add(visionCtrl);
			tapeTargets.attachChild(visionTarget);
		}
		
		for(int i = 0; i < 3; i++) {
			Geometry visionTarget = new Geometry("Vision Target Red Left " + i, new Box(.05f, .4064f, .0001f));
			RigidBodyControl visionCtrl = new RigidBodyControl(new BoxCollisionShape(new Vector3f(.05f, .4064f, .0001f)), 0);
			visionTarget.addControl(visionCtrl);
			visionTarget.setMaterial(tapeMaterial);
			visionCtrl.setPhysicsLocation(new Vector3f((float)(-.535*i+1.62), 1.28f, .06f));
			app.getPhysicsSpace().add(visionCtrl);
			tapeTargets.attachChild(visionTarget);
		}
		
		for(int i = 0; i < 3; i++) {
			Geometry visionTarget = new Geometry("Vision Target Red Right " + i, new Box(.05f, .4064f, .0001f));
			RigidBodyControl visionCtrl = new RigidBodyControl(new BoxCollisionShape(new Vector3f(.05f, .4064f, .0001f)), 0);
			visionTarget.addControl(visionCtrl);
			visionTarget.setMaterial(tapeMaterial);
			visionCtrl.setPhysicsLocation(new Vector3f((float)(-.535*i+1.62), -.93f, .06f));
			app.getPhysicsSpace().add(visionCtrl);
			tapeTargets.attachChild(visionTarget);
		}
		
		rootNode.attachChild(tapeTargets);
	}
	
	public Spatial getField() {
		return field;
	}	

	public Node getTapeMarks() {
		return tapeTargets;
	}
	
	@Override
	protected void cleanup(Application _app) {

	}

	@Override
	protected void onDisable() {

	}

	@Override
	protected void onEnable() {

	}

	private void createField() {
		field = assetManager.loadModel("Models/Field/FieldWithoutFloor.blend");
		field.rotate(FastMath.PI / 2, 0, 0); 
		CollisionShape fieldShape = CollisionShapeFactory.createMeshShape(field);
		RigidBodyControl ctrl2 = new RigidBodyControl(fieldShape, 0);
		ctrl2.setKinematic(false);
		field.addControl(ctrl2);
		app.getPhysicsSpace().add(field);
		rootNode.attachChild(field);

	}

	private void createFloor() {
		Geometry floor = new Geometry("floor", new Box(10f, 0.1f, 5f));
		RigidBodyControl floorCtrl = new RigidBodyControl(new BoxCollisionShape(new Vector3f(10f, 0.1f, 5f)), 0f);
		floor.addControl(floorCtrl);
		Material floorMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		floorMat.setBoolean("UseMaterialColors", true);
		floorMat.setColor("Diffuse", ColorRGBA.LightGray);
		floor.setMaterial(floorMat);

		floorCtrl.setPhysicsRotation(new Quaternion(1, 0, 0, 1));
		floorCtrl.setPhysicsLocation(new Vector3f(0, 0, -.04f));
		app.getPhysicsSpace().add(floor);
		rootNode.attachChild(floor);
	}

	private void createHabs() {
		Material blueHabMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		blueHabMat.setBoolean("UseMaterialColors", true);
		blueHabMat.setColor("Diffuse", ColorRGBA.Blue);

		Geometry habRamp = new Geometry("Blue Hab Ramp", new Box(.145f, HAB_WIDTH, .005f));
		RigidBodyControl habCtrlRamp = new RigidBodyControl(new BoxCollisionShape(new Vector3f(.145f, HAB_WIDTH, .005f)), 0f);
		habRamp.addControl(habCtrlRamp);
		habRamp.setMaterial(blueHabMat);
		habCtrlRamp.setPhysicsLocation(new Vector3f(-5.75f, 0f, .15f));
		habCtrlRamp.setPhysicsRotation(new Quaternion(0, .1305265f, 0, .9914448f));
		app.getPhysicsSpace().add(habRamp);
		rootNode.attachChild(habRamp);

		Geometry hab = new Geometry("Blue Hab Level One", new Box(.455f, HAB_WIDTH, HAB_HEIGHTS[0]));
		RigidBodyControl habCtrl = new RigidBodyControl(new BoxCollisionShape(new Vector3f(.455f, HAB_WIDTH, HAB_HEIGHTS[0])), 0f);
		hab.addControl(habCtrl);
		hab.setMaterial(blueHabMat);
		habCtrl.setPhysicsLocation(new Vector3f(-6.3f, 0f, .1f + HAB_HEIGHTS[0]));
		app.getPhysicsSpace().add(hab);
		rootNode.attachChild(hab);

		hab = new Geometry("Blue Hab Level Two", new Box(.6096f, HAB_WIDTH, HAB_HEIGHTS[1]));
		habCtrl = new RigidBodyControl(new BoxCollisionShape(new Vector3f(.6096f, HAB_WIDTH, HAB_HEIGHTS[1])), 0f);
		hab.addControl(habCtrl);
		hab.setMaterial(blueHabMat);
		habCtrl.setPhysicsLocation(new Vector3f(-7.35f, 0f, .14f + HAB_HEIGHTS[1]));
		app.getPhysicsSpace().add(hab);
		rootNode.attachChild(hab);

		hab = new Geometry("Blue Hab Level Three", new Box(.6096f, .61f, HAB_HEIGHTS[2] - HAB_HEIGHTS[1]));
		habCtrl = new RigidBodyControl(new BoxCollisionShape(new Vector3f(.6096f, .61f, HAB_HEIGHTS[2] - HAB_HEIGHTS[1])), 0f);
		hab.addControl(habCtrl);
		hab.setMaterial(blueHabMat);
		habCtrl.setPhysicsLocation(new Vector3f(-7.35f, 0f, .1f + HAB_HEIGHTS[2] + HAB_HEIGHTS[1]));
		app.getPhysicsSpace().add(hab);
		rootNode.attachChild(hab);

		Material redHabMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		redHabMat.setBoolean("UseMaterialColors", true);
		redHabMat.setColor("Diffuse", ColorRGBA.Red);

		habRamp = new Geometry("Red Hab Ramp", new Box(.145f, HAB_WIDTH, .005f));
		habCtrlRamp = new RigidBodyControl(new BoxCollisionShape(new Vector3f(.145f, HAB_WIDTH, .005f)), 0f);
		habRamp.addControl(habCtrlRamp);
		habRamp.setMaterial(redHabMat);
		habCtrlRamp.setPhysicsLocation(new Vector3f(5.75f, 0f, .14f));
		habCtrlRamp.setPhysicsRotation(new Quaternion(0, -.1305265f, 0, .9914448f));
		app.getPhysicsSpace().add(habRamp);
		rootNode.attachChild(habRamp);

		hab = new Geometry("Red Hab Level One", new Box(.455f, HAB_WIDTH, HAB_HEIGHTS[0]));
		habCtrl = new RigidBodyControl(new BoxCollisionShape(new Vector3f(.455f, HAB_WIDTH, HAB_HEIGHTS[0])), 0f);
		hab.addControl(habCtrl);
		hab.setMaterial(redHabMat);
		habCtrl.setPhysicsLocation(new Vector3f(6.3f, 0f, .1f + HAB_HEIGHTS[0]));
		app.getPhysicsSpace().add(hab);
		rootNode.attachChild(hab);

		hab = new Geometry("Red Hab Level Two", new Box(.6096f, HAB_WIDTH, HAB_HEIGHTS[1]));
		habCtrl = new RigidBodyControl(new BoxCollisionShape(new Vector3f(.6096f, HAB_WIDTH, HAB_HEIGHTS[1])), 0f);
		hab.addControl(habCtrl);
		hab.setMaterial(redHabMat);
		habCtrl.setPhysicsLocation(new Vector3f(7.35f, 0f, .1f + HAB_HEIGHTS[1]));
		app.getPhysicsSpace().add(hab);
		rootNode.attachChild(hab);

		hab = new Geometry("Red Hab Level Three", new Box(.6096f, .61f, HAB_HEIGHTS[2] - HAB_HEIGHTS[1]));
		habCtrl = new RigidBodyControl(new BoxCollisionShape(new Vector3f(.6096f, .61f, HAB_HEIGHTS[2] - HAB_HEIGHTS[1])), 0f);
		hab.addControl(habCtrl);
		hab.setMaterial(redHabMat);
		habCtrl.setPhysicsLocation(new Vector3f(7.35f, 0f, .1f + HAB_HEIGHTS[2] + HAB_HEIGHTS[1]));
		app.getPhysicsSpace().add(hab);
		rootNode.attachChild(hab);

	}


	private void createCargoPieces() {
		createSet(CARGO_SET_X_POS,CARGO_SET_Y_POS,CARGO_SPACING);
		createSet(-CARGO_SET_X_POS,CARGO_SET_Y_POS,CARGO_SPACING);
		createSet(CARGO_SET_X_POS,-CARGO_SET_Y_POS,CARGO_SPACING);
		createSet(-CARGO_SET_X_POS,-CARGO_SET_Y_POS,CARGO_SPACING);
	}

	private void createSet(float startX,float startY,float cargoSpacing) {
		for(int a=0;a<3;a++) 
		{
			for(int b=0;b<2;b++) 
			{
				float x = startX + (a * cargoSpacing) - (1f * cargoSpacing);	
				float y = startY + (b * cargoSpacing) - (0.5f * cargoSpacing);
				createCargo(x, y);
			}
		}
	}

	private void createCargo(float x, float y) {
		createCargo(x, y, 0.1f);
	}

	private void createCargo(float x, float y, float z) {
		Random rand = new Random();
		Sphere cargo = new Sphere(10,10, CARGO_RADIUS + CARGO_VARIANCE*rand.nextFloat());
		Geometry cargoGeom = new Geometry ("cargo", cargo);
		Material cargoMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		cargoMat.setBoolean("UseMaterialColors", true);
		cargoMat.setColor("Ambient", ColorRGBA.Orange);
		cargoMat.setColor("Diffuse", ColorRGBA.Orange);
		cargoGeom.setMaterial(cargoMat);
		rootNode.attachChild(cargoGeom);
		cargoGeom.move(x, y, cargo.radius + z);
		RigidBodyControl cargoCtrl = new RigidBodyControl(cargo.radius);
		cargoGeom.addControl(cargoCtrl);
		app.getPhysicsSpace().add(cargoGeom);
		cargoCtrl.setMass(0.2f);
		cargoCtrl.setFriction(0.1f);
		cargoCtrl.setDamping(0.01f, 0.01f);

		
	}

	
	private void createHatch(float x, float y, float z) {
		hatch = assetManager.loadModel("Models/RobotBase/Hatch/Hatch.blend");
		hatch.scale(.5f);
		rootNode.attachChild(hatch);
		hatch.move(x,y,z);
		hatch.rotate(0, 0, FastMath.PI / 2);
		CollisionShape hatchShape = CollisionShapeFactory.createDynamicMeshShape(hatch);
		hatchCtrl = new RigidBodyControl(hatchShape, 1f);
		hatch.addControl(hatchCtrl);
		app.getPhysicsSpace().add(hatch);
		hatchCtrl.setFriction(0.1f);
		hatchCtrl.setDamping(0.01f, 0.01f);
		hatchCtrlList.add(hatchCtrl);
	}



	private void addLight() {
		//You must add a light to make the models visible
		DirectionalLight sun = new DirectionalLight();
		sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
		sun.setColor(new ColorRGBA(.5f,.5f,.5f,1));
		DirectionalLight sun2 = new DirectionalLight();
		sun2.setDirection(new Vector3f(0.1f, 0.7f, -1.0f));
		sun2.setColor(new ColorRGBA(.4f,.4f,.4f,1));
		DirectionalLight sun3 = new DirectionalLight();
		sun3.setDirection(new Vector3f(0.1f, 0.7f, 1.0f));
		sun3.setColor(new ColorRGBA(.3f,.3f,.3f,1));

		rootNode.addLight(sun);
		rootNode.addLight(sun2);
		rootNode.addLight(sun3);
	}
}