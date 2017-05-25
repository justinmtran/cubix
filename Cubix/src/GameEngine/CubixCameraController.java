package GameEngine;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.IInputManager;
import sage.input.IInputManager.INPUT_ACTION_TYPE;
import sage.input.action.AbstractInputAction;
import sage.input.action.IAction;
import sage.scene.SceneNode;
import sage.util.MathUtils;

public class CubixCameraController{
	private float speed; 
	
	private ICamera camera; // the camera being controlled
	private SceneNode avatar; // the target the camera looks at
	private float cameraAzimuth; // rotation of camera around target Y axis
	private float cameraElevation; // elevation of camera above target
	private float cameraDistanceFromTarget; // radius 
	private Point3D targetPos; // avatar’s position in the world
	private Vector3D worldUpVec;
	private String controllerName; 
	
	private boolean isLPushed; 

	public CubixCameraController(ICamera cam, SceneNode avatar, IInputManager inputMgr, String controllerName){		 
		isLPushed = false; 
		camera = cam;	
		speed = 0; 
		this.avatar = avatar;
		this.controllerName = controllerName;
		worldUpVec = new Vector3D(0, 1, 0);
		cameraDistanceFromTarget = 12.0f;
		cameraAzimuth = 170; // start from BEHIND and ABOVE the target
		cameraElevation = 60.0f; // elevation is in degrees
		update(0.0f); // initialize camera state
		setupInput(inputMgr, controllerName);
	}

	public void update(float time) {
		updateTarget();
		updateCameraPosition();
		camera.lookAt(targetPos, worldUpVec); // SAGE built-in function
	}

	private void updateTarget() {
		targetPos = new Point3D(avatar.getWorldTranslation().getCol(3));
	}

	private void updateCameraPosition() {
		double theta = cameraAzimuth;
		double phi = cameraElevation;
		double r = cameraDistanceFromTarget;
		
		// calculate new camera position in Cartesian coords
		Point3D relativePosition = MathUtils.sphericalToCartesian(theta, phi, r);
		Point3D desiredCameraLoc = relativePosition.add(targetPos);
		camera.setLocation(desiredCameraLoc);
	}

	private void setupInput(IInputManager im, String cn) {
		// get the set of controllers from the controller environment
		ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
		Controller[] cs = ce.getControllers();

		// maps "LeftPushed" action and speeds to appropriate buttons on controller.
		for (int i = 0; i < cs.length; i++) {
			// find matching controller Name within the controller environment
			if(cs[i].getName().equals(this.controllerName)){
				if(cs[i].getType().toString().equals("Mouse")){
					// Left-Click or Right-Click held down
					IAction leftClick = new LeftPushed();
					im.associateAction(cn, net.java.games.input.Component.Identifier.Button.LEFT, leftClick, INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
					speed = 0.8f;
					break;
				} else if(cs[i].getType().toString().equals("Gamepad")){
					speed = 1.4f;
					isLPushed = true; 
					break;
				}
			}	
		}
			
		// Set up Camera Orbit for X and Y Axis
		IAction xRotate = new CameraRotateX();
		im.associateAction(cn, net.java.games.input.Component.Identifier.Axis.X, xRotate, INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		IAction yRotate = new CameraRotateY();
		im.associateAction(cn, net.java.games.input.Component.Identifier.Axis.Y, yRotate, INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		// Set up Camera Zoom 
		IAction zAxis = new CameraZoom();
		im.associateAction(cn, net.java.games.input.Component.Identifier.Axis.Z, zAxis, INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	}
	
	/**
	 *  Camera rotates on Y-axis with RESPECT to the AVATAR
	 */
	private class CameraRotateX extends AbstractInputAction { 
		
		@Override
		public void performAction(float time, net.java.games.input.Event evt) {
			float rotAmount = 0;
			
			if(isLPushed){
				// perform orbit on X-axis
				if (evt.getValue() < -0.2)
					rotAmount = evt.getValue() * speed;
				else if (evt.getValue() > 0.2)
					rotAmount = evt.getValue() * speed;

				cameraAzimuth += rotAmount;
				cameraAzimuth = cameraAzimuth % 360;
			}
		}
	}
	
	/**
	 *  Camera rotates on Y-axis with RESPECT to the AVATAR
	 */
	private class CameraRotateY extends AbstractInputAction {
		
		@Override
		public void performAction(float time, net.java.games.input.Event evt) {
			float rotAmount = 0;
			
			if(isLPushed){
				if (evt.getValue() < -0.2)
					rotAmount = evt.getValue() * speed;
				else if (evt.getValue() > 0.2)
						rotAmount = evt.getValue() * speed;
	
				cameraElevation += rotAmount;
				cameraElevation = cameraElevation % 360;
				
				// Y plane in between 0 and 90
				if(cameraElevation < 5)
					cameraElevation = 5; 
				else if(cameraElevation > 89)
					cameraElevation = 89; 
			}
		}
	}
	
	/**
	 * Camera zoom-in and zoom-out with RESPECT to the AVATAR
	 */
	private class CameraZoom extends AbstractInputAction{

		@Override
		public void performAction(float time, Event evt) {
			float transAmount = 0;
			if (evt.getValue() < -0.2)
				transAmount = -(evt.getValue() * speed);
			else if (evt.getValue() > 0.2)
					transAmount = -(evt.getValue() * speed);

			cameraDistanceFromTarget += transAmount;
			
			// zoom-in and zoom-out restrictions
			if(cameraDistanceFromTarget < 3f)
				cameraDistanceFromTarget = 3f; 
			else if(cameraDistanceFromTarget > 20f)
				cameraDistanceFromTarget = 20f; 
		}	
	}
	
	/**
	 * Checks if Left Mouse Button or Left Bumper is held down (raise flag if true). 
	 */
	private class LeftPushed extends AbstractInputAction{
		
		@Override
		public void performAction(float time, Event evt){
			if(evt.getValue() > 0)
				isLPushed = true;
			else
				isLPushed = false;
		}
	}
}