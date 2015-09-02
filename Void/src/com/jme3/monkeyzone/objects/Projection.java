package com.jme3.monkeyzone.objects;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Torus;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;

/**
 * This should list all available shapes for the projection.
 * @author marcok
 */
enum Shapess {
        Box,
        Sphere,
        Cylinder,
        Dome,
        Torus,  
};
/**
 * This Class shows attributes, from the camera, framebuffer etc. 
 * It also projects the texture on a geometry shape. The framebuffer should be
 * the OutputFramebuffer not any other! 
 * @author marcok
 */
@Serializable
public class Projection {
    public Geometry quader ;
    protected BoundingVolume volume;
    protected Material mat;
    protected Texture2D text;
    protected FrameBuffer frame;
    protected ViewPort view;
    protected Node root;
    protected transient int [] code = new int[0x07];
    protected Geometry body;
    protected VertexBuffer vertex;
    protected Mesh object;
    private   Shapess shape;
    protected BulletAppState physic;
    protected AppStateManager statem;
    protected transient float geowidth, geoheight;
    
    public Projection(Node root,Material material, Texture2D texture, FrameBuffer frame,ViewPort guiview, BulletAppState ph, AppStateManager state) {
        String supername = super.toString();
        
        System.err.println(supername);
        
        this.mat = material;
        this.text = texture;
        this.frame = frame;
        this.view = guiview;
        this.root = root;
        this.statem = state;
        this.physic = ph;
        this.mat.setTransparent(true);
        
        this.code[0x00] = this.mat.hashCode();
        this.code[0x01] = this.text.hashCode();
        this.code[0x02] = this.frame.hashCode();
        this.code[0x03] = this.view.hashCode();
        this.code[0x04] = this.root.hashCode();
        this.code[0x05] = this.physic.hashCode();
        this.code[0x06] = this.statem.hashCode();
        
        if (material.getName().isEmpty() && material instanceof Material) {
            System.err.println("Material is not correct!");
            throw new NullPointerException();
        } else {
           this.mat.setName(material.getName());
        }
        
        if (texture.getName().isEmpty() || !(texture instanceof Texture2D)) {
            System.err.println("Texture is not correct!");
            throw new NullPointerException();
        } else {
            this.text.setName(texture.getName());
        }
        
        if (frame.getId() != 0x00 || !(frame instanceof FrameBuffer)) {
            this.frame.setId(frame.getId());
        } else {
            System.err.println("The Frame is not correct!");
            throw new NullPointerException();
        }
    }
    
    public String getTextureName() {
        return this.text.getName();
    }
    
    public void setTexture() {
        this.mat.setTexture(this.text.getName(), this.text);
    }
    
    public float getTextWidth() {
        return this.text.getImage().getWidth();
    }
    
    /** I would recommend to use this function, because there are some 
     *  serious problems with the second one (initProjection).
     *  This method implements the projection as a mesh object and
     *  displays them to the screen.
     * @param width - stands for the width of the screen.
     * @param height - stands for the height of the screen.
     * @param position - is the local translation of the screen.
     * @return 
     */
    public Geometry alternateinit(float width, float height, Vector3f position) {
        this.object = new Box(width,height,0x00);
        
        text.setMinFilter(Texture.MinFilter.Trilinear);
        text.setMagFilter(Texture.MagFilter.Bilinear);
        
        RigidBodyControl rigid = new RigidBodyControl(1f);
        
        rigid.setMass(0x00);
        
        frame.addColorTexture(text);
        
        view.setOutputFrameBuffer(frame);
        view.setClearFlags(true, true, true);

        view.setClearStencil(true);
        
        quader = new Geometry(object.toString(), object);
        
        quader.addControl(rigid);
        
        try {
            physic.getPhysicsSpace().add(quader);
            physic.startPhysics();
            rigid.setKinematic(true);
            rigid.setPhysicsLocation(new Vector3f(position));
            quader.setLocalTranslation(position);
            quader.updateGeometricState();
        } catch(NullPointerException e) {
            System.err.println(e);
            System.err.println(physic.toString());
        }
        mat.setTexture("ColorMap", text);
        quader.setMaterial(this.mat);

        return quader;
    }
    
    /* Now some get-set Accessores for some useful informations */
    public float getTextHeight() {
        return text.getImage().getHeight();
    }
    
    public BoundingVolume getVolume() {
        return volume;
    }
    
    public Mesh getBodyMesh() {
        return body.getMesh();
    }
    
    public int getVertex() {
        return body.getVertexCount();
    }
    
    public int getTriangle() {
        return body.getTriangleCount();
    }
    
    public float getFrameHeight() {
        return frame.getHeight();
    }
    
    public float getFrameWidth() {
        return frame.getWidth();
    }
    
    public float getGeoWidth() {
        return geowidth;       
    }
    
    public float getGeoHeight() {
        return geoheight;
    }
    
    public float getFrameId() {
        return frame.getId();
    }
        
    public String getViewCamera() {
        return view.getCamera().getName();
    }
    
    public void setFrameId(int Id) {
        this.frame.setId(Id);
    }
    
