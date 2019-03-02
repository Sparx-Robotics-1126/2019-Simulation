	package simulator;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Quad;

public class HelpDisplay extends BaseAppState {
	private ViewPort guiViewPort;
	private Node consoleBaseNode;
	private boolean helpVisible = false;

	protected void initialize(Application _app) {
		SimpleApplication app;
		app = (SimpleApplication) _app;
		Node guiNode = app.getGuiNode();
		guiViewPort = app.getGuiViewPort();
		InputManager manager = app.getInputManager();
		manager.addListener(keyListener, "h");
		manager.addMapping("h", new KeyTrigger(KeyInput.KEY_H));
		BlendMode materialBlendMode = BlendMode.Alpha;
		AssetManager assetManager = app.getAssetManager();
		String enqBitmapFontAssetName = "Interface/Fonts/Default.fnt";
		consoleBaseNode = new Node("Console");

		Material darkGrayMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		darkGrayMat.setColor("Color", new ColorRGBA(0, 0, 10, 0.3f));
		darkGrayMat.getAdditionalRenderState().setBlendMode(materialBlendMode);

		Geometry helpQuad = new Geometry("helpQuad");
		helpQuad.setMaterial(darkGrayMat);

		consoleBaseNode.attachChild(helpQuad);
		consoleBaseNode.setCullHint(CullHint.Always);

		guiNode.attachChild(consoleBaseNode);

		int viewPortHeight = guiViewPort.getCamera().getHeight();
		int viewPortWidth = guiViewPort.getCamera().getWidth();
		float helpQuadHeight = (viewPortHeight / 2.1f );
		float helpQuadWidth = (viewPortWidth / 5);
		float helpQuadStartX = guiViewPort.getCamera().getWidth() - helpQuadWidth;
		float helpQuadStartY = guiViewPort.getCamera().getHeight() - helpQuadHeight;

		BitmapFont guiFont = assetManager.loadFont(enqBitmapFontAssetName); // Interface/Fonts/Default.fnt
		BitmapText scrollingBitmapText = new BitmapText(guiFont, false);
		scrollingBitmapText.setName("scrollingBitmapText");
		scrollingBitmapText.setColor("Color", new ColorRGBA(255, 0, 0, 1f));
		scrollingBitmapText.setText("left move forward = q\n " + 
				"left move back = a\n" + 
				"right move forward = e\n" + 
				"right move back = d\n" + 
				"pause  = p\n" + "resume = space\n" + 
				"(you must resume after you start\n" + 
				"before the robot will move)\n" + 
				"reset = r\n" + 
				"help = h\n" + 
				"move camera = left mouse\n" + 
				"zoom = middle mouse\n" + 
				"pick up hatch = w\n" + 
				"drop hatch = s\n" + 
				"debug window = `\n" +
				"lift arms down = z\n" +
				"lift arms up = x\n" +
				"lead screw down = c\n" +
				"lead screw up = v");
		scrollingBitmapText.setLocalTranslation(helpQuadStartX + 100, helpQuadStartY + helpQuadHeight - 60, 0);
		consoleBaseNode.attachChild(scrollingBitmapText);

		helpQuad.setMesh(new Quad(helpQuadWidth, helpQuadHeight));
		helpQuad.setLocalTranslation(helpQuadStartX, helpQuadHeight, 0);
	}

	private final ActionListener keyListener = new ActionListener() {
		public void onAction(String name, boolean keyPressed, float tpf) {

			if (name.equals("h") && keyPressed) {
				if (helpVisible == false) {
					consoleBaseNode.setCullHint(CullHint.Dynamic);
					helpVisible = true;
				} else {
					consoleBaseNode.setCullHint(CullHint.Always);
					helpVisible = false;
				}
			}
		}

	};

	@Override
	protected void cleanup(Application arg0) {
 
	}

	@Override
	protected void onDisable() {

	}

	@Override
	protected void onEnable() {

	}
}