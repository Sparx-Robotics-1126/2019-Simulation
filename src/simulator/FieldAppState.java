package simulator;


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
	private final float CARGO_VARIANCE = 0.0254f; // +- .5 inch for a total of 1 inch.
	private final float CARGO_RADIUS  = 0.3302f/2f - CARGO_VARIANCE; // 

	private final float CARGO_SPACEING = 0.35f;
	private final float CARGO_SET_X_POS = 7.35f;
	private final float CARGO_SET_Y_POS = 2.0f;
	
	@Override
	public void update(float tpf) {
        
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
		
		createSet(CARGO_SET_X_POS,CARGO_SET_Y_POS,CARGO_SPACEING);
		createSet(-CARGO_SET_X_POS,CARGO_SET_Y_POS,CARGO_SPACEING);
		createSet(CARGO_SET_X_POS,-CARGO_SET_Y_POS,CARGO_SPACEING);
		createSet(-CARGO_SET_X_POS,-CARGO_SET_Y_POS,CARGO_SPACEING);
				

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
		cargoGeom.move(x, y, cargo.radius);
		RigidBodyControl cargoCtrl = new RigidBodyControl(cargo.radius);
		cargoGeom.addControl(cargoCtrl);
		app.getPhysicsSpace().add(cargoGeom);
		cargoCtrl.setMass(0.2f);
		cargoCtrl.setFriction(0.1f);
		cargoCtrl.setDamping(0.01f, 0.01f);
	}
	
	
}
