package Game;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;

import GameEngine.GameClient;
import GameEngine.GameServer;
import GameEngine.MoveDownKey;
import GameEngine.MoveLeftKey;
import GameEngine.MoveRightKey;
import GameEngine.MoveUpKey;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.app.BaseGame;
import sage.camera.ICamera;
import sage.display.IDisplaySystem;
import sage.input.IInputManager;
import sage.input.ThirdPersonCameraController;
import sage.input.action.IAction;
import sage.networking.IGameConnection.ProtocolType;
import sage.renderer.IRenderer;
import sage.scene.SceneNode;
import sage.scene.SkyBox;
import sage.scene.shape.Cube;
import sage.scene.shape.Line;
import sage.scene.state.RenderState;
import sage.scene.state.TextureState;
import sage.terrain.AbstractHeightMap;
import sage.terrain.ImageBasedHeightMap;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.TextureManager;

	public class CubixGame extends BaseGame{
		// Mechanical Objects
		private ThirdPersonCameraController camController; 
		private ICamera cam; 
		private IRenderer renderer;
		private IDisplaySystem display;
		private IInputManager im;
		
		private String serverAddress;
		private int serverPort;
		private ProtocolType serverProtocol;
		private GameClient gameClient;
		
		// Texture Objects
		private TerrainBlock imgTerrain;
		
		// Gameworld Objects
		private SceneNode avatar;  
		private SkyBox skybox; 
		private boolean isConnected;
		private ScriptEngine engine;
		private String scriptName = "scripts/Script.js";
		private File scriptFile;
		private long fileLastModifiedTime;
		
		//public CubixGame(String serverAddress, int serverPort)
		public CubixGame()
		{
			super();
			
			//Get server information from console
			//this.serverAddress = serverAddress;
			//this.serverPort = serverPort;
			//this.serverProtocol = ProtocolType.TCP;
			
			this.serverAddress = "127.0.0.1";
			this.serverPort = 6000;
			this.serverProtocol = ProtocolType.TCP;
		}
		
		protected void initGame(){
			im = getInputManager();
			display = getDisplaySystem();
			renderer = display.getRenderer();
			// init Camera
			cam = renderer.getCamera();
			cam.setPerspectiveFrustum(60, 1, 1, 1000);
			
			 int result = JOptionPane.showConfirmDialog(null,  "Create server?","Server",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(result == JOptionPane.YES_OPTION)
				{
					try
					{
						new GameServer(6000);
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
				}
			
			try
			{
				gameClient = new GameClient(InetAddress.getByName(serverAddress), serverPort, serverProtocol, this);
				System.out.println(gameClient);
			}
			catch(UnknownHostException e) {e.printStackTrace();}
			catch(IOException e) {e.printStackTrace();}
			
			if(gameClient != null) {gameClient.sendJoinMessage();}
			
			ScriptEngineManager factory = new ScriptEngineManager();
			List<ScriptEngineFactory> list = factory.getEngineFactories();
			engine = factory.getEngineByName("js");
			scriptFile = new File(scriptName);
			fileLastModifiedTime = 0; //scriptFile.lastModified();
			this.runScript();
			
			
			createScene(); 
			initTerrain();
			initInput(); 
		}
		
		private void createScene(){
			// create Textures
			Texture north = TextureManager.loadTexture2D("images/textures/stage_island/island_north.jpg");
			Texture south = TextureManager.loadTexture2D("images/textures/stage_island/island_south.jpg");
			Texture up = TextureManager.loadTexture2D("images/textures/stage_island/island_up.jpg");
			Texture down = TextureManager.loadTexture2D("images/textures/stage_island/island_down.jpg");
			Texture east = TextureManager.loadTexture2D("images/textures/stage_island/island_east.jpg"); 
			Texture west = TextureManager.loadTexture2D("images/textures/stage_island/island_west.jpg");
			
			// add Skybox
			skybox = new SkyBox("Background",20.0f, 20.0f, 20.0f); 
			skybox.setTexture(SkyBox.Face.North, north);
			skybox.setTexture(SkyBox.Face.South, south);
			skybox.setTexture(SkyBox.Face.Up, up);
			skybox.setTexture(SkyBox.Face.Down, down);
			skybox.setTexture(SkyBox.Face.East, east);
			skybox.setTexture(SkyBox.Face.West, west);
			addGameWorldObject(skybox);
			
			// add Player 
			avatar = new Cube();
			avatar.rotate(180, new Vector3D(0,1,0));
			addGameWorldObject(avatar);
			
			// add 3D axis
			Point3D origin = new Point3D(0,0,0);
			Point3D xEnd = new Point3D(100,0,0);
			Point3D yEnd = new Point3D(0,100,0);
			Point3D zEnd = new Point3D(0,0,100);
			Line xAxis = new Line (origin, xEnd, Color.red, 2);
			Line yAxis = new Line (origin, yEnd, Color.green, 2);
			Line zAxis = new Line (origin, zEnd, Color.blue, 2);
			addGameWorldObject(xAxis); 
			addGameWorldObject(yAxis);
			addGameWorldObject(zAxis);
		}
		
		private void initTerrain() { 
			// create height map and terrain block
			ImageBasedHeightMap myHeightMap = new ImageBasedHeightMap("images/terrains/height_map.jpg");
			imgTerrain = createTerBlock(myHeightMap);
			
			// create texture and texture state to color the terrain
			TextureState sandState;
			Texture sandTexture = TextureManager.loadTexture2D("images/textures/stage_island/sand_texture.jpg");
			sandTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
			sandState = (TextureState) display.getRenderer().createRenderState(RenderState.RenderStateType.Texture);
			sandState.setTexture(sandTexture, 0);
			sandState.setEnabled(true);
			
			// apply the texture to the terrain
			imgTerrain.setRenderState(sandState);
			addGameWorldObject(imgTerrain);
		}
		
		private TerrainBlock createTerBlock(AbstractHeightMap heightMap) {
			float heightScale = .008f; // scaling the height of terrain 
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
		
		protected void initInput(){
			 String mouseName = im.getMouseName();
			 String kbName = im.getKeyboardName();
			
			// apply SAGE built-in 3P camera controller
			camController = new ThirdPersonCameraController(cam, avatar, im, mouseName);
			
			// initialize A key
			IAction moveA = new MoveLeftKey(avatar, gameClient, imgTerrain);
			im.associateAction (
					 kbName, net.java.games.input.Component.Identifier.Key.A,
					 moveA, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			
			// initialize D key
			IAction moveD = new MoveRightKey(avatar, gameClient, imgTerrain);
			im.associateAction (
					 kbName, net.java.games.input.Component.Identifier.Key.D,
					 moveD, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			
			// initialize W key
			IAction moveW = new MoveUpKey(avatar, gameClient, imgTerrain);
			im.associateAction (
					 kbName, net.java.games.input.Component.Identifier.Key.W,
					 moveW, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			
			// initialize S key
			IAction moveS = new MoveDownKey(avatar, gameClient, imgTerrain);
			im.associateAction (
					 kbName, net.java.games.input.Component.Identifier.Key.S,
					 moveS, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		}
		
		public void update(float time)
		{
			// update 3p camera
			camController.update(time);
			
			// update skybox position
			Point3D camLoc = cam.getLocation();
			Matrix3D camTranslation = new Matrix3D(); 
			camTranslation.translate(camLoc.getX(), camLoc.getY(), camLoc.getZ());
			skybox.setLocalTranslation(camTranslation);
			
			// check packets
			if(gameClient != null)
			{
				gameClient.processPackets();
			}
			
			//Run script if file changes
			long modTime = scriptFile.lastModified();
			if(modTime > fileLastModifiedTime)
			{
				fileLastModifiedTime = modTime;
				runScript();
				executeScript();
			}
			
			// regular update
			super.update(time);
		}

		public void setIsConnected(boolean b) {
			isConnected = b;
		}

		public Vector3D getPosition() {
			return avatar.getWorldTranslation().getCol(3);
		}
		
		protected void shutdown()
		{
			super.shutdown();
			if(gameClient != null)
			{
				gameClient.sendByeMessage();
				try
				{
					gameClient.shutdown();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		public void addGhost(GhostAvatar ghost)
		{
			addGameWorldObject(ghost);
			executeScript();
		}
		
		public void removeGhost(GhostAvatar ghost)
		{
			removeGameWorldObject(ghost);
		}
		
		private void runScript()
		{
			try
			{
				FileReader fileReader = new FileReader(scriptFile);
				engine.eval(fileReader);
				fileReader.close();
			}
			catch (FileNotFoundException e1)
			{
				System.out.println(scriptFile + " not found" + e1);
			}
			catch (IOException e2)
			{
				System.out.println("IO problem with " + scriptFile + e2);
			}
			catch (ScriptException e3)
			{
				System.out.println("ScriptException in " + scriptFile + e3);
			}
			catch (NullPointerException e4)
			{
				System.out.println("Null ptr exception reading " + scriptFile + e4);
			}
			
		}
		
		private void executeScript()
		{
			Invocable invocableEngine = (Invocable) engine;
			try
			{
				ArrayList<GhostAvatar> ghosts = gameClient.getGhostAvatars();
				for(int i = 0; i < ghosts.size(); i++)
				{
					invocableEngine.invokeFunction("updateGhost", ghosts.get(i));
				}
			}
			catch (ScriptException e1)
			{
				System.out.println("ScriptException in " + scriptFile + e1);
			}
			catch (NoSuchMethodException e2)
			{
				System.out.println("No such method exception in " + scriptFile + e2);
			}
			catch(NullPointerException e3)
			{
				System.out.println("Null pointer exception reading " + scriptFile + e3);
			}
		}

}
