package Game;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;

import GameEngine.CubixCameraController;
import GameEngine.GameClient;
import GameEngine.GameServer;
import GameEngine.MoveDownKey;
import GameEngine.MoveLeftKey;
import GameEngine.MoveRightKey;
import GameEngine.MoveUpKey;
import GameEngine.SettingsDialog;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import GameEngine.CollisionEvent;
import GameEngine.QuitGameAction;
import sage.app.BaseGame;
import sage.audio.AudioManagerFactory;
import sage.audio.AudioResource;
import sage.audio.AudioResourceType;
import sage.audio.IAudioManager;
import sage.audio.Sound;
import sage.audio.SoundType;
import sage.camera.ICamera;
import sage.display.DisplaySettingsDialog;
import sage.display.DisplaySystem;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.action.IAction;
import sage.model.loader.ogreXML.OgreXMLParser;
import sage.networking.IGameConnection.ProtocolType;
import sage.physics.IPhysicsEngine;
import sage.physics.PhysicsEngineFactory;
import sage.renderer.IRenderer;
import sage.scene.Group;
import sage.scene.HUDString;
import sage.scene.Model3DTriMesh;
import sage.scene.SceneNode;
import sage.scene.shape.Line;
import sage.scene.shape.Sphere;
import sage.scene.state.RenderState;
import sage.scene.state.TextureState;
import sage.terrain.AbstractHeightMap;
import sage.terrain.ImageBasedHeightMap;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.Texture.ApplyMode;
import sage.texture.TextureManager;

/*
	STAGE_DIMENSION: 
	Island Stage 	- 0 (8x8)
	Snow Stage  	- 1 (10x10)
	Haunted Stage   - 2 (12x12)
*/

public class CubixGame extends BaseGame {
	// Constants
	private final int MAX_SNOW = 40;
	private final int[] STAGE_DIMENSION = { 8,10,12 };

	// Mechanical Objects
	private CubixCameraController camController;
	private ICamera cam;
	private IRenderer renderer;
	private IDisplaySystem display;
	private IInputManager im;
	private IPhysicsEngine pe;
	
	private HUDString timeDisplay;
	private float timeTotal;

	// Network Objects
	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private GameClient gameClient;
	private String playerTextureName;
	private boolean isHosting;
	private boolean isMultiplayer;
	private boolean isFullScreen;
	private String levelThemeName;
	private boolean isConnected;

	// Audio Objects
	IAudioManager audioMgr;
	Sound ghostSound;
	AudioResource resource1;

	// Script Objects
	private ScriptEngine engine;
	private File scriptFile;
	Invocable invocableEngine;

	// Terrain Objects
	private TerrainBlock imgTerrain, gridTerrain;

	// Gameworld Objects
	private PlayerAvatar player;
	private Theme skybox;
	
	private IEventManager eventMgr;
	private CollisionEvent collision;
	
	private Sphere[] snow;
	private float windTimer;
	private NPCGhostController ghost;
	private Tile[][] tiles;
	private Tile startTile;

	// Animated Objects
	private Group lighthouse;

	// public CubixGame(String serverAddress, int serverPort)
	public CubixGame() {
		super();
		this.serverProtocol = ProtocolType.TCP;
	}

	protected void initGame() {
		// Get Option Selections (Network options, Player texture, Level theme)
		getOptions();
		initNetwork();

		// check to see if this is a game Client
		if (gameClient != null)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			gameClient.processPackets();
		}


		// initialize Input Manager, Display, Renderer, and Camera.
		im = getInputManager();
		eventMgr = EventManager.getInstance();
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = environment.getDefaultScreenDevice();
		if(isFullScreen)
		{
			//display = new DisplaySystem(1024, 768, 32, 60, isFullScreen, "sage.renderer.jogl.JOGLRenderer");
			display = new DisplaySystem(1980, 1200, 32, 60, isFullScreen, "sage.renderer.jogl.JOGLRenderer");
		}
		else
		{
			display = getDisplaySystem();
		}
		display.setTitle("CUBIX");

