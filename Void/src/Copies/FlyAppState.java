/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Copies;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.monkeyzone.objects.CameraMovement;

/**
 *
 * @author john
 */
public class FlyAppState extends AbstractAppState {
    private Application app;
    private CameraMovement flyCam;
    
    public FlyAppState() {
    } 
    
    /**
     * This is called by SimpleApplication during initialize().
     * @param stateManager
     * @param app 
     */
    void setCamera(CameraMovement cam) {
        this.flyCam = cam;
    }
    
    public CameraMovement getCamera() {
        return flyCam;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        this.app = app;
        
        if (app.getInputManager() != null) {
            
            if (flyCam == null) {
                flyCam = new CameraMovement(app.getCamera());
            }
            
            flyCam.registerWithInput(app.getInputManager());
        }
    }
    
    @Override 
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        
        flyCam.setEnabled(enabled);
    }
    
    
    
    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        
        if (app.getInputManager() != null) {
            flyCam.unregisterInput();
        }
    }
}
