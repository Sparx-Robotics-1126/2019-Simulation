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
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;

import de.lessvoid.nifty.elements.Element;
import strongdk.jme.appstate.console.ConsoleAppState;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class infoDisplay extends BaseAppState  {
	
	private boolean initialized = false;
    private SimpleApplication app;
    private AssetManager assetManager;
    private ViewPort guiViewPort;
    private Node guiNode;
    private Node consoleBaseNode;
    private BlendMode materialBlendMode = BlendMode.Alpha;
    private Geometry infoQuad;
    private String enqBitmapFontAssetName = "Interface/Fonts/Default.fnt"; 
    // this variable is reset to null after being consumed by applyViewportChange().
    private BitmapText scrollingBitmapText;
    Robot robot;
    Vector3f lastPosition;
    float timePast;
    float timeTotal;
    float speed;
;	
	protected void initialize(Application _app) {
		initialized = true;
        app = (SimpleApplication) _app;
        assetManager = app.getAssetManager();
        guiViewPort = app.getGuiViewPort();
        robot = app.getStateManager().getState(Robot.class);
        guiNode = app.getGuiNode();
        if (consoleBaseNode == null) {
              consoleBaseNode = new Node("Console");
              
              Material darkGrayMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
              darkGrayMat.setColor("Color", new ColorRGBA(0, 0, 2, .3f));
              darkGrayMat.getAdditionalRenderState().setBlendMode(materialBlendMode);

              infoQuad = new Geometry("infoQuad");
              infoQuad.setMaterial(darkGrayMat);

              consoleBaseNode.attachChild(infoQuad);
              guiNode.attachChild(consoleBaseNode);
              
              applyViewPortChangeNotThreadSafe();
        }
	} 
    private void applyViewPortChangeNotThreadSafe() {
    	int viewPortHeight = guiViewPort.getCamera().getHeight();
        int viewPortWidth = guiViewPort.getCamera().getWidth();
        float infoQuadHeight = (viewPortHeight/4);
        float infoQuadWidth = (viewPortWidth/7);
    
            BitmapFont guiFont = assetManager.loadFont(enqBitmapFontAssetName); //Interface/Fonts/Default.fnt
            enqBitmapFontAssetName = null;
            scrollingBitmapText = new BitmapText(guiFont, false);
            scrollingBitmapText.setName("scrollingBitmapText");
            scrollingBitmapText.setLineWrapMode(LineWrapMode.Word);
            scrollingBitmapText.setColor("Color", new ColorRGBA(255,0,0,1f));
            scrollingBitmapText.setLocalTranslation(infoQuadWidth/3.9f,infoQuadHeight/2,0);
            consoleBaseNode.attachChild(scrollingBitmapText);
     
       infoQuad.setMesh(new Quad(infoQuadWidth, infoQuadHeight));
       infoQuad.setLocalTranslation(0, 0, 0);
      
    }
    public void setUseAlphaOnConsoleGeoms(boolean useAlpha) {
        if (useAlpha) {
              materialBlendMode = BlendMode.Alpha;
        } else {
              materialBlendMode = BlendMode.Off;
        }

        if (this.isInitialized()) {
              app.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                          infoQuad.getMaterial().getAdditionalRenderState().setBlendMode(materialBlendMode);
                          return null;
                    }
              });
        }
  }
    public void update(float tpf) {    
    	Vector3f newPosition = robot.getRobotBase().getWorldTranslation();
    	timePast += tpf;
    	timeTotal += tpf;
    	if(timePast > 0.5)
		{
			if (lastPosition != null) {
				float distance = newPosition.distance(lastPosition);			
				speed = distance/timePast;
			}
			lastPosition = newPosition.clone();
			timePast = 0;
		}

    	String displayText = String.format("the total time is %.2f", timeTotal);
    	displayText += String.format("\n   your speed is %.2f", speed);

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
