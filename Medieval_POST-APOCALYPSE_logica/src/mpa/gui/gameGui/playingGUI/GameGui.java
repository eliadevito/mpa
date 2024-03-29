package mpa.gui.gameGui.playingGUI;

import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import mpa.core.util.GameProperties;
import mpa.gui.gameGui.listener.GameGuiClickListener;
import mpa.gui.gameGui.listener.GameGuiKeyActionListener;
import mpa.gui.gameGui.listener.GameGuiMouseListener;
import mpa.gui.gameGui.listener.HandlerImplementation;
import mpa.gui.gameGui.listener.MultiPlayerController;
import mpa.gui.gameGui.listener.SinglePlayerController;
import mpa.gui.gameGui.panel.NiftyHandler;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

public class GameGui extends SimpleApplication implements AnimEventListener
{
	private ReentrantLock lock = new ReentrantLock();
	boolean cursorOnTheRightEdge = false;
	boolean cursorOnTheLeftEdge = false;
	boolean cursorOnTheTopEdge = false;
	boolean cursorOnTheBottomEdge = false;
	private float cameraHeight = 270;// 120
	float lz = (float) Math.sqrt(Math.pow(cameraHeight / Math.sin(90), 2) - Math.pow(cameraHeight, 2)); // sin(40)

	private Node groundNode;
	private Node mobileObjects = new Node("Mobile Objects");
	private Node staticObjects = new Node("Static Objects");
	private Node circlesUnderPlayers = new Node("Players' Circles");

	private Node lifeBars = new Node("Life Bars");

	private String playingPlayer;
	NiftyHandler niftyHandler;
	private AnalogListener mouseActionListener;
	private GameGuiClickListener clickActionListener;
	private GameGuiKeyActionListener keyActionListener;

	private boolean endGame = false;

	private HandlerImplementation listenerImplementation;

	public GameGui(String playingPlayer, Socket socket)
	{
		super();
		this.playingPlayer = playingPlayer;
		if (socket == null)
		{
			listenerImplementation = new SinglePlayerController();

		}
		else
		{
			listenerImplementation = new MultiPlayerController(socket);
		}
	}

	public AppSettings getSettings()
	{
		return settings;
	}

	public float getCameraHeight()
	{
		return cameraHeight;
	}

	@Override
	public void simpleInitApp()
	{
		initialCameraConfiguration();
		setLights();

		GuiObjectManager.init(this, playingPlayer);
		listenerImplementation.createStateInformation();
		niftyHandler = new NiftyHandler(assetManager, inputManager, audioRenderer, guiViewPort, stateManager, listenerImplementation, this);
		setCamera(new Vector3f(250, cameraHeight, 250));

		niftyHandler.updateResourcePanel();
		rootNode.attachChild(staticObjects);
		rootNode.attachChild(mobileObjects);
		mobileObjects.attachChild(circlesUnderPlayers);
		mobileObjects.attachChild(lifeBars);

		mouseActionListener = new GameGuiMouseListener(this);
		clickActionListener = new GameGuiClickListener(listenerImplementation, this);
		keyActionListener = new GameGuiKeyActionListener(listenerImplementation, niftyHandler);

		setEventTriggers();

		new GraphicUpdater(this).start();
	}

	void attachMobileObject(Spatial model)
	{
		mobileObjects.attachChild(model);
	}

	void detachMobileObject(Spatial model)
	{
		mobileObjects.detachChild(model);
	}

	void detachStaticObject(Spatial model)
	{
		staticObjects.detachChild(model);
	}

	void attachStaticObject(Spatial model)
	{
		staticObjects.attachChild(model);
	}

	void attachCircle(Spatial cirlce)
	{
		circlesUnderPlayers.attachChild(cirlce);
	}

	void detachCircle(Spatial cirlce)
	{
		circlesUnderPlayers.detachChild(cirlce);
	}

	void attachLifeBar(LifeBar lifeBar)
	{
		lifeBars.attachChild(lifeBar.getGreenGeometry());
		lifeBars.attachChild(lifeBar.getGrayGeometry());
	}

	void detachLifeBar(LifeBar lifeBar)
	{
		lifeBars.detachChild(lifeBar.getGreenGeometry());
		lifeBars.detachChild(lifeBar.getGrayGeometry());
	}

	protected void makeFloor(float worldDimension)
	{
		Box box = new Box(worldDimension / 2, 0, worldDimension / 2);

		Geometry floor = new Geometry("the Floor", box);
		floor.setLocalTranslation(worldDimension / 2, 0, worldDimension / 2);

		assetManager.registerLocator(GameProperties.getInstance().getPath("TexturePath"), FileLocator.class);
		Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Texture text1 = assetManager.loadTexture("grass-pattern.png");
		floor.getMesh().scaleTextureCoordinates(new Vector2f(20, 20));
		text1.setWrap(WrapMode.Repeat);

		mat1.setTexture("ColorMap", text1);
		floor.setMaterial(mat1);

		groundNode.attachChild(floor);
	}

