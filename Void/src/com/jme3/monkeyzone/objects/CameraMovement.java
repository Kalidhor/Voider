package com.jme3.monkeyzone.objects;

import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.Joystick;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Spline;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.control.Control;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * A list of all possible velocities of the camera.
 */
enum CameraSpeed {
    /*
     * Use this only if the camera rotation is normalized.
     */
    FAST(),
    /*
     * Normal rotationspeed, but not used in the game itself.
     */
    MEDIUM(),
    /*
     * Look at the method for more information about the speed.
     */
    SLOW(),
};

abstract interface Movement {
    public Camera getCamera();
    public String getAction();
    public float getCameraSpeed();
    public void setCameraLocation(Vector3f position, Vector3f ... relative);
    public Vector3f getSiteDirection();
    public void initializeEvents (final int indexNumber);
    public void registerWithInput(InputManager inputManager);
    public List<String> getDimensions();
    public List<String> getViewPorts();
    public List<String> getFrustums();
    public float setCameraSpeed();
}

/**
 * Camera for the slow rotations at the beginning of the game.
 * Use this class instead of the normal FlyByCamera, because it supports more 
 * features than FlyByCamera.
 * @version 1.1.0 Oblivion
 * @see getCameraSpeed() for velocity values.
 * @author marcok <h1> Kalidhor (oldklickers@gmail.com) </h1>
 */
public final class CameraMovement extends FlyByCamera implements Movement {
    protected final InputManager InputManager;
    protected transient Vector3f direction;
    protected List<List<? extends String>> attribute = new ArrayList<List<? extends String>>();
    protected List<String> frustum = new ArrayList();
    protected List<String> dimension = new ArrayList();
    protected List<String> viewPort = new ArrayList();
    protected float midvelocity;
    protected long acceleration;
    protected float velocity;
    private   CameraSpeed speed;
    protected Matrix3f rotationmatrix;
    protected Integer degree;
    protected InputListener inputListener;
    protected String actionName;
    protected CameraNode camNode;
    protected MotionPath [] path;
    protected Node rootNode;
    protected ChaseCamera chaser;
    protected boolean active = true;
    protected boolean playing = false;
    protected AssetManager assetManager;
    protected MotionEvent [] event;
    
    private static String[] mappings = new String[] {
        "CAMERA_Left",
        "CAMERA_Right",
        "CAMERA_Up",
        "CAMERA_Down",
        
        "CAMERA_StrafeLeft",
        "CAMERA_StrafeRight",
        "CAMERA_Forward",
        "CAMERA_Backward",
        
        "CAMERA_ZoomIn",
        "CAMERA_ZoomOut",
        "CAMERA_RotateDrag",
        
        "CAMERA_Rise",
        "CAMERA_Lower",
        
        "CAMERA_InvertY"
    };
    
    public CameraMovement (Camera cam) {
        super(cam);
        this.cam = cam;
        this.InputManager = this.inputManager;
    }
    
    public CameraMovement (Camera cam,AssetManager assetManager, Node rootNode,InputManager inputManager ) {
        super(cam);
        this.cam = cam;
        this.assetManager = assetManager;
        this.rootNode = rootNode;
        this.actionName = "Nothing";
        this.InputManager = inputManager;
        this.inputManager = this.InputManager;
        event = new MotionEvent[0x05];
        path = new MotionPath[0x05];
        camNode = new CameraNode("Motion cam", this.cam);
        camNode.setControlDir(ControlDirection.SpatialToCamera);
               
        initializeEvents(0x00);
        initializeEvents(0x01);
        initializeEvents(0x02);        
        initializeEvents(0x03);
    }
    
    /**
     * Initializes the MotionPaths.
     * The distance is determined by the order of magnitude. This is a ugly code 
     * snippet so i've to fix it anytime.
     * @param indexNumber tuple number for implementation.
     * @param magnitude gives the scaleable distance faktor.
     */
    public void initalizePaths(final int indexNumber, int magnitude) {
        if (indexNumber == 0x00) {
            for (int iterator = 0x00; iterator <= 0x03; iterator++)  {
                // Don't try to understand this equation. It only creates a matrix for the horizontal waypoints, of the right direction.
                path[indexNumber].addWayPoint(new Vector3f(0f - (iterator < 2f ? magnitude*(iterator) : -(magnitude*(iterator-2f))),(iterator > 2 ? -11.0f : -magnitude),magnitude - (iterator > 1 ? (iterator == 3 && iterator != 2 ? magnitude : 20.0f ) : (magnitude*iterator))));                        
            }
        } else if (indexNumber == 0x01) {
            for (int iterator = 0x00; iterator <= 0x03; iterator++) {
//                // Don't try to understand this equation either. It only creates a matrix for the horizontal waypoints, of the left direction.
                path[indexNumber].addWayPoint(new Vector3f(magnitude - (iterator >= 0x02 ? -magnitude*(iterator-3) : magnitude*(iterator+1)),-magnitude,0.0f - (iterator > 0x01 ? magnitude*(iterator == 0x02 ? -(iterator-1) : (iterator-3)) : (magnitude*(iterator == 0 ? 1 : 0)))));                            
            }
        } else if (indexNumber == 0x02) {
            for (int iterator = 0x00; iterator <= 0x03; iterator++) {       
                // Don't try to understand this equation either. It only creates a matrix for the vertical waypoints, of the down direction.
                path[indexNumber].addWayPoint(new Vector3f(0.0f,-10.0f - (iterator < 1.0f ? magnitude*(iterator) : -(magnitude*(iterator-2))), 10.0f - (iterator > 1 ? (iterator == 3 && iterator != 2 ? 10.0f : 20.0f ) : (magnitude*iterator))));          
            }
        } else if (indexNumber == 0x03) {
            for (int iterator = 0x00; iterator <= 0x03; iterator++) {
                // Don't try to understand this equation either. It only creates a matrix for the vertical waypoints, of the up direction.
                path[indexNumber].addWayPoint(new Vector3f(0.0f,-magnitude + (iterator < 1 ? (magnitude*iterator) : -(magnitude*(iterator-2))),10.0f - (iterator > 1 ? (iterator == 3 && iterator != 2 ? 10.0f : 20.0f ) : (magnitude*iterator))));        
            }
        }
    }
     
