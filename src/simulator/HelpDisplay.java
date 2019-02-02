package simulator;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Quad;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;

public class HelpDisplay extends BaseAppState  {
	
	private boolean initialized = false;
    private boolean visible = false;
    private SimpleApplication app;
    private AssetManager assetManager;
    private InputManager inputManager;
    private ViewPort guiViewPort;
    private Node guiNode;
    private Node consoleBaseNode;
    private BlendMode materialBlendMode = BlendMode.Alpha;
    private Geometry helpQuad;
    private String enqBitmapFontAssetName = "Interface/Fonts/Default.fnt"; // this variable is reset to null after being consumed by applyViewportChange().
    private BitmapText scrollingBitmapText;
	private boolean helpVisible = false;
  	
	protected void initialize(Application _app) {
		
		initialized = true;
        app = (SimpleApplication) _app;
        assetManager = app.getAssetManager();
        inputManager = app.getInputManager();
        guiViewPort = app.getGuiViewPort();
        guiNode = app.getGuiNode();
        visible = true;
        InputManager manager = app.getInputManager();
		manager.addMapping("h", new KeyTrigger(KeyInput.KEY_H));
		manager.addListener(keyListener,"h");

        if (consoleBaseNode == null) {
              consoleBaseNode = new Node("Console");
              
              Material darkGrayMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
              darkGrayMat.setColor("Color", new ColorRGBA(0, 0, 10, 0.3f));
              darkGrayMat.getAdditionalRenderState().setBlendMode(materialBlendMode);

              helpQuad = new Geometry("helpQuad");
              helpQuad.setMaterial(darkGrayMat);
              
              consoleBaseNode.attachChild(helpQuad);
              consoleBaseNode.setCullHint(CullHint.Always);
              
              guiNode.attachChild(consoleBaseNode); 
              applyViewPortChangeNotThreadSafe();
        }
	} 
    private void applyViewPortChangeNotThreadSafe() {
    	int viewPortHeight = guiViewPort.getCamera().getHeight();
        int viewPortWidth = guiViewPort.getCamera().getWidth();
    	 float helpQuadHeight = (viewPortHeight/2);
         float helpQuadWidth = (viewPortWidth/5);
         float helpQuadStartX = guiViewPort.getCamera().getWidth() - helpQuadWidth;
         float helpQuadStartY = guiViewPort.getCamera().getHeight() - helpQuadHeight;

            BitmapFont guiFont = assetManager.loadFont(enqBitmapFontAssetName); //Interface/Fonts/Default.fnt
            enqBitmapFontAssetName = null;
            scrollingBitmapText = new BitmapText(guiFont, false);
            scrollingBitmapText.setName("scrollingBitmapText");
            scrollingBitmapText.setLineWrapMode(LineWrapMode.Word);
            scrollingBitmapText.setColor("Color", new ColorRGBA(255,0,0,1f));
            scrollingBitmapText.setText("left move forward = q\n "
               + "left move back = a\n"
               + "right move forward = e\n"
               + "right move back = d\n" 
               + "pause  = p\n"
               + "resume = space\n"
               + "(you must resume after you start\n"
               + "before the robot will move)\n"
               + "reset = r\n"
               + "help = h\n"
               + "move camera = left mouse\n"
               + "zoom = middle mouse\n"
               + "pick up hatch = w\n"
               + "drop hatch = s\n"
               + "debug window = t");
            scrollingBitmapText.setLocalTranslation(helpQuadStartX + 100,
            		helpQuadStartY + helpQuadHeight - 100,0);
            consoleBaseNode.attachChild(scrollingBitmapText);	 
   
       helpQuad.setMesh(new Quad(helpQuadWidth, helpQuadHeight));
       helpQuad.setLocalTranslation(helpQuadStartX, helpQuadHeight, 0);
    }
    public void setUseAlphaOnConsoleGeoms(boolean useAlpha) {
        if (useAlpha) {
              materialBlendMode = BlendMode.Alpha;
        } else {
              materialBlendMode = BlendMode.Off;
      if (this.isInitialized()) {
          app.enqueue(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                      helpQuad.getMaterial().getAdditionalRenderState().setBlendMode(materialBlendMode);
                      return null;
            }
	      });
	    }
      }
    }		
	private final ActionListener keyListener = new ActionListener() {		
		 public void onAction(String name, boolean keyPressed, float tpf) {

				if (name.equals("h") && keyPressed) {
					if(helpVisible == false) {
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
	
	
	
