//package simulator;
//
//import com.jme3.app.Application;
//import com.jme3.app.SimpleApplication;
//import com.jme3.app.state.BaseAppState;
//import com.jme3.input.InputManager;
//import com.jme3.input.KeyInput;
//import com.jme3.input.controls.ActionListener;
//import com.jme3.input.controls.KeyTrigger;
//import com.jme3.niftygui.NiftyJmeDisplay;
//import com.jogamp.opengl.util.awt.TextRenderer;
//
//import de.lessvoid.nifty.Nifty;
//import de.lessvoid.nifty.builder.EffectBuilder;
//import de.lessvoid.nifty.builder.LayerBuilder;
//import de.lessvoid.nifty.builder.PanelBuilder;
//import de.lessvoid.nifty.builder.ScreenBuilder;
//import de.lessvoid.nifty.builder.TextBuilder;
//import de.lessvoid.nifty.controls.Label;
//import de.lessvoid.nifty.elements.Element;
//import de.lessvoid.nifty.screen.ScreenController;
//import de.lessvoid.nifty.screen.DefaultScreenController;
//import de.lessvoid.nifty.screen.Screen;
//
//
//
//
//class DebugWindow extends BaseAppState implements ScreenController    {
//	Nifty nifty;
//	Screen textScreen;
//	
//	static DebugWindow myself = null;
//	public static DebugWindow getInstance() {
//		return myself;
//	}
//	
//	public void log(String logLine) {
//		//System.out.println(textScreen.debugOutput());
//	}
//	
//	public void bind(Nifty nifty, Screen screen)
//	{
//	  textScreen = screen;
//	  Element element = screen.findElementByName("debug_panel_mid");
//	  element.findNiftyControl("debug_panel_mid", Label.class).setText("Good bye");
//	}
//	
//	public void onStartScreen()
//	{
//	}
//	
//	public void onEndScreen()
//	{
//		
//	}
//	
//	@Override
//	protected void cleanup(Application arg0) {
//		// TODO Auto-generated method stub
//	}
//
//	@Override
//	protected void initialize(Application _app) {
//		myself = this;
//		SimpleApplication app = (SimpleApplication) _app;
//		
//		
//		NiftyJmeDisplay niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
//		        getApplication().getAssetManager(),
//		        getApplication().getInputManager(),
//		        getApplication().getAudioRenderer(),
//		        getApplication().getGuiViewPort());
//
//		nifty = niftyDisplay.getNifty();
//		getApplication().getGuiViewPort().addProcessor(niftyDisplay);
//
//		nifty.addScreen("hideDebug", new ScreenBuilder("hideDebug") {{
//		    controller(new DefaultScreenController());
//		    }}.build(nifty));
//		
//		nifty.addScreen("debug", new ScreenBuilder("debug") {{
//		    controller(DebugWindow.this);
//		    // layer added
//		    layer(new LayerBuilder("background") {{
//		        childLayoutCenter();
//		    }});
//		    
//		    layer(new LayerBuilder("foreground") {{
//		        childLayoutCenter();
//
//		        panel(new PanelBuilder("debug_panel_mid") {{
//		            childLayoutVertical();
//		            alignLeft();
//		            valignTop();
//		            height("30%");
//		            width("30%");
//		            backgroundColor("#46821");
//		            // add text
//		            text(new TextBuilder() {{
//		                text("hi");
//				        font("Interface/Fonts/Default.fnt");
//		                wrap(true);
//		                color("#ff0000");
//		                height("100%");
//		                width("20%");
//		                onActiveEffect(new EffectBuilder("textSize"){{
//                            effectParameter("endSize", "1.5");
//                        }});
//		        	}});
//		        }});
//            }});
//		}}.build(nifty));
//		
//		InputManager manager = app.getInputManager();
//		manager.addMapping("t", new KeyTrigger(KeyInput.KEY_T));
//		manager.addListener(keyListener,"t");
//	};
//		
//	private final ActionListener keyListener = new ActionListener() {
//		boolean hudVisible;	
//			
//		public void onAction(String name, boolean keyPressed, float tpf) {
//			if (name.equals("t") && keyPressed) {
//				if(hudVisible == false) {
//				    nifty.gotoScreen("debug");	
//				    hudVisible = true;
//				} else {
//					nifty.gotoScreen("hideDebug");
//					hudVisible = false;
//				}
//			}
//		}
//	};
//
//	@Override
//	protected void onDisable() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	protected void onEnable() {
//		// TODO Auto-generated method stub
//
//	}
//}