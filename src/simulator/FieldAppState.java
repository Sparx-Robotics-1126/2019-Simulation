package simulator;


import java.util.ArrayList;
import java.util.Random;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;
import com.jme3.scene.plugins.blender.BlenderLoader;

public class FieldAppState extends BaseAppState {
	private SimMain app;
	private AssetManager assetManager;
	private Node rootNode;
	private Geometry cargoGeom;
	private ArrayList<RigidBodyControl> cargoCtrlList = new ArrayList<RigidBodyControl>();
	
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


		assetManager = app.getAssetManager();
		assetManager.registerLocator("assets.zip", ZipLocator.class);
		assetManager.registerLoader(BlenderLoader.class, "blend");
		
		Spatial field = assetManager.loadModel("assets/Models/Field/FullField.blend");
		rootNode.attachChild(field);
		field.rotate(FastMath.PI / 2, 0, 0); 
		
		
		
		createSet(7.2517f,2.393f,0.3302f);
		createSet(-7.9102f,2.7295f,0.3302f);
		createSet(7.2517f,-2.393f,0.3302f);
		createSet(-7.9102f,-2.7295f,0.3302f);
		
		

		CollisionShape fieldShape = CollisionShapeFactory.createMeshShape(field);
		RigidBodyControl ctrl2 = new RigidBodyControl(fieldShape);
		ctrl2.setKinematic(true);
		field.addControl(ctrl2);
		app.getPhysicsSpace().add(field);
	
	}
	
	@Override
	protected void onDisable() {

	}

	@Override
	protected void onEnable() {

	}
	
	
	private void createSet(float startX,float startY,float ballSpacing) {
		for(int a=0;a<3;a++) 
		{
			/*for x values*/
			float x = startX + a * 0.3302f;
			
			for(int b=0;b<2;b++) 
			{
				/*			for y values	*/	
				float y = startY + b * 0.3302f;
				createCargo(x, y);
			}
		}

	}
	private void createCargo(float x, float y)
	{
		Random rand = new Random();
		Sphere cargo = new Sphere(10,10,0.1524f + 0.0254f*rand.nextFloat());
		//Box cargo = new Box(1,1,1);
		cargoGeom = new Geometry ("cargo", cargo);
		Material cargoMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		cargoMat.setBoolean("UseMaterialColors", true);
		cargoMat.setColor("Ambient", ColorRGBA.Orange);
		cargoMat.setColor("Diffuse", ColorRGBA.Orange);
		cargoGeom.setMaterial(cargoMat);
		rootNode.attachChild(cargoGeom);
		cargoGeom.move(x, y, cargo.radius + 0.02f);
		RigidBodyControl cargoCtrl = new RigidBodyControl(cargo.radius);
		cargoCtrl.setMass(1f);
//		cargoCtrl.setFriction(0.1f);
//		cargoCtrl.setDamping(0.01f, 0.01f);
		cargoGeom.addControl(cargoCtrl);
		app.getPhysicsSpace().add(cargoGeom);
		cargoCtrlList.add(cargoCtrl);
		
	}
	
	
}
