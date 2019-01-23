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

	private ArrayList<RigidBodyControl> cargoCtrlList = new ArrayList<RigidBodyControl>();

	private final float CARGO_VARIANCE = 0.0254f; // +- .5 inch for a total of 1 inch.
	private final float CARGO_RADIUS  = 0.3302f/2f - CARGO_VARIANCE; // 

	private final float CARGO_SPACING = 0.35f;
	private final float CARGO_SET_X_POS = 7.35f;
	private final float CARGO_SET_Y_POS = 2.0f;

	@Override
	public void update(float tpf) {
        for (RigidBodyControl ctrl : cargoCtrlList)
        { 
        	boolean change = false;
        	Vector3f temp = new Vector3f();
        	ctrl.getPhysicsLocation(temp);
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
	        	ctrl.setPhysicsLocation(temp);   	
	        }
        }
	}

	@Override
	protected void cleanup(Application _app) {

	}

	@Override
	protected void initialize(Application _app) {
		app = (SimMain) _app;
		rootNode = app.getRootNode();

		Geometry floor = new Geometry("floor", new Box(10f, 0.1f, 5f));
		RigidBodyControl floorCtrl = new RigidBodyControl(new BoxCollisionShape(new Vector3f(10f, 0.1f, 5f)), 0f);
		floor.addControl(floorCtrl);
		floorCtrl.setPhysicsRotation(new Quaternion(3, 0, 0, 3));
		floorCtrl.setPhysicsLocation(new Vector3f(0, 0, -.04f));
		app.getPhysicsSpace().add(floor);
		
		assetManager = app.getAssetManager();
		
		Spatial field = assetManager.loadModel("Models/Field/FieldWithoutFloor.blend");
		rootNode.attachChild(field);
		field.rotate(FastMath.PI / 2, 0, 0); 
			
		createSet(CARGO_SET_X_POS,CARGO_SET_Y_POS,CARGO_SPACING);
		createSet(-CARGO_SET_X_POS,CARGO_SET_Y_POS,CARGO_SPACING);
		createSet(CARGO_SET_X_POS,-CARGO_SET_Y_POS,CARGO_SPACING);
		createSet(-CARGO_SET_X_POS,-CARGO_SET_Y_POS,CARGO_SPACING);
				
		CollisionShape fieldShape = CollisionShapeFactory.createMeshShape(field);
		RigidBodyControl ctrl2 = new RigidBodyControl(fieldShape, 0);
		ctrl2.setKinematic(false);
		field.addControl(ctrl2);
		app.getPhysicsSpace().add(field);
	
	}
	
	@Override
	protected void onDisable() {

	}

	@Override
	protected void onEnable() {

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
	
	private void createCargo(float x, float y)
	{
		createCargo(x, y, 0.0f);
	}
	
	private void createCargo(float x, float y, float z)
	{
		Random rand = new Random();
		Sphere cargo = new Sphere(10,10, CARGO_RADIUS + CARGO_VARIANCE*rand.nextFloat());
		//Box cargo = new Box(1,1,1);
		Geometry cargoGeom = new Geometry ("cargo", cargo);
		Material cargoMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		cargoMat.setBoolean("UseMaterialColors", true);
		cargoMat.setColor("Ambient", ColorRGBA.Orange);
		cargoMat.setColor("Diffuse", ColorRGBA.Orange);
		cargoGeom.setMaterial(cargoMat);
		rootNode.attachChild(cargoGeom);
		cargoGeom.move(x, y, cargo.radius + 0.01f + z);
		RigidBodyControl cargoCtrl = new RigidBodyControl(cargo.radius);
		cargoGeom.addControl(cargoCtrl);
		app.getPhysicsSpace().add(cargoGeom);
		cargoCtrl.setMass(0.2f);
		cargoCtrl.setFriction(0.1f);
		cargoCtrl.setDamping(0.01f, 0.01f);
	}	
}