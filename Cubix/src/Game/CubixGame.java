package Game;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

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
import graphicslib3D.Vertex3D;
import sage.app.BaseGame;
import sage.audio.AudioManagerFactory;
import sage.audio.AudioResource;
import sage.audio.AudioResourceType;
import sage.audio.IAudioManager;
import sage.audio.Sound;
import sage.audio.SoundType;
import sage.camera.ICamera;
import sage.display.IDisplaySystem;
import sage.input.IInputManager;
import sage.input.action.IAction;
import sage.model.loader.OBJLoader;
import sage.model.loader.ogreXML.OgreXMLParser;
import sage.networking.IGameConnection.ProtocolType;
import sage.physics.IPhysicsEngine;
import sage.physics.PhysicsEngineFactory;
import sage.renderer.IRenderer;
import sage.scene.Group;
import sage.scene.Model3DTriMesh;
import sage.scene.SceneNode;
import sage.scene.SceneNode.RENDER_MODE;
import sage.scene.TriMesh;
import sage.scene.shape.Line;
import sage.scene.shape.Sphere;
import sage.scene.state.BlendState;
import sage.scene.state.RenderState;
import sage.scene.state.RenderState.RenderStateType;
import sage.scene.state.TextureState;
import sage.terrain.AbstractHeightMap;
import sage.terrain.ImageBasedHeightMap;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.TextureManager;
import sage.texture.Texture.ApplyMode;

	public class CubixGame extends BaseGame{
		// Constants
		private final int MAX_SNOW = 40;
		
		
		
		// Mechanical Objects
		private CubixCameraController camController; 
		private ICamera cam; 
		private IRenderer renderer;
		private IDisplaySystem display;
		private IInputManager im;
		private IPhysicsEngine pe; 
		
		private String serverAddress;
		private int serverPort;
		private ProtocolType serverProtocol;
		private GameClient gameClient;
		private String playerTextureName;
		private boolean isHosting;
		private boolean isMultiplayer;
		private String levelThemeName;
		
		IAudioManager audioMgr;
		Sound ghostSound;
		AudioResource resource1;
		
		// Texture Objects
		private TerrainBlock imgTerrain;
		
		// Gameworld Objects
		private PlayerAvatar player;
		private Theme skybox; 
		private boolean isConnected;
		private ScriptEngine engine;
		private String scriptName = "scripts/Script.js";
		private File scriptFile;
		private long fileLastModifiedTime;
		private Sphere[] snow; 
		private float windTimer; 
		private NPCGhostController ghost;
		
		//Animated Objects
		private Group lighthouse;
		
		//public CubixGame(String serverAddress, int serverPort)
		public CubixGame()
		{
			super();
			
			//Get server information from console
			//this.serverAddress = serverAddress;
			//this.serverPort = serverPort;
			//this.serverProtocol = ProtocolType.TCP;
			
			//this.serverAddress = "127.0.0.1";
			//this.serverPort = 6000;
			this.serverProtocol = ProtocolType.TCP;
		}
		
		protected void initGame(){
			im = getInputManager();
			display = getDisplaySystem();
			renderer = display.getRenderer();
			
			// init Camera
			cam = renderer.getCamera();
			cam.setPerspectiveFrustum(60, 1, 1, 1000);
			
			//Get game initialization options.  Network options, player texture, level theme
			getOptions();
			initNetwork();
			
			ScriptEngineManager factory = new ScriptEngineManager();
			List<ScriptEngineFactory> list = factory.getEngineFactories();
			engine = factory.getEngineByName("js");
			scriptFile = new File(scriptName);
			fileLastModifiedTime = 0; //scriptFile.lastModified();
			this.runScript();
			

			if(gameClient != null)
			{
				gameClient.processPackets();
			}

			createScene(); 
			initTerrain();
			
			// initalize physics
			if(levelThemeName.equals("Snow"))
			{
				initPhysicsSystem(); 
				createSagePhysicsWorld(); 	
			}

			createPlayer(); 
			if(levelThemeName.equals("Halloween"))
			{
				//Add ghost
				ghost = new NPCGhostController(player, this);
				ghost.translate(10, 1.5f, 10);
				ghost.scale(0.35f, 0.35f, 0.35f);
				ghost.updateGeometricState(0, true);
				addGameWorldObject(ghost);
			}
			initInput(); 
			initAudio();
		}
		
		private void getOptions()
		{
			SettingsDialog dialog = new SettingsDialog();
			String[] data = dialog.showDialog();
			try
			{
				levelThemeName = data[0];
				playerTextureName = data[1];
				isMultiplayer = new Boolean(data[2]);
				isHosting = new Boolean(data[3]);
				serverAddress = data[4];
			    serverPort = Integer.parseInt(data[5]);			
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(null, "Invalid Selection");
				System.exit(1);
			}

		}
		
		private void createPlayer(){
			playerTextureName = "images/textures/objects/" + playerTextureName + ".png";
			player = new PlayerAvatar(playerTextureName, this, gameClient);
			player.translate(3, 0, 3);
			addGameWorldObject(player);
			if(gameClient != null)
			{
				gameClient.sendCreateMessage(getPosition(), getPlayerTextureName());
			}
			
		}
		
		protected void initPhysicsSystem(){
			String engine = "sage.physics.ODE4J.ODE4JPhysicsEngine";
			pe = PhysicsEngineFactory.createPhysicsEngine(engine);
			pe.initSystem();
			pe.setGravity(new float[] {0, -.1f, 0});
		}
		
		private void createSagePhysicsWorld(){
			for(int i = 0; i < MAX_SNOW; i++){
				// add the snow physics
				float mass = 1.0f;
				snow[i].setPhysicsObject(pe.addSphereObject(pe.nextUID(), mass, 
										 snow[i].getWorldTransform().getValues(),1.0f));
			}
		}
		
		private void initNetwork()
		{
			if(isMultiplayer)
			{
				if(isHosting)
				{
					try
					{
						new GameServer(serverPort, levelThemeName);
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
			}

		}
		
		private void createScene(){
			Iterator<SceneNode> itr;
			// add Skybox
			skybox = new Theme("Background",20.0f, 20.0f, 20.0f); 
			switch(levelThemeName)
			{
				case "Island":
					skybox.islandTheme();
					addGameWorldObject(skybox);
					
					//Add lighthouse
					lighthouse = getLighthouse();
					lighthouse.translate(20, 0, 20);
					lighthouse.updateGeometricState(0, true);
					addGameWorldObject(lighthouse);
					itr = lighthouse.getChildren();
					while(itr.hasNext())
					{
						Model3DTriMesh mesh = ((Model3DTriMesh)itr.next());
						mesh.startAnimation("Rotate");
					}
					
					BlendState btransp = (BlendState) renderer.createRenderState(RenderStateType.Blend);
					btransp.setBlendEnabled(true);
					btransp.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
					btransp.setDestinationFunction(BlendState.DestinationFunction.DestinationAlpha);
					btransp.setTestEnabled(true);
					btransp.setTestFunction(BlendState.TestFunction.GreaterThan);
					btransp.setEnabled(true);
					lighthouse.setRenderState(btransp);
					lighthouse.updateRenderStates();
					lighthouse.setRenderMode(RENDER_MODE.TRANSPARENT);
					break;
				case "Snow":
					skybox.snowTheme(this);
					addGameWorldObject(skybox);
					
					snow = new Sphere[MAX_SNOW];
					
					// add snow
					Random rn = new Random();  
					for(int i = 0; i < MAX_SNOW; i++){
						snow[i] = new Sphere(.09, 16, 16, Color.white);
						Matrix3D xform = new Matrix3D();
						xform.translate(rn.nextInt(30), rn.nextInt(15)+10, rn.nextInt(30));
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
		
		public void initAudio()
		{
			audioMgr = AudioManagerFactory.createAudioManager("sage.audio.joal.JOALAudioManager");
			if(!audioMgr.initialize())
			{
				System.out.println("Audio Manager failed to initialize!");;
				return;				
			}
			

			resource1 = audioMgr.createAudioResource("sounds/ghost.wav", AudioResourceType.AUDIO_SAMPLE);
			ghostSound = new Sound(resource1, SoundType.SOUND_EFFECT, 75, true);
			ghostSound.initialize(audioMgr);
	
			setEarParameters();
			
			if(levelThemeName.equals("Halloween"))
			{
				ghostSound.setMaxDistance(50f);
				ghostSound.setMinDistance(5f);
				ghostSound.setRollOff(5.0f);
				ghostSound.setLocation(new Point3D(ghost.getWorldTranslation().getCol(3)));
				ghostSound.play();
			}
			
		}
		
		public void releaseSounds()
		{
			ghostSound.release(audioMgr);
			resource1.unload();
			audioMgr.shutdown();
		}
		
		public void setEarParameters()
		{			
			audioMgr.getEar().setLocation(new Point3D(player.getWorldTranslation().getCol(3)));
			audioMgr.getEar().setOrientation(cam.getViewDirection(), new Vector3D(0,1,0));
		}
		
		private Group getLighthouse()
		{
			Group model = null;
			OgreXMLParser loader = new OgreXMLParser();
			try
			{
				String slash = File.separator;
				model = loader.loadModel("objects" + slash + "Lighthouse.mesh.xml",
										"materials" + slash + "Lighthouse.material",
										"objects" + slash + "Lighthouse.skeleton.xml", "images" + slash + "textures" + slash + "objects" + slash, ApplyMode.Replace);
				model.updateGeometricState(0, true);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
			
			return model;
		}
		
		public Group getGhost()
		{
			Group model = null;
			OgreXMLParser loader = new OgreXMLParser();
			try
			{
				String slash = File.separator;
				model = loader.loadModel("objects" + slash + "ghost.mesh.xml",
										"materials" + slash + "ghost.material",
										"objects" + slash + "ghost.skeleton.xml");
				model.updateGeometricState(0, true);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
			
			return model;
		}
		
		private void initTerrain() { 
			// create height map and terrain block
			ImageBasedHeightMap myHeightMap = new ImageBasedHeightMap("images/terrains/height_map.jpg");
			imgTerrain = createTerBlock(myHeightMap);
			
			// create texture and texture state to color the terrain
			TextureState state;
			Texture sandTexture = TextureManager.loadTexture2D("images/textures/stage_" + levelThemeName + "/" + levelThemeName + "_texture.jpg");
			sandTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
			state = (TextureState) display.getRenderer().createRenderState(RenderState.RenderStateType.Texture);
			state.setTexture(sandTexture, 0);
			state.setEnabled(true);
			
			// apply the texture to the terrain
			imgTerrain.setRenderState(state);
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
			camController = new CubixCameraController(cam, player, im, mouseName);
			
			
			// initialize A key
			IAction moveA = new MoveLeftKey(player, gameClient, imgTerrain);
			im.associateAction (
					 kbName, net.java.games.input.Component.Identifier.Key.A,
					 moveA, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
			
			// initialize D key
			IAction moveD = new MoveRightKey(player, gameClient, imgTerrain);
			im.associateAction (
					 kbName, net.java.games.input.Component.Identifier.Key.D,
					 moveD, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
			
			// initialize W key
			IAction moveW = new MoveUpKey(player, gameClient, imgTerrain);
			im.associateAction (
					 kbName, net.java.games.input.Component.Identifier.Key.W,
					 moveW, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
			
			// initialize S key
			IAction moveS = new MoveDownKey(player, gameClient, imgTerrain);
			im.associateAction (
					 kbName, net.java.games.input.Component.Identifier.Key.S,
					 moveS, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		}

		public void setIsConnected(boolean b) {
			isConnected = b;
		}

		public Vector3D getPosition() {
			return player.getLocalTranslation().getCol(3);
		}
		
		protected void shutdown()
		{
			super.shutdown();
			releaseSounds();
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
			//executeScript();
		}
		
		public void removeGhost(GhostAvatar ghost)
		{
			removeGameWorldObject(ghost);
		}
		
		public String getPlayerTextureName()
		{
			return playerTextureName;
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
		
		public void update(float time)
		{
			Iterator<SceneNode> itr;
			if(levelThemeName.equals("Snow"))
			{
				// WIND PHYSICS
				windTimer +=time; 
				if(windTimer < 5000) // NO WIND
					pe.setGravity(new float[] {-.01f, -.1f, 0});
				else{ // WIND
					pe.setGravity(new float[] { -.2f, -.1f, 0});
					if(windTimer > 8000)
						windTimer = 0; 
				}
				// WIND PHYSICS
				for(int i = 0; i < MAX_SNOW; i++){
					if(snow[i].getWorldTransform().getCol(3).getY() <= 1){
						Random rn = new Random(); 
						Matrix3D xform = new Matrix3D();
						xform.translate(rn.nextInt(30), rn.nextInt(15)+10, rn.nextInt(30));
						snow[i].getLocalTranslation().setCol(3,xform.getCol(3));
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
			
			//update sounds, ear
			setEarParameters();
			if(levelThemeName.equalsIgnoreCase("Halloween"))
			{
				ghostSound.setLocation(new Point3D(ghost.getWorldTranslation().getCol(3)));
				ghost.npcLoop(time);
			}
			
			if(levelThemeName.equals("Island"))
			{
				//Update animations
				itr = lighthouse.getChildren();
				while(itr.hasNext())
				{
					Model3DTriMesh submesh = ((Model3DTriMesh)itr.next());
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
			if(gameClient != null)
			{
				gameClient.processPackets();
			}
			
			//Run script if file changes
			//long modTime = scriptFile.lastModified();
			//if(modTime > fileLastModifiedTime)
			{
				//fileLastModifiedTime = modTime;
				//runScript();
				//executeScript();  Need to implement this as grid creation.  Temporarily removed.
			}
			
			player.update(time);
				
			//Update ghostAvatars
			if(gameClient != null)
			{
				ArrayList<GhostAvatar> ghosts = gameClient.getGhostAvatars();
				for(int i = 0; i < ghosts.size(); i++)
				{
					ghosts.get(i).update(time);
				}
					
			}
			
			// regular update
			super.update(time);
		}
		
		public void setTheme(String t)
		{
			levelThemeName = t;
		}
		
		public void updateVerticalPosition(PlayerAvatar target){
			 // get avatar's X and Y coord.
			 Point3D avLoc = new Point3D(target.getLocalTranslation().getCol(3)); // get local XYZ coord
			 float x = (float) avLoc.getX();
			 float z = (float) avLoc.getZ();
			 
			 // get Y coord based of terrain's local X,Y
			 float terHeight = imgTerrain.getHeight(x,z);
			 
			 // calculate new Y for avatar 
			 float desiredHeight = terHeight + (float)imgTerrain.getOrigin().getY() + 0.5f;
			 
			 // apply Y translation 
			 if(desiredHeight >= -2)
			 {
				 target.getLocalTranslation().setElementAt(1, 3, desiredHeight+0.6);
			 }
			 
		 }
		
}
