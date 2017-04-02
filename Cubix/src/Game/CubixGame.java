	package Game;
	import java.awt.Color;

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
	import sage.renderer.IRenderer;
	import sage.scene.Group;
	import sage.scene.SceneNode;
	import sage.scene.SkyBox;
	import sage.scene.shape.Cube;
	import sage.scene.shape.Line;
	import sage.texture.Texture;
	import sage.texture.TextureManager;

	public class CubixGame extends BaseGame{
		// Mechanical Objects
		private ThirdPersonCameraController camController; 
		private ICamera cam; 
		private IRenderer renderer;
		private IDisplaySystem display;
		private IInputManager im;
		
		// Gameworld Objects
		private SceneNode avatar;  
		private SkyBox skybox; 
		Group rootNode; 
		
		protected void initGame(){
			im = getInputManager();
			display = getDisplaySystem();
			renderer = display.getRenderer();
			// init Camera
			cam = renderer.getCamera();
			cam.setPerspectiveFrustum(60, 1, 1, 1000);
			
			createScene(); 
			initInput(); 
		}
		
		private void createScene(){
			// create Textures
			Texture north = TextureManager.loadTexture2D("images/textures/island_north.jpg");
			Texture south = TextureManager.loadTexture2D("images/textures/island_south.jpg");
			Texture up = TextureManager.loadTexture2D("images/textures/island_up.jpg");
			Texture down = TextureManager.loadTexture2D("images/textures/island_down.jpg");
			Texture east = TextureManager.loadTexture2D("images/textures/island_east.jpg"); 
			Texture west = TextureManager.loadTexture2D("images/textures/island_west.jpg");
			
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
		
		protected void initInput(){
			 String mouseName = im.getMouseName();
			 String kbName = im.getKeyboardName();
			
			// apply SAGE built-in 3P camera controller
			camController = new ThirdPersonCameraController(cam, avatar, im, mouseName);
			
			// initialize A key
			IAction moveA = new MoveLeftKey(avatar);
			im.associateAction (
					 kbName, net.java.games.input.Component.Identifier.Key.A,
					 moveA, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			
			// initialize D key
			IAction moveD = new MoveRightKey(avatar);
			im.associateAction (
					 kbName, net.java.games.input.Component.Identifier.Key.D,
					 moveD, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			
			// initialize W key
			IAction moveW = new MoveUpKey(avatar);
			im.associateAction (
					 kbName, net.java.games.input.Component.Identifier.Key.W,
					 moveW, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			
			// initialize S key
			IAction moveS = new MoveDownKey(avatar);
			im.associateAction (
					 kbName, net.java.games.input.Component.Identifier.Key.S,
					 moveS, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		}
		
		public void update(float time){
			// update 3p camera
			camController.update(time);
			
			// update skybox position
			Point3D camLoc = cam.getLocation();
			Matrix3D camTranslation = new Matrix3D(); 
			camTranslation.translate(camLoc.getX(), camLoc.getY(), camLoc.getZ());
			skybox.setLocalTranslation(camTranslation);
			
			// regular update
			super.update(time);
		}
}