    /**
     * Initializes the MotionEvents and the MotionPaths.
     * Don't call it. Furthermore it implements the waypoints for the 
     * MotionPath and if you initalize it twice, it will cause a stack
     * of problems.
     * @param indexNumber number for the tuples. It's importent when you call the method.
     */
    public void initializeEvents (final int indexNumber) {
        path[indexNumber] = new MotionPath();
        path[indexNumber].setCycle(true);
        Quaternion pathrotation = new Quaternion();
        pathrotation.fromAngleAxis(FastMath.PI / 2, Vector3f.UNIT_X);
        
        initalizePaths(indexNumber, 10);
        
        path[indexNumber].setCurveTension(1.0f);
        path[indexNumber].setPathSplineType(Spline.SplineType.CatmullRom); 

        event[indexNumber] = new MotionEvent(camNode,path[indexNumber]);
        event[indexNumber].setLoopMode(LoopMode.Cycle);
        event[indexNumber].setRotation(pathrotation);
        event[indexNumber].setLookAt(new Vector3f(0.0f,-10.0f,0.0f), Vector3f.UNIT_Y);
        event[indexNumber].setDirectionType(MotionEvent.Direction.LookAt);
                
        rootNode.attachChild(camNode);

        path[indexNumber].addListener(new MotionPathListener() {
            
            public synchronized void onWayPointReach(MotionEvent motionControl, int wayPointIndex) {
                AtomicInteger iter = new AtomicInteger(0);                
                iter.incrementAndGet();
                
                if (path[indexNumber].getNbWayPoints() == wayPointIndex + 1){
                    System.out.println(motionControl.getSpatial().getName() + "Finish!!!");
                } else {
                    System.out.println(motionControl.getSpatial().getName() + " Reached way point " + wayPointIndex);                   
                }
                
                if (wayPointIndex == iter.get() || wayPointIndex == 2 || wayPointIndex == 3 || wayPointIndex == 0) {
                    event[indexNumber].pause();
                    
                    registerWithInput(InputManager);
                    iter.set(wayPointIndex == 3 ? 0 : iter.get());
                }
            }
        });
    }
    
    @SafeVarargs
    public Node getCamNode() {
        return camNode;
    }
    
    @SafeVarargs 
    public Camera getCamera() {
        return cam; 
    }
    
