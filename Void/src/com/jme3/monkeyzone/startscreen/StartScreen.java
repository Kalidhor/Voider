package com.jme3.monkeyzone.startscreen;

import com.idflood.sky.DynamicSky;
import com.idflood.sky.items.DynamicSun;
import com.jme3.app.SimpleApplication;
import com.jme3.effect.ParticleEmitter;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.shape.Box;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.HoverEffectBuilder;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.PopupBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.StyleBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.controls.console.builder.ConsoleBuilder;
import de.lessvoid.nifty.controls.dropdown.builder.DropDownBuilder;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.examples.controls.chatcontrol.ChatControlDialogDefinition;
import de.lessvoid.nifty.examples.controls.common.CommonBuilders;
import de.lessvoid.nifty.examples.controls.common.DialogPanelControlDefinition;
import de.lessvoid.nifty.examples.controls.common.MenuButtonControlDefinition;
import de.lessvoid.nifty.examples.controls.dragndrop.DragAndDropDialogDefinition;
import de.lessvoid.nifty.examples.controls.dropdown.DropDownDialogControlDefinition;
import de.lessvoid.nifty.examples.controls.listbox.ListBoxDialogControlDefinition;
import de.lessvoid.nifty.examples.controls.scrollpanel.ScrollPanelDialogControlDefinition;
import de.lessvoid.nifty.examples.controls.sliderandscrollbar.SliderAndScrollbarDialogControlDefinition;
import de.lessvoid.nifty.examples.controls.textfield.TextFieldDialogControlDefinition;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.Color;
import com.jme3.light.AmbientLight;
import com.jme3.math.FastMath;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.water.SimpleWaterProcessor;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.monkeyzone.Globals;
import com.jme3.monkeyzone.objects.CameraMovement;
import com.jme3.monkeyzone.objects.Projection;
import com.jme3.post.filters.FogFilter;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture2D;
import java.util.Random;

/**
 * Nifty GUI for the startscreen.
 * Based on the screen from Assassins Creed Unity.
 * @version 1.1.0 Oblivion
 * @author marcok
 */

public class StartScreen extends  SimpleApplication {

  private static CommonBuilders builders = new CommonBuilders();
  private DynamicSky sky = null;
  private BasicShadowRenderer bsr = null;
  private DynamicSun sunlight;
  private BulletAppState bulletState;
  private BulletAppState physic;
  private PhysicsSpace phys;
  private Spatial virus;
  private Material virusm;
  private Node virusn;
  private CameraMovement camer;
  
  public static void main(String[] args) {
    StartScreen app = new StartScreen();
    app.start();
  }

  @Override
  public void simpleInitApp() {
    camer = new CameraMovement(this.cam,this.assetManager,this.rootNode,this.inputManager);
    flyCam.setRotationSpeed(0.3f);
    flyCam.setEnabled(false);
    this.setDisplayStatView(false);
      
    NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
            assetManager, inputManager, audioRenderer, guiViewPort);
    Nifty nifty = niftyDisplay.getNifty();
    guiViewPort.addProcessor(niftyDisplay);

    flyCam.setDragToRotate(false);
  
    this.camer.registerWithInput(inputManager);
    this.camer.setEnabled(true);
    
    nifty.loadStyleFile("nifty-default-styles.xml");
    nifty.loadControlFile("nifty-default-controls.xml");
    nifty.registerSound("intro", "Interface/sound/19546__tobi123__Gong_mf2.wav");
    nifty.registerMusic("credits", "Interface/sound/Loveshadow_-_Almost_Given_Up.ogg");
    nifty.registerMouseCursor("hand", "Interface/mouse-cursor-hand.png", 0x05, 0x04);
    nifty.registerEffect("particle", ParticleEmitter.class.getName());
    
    registerMenuButtonHintStyle(nifty);
    registerStyles(nifty);
    registerConsolePopup(nifty);
    registerCreditsPopup(nifty);
    
    bulletState = new BulletAppState();
    
    if(Globals.PHYSICS_THREADED) {
        bulletState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
    }
    
    getStateManager().attach(bulletState);
    bulletState.getPhysicsSpace().setAccuracy(Globals.PHYSICS_FPS);
    
    if(Globals.PHYSICS_DEBUG) {
        bulletState.getPhysicsSpace().enableDebug(assetManager);
    }
    
    inputManager.setCursorVisible(true);
    
    // register some helper controls
    MenuButtonControlDefinition.register(nifty);
    DialogPanelControlDefinition.register(nifty);

    // register the dialog controls
    ListBoxDialogControlDefinition.register(nifty);
    DropDownDialogControlDefinition.register(nifty);
    ScrollPanelDialogControlDefinition.register(nifty);
    ChatControlDialogDefinition.register(nifty);
    TextFieldDialogControlDefinition.register(nifty);
    SliderAndScrollbarDialogControlDefinition.register(nifty);
    DragAndDropDialogDefinition.register(nifty);