	private void setLights()
	{
		DirectionalLight dl = new DirectionalLight();
		dl.setDirection(new Vector3f(-0.8f, -0.6f, -0.08f).normalizeLocal());
		rootNode.addLight(dl);

		AmbientLight al = new AmbientLight();
		rootNode.addLight(al);
	}

	public void setCamera(Vector3f newPosition)
	{
		cam.setLocation(newPosition);
		cam.lookAt(new Vector3f(newPosition.x, 0, newPosition.z + lz), new Vector3f(0, 1, 0));

	}

	public Vector3f getCamPosition()
	{
		return cam.getLocation();
	}

	private void initialCameraConfiguration()
	{
		cam.clearViewportChanged();
		flyCam.setEnabled(false);
		inputManager.setCursorVisible(true);
	}

	public void takeLock()
	{
		lock.lock();
	}

	public void leaveLock()
	{
		lock.unlock();
	}

	@Override
	public void simpleUpdate(float tpf)
	{

		if (endGame && getTimer().getTimeInSeconds() > 4)
		{
			System.exit(0);
		}
		else if (!endGame && getTimer().getTimeInSeconds() > 2)
		{
			niftyHandler.updateResourcePanel();
			getTimer().reset();
		}

		listenerImplementation.updateInformation();
	}

	public Vector3f getCameraLookAt()
	{
		return cam.getDirection();
	}

	public NiftyHandler getNiftyHandler()
	{
		return niftyHandler;
	}

	public Node getGroundNode()
	{
		return groundNode;
	}

	public ReentrantLock getLock()
	{
		return lock;
	}

	public void setCursorOnTheBottomEdge(boolean cursorOnTheBottomEdge)
	{
		this.cursorOnTheBottomEdge = cursorOnTheBottomEdge;
	}

	public void setCursorOnTheLeftEdge(boolean cursorOnTheLeftEdge)
	{
		this.cursorOnTheLeftEdge = cursorOnTheLeftEdge;
	}

	public void setCursorOnTheRightEdge(boolean cursorOnTheRightEdge)
	{
		this.cursorOnTheRightEdge = cursorOnTheRightEdge;
	}

	public void setCursorOnTheTopEdge(boolean cursorOnTheTopEdge)
	{
		this.cursorOnTheTopEdge = cursorOnTheTopEdge;
	}

	@Override
	public void onAnimChange(AnimControl arg0, AnimChannel arg1, String arg2)
	{
		// TODO Stub di metodo generato automaticamente

	}

	@Override
	public void onAnimCycleDone(AnimControl arg0, AnimChannel arg1, String arg2)
	{
		// TODO Stub di metodo generato automaticamente

	}

	private void setEventTriggers()
	{
		inputManager.addMapping("Shift_Map_Negative_X", new MouseAxisTrigger(MouseInput.AXIS_X, true));

		inputManager.addMapping("Shift_Map_Positive_X", new MouseAxisTrigger(MouseInput.AXIS_X, false));
		inputManager.addMapping("Shift_Map_Negative_Y", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
		inputManager.addMapping("Shift_Map_Positive_Y", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
		inputManager.addListener(mouseActionListener, "Shift_Map_Negative_X", "Shift_Map_Positive_X", "Shift_Map_Negative_Y", "Shift_Map_Positive_Y");

		inputManager.addMapping("Wheel_DOWN", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
		inputManager.addMapping("Wheel_UP", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));

		inputManager.addMapping("Tab", new KeyTrigger(KeyInput.KEY_TAB));
		inputManager.addListener(keyActionListener, "Tab");

		// inputManager.addRawInputListener( new MyButtonListener() );
		inputManager.addMapping("Click", new MouseButtonTrigger(0));
		inputManager.addListener(clickActionListener, "Click");

		inputManager.addMapping("ChooseItem", new KeyTrigger(KeyInput.KEY_LSHIFT));
		inputManager.addMapping("attack", new MouseButtonTrigger(1));
		inputManager.addListener(clickActionListener, "attack", "Wheel_DOWN", "Wheel_UP", "ChooseItem");

		inputManager.addMapping("pause", new KeyTrigger(KeyInput.KEY_P));
		inputManager.addListener(keyActionListener, "pause");

	}

	public int windowWidth()
	{
		return settings.getWidth();
	}

	public int windowHeight()
	{
		return settings.getHeight();
	}

	public void createWorld(float dimension)
	{
		groundNode = new Node("Ground");
		makeFloor(dimension);
		rootNode.attachChild(groundNode);
	}

	public HandlerImplementation getGameListenerImplementation()
	{
		return listenerImplementation;
	}

	public boolean canClick()
	{
		return niftyHandler.canClick();
	}

	public void setEndGame()
	{
		this.endGame = true;
		getTimer().reset();
	}
}