    @Override 
    public void registerWithInput(InputManager inputManager) {
        inputManager.addMapping("CAMERA_Left",  new MouseAxisTrigger(MouseInput.AXIS_X,true),
                                                new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("CAMERA_Right", new MouseAxisTrigger(MouseInput.AXIS_X, false),                                                
                                                new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("CAMERA_Up",    new MouseAxisTrigger(MouseInput.AXIS_Y,false),
                                                new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("CAMERA_Down",  new MouseAxisTrigger(MouseInput.AXIS_Y,true),
                                                new KeyTrigger(KeyInput.KEY_DOWN));
        
        // mouse only - zoom in/out with wheel, and rotate drag
        inputManager.addMapping("CAMERA_ZoomIn", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("CAMERA_ZoomOut", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addMapping("CAMERA_RotateDrag", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        // keyboard only WASD for movement and WZ for rise/lower height
        inputManager.addMapping("CAMERA_StrafeLeft", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("CAMERA_StrafeRight", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("CAMERA_Forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("CAMERA_Backward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("CAMERA_Rise", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("CAMERA_Lower", new KeyTrigger(KeyInput.KEY_Z));

        inputManager.setCursorVisible(dragToRotate || !isEnabled());
        inputManager.addListener(this, mappings);
        
        Joystick[] joysticks = inputManager.getJoysticks();
        if (joysticks != null && joysticks.length > 0) 
            for (Joystick j : joysticks) {
                mapJoystick(j);
            }
    }
    
    /**
     * Gets the dimensions of the camera and returns them in a string list.
     * @return list tupel of geometric attributes.
     */
    @SafeVarargs
    public List<String> getDimensions() {
        List<String> list = new ArrayList();
        
        list.add(String.valueOf(this.cam.getHeight()) + "\n");
        list.add(String.valueOf(this.cam.getWidth()) + "\n");
        list.add(String.valueOf(this.cam.getLeft()) + "\n");
        list.add(String.valueOf(this.cam.getPlaneState()));
        
        return list;
    }
    
    /**
     * Gets the viewports and returns them in a string list. 
     * @return list 
     */
    @SafeVarargs
    public List<String> getViewPorts() {
        List<String> list = new ArrayList();
        
        list.add(String.valueOf("ViewPort: "+this.cam.getViewPortBottom()) + "\n");
        list.add(String.valueOf(this.cam.getViewPortLeft()) + "\n");
        list.add(String.valueOf(this.cam.getViewPortRight()) + "\n");
        list.add(String.valueOf(this.cam.getViewPortTop()) + "\n");
        list.add(this.cam.getViewMatrix().toString() + "\n");
        
        return list;
    }
    
    /**
     * Gets the frustums and returns them in a string list.
     * @return list tup of frustum values.
     */
    @SafeVarargs
    public List<String> getFrustums() {
        List<String> list = new ArrayList();
        
        list.add(String.valueOf("Frustum: "+this.cam.getFrustumBottom()) + "\n");
        list.add(String.valueOf(this.cam.getFrustumFar()) + "\n");
        list.add(String.valueOf(this.cam.getFrustumLeft()) + "\n");
        list.add(String.valueOf(this.cam.getFrustumNear()) + "\n");
        list.add(String.valueOf(this.cam.getFrustumRight()) + "\n");
        list.add(String.valueOf(this.cam.getFrustumTop()) + "\n");
              
        return list;
    }

    /**
     * Manages the actionevents for the camerarotation.
     * @param name string element of the event.
     * @param value true when an action has started.
     * @param tpf update value.
     * @see initalizeEvents(final int indexNumber) for declaration.
     */
    @Override
    public void onAction(String name, boolean value, float tpf) {
        active = false;
        if (name.equals("CAMERA_Right") && value) {
            if (playing) {

                path[1].addWayPoint(event[0].getPath().getWayPoint(event[0].getCurrentWayPoint()).add(new Vector3f(0.0f,-10.0f,10.0f)));

                playing = false;
                event[0].pause();        
                this.setEnabled(true);
                inputManager.setCursorVisible(true);
            } else {
                this.unregisterInput();
                inputManager.setCursorVisible(false);
                playing = true;
                camNode.setEnabled(true);
                event[0].play();        
            }
        } else if (name.equals("CAMERA_Up") && value) {
            if (playing) {
                playing = false;
                event[3].pause();
            } else {
                this.unregisterInput();
                playing = true; 
                camNode.setEnabled(true);
                event[3].play();
            }
        } else if(name.equals("CAMERA_Down") && value) {
            if (playing) {
                playing = false;
                event[2].pause();
            } else {
                this.unregisterInput();
                playing = true;
                camNode.setEnabled(true);
                event[2].play();
            }
        } else if (name.equals("CAMERA_Left") && value) {
            if (playing) {
                playing = false;
                event[1].pause();
                this.setEnabled(true);
                inputManager.setCursorVisible(true);
            } else {
                this.unregisterInput();
                inputManager.setCursorVisible(false);
                playing = true;
                camNode.setEnabled(true);
                event[1].play();      
            }
        }
    }
    
    /* 
     * Sets the cameraspeed.
     * Use it only once for the camera!
     */
    @SafeVarargs
    public float setCameraSpeed() {
        switch(this.speed) {
            case FAST:
                return this.moveSpeed = 10f;
            case MEDIUM:
                return this.moveSpeed = 5f;
            case SLOW:
                return this.moveSpeed = 1f;
            default:
                System.err.println("This option isn't supported yet!");
                return 0x00;
        }
    }
    
    @SafeVarargs 
    public String getAction() {
        return this.actionName;
    }
    
    /**
     * Sets the camera location.
     * the second parameter should be the relative position of the camera.
     * @param position LocalTranslation of the cam.
     * @param relative relative position of the cam.
     */
    public void setCameraLocation(Vector3f position, Vector3f ... relative) {
        Matrix3f matrix = new Matrix3f();
        
        this.cam.setLocation(position);
        
        for (Vector3f vector : relative) {
            matrix.fromStartEndVectors(vector, vector.add(vector.cross(vector)));
            matrix.invert();
            System.out.println(matrix.toString());
        }
    }
    
    public float getCameraSpeed() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Vector3f getSiteDirection() {
        return this.cam.getDirection(); 
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println(e.paramString());
        System.out.println(e.getActionCommand());
    }

    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void simpleUpdate(float tpf) {     
        Quaternion test = new Quaternion();
        test.fromAngleAxis(2*FastMath.PI, Vector3f.UNIT_Y);
         camNode.setLocalRotation(test);
         camNode.rotate(test);
         camNode.updateModelBound();
         camNode.updateLogicalState(tpf);
    }

    public void render(RenderManager rm, ViewPort vp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