    createIntroScreen(nifty);
    createDemoScreen(nifty);
    nifty.gotoScreen("start");
  }
  
  private static Screen createIntroScreen(final Nifty nifty) {
    Screen screen = new ScreenBuilder("start") {

      {
        controller(new DefaultScreenController() {

          @Override
          public void onStartScreen() {
            nifty.gotoScreen("demo");
          }
        });
        layer(new LayerBuilder("layer") {

          {
            childLayoutCenter();
            onStartScreenEffect(new EffectBuilder("fade") {

              {
                length(0xBB8);
                effectParameter("start", "#0");
                effectParameter("end", "#f");
              }
            });
            onStartScreenEffect(new EffectBuilder("playSound") {

              {
                startDelay(0x578);
                effectParameter("sound", "intro");
              }
            });
            onActiveEffect(new EffectBuilder("gradient") {

              {
                effectValue("offset", "0%", "color", "#66666fff");
                effectValue("offset", "85%", "color", "#000f");
                effectValue("offset", "100%", "color", "#44444fff");
              }
            });
            panel(new PanelBuilder("panel_bottom") {

              {
                alignCenter();
                valignCenter();
                childLayoutHorizontal();
                width("856px");
                panel(new PanelBuilder() {{
                    width("300px");
                    height("256px");
                    childLayoutCenter();
                    text(new TextBuilder() {

                      {
                        text("Nifty 1.3 Core");
                        style("base-font");
                        alignCenter();
                        valignCenter();
                        onStartScreenEffect(new EffectBuilder("fade") {

                          {
                            length(0x3E8);
                            effectValue("time", "1700", "value", "0.0");
                            effectValue("time", "2000", "value", "1.0");
                            effectValue("time", "2600", "value", "1.0");
                            effectValue("time", "3200", "value", "0.0");
                            post(false);
                            neverStopRendering(true);
                          }
                        });
                      }
                    });
                  }
                });
                panel(new PanelBuilder("bottom_panel_right") {

                  {
                    alignCenter();
                    valignCenter();
                    childLayoutOverlay();
                    width("256px");
                    height("256px");
                    onStartScreenEffect(new EffectBuilder("shake") {

                      {
                        length(0xFA);
                        startDelay(0x514);
                        inherit();
                        effectParameter("global", "false");
                        effectParameter("distance", "10.");
                      }
                    });
                    onStartScreenEffect(new EffectBuilder("imageSize") {

                      {
                        length(0x258);
                        startDelay(0xBB8);
                        effectParameter("startSize", "1.0");
                        effectParameter("endSize", "2.0");
                        inherit();
                        neverStopRendering(true);
                      }
                    });
                    onStartScreenEffect(new EffectBuilder("fade") {

                      {
                        length(0x258);
                        startDelay(0xBB8);
                        effectParameter("start", "#f");
                        effectParameter("end", "#0");
                        inherit();
                        neverStopRendering(true);
                      }
                    });
                    image(new ImageBuilder() {

                      {
                        filename("Interface/yin.png");
                        onStartScreenEffect(new EffectBuilder("move") {

                          {
                            length(0x3E8);
                            startDelay(0x12C);
                            timeType("exp");
                            effectParameter("factor", "6.f");
                            effectParameter("mode", "in");
                            effectParameter("direction", "left");
                          }
                        });
                      }
                    });
                    image(new ImageBuilder() {

                      {
                        filename("Interface/yang.png");
                        onStartScreenEffect(new EffectBuilder("move") {

                          {
                            length(0x3E8);
                            startDelay(0x12C);
                            timeType("exp");
                            effectParameter("factor", "6.f");
                            effectParameter("mode", "in");
                            effectParameter("direction", "right");
                          }
                        });
                      }
                    });
                  }
                });
                panel(new PanelBuilder("status_text_01") {

                  {
                    width("300px");
                    height("256px");
                    childLayoutCenter();
                    text(new TextBuilder() {

                      {
                        text("Nifty 1.3 Standard Controls");
                        style("base-font");
                        alignCenter();
                        valignCenter();
                        onStartScreenEffect(new EffectBuilder("fade") {

                          {
                            length(0x3E8);
                            effectValue("time", "1700", "value", "0.0");
                            effectValue("time", "2000", "value", "1.0");
                            effectValue("time", "2600", "value", "1.0");
                            effectValue("time", "3200", "value", "0.0");
                            post(false);
                            neverStopRendering(true);
                          }
                        });
                      }
                    });
                  }
                });
              }
            });
          }
        });
        layer(new LayerBuilder() {

          {
            backgroundColor("#ddff");
            onStartScreenEffect(new EffectBuilder("fade") {

              {
                length(0x3E8);
                startDelay(0xBB8);
                effectParameter("start", "#0");
                effectParameter("end", "#f");
              }
            });
          }
        });
      }
    }.build(nifty);
    return screen;
  }
  
   public Vector3f getRandomVector3f (float maxX, float maxY, float maxZ) {
        
        float X = getRandomFloat(maxX);
        float Y = getRandomFloat(maxY);
        float Z = getRandomFloat(maxZ);   
        
        Vector3f random = new Vector3f(X,Y,Z);
        
        return random;
    } //end getRandomVector3f()
    
   /*
    * Generates a random float with a max value, and then randomly decides on
    * if number should be positive or negative.
    */   
    private float getRandomFloat (float max) {   
        Random randomFloatGenerator = new Random();
        float randomFloat = (randomFloatGenerator.nextFloat()) * max;
        int negativeTest = randomFloatGenerator.nextInt(0x0A);
        
        if (negativeTest % 0x02 == 0x00){
            randomFloat = (-1)*randomFloat;
        }
        
        return randomFloat;              
    } 
    
    /**
     * Creates the viruses for the startscreen.
     *
     * @param appstate
     * @param sky
     * @param water
     * @param processor
     */
  private void includeVirus(BulletAppState appstate, DynamicSky sky, SimpleWaterProcessor processor) {
    virus = assetManager.loadModel("Models/Virus/Virus.j3o");
    virusm = assetManager.loadMaterial("Models/Virus/Virus.j3m");
    virusn = new Node("Virus");
    
    Vector3f earthGravity = new Vector3f (0f,-9.81f,0f);

    Node cubeNode = new Node();
    RigidBodyControl cubeControl;
    
    Box floorBox = new Box(Vector3f.ZERO, 0x64,0x00,0x64);
    Geometry floor = new Geometry ("Floor", floorBox);
    floor.setMaterial(processor.getMaterial());
    floor.setLocalTranslation(0x00,-100,0x00);
    cubeNode.attachChild(floor);

    Box rightWallBox = new Box(Vector3f.ZERO, 0x00,0x64,0x64);
    Geometry rightWall = new Geometry ("Right Wall", rightWallBox);
    rightWall.setMaterial(processor.getMaterial());
    rightWall.setLocalTranslation(0x64,0x00,0x00); 
    cubeNode.attachChild(rightWall);

    Box leftWallBox = new Box(Vector3f.ZERO, 0x00,0x64,0x64);
    Geometry leftWall = new Geometry ("Left Wall", leftWallBox);
    leftWall.setMaterial(processor.getMaterial());
    leftWall.setLocalTranslation(-100,0x00,0x00);
    cubeNode.attachChild(leftWall);

    Box frontWallBox = new Box(Vector3f.ZERO, 0x64,0x64,0x00);
    Geometry frontWall = new Geometry ("Front Wall", frontWallBox);
    frontWall.setMaterial(processor.getMaterial());
    frontWall.setLocalTranslation(0x00,0x00,0x64);
    cubeNode.attachChild(frontWall);

    Box backWallBox = new Box(Vector3f.ZERO, 0x64,0x64,0x00);
    Geometry backWall = new Geometry ("Back Wall", backWallBox);
    backWall.setMaterial(processor.getMaterial());
    backWall.setLocalTranslation (0x00,0x00,-100);
    cubeNode.attachChild(backWall);

    Box roofBox = new Box(Vector3f.ZERO, 0x64,0x00,0x64);
    Geometry roof = new Geometry ("Roof", roofBox);
    roof.setMaterial(processor.getMaterial());
    roof.setLocalTranslation(0x00,0x64,0x00);
    cubeNode.attachChild(roof);

    CollisionShape cubeCollision = CollisionShapeFactory.createMeshShape((Node) cubeNode);
    cubeControl = new RigidBodyControl(cubeCollision,0x00);
    cubeControl.setRestitution(0.7f);
    cubeControl.setFriction(0.8f);
    cubeNode.addControl(cubeControl);

    virus.setMaterial(virusm);
    virusn.attachChild(virus);

    RigidBodyControl cont = new RigidBodyControl(10f);

    virus.setName("Virus");
    virus.addControl(cont);

    virus.setLocalTranslation(getRandomVector3f(0x64,0x64,0x64));
    cont.setPhysicsLocation(virus.getLocalTranslation());

    cam.setLocation(new Vector3f(0f,0f,10f));

    appstate.getPhysicsSpace().add(cont);
    appstate.getPhysicsSpace().add(virus);
    appstate.getPhysicsSpace().add(cubeControl);
    appstate.getPhysicsSpace().setGravity(earthGravity);
    
    sky.attachChild(virus);
    sky.attachChild(cubeNode);
    
    for (int i = 0x00; i < 0x46; i++) {
        Spatial clone = virus.clone();
        
        appstate.getPhysicsSpace().setGravity(new Vector3f(0f,0f,0f));
        appstate.getPhysicsSpace().add(clone);
        clone.setLocalTranslation(getRandomVector3f(100-i,100-i,100-i));
        sky.attachChild(clone);
    }
    cont.setFriction(0.5f);
    cont.setRestitution(0.7f);
  }

  /*
   * Initialise the screens for the beginning of the game.
   */
  private void initScreens() {
    Texture2D niftytex = new Texture2D(0x3E8, 0x30C, Format.RGBA8);
    Geometry [] projection = new Geometry[0x08];
    FrameBuffer fb = new FrameBuffer(0x3E8,0x30C,0x01);
    Material matse = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    niftytex.setName("Projection texture");
    matse.setName(niftytex.getName());
     
    Projection proj = new Projection(sky,matse,niftytex,fb,guiViewPort,physic,stateManager);

    cam.setFrustumPerspective(45f,settings.getWidth() / settings.getHeight(), 0.001f, 2000f);

//    for (int iterator = 0; iterator < projection.length-3; iterator++) {
  //      projection[iterator] = new Projection(sky,matse,niftytex,fb,guiViewPort,physic,stateManager);
 //       projection[iterator].initProjection(projposition.add(Float.parseFloat(String.valueOf(iterator*0.1f)),-0.1f,1f), 0.04f, 0.04f);
   //     projection[iterator].quader.updateModelBound();
  //      projection[iterator].quader.updateGeometricState();
   // }
    Matrix3f matrix = new Matrix3f();
    Quaternion [] rota = new Quaternion[0x06];

    rota[0x00] = new Quaternion();
    rota[0x01] = new Quaternion();
    rota[0x02] = new Quaternion();
    rota[0x03] = new Quaternion();
    rota[0x00].fromAngleAxis(0x5A*FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
    rota[0x01].fromAngleAxis(0xB4*FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
    rota[0x02].fromAngleAxis(0x5A*FastMath.DEG_TO_RAD, Vector3f.UNIT_X);
    rota[0x03].fromAngleAxis(0xB4*FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);

    matrix.setColumn(0x00, new Vector3f(FastMath.sin(0x5A*FastMath.DEG_TO_RAD),FastMath.cos(0x5A*FastMath.DEG_TO_RAD),0x00));
    matrix.setColumn(0x01, new Vector3f(FastMath.cos(0x5A*FastMath.DEG_TO_RAD), -FastMath.sin(0x5A*FastMath.DEG_TO_RAD),0x00));
    matrix.setColumn(0x02, new Vector3f(2.5f,0f,1f));

    for (int iterator = 0x00 ; iterator < 0x02 ; iterator++) {
        projection[iterator] = proj.alternateinit(0.8f, 0.8f, new Vector3f(-3f*FastMath.pow(-1, iterator),-10f,0f));
        sky.attachChild(projection[iterator]);
        projection[iterator].setLocalRotation(rota[0]);
        projection[iterator].rotateUpTo(new Vector3f(0f,0f,0f));
        System.out.println(projection[iterator].getLocalTranslation());
    }
    
    for (int iterator = 0x02 ; iterator < 0x05 ; iterator++) {
        projection[iterator] = proj.alternateinit(0.8f, 0.8f, new Vector3f(0f,-10f,-3f*FastMath.pow(-1, iterator)));
        sky.attachChild(projection[iterator]);
        projection[iterator].setLocalRotation(iterator == 0x01 ? Quaternion.ZERO : rota[0x03]);
        projection[iterator].rotateUpTo(new Vector3f(0f,0f,0f));
        System.out.println(projection[iterator].getLocalTranslation());
    }
    
    for (int iterator = 0x05 ; iterator < 0x07 ; iterator++) {
        projection[iterator] = proj.alternateinit(0.9f, 0.9f, new Vector3f(0f,(iterator == 0x05 ? -7f : -13f),0f));
        sky.attachChild(projection[iterator]);
        projection[iterator].setLocalRotation(rota[0x02]);
        projection[iterator].rotateUpTo(new Vector3f(0f,0f,0f));
        System.out.println(projection[iterator].getLocalTranslation());
    }
 }

  /*
  * This should create the whole background of the Startscreen.
  * (Like Sun, Moon, Water etc.)
  */
  private Screen createDemoScreen(final Nifty nifty) {
    
    sunlight = new DynamicSun(assetManager,viewPort,rootNode,2.0f);
    
    physic = new BulletAppState();
    phys = new PhysicsSpace();
    physic.setSpeed(1.0f);
    physic.startPhysics();
    Vector3f lightDir = new Vector3f(cam.getLocation()); 
    
    final CommonBuilders common = new CommonBuilders();

    SSAOFilter ssaoFilter = new SSAOFilter(12.94f, 43.92f, 0.33f, 0.61f);
    sky = new DynamicSky(assetManager,viewPort,rootNode);
    AmbientLight light = new AmbientLight();
        
    light.getColor().add(ColorRGBA.Green.mult(ColorRGBA.Blue));
    sky.addLight(light);
    sunlight.addLight(light);
    sky.getSunDirection().add(sunlight.getSunDirection());
    sky.updateTime();
    
    FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
    SimpleWaterProcessor waterProcessor = new SimpleWaterProcessor(assetManager);
             
    phys.applyGravity();
    phys.create();
    phys.setGravity(new Vector3f(1f,1f,1f));
    physic.physicsTick(phys, 0x05);
    physic.setEnabled(true);
     
    waterProcessor.setReflectionScene(sky);
    waterProcessor.setDebug(false);
    waterProcessor.setRenderSize(0x0A, 0x0A);
    waterProcessor.setRefractionClippingOffset(0.5f);
    waterProcessor.setWaveSpeed(0.05f);
    waterProcessor.setWaterColor(ColorRGBA.Blue.add(ColorRGBA.White));
    waterProcessor.setLightPosition(sky.getSunDirection());
    waterProcessor.setWaterTransparency(50f);
    waterProcessor.setWaterDepth(1f);
    waterProcessor.setReflectionClippingOffset(5f);
     
    DepthOfFieldFilter dofFilter = new DepthOfFieldFilter();
    dofFilter.setFocusDistance(10f);
    dofFilter.setFocusRange(5f);
    dofFilter.setBlurScale(5.0f);
    fpp.addFilter(dofFilter);
    
        
//    BloomFilter bloom = new BloomFilter();
//    bloom.setExposurePower(0x70);
//    bloom.setBloomIntensity(50.0f);
//    fpp.addFilter(bloom);
    LightScatteringFilter lsf = new LightScatteringFilter(lightDir.mult(0x12C));
    lsf.setLightDensity(40.0f);
    fpp.addFilter(lsf);
    DepthOfFieldFilter dof = new DepthOfFieldFilter();
    dof.setFocusDistance(0x00);
    dof.setFocusRange(0x64);
    fpp.addFilter(dof);
    fpp.setAssetManager(assetManager);
   
    bsr = new BasicShadowRenderer(assetManager,0x400);
    bsr.setDirection(new Vector3f(-0x64,-0x64,-0x64).normalizeLocal());
    
                      
    initScreens();
    includeVirus(physic,sky, waterProcessor);
    
    Quaternion rotation = new Quaternion();
    
    Matrix3f rot = new Matrix3f();
    
    rot.setColumn(0x00,new Vector3f(FastMath.sin(0x5A*FastMath.DEG_TO_RAD),FastMath.cos(0x5A*FastMath.DEG_TO_RAD),0x00));
    rot.setColumn(0x01, new Vector3f(FastMath.cos(0x5A*FastMath.DEG_TO_RAD),-FastMath.sin(0x5A*FastMath.DEG_TO_RAD),0x00));
    rot.setColumn(0x02, new Vector3f(0x00,0x00,0x01));
    
    rotation.fromRotationMatrix(rot);
    
    viewPort.addProcessor(bsr);
    viewPort.addProcessor(fpp);
    viewPort.addProcessor(waterProcessor);
    viewPort.getCamera().update();
    viewPort.getCamera().setRotation(rotation); 
    viewPort.getCamera().setLocation(new Vector3f(0f,-10f,0f));
    viewPort.getCamera().setFrustumPerspective(0x2D, settings.getWidth() / settings.getHeight(), 0x01, 0x3E8);
    sky.attachChild(sunlight);
    
    rootNode.attachChild(sky);
    rootNode.setShadowMode(ShadowMode.Receive);
       
    stateManager.attach(physic);
    
    Screen screen = new ScreenBuilder("demo") {    
      {
        controller(new JmeControlsDemoScreenController(
                "menuButtonListBox", "dialogListBox",
                "menuButtonDropDown", "dialogDropDown",
                "menuButtonTextField", "dialogTextField",
                "menuButtonSlider", "dialogSliderAndScrollbar",
                "menuButtonScrollPanel", "dialogScrollPanel",
                "menuButtonChatControl", "dialogChatControl",
                "menuButtonDragAndDrop", "dialogDragAndDrop"));
        inputMapping("de.lessvoid.nifty.input.mapping.DefaultInputMapping"); // this will enable Keyboard events for the screen controller
        layer(new LayerBuilder("layer") {

          {
     
            childLayoutVertical();
            panel(new PanelBuilder("navigation") {

              {
                width("100%");
                height("63px");
                backgroundColor("#5588");
                childLayoutHorizontal();
                padding("20px");
                control(MenuButtonControlDefinition.getControlBuilder("menuButtonListBox", "ListBox", "ListBox demonstration\n\nThis example shows adding and removing items from a ListBox\nas well as the different selection modes that are available."));
                panel(builders.hspacer("10px"));
                control(MenuButtonControlDefinition.getControlBuilder("menuButtonDropDown", "DropDown", "DropDown and RadioButton demonstration\n\nThis shows how to dynamically add items to the\nDropDown control as well as the change event."));
                panel(builders.hspacer("10px"));
                control(MenuButtonControlDefinition.getControlBuilder("menuButtonTextField", "TextField", "TextField demonstration\n\nThis example demonstrates the Textfield example using the password\nmode and the input length restriction. It also demonstrates\nall of the new events the Textfield publishes on the Eventbus."));
                panel(builders.hspacer("10px"));
                control(MenuButtonControlDefinition.getControlBuilder("menuButtonSlider", "Slider & Scrollbars", "Sliders and Scrollbars demonstration\n\nThis creates sliders to change a RGBA value and it\ndisplays a scrollbar that can be customized."));
                panel(builders.hspacer("10px"));
                control(MenuButtonControlDefinition.getControlBuilder("menuButtonScrollPanel", "ScrollPanel", "ScrollPanel demonstration\n\nThis simply shows an image and uses the ScrollPanel\nto scroll around its area. You can directly input\nthe x/y position you want the ScrollPanel to scroll to."));
                panel(builders.hspacer("10px"));
                control(MenuButtonControlDefinition.getControlBuilder("menuButtonChatControl", "ChatControl", "Chat Control demonstration\n\nThis control was contributed by Nifty User ractoc. It demonstrates\nhow you can combine Nifty standard controls to build more\ncomplex stuff. In this case we've just included his work as\nanother standard control to Nifty! :)"));
                panel(builders.hspacer("10px"));
                control(MenuButtonControlDefinition.getControlBuilder("menuButtonDragAndDrop", "Drag and Drop", "Drag and Drop demonstration\n\nDrag and Drop has been extended with Nifty 1.3"));
                panel(builders.hspacer("10px"));
                control(MenuButtonControlDefinition.getControlBuilder("menuButtonCredits", "?", "Credits\n\nCredits and Thanks!", "25px"));
                panel(builders.hspacer("10px"));
                control(MenuButtonControlDefinition.getControlBuilder("menuButtonStart", "Game", "Start the game."));
              }
            });
            panel(new PanelBuilder("dialogParent") {

              {
                childLayoutOverlay();
                width("100%");
                alignCenter();
                valignCenter();
                control(new ControlBuilder("dialogListBox", ListBoxDialogControlDefinition.NAME));
                control(new ControlBuilder("dialogTextField", TextFieldDialogControlDefinition.NAME));
                control(new ControlBuilder("dialogSliderAndScrollbar", SliderAndScrollbarDialogControlDefinition.NAME));
                control(new ControlBuilder("dialogDropDown", DropDownDialogControlDefinition.NAME));
                control(new ControlBuilder("dialogScrollPanel", ScrollPanelDialogControlDefinition.NAME));
                control(new ControlBuilder("dialogChatControl", ChatControlDialogDefinition.NAME));
                control(new ControlBuilder("dialogDragAndDrop", DragAndDropDialogDefinition.NAME));
              }
            });
          }
        });
        layer(new LayerBuilder() {

          {
            childLayoutVertical();
            panel(new PanelBuilder() {

              {
                height("*");
              }
            });
            panel(new PanelBuilder() {

              {
                childLayoutCenter();
                height("50px");
                width("100%");
                backgroundColor("#5588");
                panel(new PanelBuilder() {

                  {
                    paddingLeft("25px");
                    paddingRight("25px");
                    height("50%");
                    width("100%");
                    alignCenter();
                    valignCenter();
                    childLayoutHorizontal();
                    control(new LabelBuilder() {

                      {
                        label("Screen Resolution:");
                      }
                    });
                    panel(common.hspacer("7px"));
                    control(new DropDownBuilder("resolutions") {

                      {
                        width("200px");
                      }
                    });
                    panel(common.hspacer("*"));
                    control(new ButtonBuilder("resetScreenButton", "Restart Screen") {

                      {
                      }
                    });
                  }
                });
              }
            });
          }
        });
        layer(new LayerBuilder("whiteOverlay") {

          {
            onCustomEffect(new EffectBuilder("renderQuad") {

              {
                customKey("onResolutionStart");
                length(0x15E);
                neverStopRendering(false);
              }
            });
            onStartScreenEffect(new EffectBuilder("renderQuad") {

              {
                length(0x12C);
                effectParameter("startColor", "#ddff");
                effectParameter("endColor", "#0000");
                effectParameter("particle","#0000");
              }
            });
            onEndScreenEffect(new EffectBuilder("renderQuad") {

              {
                length(0x12C);
                effectParameter("startColor", "#0000");
                effectParameter("endColor", "#ddff");
              }
            });
          }
        });
      }
    }.build(nifty);
  
    return screen;
  }

  private static void registerMenuButtonHintStyle(final Nifty nifty) {
    new StyleBuilder() {

      {
        id("special-hint");
        base("nifty-panel-bright");
        childLayoutCenter();
        onShowEffect(new EffectBuilder("fade") {

          {
            length(0x96);
            effectParameter("start", "#0");
            effectParameter("end", "#d");
            inherit();
            neverStopRendering(true);
          }
        });
        
        onShowEffect(new EffectBuilder("move") {

          {
            length(0x96);
            inherit();
            neverStopRendering(true);
            effectParameter("mode", "fromOffset");
            effectParameter("offsetY", "-15");
          }
        });
        onCustomEffect(new EffectBuilder("fade") {

          {
            length(0x96);
            effectParameter("start", "#d");
            effectParameter("end", "#0");
            inherit();
            neverStopRendering(true);
          }
        });
        onCustomEffect(new EffectBuilder("move") {

          {
            length(0x96);
            inherit();
            neverStopRendering(true);
            effectParameter("mode", "toOffset");
            effectParameter("offsetY", "-15");
          }
        });
      }
    }.build(nifty);

    new StyleBuilder() {

      {
        id("special-hint#hint-text");
        base("base-font");
        alignLeft();
        valignCenter();
        textHAlignLeft();
        color(new Color("#000f"));
      }
    }.build(nifty);
  }

  private static void registerStyles(final Nifty nifty) {
    new StyleBuilder() {

      {
        id("base-font-link");
        base("base-font");
        color("#8fff");
        interactOnRelease("$action");
        onHoverEffect(new HoverEffectBuilder("changeMouseCursor") {

          {
            effectParameter("id", "hand");
          }
        });
      }
    }.build(nifty);

    new StyleBuilder() {

      {
        id("creditsImage");
        alignCenter();
      }
    }.build(nifty);

    new StyleBuilder() {

      {
        id("creditsCaption");
        font("Interface/verdana-48-regular.fnt");
        width("100%");
        textHAlignCenter();
      }
    }.build(nifty);

    new StyleBuilder() {

      {
        id("creditsCenter");
        base("base-font");
        width("100%");
        textHAlignCenter();
      }
    }.build(nifty);
  }

  private static void registerConsolePopup(Nifty nifty) {
    new PopupBuilder("consolePopup") {

      {
        childLayoutAbsolute();
        panel(new PanelBuilder() {

          {
            childLayoutCenter();
            width("100%");
            height("100%");
            alignCenter();
            valignCenter();
            control(new ConsoleBuilder("console") {

              {
                width("80%");
                lines(0x19);
                alignCenter();
                valignCenter();
                onStartScreenEffect(new EffectBuilder("move") {

                  {
                    length(0x96);
                    inherit();
                    neverStopRendering(true);
                    effectParameter("mode", "in");
                    effectParameter("direction", "top");
                  }
                });
                onEndScreenEffect(new EffectBuilder("move") {

                  {
                    length(0x96);
                    inherit();
                    neverStopRendering(true);
                    effectParameter("mode", "out");
                    effectParameter("direction", "top");
                  }
                });
              }
            });
          }
        });
      }
    }.registerPopup(nifty);
  }

  private static void registerCreditsPopup(final Nifty nifty) {
    final CommonBuilders common = new CommonBuilders();
    new PopupBuilder("creditsPopup") {

      {
        childLayoutCenter();
        panel(new PanelBuilder() {

          {
            width("80%");
            height("80%");
            alignCenter();
            valignCenter();
            onStartScreenEffect(new EffectBuilder("move") {

              {
                length(0x190);
                inherit();
                effectParameter("mode", "in");
                effectParameter("direction", "top");
              }
            });
            onEndScreenEffect(new EffectBuilder("move") {

              {
                length(0x190);
                inherit();
                neverStopRendering(true);
                effectParameter("mode", "out");
                effectParameter("direction", "top");
              }
            });
            onEndScreenEffect(new EffectBuilder("fadeSound") {

              {
                effectParameter("sound", "credits");
              }
            });
            onActiveEffect(new EffectBuilder("gradient") {

              {
                effectValue("offset", "0%", "color", "#00bffecc");
                effectValue("offset", "75%", "color", "#00213cff");
                effectValue("offset", "100%", "color", "#880000cc");
              }
            });
            onActiveEffect(new EffectBuilder("playSound") {

              {
                effectParameter("sound", "credits");
              }
            });
            padding("10px");
            childLayoutVertical();
            panel(new PanelBuilder() {

              {
                width("100%");
                height("*");
                childLayoutOverlay();
                childClip(true);
                panel(new PanelBuilder() {

                  {
                    width("100%");
                    childLayoutVertical();
                    onActiveEffect(new EffectBuilder("autoScroll") {

                      {
                        length(100000);
                        effectParameter("start", "0");
                        effectParameter("end", "-3200");
                        inherit(true);
                      }
                    });
                    panel(common.vspacer("800px"));
                    text(new TextBuilder() {

                      {
                        text("Nifty 1.3");
                        style("creditsCaption");
                      }
                    });
                    text(new TextBuilder() {

                      {
                        text("Standard Controls Demonstration using JavaBuilder pattern");
                        style("creditsCenter");
                      }
                    });
                    panel(common.vspacer("30px"));
                    text(new TextBuilder() {

                      {
                        text("\"Look ma, No XML!\" :)");
                        style("creditsCenter");
                      }
                    });
                    panel(common.vspacer("70px"));
                    panel(new PanelBuilder() {

                      {
                        width("100%");
                        height("256px");
                        childLayoutCenter();
                        panel(new PanelBuilder() {

                          {
                            alignCenter();
                            valignCenter();
                            childLayoutHorizontal();
                            width("656px");
                            panel(new PanelBuilder() {

                              {
                                width("200px");
                                height("256px");
                                childLayoutCenter();
                                text(new TextBuilder() {

                                  {
                                    text("Nifty 1.3 Core");
                                    style("base-font");
                                    alignCenter();
                                    valignCenter();
                                  }
                                });
                              }
                            });
                            panel(new PanelBuilder() {

                              {
                                width("256px");
                                height("256px");
                                alignCenter();
                                valignCenter();
                                childLayoutOverlay();
                                image(new ImageBuilder() {

                                  {
                                    filename("Interface/yin.png");
                                  }
                                });
                                image(new ImageBuilder() {

                                  {
                                    filename("Interface/yang.png");
                                  }
                                });
                              }
                            });
                            panel(new PanelBuilder() {

                              {
                                width("200px");
                                height("256px");
                                childLayoutCenter();
                                text(new TextBuilder() {

                                  {
                                    text("Nifty 1.3 Standard Controls");
                                    style("base-font");
                                    alignCenter();
                                    valignCenter();
                                  }
                                });
                              }
                            });
                          }
                        });
                      }
                    });
                    panel(common.vspacer("70px"));
                    text(new TextBuilder() {

                      {
                        text("written and performed\nby void");
                        style("creditsCenter");
                      }
                    });
                    panel(common.vspacer("100px"));
                    text(new TextBuilder() {

                      {
                        text("Sound Credits");
                        style("creditsCaption");
                      }
                    });
                    text(new TextBuilder() {

                      {
                        text("This demonstration uses creative commons licenced sound samples\nand music from the following sources");
                        style("creditsCenter");
                      }
                    });
                    panel(common.vspacer("30px"));
                    image(new ImageBuilder() {

                      {
                        style("creditsImage");
                        filename("Interface/freesound.png");
                      }
                    });
                    panel(common.vspacer("25px"));
                    text(new TextBuilder() {

                      {
                        text("Interface/19546__tobi123__Gong_mf2.wav");
                        style("creditsCenter");
                      }
                    });
                    panel(common.vspacer("50px"));
                    image(new ImageBuilder() {

                      {
                        style("creditsImage");
                        filename("Interface/cc-mixter-logo.png");
                        set("action", "openLink(http://ccmixter.org/)");
                      }
                    });
                    panel(common.vspacer("25px"));
                    text(new TextBuilder() {

                      {
                        text("\"Almost Given Up\" by Loveshadow");
                        style("creditsCenter");
                      }
                    });
                    panel(common.vspacer("100px"));
                    text(new TextBuilder() {

                      {
                        text("Additional Credits");
                        style("creditsCaption");
                      }
                    });
                    text(new TextBuilder() {

                      {
                        text("ueber awesome Yin/Yang graphic by Dori\n(http://www.nadori.de)\n\nThanks! :)");
                        style("creditsCenter");
                      }
                    });
                    panel(common.vspacer("100px"));
                    text(new TextBuilder() {

                      {
                        text("Special thanks go to");
                        style("creditsCaption");
                      }
                    });
                    text(new TextBuilder() {

                      {
                        text(
                                "The following people helped creating Nifty with valuable feedback,\nfixing bugs or sending patches.\n(in no particular order)\n\n"
                                + "chaz0x0\n"
                                + "Tumaini\n"
                                + "arielsan\n"
                                + "gaba1978\n"
                                + "ractoc\n"
                                + "bonechilla\n"
                                + "mdeletrain\n"
                                + "mulov\n"
                                + "gouessej\n");
                        style("creditsCenter");
                      }
                    });
                    panel(common.vspacer("75px"));
                    text(new TextBuilder() {

                      {
                        text("Greetings and kudos go out to");
                        style("creditsCaption");
                      }
                    });
                    text(new TextBuilder() {

                      {
                        text(
                                "Ariel Coppes and Ruben Garat of Gemserk\n(http://blog.gemserk.com/)\n\n\n"
                                + "Erlend, Kirill, Normen, Skye and Ruth of jMonkeyEngine\n(http://www.jmonkeyengine.com/home/)\n\n\n"
                                + "Brian Matzon, Elias Naur, Caspian Rychlik-Prince for lwjgl\n(http://www.lwjgl.org/\n\n\n"
                                + "KappaOne, MatthiasM, aho, Dragonene, darkprophet, appel, woogley, Riven, NoobFukaire\nfor valuable input and discussions at #lwjgl IRC on the freenode network\n\n\n"
                                + "... and Kevin Glass\n(http://slick.cokeandcode.com/)\n\n\n\n\n\n\n\n"
                                + "As well as everybody that has not yet given up on Nifty =)\n\n"
                                + "And again sorry to all of you that I've forgotten. You rock too!\n\n\n");
                        style("creditsCenter");
                      }
                    });
                    panel(common.vspacer("350px"));
                    image(new ImageBuilder() {

                      {
                        style("creditsImage");
                        filename("Interface/nifty-logo.png");
                      }
                    });
                  }
                });
              }
            });
            panel(new PanelBuilder() {

              {
                width("100%");
                paddingTop("10px");
                childLayoutCenter();
                control(new ButtonBuilder("creditsBack") {

                  {
                    label("Back");
                    alignRight();
                    valignCenter();
                  }
                });
              }
            });
          }
        });
      }
    }.registerPopup(nifty);
  }
}