    /** This method forms the geometry from the mesh object.
     *  It's a little bit messy.
     */
    public Geometry geo() {
        object = objectForm(0x0A,0x0A,0x00);
        body = new Geometry(object.toString(),object);
                
        code[0x05] = this.object.getId();
        code[0x06] = this.object.getMaxNumWeights();
        
        if (vertex.getBufferType() instanceof VertexBuffer.Type) {
            System.out.println("Vertexes: "+ object.getBuffer(VertexBuffer.Type.Normal).getNumElements());
        } else {
            System.err.println("Vertexbuffer is not correct!");
        }
        
        try {   
            body.setMesh(object);
            body.setMaterial(mat);
        } catch (AssetNotFoundException e) {
            System.err.println(e);
        }
        return body;
    }
    
    /**
     * Decides which form the shape should have and constructs it with the 
     * values of the parameters.
     * @param width
     * @param height
     * @param option
     * @return 
     */
    public Mesh objectForm(float width, float height, int option) {
        switch(option) {
            case 0x00: 
                shape = Shapess.Box;
                System.out.println(shape.name());
                object = new Box(width,height,0x00);
                object.setStatic();
                object.setId(object.hashCode());
                break;
                
            case 0x01: 
                object = new Cylinder(0x20,0x20,width,height);
                object.setStatic();
                object.setId(object.hashCode());
                break;
                
            case 0x02:
                object = new Dome(new Vector3f(width,height,width).normalize(),0x20,0x20,width,true);
                object.setId(object.hashCode());
                break;
                
            case 0x03:
                shape = Shapess.Sphere;
                System.out.println(shape.name());
                object = new Sphere(0x20,0x20,width);
                object.setId(object.hashCode());
                break;
                
            case 0x04: 
                object = new Torus(0x20,0x20,width*0.75f,width);
                object.setId(object.hashCode());
                break;
                
            default:
                System.out.println("Diese Form wird nicht unterstützt.");
                break;
        }
        Vector3f center = new Vector3f(object.getBound().getCenter());
        Vector3f dimension = new Vector3f(getTextWidth(),getTextHeight(),Vector3f.ZERO.getZ());
        volume = new BoundingBox(center,dimension);
        vertex = new VertexBuffer(VertexBuffer.Type.Color);
        
        view.getCamera().containsGui(volume);
        object.setBound(volume);
        object.updateBound();
        object.updateCounts();
        object.setBuffer(vertex);
        object.updateBound();
        return object;
    }
    
    /**
     * Inits the projection into the rootNode and delivers the position of 
     * the Shape.
     * @param position 
     */
    public void initProjection(Vector3f position,float width, float height) {
        geoheight = height;
        geowidth = width;
        text.setMinFilter(Texture.MinFilter.Trilinear);
        text.setMagFilter(Texture.MagFilter.Bilinear);
        RigidBodyControl rigid = new RigidBodyControl(1f);
              
        rigid.setMass(0f);
        frame.addColorTexture(text);
    
        view.setOutputFrameBuffer(frame);
        view.setClearFlags(true, true, true);
        
        object = objectForm(width,height,0x00);
        
        quader = new Geometry(this.object.toString(),object);
      
        quader.addControl(rigid);
        
        try {
            physic.getPhysicsSpace().add(quader);
            physic.startPhysics();
            physic.stateAttached(statem);
            rigid.setPhysicsLocation(position);
            quader.setLocalTranslation(position);
            quader.updateGeometricState();
            statem.attach(physic);
            
        } catch (NullPointerException e) {
            System.err.println(e);
            System.err.println(physic.toString());
        }
        
        mat.setTexture("ColorMap", text);
        
        List<String> obk = Attribute();
        System.out.println(obk.toString());
        
        quader.setMaterial(mat);
        
        root.attachChild(quader);
        object.updateBound();
        quader.updateModelBound();
        object.updateCounts();
    }
    
   /** Some details about the added Processors.
    *  Don't call it twice!
    * @return 
    */ 
    public Object[] viewProcessors() {
       List<SceneProcessor> proc = new ArrayList();
       
       try {  
        for (Iterator<SceneProcessor> it = this.view.getProcessors().iterator(); it.hasNext();)
            proc.add(it.next());
            return proc.toArray();
       } catch(IndexOutOfBoundsException e) {
           System.err.println(e);
       }
       return proc.toArray();
    }
    
    /* 
     * List all the necessary attributes of the projection and returns them
     * in a string list.
     */
    public List<String> Attribute() {
        List<String> attr = new ArrayList();
        
        try {
            attr.add("Camera: " + String.valueOf(getViewCamera())+"\n");
            attr.add("Processors: " + String.valueOf(viewProcessors()) + "\n");
            attr.add("FrameBuffer: " + String.valueOf(view.getOutputFrameBuffer()) + "\n");
            attr.add("AnisotropicFilter: " + String.valueOf(text.getAnisotropicFilter()) +"\n");
            attr.add("Width: " + String.valueOf(getGeoWidth()) + "\n");
            attr.add("Height: " + String.valueOf(getGeoWidth()) + "\n");
        } catch(IllegalFormatException e) {
            System.out.println(e);
        }
        
        for (String attri : attr) {
            System.out.println(attri.toString());
        }
        return attr;
    }
}