		renderer = display.getRenderer();
		cam = renderer.getCamera();
		cam.setPerspectiveFrustum(60, 1, 1, 1000);

		// initialize Script Engine
		ScriptEngineManager factory = new ScriptEngineManager();
		engine = factory.getEngineByName("js");
		scriptFile = new File("scripts/CreateStageGrid.js");
		loadScript();
		invocableEngine = (Invocable) engine;

		// create gridmap based on menu option
		switch(levelThemeName){
			case "Island": stageSelect(0); break; 
			case "Snow": stageSelect(1); break; 
			case "Halloween": stageSelect(2); break;
		}
		
		createScene();
		initTerrain();

		// initialize physics
		if (levelThemeName.equals("Snow")) {
			initPhysicsSystem();
			createSagePhysicsWorld();
		}

		createPlayer();
		if (levelThemeName.equals("Halloween")) {
			// Add ghost
			ghost = new NPCGhostController(player, this);
			ghost.translate(11, 3, 23);
			ghost.updateWorldBound();
			ghost.scale(0.35f, 0.35f, 0.35f);
			ghost.updateGeometricState(0, true);

			addGameWorldObject(ghost);
			eventMgr.addListener(ghost, CollisionEvent.class);
		}
		initInput();
		initAudio();
		
		 timeDisplay = new HUDString("Time: " + timeTotal);
		 timeDisplay.setLocation(0,0.10);
		 cam.addToHUD(timeDisplay);
	}

	private void getOptions() {
		SettingsDialog dialog = new SettingsDialog();
		String[] data = dialog.showDialog();
		try {
			levelThemeName = data[0];
			playerTextureName = data[1];
			isMultiplayer = new Boolean(data[2]);
			isHosting = new Boolean(data[3]);
			serverAddress = data[4];
			serverPort = Integer.parseInt(data[5]);
			isFullScreen = new Boolean(data[6]);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Invalid Selection");
			System.exit(1);
		}

	}

	private void createPlayer() {
		playerTextureName = "textures/objects/" + playerTextureName + ".png";
		player = new PlayerAvatar(playerTextureName, this, gameClient, startTile);
		player.setLocalTranslation((Matrix3D)startTile.getLocalTranslation().clone());
		player.translate(0, 1, 0);
		player.scale(.8f, .8f, .8f);
		player.updateWorldBound();
		addGameWorldObject(player);
		if (gameClient != null) {
			gameClient.sendCreateMessage(getPosition(), getPlayerTextureName());
		}

	}

	protected void initPhysicsSystem() {
		String engine = "sage.physics.ODE4J.ODE4JPhysicsEngine";
		pe = PhysicsEngineFactory.createPhysicsEngine(engine);
		pe.initSystem();
		pe.setGravity(new float[] { 0, -.1f, 0 });
	}

	private void createSagePhysicsWorld() {
		for (int i = 0; i < MAX_SNOW; i++) {
			// add the snow physics
			float mass = 1.0f;
			snow[i].setPhysicsObject(
					pe.addSphereObject(pe.nextUID(), mass, snow[i].getWorldTransform().getValues(), 1.0f));
		}
	}

	private void initNetwork() {
		if (isMultiplayer) {
			if (isHosting) {
				try {
					new GameServer(serverPort, levelThemeName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				gameClient = new GameClient(InetAddress.getByName(serverAddress), serverPort, serverProtocol, this);
				System.out.println(gameClient);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (gameClient != null)
				gameClient.sendJoinMessage();
		}

	}

	private void createScene() {
		Iterator<SceneNode> itr;
		// add Skybox
		skybox = new Theme("Background", 20.0f, 20.0f, 20.0f);
		switch (levelThemeName) {
		case "Island":
			skybox.islandTheme();
			addGameWorldObject(skybox);

			// Add lighthouse
			lighthouse = getLighthouse();
			lighthouse.translate(20, 0, 20);
			lighthouse.updateGeometricState(0, true);
			addGameWorldObject(lighthouse);
			itr = lighthouse.getChildren();
			while (itr.hasNext()) {
				Model3DTriMesh mesh = ((Model3DTriMesh) itr.next());
				mesh.startAnimation("Rotate");
			}

//			BlendState btransp = (BlendState) renderer.createRenderState(RenderStateType.Blend);
//			btransp.setBlendEnabled(true);
//			btransp.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
//			btransp.setDestinationFunction(BlendState.DestinationFunction.DestinationAlpha);
//			btransp.setTestEnabled(true);
//			btransp.setTestFunction(BlendState.TestFunction.GreaterThan);
//			btransp.setEnabled(true);
//			lighthouse.setRenderState(btransp);
//			lighthouse.updateRenderStates();
//			lighthouse.setRenderMode(RENDER_MODE.TRANSPARENT);
			break;
		case "Snow":
			skybox.snowTheme(this);
			addGameWorldObject(skybox);

			snow = new Sphere[MAX_SNOW];

			// add snow
			Random rn = new Random();
			for (int i = 0; i < MAX_SNOW; i++) {
				snow[i] = new Sphere(.09, 16, 16, Color.white);
				Matrix3D xform = new Matrix3D();
				xform.translate(rn.nextInt(30), rn.nextInt(15) + 10, rn.nextInt(30));
				snow[i].setLocalTranslation(xform);
				addGameWorldObject(snow[i]);
				snow[i].updateGeometricState(1.0f, true);
			}
			break;
		case "Halloween":
			skybox.halloweenTheme();
			addGameWorldObject(skybox);

			break;
		}

		// add Grid
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles.length; j++) {
				tiles[i][j].translate(2 * i + 1, .2f, 2 * j + 1);
				tiles[i][j].scale(.8f, .8f, .8f);
				addGameWorldObject(tiles[i][j]);
			}
		}

		// add 3D axis
		Point3D origin = new Point3D(0, 0, 0);
		Point3D xEnd = new Point3D(100, 0, 0);
		Point3D yEnd = new Point3D(0, 100, 0);
		Point3D zEnd = new Point3D(0, 0, 100);
		Line xAxis = new Line(origin, xEnd, Color.red, 2);
		Line yAxis = new Line(origin, yEnd, Color.green, 2);
		Line zAxis = new Line(origin, zEnd, Color.blue, 2);
		addGameWorldObject(xAxis);
		addGameWorldObject(yAxis);
		addGameWorldObject(zAxis);

	}

	public void initAudio() {
		audioMgr = AudioManagerFactory.createAudioManager("sage.audio.joal.JOALAudioManager");
		if (!audioMgr.initialize()) {
			System.out.println("Audio Manager failed to initialize!");
			;
			return;
		}

		resource1 = audioMgr.createAudioResource("sounds/ghost.wav", AudioResourceType.AUDIO_SAMPLE);
		ghostSound = new Sound(resource1, SoundType.SOUND_EFFECT, 75, true);
		ghostSound.initialize(audioMgr);

		setEarParameters();

		if (levelThemeName.equals("Halloween")) {
			ghostSound.setMaxDistance(50f);
			ghostSound.setMinDistance(5f);
			ghostSound.setRollOff(5.0f);
			ghostSound.setLocation(new Point3D(ghost.getWorldTranslation().getCol(3)));
		}
	}
	
	public void playGhostSound()
	{
		ghostSound.play();
	}
	
	public void stopGhostSound()
	{
		ghostSound.stop();
	}

	public void releaseSounds() {
		ghostSound.release(audioMgr);
		resource1.unload();
		audioMgr.shutdown();
	}

	public void setEarParameters() {
		audioMgr.getEar().setLocation(new Point3D(player.getWorldTranslation().getCol(3)));
		audioMgr.getEar().setOrientation(cam.getViewDirection(), new Vector3D(0, 1, 0));
	}

	private Group getLighthouse() {
		Group model = null;
		OgreXMLParser loader = new OgreXMLParser();
		try {
			String slash = File.separator;
			model = loader.loadModel("objects" + slash + "Lighthouse.mesh.xml",
					"materials" + slash + "Lighthouse.material", "objects" + slash + "Lighthouse.skeleton.xml",
					"textures" + slash + "objects" + slash, ApplyMode.Replace);
			model.updateGeometricState(0, true);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		return model;
	}

	public Group getGhost() {
		Group model = null;
		OgreXMLParser loader = new OgreXMLParser();
		try {
			String slash = File.separator;
			model = loader.loadModel("objects" + slash + "ghost.mesh.xml", "materials" + slash + "ghost.material",
					"objects" + slash + "ghost.skeleton.xml");
			model.updateGeometricState(0, true);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		return model;
	}

	private void initTerrain() {
		// create height map and terrain block
		ImageBasedHeightMap myHeightMap = new ImageBasedHeightMap("terrains/" + levelThemeName + "_height_map.jpg");
		imgTerrain = createTerBlock(myHeightMap);
		
		// create grid terrain block
		myHeightMap = new ImageBasedHeightMap("terrains/" + levelThemeName + "_grid_map.jpg");
		gridTerrain = createTerBlock(myHeightMap);

		// create texture and texture state to color the TERRAIN
		TextureState stateTerrain;
		Texture stageTerrainTexture = TextureManager
				.loadTexture2D("textures/stage_" + levelThemeName + "/" + levelThemeName + "_texture.jpg");
		stageTerrainTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
		stateTerrain = (TextureState) display.getRenderer().createRenderState(RenderState.RenderStateType.Texture);
		stateTerrain.setTexture(stageTerrainTexture, 0);
		stateTerrain.setEnabled(true);
		
		// create texture and texture state to color the GRID TERRAIN
		TextureState stateGridTerrain;
		Texture gridTexture = TextureManager
				.loadTexture2D("textures/stage_" + levelThemeName + "/" + levelThemeName + "_grid" +"_texture.jpg");
		gridTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
		stateGridTerrain = (TextureState) display.getRenderer().createRenderState(RenderState.RenderStateType.Texture);
		stateGridTerrain.setTexture(gridTexture, 0);
		stateGridTerrain.setEnabled(true);

		// apply the texture to the terrain
		imgTerrain.setRenderState(stateTerrain);
		switch(levelThemeName){
			case "Island": imgTerrain.translate(-8,.55f,-8); break;
			case "Snow": imgTerrain.translate(-12, .55f, -12); break;
			case "Halloween": imgTerrain.translate(-16, .65f, -16); break; 
		}

		addGameWorldObject(imgTerrain);
		
		// apply the texture to the grid terrain
		gridTerrain.setRenderState(stateGridTerrain);
		addGameWorldObject(gridTerrain);
	}

	private TerrainBlock createTerBlock(AbstractHeightMap heightMap) {
		float heightScale = .01f; // scaling the height of terrain
		Vector3D terrainScale = new Vector3D(.2, heightScale, .2);

		// use the size of the height map as the size of the terrain
		int terrainSize = heightMap.getSize();

		// specify terrain origin so heightmap (0,0) is at world origin
		float cornerHeight = heightMap.getTrueHeightAtPoint(0, 0) * heightScale;
		Point3D terrainOrigin = new Point3D(0, -cornerHeight, 0);

		// create a terrain block using the height map
		String name = "Terrain:" + heightMap.getClass().getSimpleName();
		TerrainBlock tb = new TerrainBlock(name, terrainSize, terrainScale, heightMap.getHeightData(), terrainOrigin);
		return tb;
	}

	protected void initInput() {
		String mouseName = im.getMouseName();
		String kbName = im.getKeyboardName();

		// apply SAGE built-in 3P camera controller
		camController = new CubixCameraController(cam, player, im, mouseName);

		// initialize A key
		IAction moveA = new MoveLeftKey(player, gameClient, imgTerrain);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.A, moveA,
				IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);

		// initialize D key
		IAction moveD = new MoveRightKey(player, gameClient, imgTerrain);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.D, moveD,
				IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);

		// initialize W key
		IAction moveW = new MoveUpKey(player, gameClient, imgTerrain);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.W, moveW,
				IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);

		// initialize S key
		IAction moveS = new MoveDownKey(player, gameClient, imgTerrain);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.S, moveS,
				IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		 
		IAction quitGame = new QuitGameAction();
		im.associateAction(kbName,
				 net.java.games.input.Component.Identifier.Key.ESCAPE, quitGame,
				 IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
	}

	public void setIsConnected(boolean b) {
		isConnected = b;
	}

	public Vector3D getPosition() {
		return player.getWorldTranslation().getCol(3);
	}

	protected void shutdown() {
		super.shutdown();
		releaseSounds();
		if (gameClient != null) {
			gameClient.sendByeMessage();
			try {
				gameClient.shutdown();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		display.close();
	}

	public void addGhost(GhostAvatar ghost) {
		addGameWorldObject(ghost);
		// executeScript();
	}

	public void removeGhost(GhostAvatar ghost) {
		removeGameWorldObject(ghost);
	}

	public String getPlayerTextureName() {
		return playerTextureName;
	}

	private void loadScript() {
		try {
			FileReader fileReader = new FileReader(scriptFile);
			engine.eval(fileReader);
			fileReader.close();
		} catch (FileNotFoundException e1) {
			System.out.println(scriptFile + " not found" + e1);
		} catch (IOException e2) {
			System.out.println("IO problem with " + scriptFile + e2);
		} catch (ScriptException e3) {
			System.out.println("ScriptException in " + scriptFile + e3);
		} catch (NullPointerException e4) {
			System.out.println("Null ptr exception reading " + scriptFile + e4);
		}
	}

	public void update(float time) {
		
		 //Initialize HUD objects
		 timeTotal += time;
		 timeDisplay.setText(String.format("Time: %1$.1f", timeTotal/1000));
		 
		 
		Iterator<SceneNode> itr;
		if (levelThemeName.equals("Snow")) {
			// WIND PHYSICS
			windTimer += time;
			if (windTimer < 5000) // NO WIND
				pe.setGravity(new float[] { -.01f, -.1f, 0 });
			else { // WIND
				pe.setGravity(new float[] { -.2f, -.1f, 0 });
				if (windTimer > 8000)
					windTimer = 0;
			}
			// WIND PHYSICS
			for (int i = 0; i < MAX_SNOW; i++) {
				if (snow[i].getWorldTransform().getCol(3).getY() <= 1) {
					Random rn = new Random();
					Matrix3D xform = new Matrix3D();
					xform.translate(rn.nextInt(30), rn.nextInt(15) + 10, rn.nextInt(30));
					snow[i].getLocalTranslation().setCol(3, xform.getCol(3));
					snow[i].getPhysicsObject().setTransform(xform.getValues());
				}
			}
			// WIND PHYSICS
			Matrix3D mat;
			pe.update(time);
			for (SceneNode s : getGameWorld()) {
				if (s.getPhysicsObject() != null) {
					mat = new Matrix3D(s.getPhysicsObject().getTransform());
					s.getLocalTranslation().setCol(3, mat.getCol(3));
					// should also get and apply rotation
				}
			}
		}

		// update sounds, ear
		setEarParameters();
		if (levelThemeName.equalsIgnoreCase("Halloween")) {
			ghostSound.setLocation(new Point3D(ghost.getWorldTranslation().getCol(3)));
			ghost.npcLoop(time);
			
			if(ghost.getWorldBound().intersects(player.getWorldBound()))
			{
				 collision = new CollisionEvent(player);
				 eventMgr.triggerEvent(collision);
			}
		}

		if (levelThemeName.equals("Island")) {
			// Update animations
			itr = lighthouse.getChildren();
			while (itr.hasNext()) {
				Model3DTriMesh submesh = ((Model3DTriMesh) itr.next());
				submesh.updateAnimation(time);
			}
		}

		// update 3p camera
		camController.update(time);

		// update skybox position
		Point3D camLoc = cam.getLocation();
		Matrix3D camTranslation = new Matrix3D();
		camTranslation.translate(camLoc.getX(), camLoc.getY(), camLoc.getZ());
		skybox.setLocalTranslation(camTranslation);

		// check packets
		if (gameClient != null) {
			gameClient.processPackets();
		}

		player.update(time);

		// Update ghostAvatars
		if (gameClient != null) {
			ArrayList<GhostAvatar> ghosts = gameClient.getGhostAvatars();
			for (int i = 0; i < ghosts.size(); i++) {
				ghosts.get(i).update(time);
			}
		}

		// regular update
		super.update(time);
	}

	public void setTheme(String t) {
		levelThemeName = t;
	}

	public void updateVerticalPosition(PlayerAvatar target) {
		// get avatar's X and Y coord.
		Point3D avLoc = new Point3D(target.getLocalTranslation().getCol(3)); // get
																				// local
																				// XYZ
																				// coord
		float x = (float) avLoc.getX();
		float z = (float) avLoc.getZ();

		// get Y coord based of terrain's local X,Y
		float terHeight = gridTerrain.getHeight(x, z);

		// calculate new Y for avatar
		float desiredHeight = terHeight + (float) gridTerrain.getOrigin().getY() + 0.5f;

		// apply Y translation
		if (desiredHeight >= -2) {
			target.getLocalTranslation().setElementAt(1, 3, desiredHeight + 0.7);
		}

	}
	
	//Check if player tile is valid
	public void checkTile(PlayerAvatar p, int i, int j)
	{
		System.out.println(tiles[i][j].getTileType());
		
		if(tiles[i][j].getTileType() != (player.getBottomFace()+1))
		{
			switch(tiles[i][j].getTileType())
			{
			case 7: //Start tile, do nothing
				break;
			case 8: //Finish tile, WIN
				System.out.println("FINISH!");
				JOptionPane.showMessageDialog(null,
					    "Finished in: " + String.format("%1$.1f", timeTotal/1000) + " seconds.  Press OK to quit",
					    "WIN",
					    JOptionPane.PLAIN_MESSAGE);
				System.exit(1);
				break;
			case 9:
				p.Slide();
				break;
			case 10:
				break;
			default://Bad tile, reset player
				player.reset();
				break;
			}
		}
	}
	
	public Tile getTile(int i, int j)
	{
		//Check if requested tile is in bounds
		if(i < 0 || i >= tiles.length || j < 0 || j >= tiles[i].length)
		{
			return null;
		}
		
		return tiles[i][j];
	}
	
	public Tile getStartTile()
	{
		return startTile;
	}

	private void stageSelect(int selection) {
		int dimension = STAGE_DIMENSION[selection];
		
		// clear previous grid map 
		tiles = new Tile[dimension][dimension]; 
		
		// Initialize grid map with random colors 
		for(int i = 0; i < dimension; i++){
			for(int j = 0; j < dimension; j++){
				tiles[i][j] = new Tile(); 
				tiles[i][j].updateGeometricState(0, true);
			}
		}
		
		// Create grid map based on stage #
		try {
			invocableEngine.invokeFunction("createStageGrid", tiles, selection);
		} catch (NoSuchMethodException | ScriptException e) {
			e.printStackTrace();
		}
		
		// Get starting tile
		for(int i = 0; i < dimension; i++){
			for(int j = 0; j < dimension; j++){
				if(tiles[i][j].getTileType() == 7)
				{
					startTile = tiles[i][j];
				}
			}
		}
		
	}

}
