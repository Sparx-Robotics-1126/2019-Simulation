package simulator;

import java.util.concurrent.ConcurrentHashMap;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

public class infoDisplay extends BaseAppState {
	private BitmapText scrollingBitmapText;
	private Robot robot;
	private Vector3f lastPosition;
	private float timePast;
	private float timeTotal;
	private float speed;

	protected void initialize(Application _app) {
		String enqBitmapFontAssetName = "Interface/Fonts/Default.fnt";
		SimpleApplication app = (SimpleApplication) _app;
		robot = app.getStateManager().getState(Robot.class);
		AssetManager assetManager = app.getAssetManager();
		ViewPort guiViewPort = app.getGuiViewPort();
		BlendMode materialBlendMode = BlendMode.Alpha;
		Node guiNode = app.getGuiNode();

		Node consoleBaseNode = new Node("Console");

		Material darkGrayMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		darkGrayMat.setColor("Color", new ColorRGBA(0, 0, 2, .3f));
		darkGrayMat.getAdditionalRenderState().setBlendMode(materialBlendMode);

		Geometry infoQuad = new Geometry("infoQuad");
		infoQuad.setMaterial(darkGrayMat);

		consoleBaseNode.attachChild(infoQuad);
		guiNode.attachChild(consoleBaseNode);

		int viewPortHeight = guiViewPort.getCamera().getHeight();
		int viewPortWidth = guiViewPort.getCamera().getWidth();
		float infoQuadHeight = (viewPortHeight / 4);
		float infoQuadWidth = (viewPortWidth / 7);
		
		BitmapFont guiFont = assetManager.loadFont(enqBitmapFontAssetName);
		scrollingBitmapText = new BitmapText(guiFont, false);
		scrollingBitmapText.setName("scrollingBitmapText");
		scrollingBitmapText.setColor("Color", new ColorRGBA(255, 0, 0, 1f));
		scrollingBitmapText.setLocalTranslation(infoQuadWidth / 3.9f, infoQuadHeight / 2f, 0);
		consoleBaseNode.attachChild(scrollingBitmapText);
		infoQuad.setMesh(new Quad(infoQuadWidth, infoQuadHeight));
		RobotCodeCommunication.run();
	}

	public void update(float tpf) {
		Vector3f newPosition = robot.getRobotBase().getWorldTranslation();
		timePast += tpf;
		timeTotal += tpf;
		if (timePast > 0.5) {
			if (lastPosition != null) {
				float distance = newPosition.distance(lastPosition);
				speed = distance / timePast;
			}
			lastPosition = newPosition.clone();
			timePast = 0;
		}

		String displayText = String.format("the total time is %.2f", timeTotal);
		displayText += String.format("\n  your speed is %.2f", speed);
		displayText += "\n Network Tables to follow";
		ConcurrentHashMap<String, Double> map= RobotCodeCommunication.getMap();
		for(String key : map.keySet()) {
			displayText += "\n" + key + " " + map.get(key);
		}
		scrollingBitmapText.setText(displayText);
	}

	@Override
	protected void cleanup(Application arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDisable() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onEnable() {
		// TODO Auto-generated method stub

	}
}
