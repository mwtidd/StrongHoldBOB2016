// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc319.subsystems;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.usfirst.frc319.Robot;
import org.usfirst.frc319.commands.*;
import org.usfirst.frc319.commands.camera.RunTowerCamera;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;
import com.ni.vision.NIVision.ShapeMode;
import com.team319.robot.StatefulSubsystem;
import com.team319.robot.logging.LoggableSensor;
import com.team319.trajectory.PathManager;
import com.team319.trajectory.Waypoint;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.AxisCamera;
import edu.wpi.first.wpilibj.vision.AxisCamera.ExposureControl;
import edu.wpi.first.wpilibj.vision.AxisCamera.Resolution;
import edu.wpi.first.wpilibj.vision.AxisCamera.WhiteBalance;
import edu.wpi.first.wpilibj.vision.USBCamera;


/**
 *
 */
public class TowerCamera extends Subsystem{//extends StatefulSubsystem{

	public static boolean gotImage = false;
	
	private Image frame;

	//Images
	Image binaryFrame;
	int imaqError;
	USBCamera camera;
	//AxisCamera camera;

	//Final Constants
	private static double VIEW_ANGLE = 49.4; //View angle fo camera, set to Axis m1011 by default, 64 for m1013, 51.7 for 206, 52 for HD3000 square, 60 for HD3000 640x480

	//Flexible Constants

	private boolean SHOW_MASK = false;

	private NIVision.Range GREEN_TARGET_H_RANGE = new NIVision.Range(60, 150);	//Default hue range for the green target
	private NIVision.Range GREEN_TARGET_S_RANGE = new NIVision.Range(0, 255);		//Default saturation range for the green target
	private NIVision.Range GREEN_TARGET_V_RANGE = new NIVision.Range(10, 200);		//Default value range for the green target

	int MAX_PARTICLES = 5; //The maximum number of particles to iterate over
	double AREA_MINIMUM = 0.1; //Default Area minimum for particle as a percentage of total image area
	double SCORE_MIN = 75.0;  //Minimum score to be considered a target
	NIVision.ParticleFilterCriteria2 criteria[] = new NIVision.ParticleFilterCriteria2[1];
	NIVision.ParticleFilterOptions2 filterOptions = new NIVision.ParticleFilterOptions2(0,0,1,1);
	Scores scores = new Scores();
	
	//int session;
	
	private Thread thread;

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
	
	public TowerCamera(){
		
		initialize();
		
		thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(!thread.isInterrupted()){
					try{
						
			    		/**
			    		if(!camera.isFreshImage()){
			    			return;
			    		}
			    		**/

			    		frame = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
				    	
				    	

				    	/**
				    	//read file in from disk. For this example to run you need to copy image.jpg from the SampleImages folder to the
						//directory shown below using FTP or SFTP: http://wpilib.screenstepslive.com/s/4485/m/24166/l/282299-roborio-ftp
						NIVision.imaqReadFile(frame, "/home/lvuser/SampleImages/image.jpg");
						**/

				    	//this.readDashboard();

				    	//width to distance (distance = 0.002x^2 - 0.558x + 46.105)
			    		
			    		camera.getImage(frame);

				    	//NIVision.IMAQdxGrab(session, frame, 0);
				    	
				    	Robot.towerCamera.setFrame(frame);
				    	//camera.getImage(frame);
				    	Robot.towerCamera.gotImage = true;

				    	CameraServer.getInstance().setImage(frame);

				    	//SmartDashboard.putBoolean("GOT IMAGE", true);
						//CameraServer.getInstance().setImage(binaryFrame);

						frame.free();

			    	}catch(Exception e){
			    		e.printStackTrace();
			    		System.out.println(e.getMessage());
			    	}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		thread.start();
	}


    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public void initDefaultCommand() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        setDefaultCommand(new RunTowerCamera());

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
    }

    //PLEASE COMMENT
    public void initialize(){
    	/**
    	camera = new AxisCamera("10.3.20.11");

    	camera.writeMaxFPS(5);
    	camera.writeWhiteBalance(WhiteBalance.kHold);
    	camera.writeExposureControl(ExposureControl.kHold);
    	camera.writeResolution(Resolution.k640x480);
    	camera.writeCompression(0);
    	camera.writeExposurePriority(100);
    	**/
    	
    	initializeDashboard();

    	// create images
		criteria[0] = new NIVision.ParticleFilterCriteria2(NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA, AREA_MINIMUM, 100.0, 0, 0);

		// the camera name (ex "cam0") can be found through the roborio web interface
        //session = NIVision.IMAQdxOpenCamera("cam1", NIVision.IMAQdxCameraControlMode.CameraControlModeController);
        camera = new USBCamera("cam1");
        
        camera.setExposureManual(0);
        camera.setBrightness(25);
        
        camera.openCamera();
        camera.startCapture();

        //NIVision.IMAQdxStartAcquisition(session);
        
		//this.initializeDashboard();
    }

    public void processFrame(){

    	Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try{
					
					readDashboard();

	    			long startTime = System.nanoTime();


	    		Image frame = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
	    		binaryFrame = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);

	    		//NIVision.IMAQdxGrab(session, frame, 1000);
	    		
	    		camera.getImage(frame);
	    		//Robot.towerCamera.gotImage = true;
		    	//camera.getImage(frame);
	    		
	    		//CameraServer.getInstance().setImage(frame);

				//APPLY FILTER 2 (COLOR)
				NIVision.imaqColorThreshold(binaryFrame, frame, 255, NIVision.ColorMode.HSV, GREEN_TARGET_H_RANGE, GREEN_TARGET_S_RANGE, GREEN_TARGET_V_RANGE);

				//Send particle count to dashboard
				int numParticles = NIVision.imaqCountParticles(binaryFrame, 1);
				SmartDashboard.putNumber("PARTICLES FROM FILTER 2 (RGB)", numParticles);

				//filter out small particles
				//MWT: IN 2014 WE USED A WIDTH FILTER INSTEAD OF AREA
				criteria[0].lower = (float)(AREA_MINIMUM);
				imaqError = NIVision.imaqParticleFilter4(binaryFrame, binaryFrame, criteria, filterOptions, null);

				//Send particle count after filtering to dashboard
				numParticles = NIVision.imaqCountParticles(binaryFrame, 1);
				SmartDashboard.putNumber("PARTICLES FROM FILTER 3 (AREA)", numParticles);

				boolean foundTarget = false;
				double width = 0d;

				double minLeft = Double.MAX_VALUE;

				ParticleReport leftMostTarget = null;

				if(numParticles > 0){
					//Measure particles and sort by particle size
					Vector<ParticleReport> particles = new Vector<ParticleReport>();
					//MWT: IN 2014 WE USED A MAX PARTICLE COUNT TO AVOID BOGGING DOWN THE CPU
					for(int particleIndex = 0;  particleIndex < MAX_PARTICLES && particleIndex < numParticles; particleIndex++){
						//MWT: IN 2014 WE USED AN ASPECT RATIO FILTER HERE
						ParticleReport par = new ParticleReport();
						par.PercentAreaToImageArea = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA);
						par.Area = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_AREA);
						par.BoundingRectTop = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_TOP);
						par.BoundingRectLeft = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_LEFT);
						par.BoundingRectBottom = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_BOTTOM);
						par.BoundingRectRight = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_RIGHT);
						particles.add(par);

						if(par.BoundingRectLeft < minLeft){
							leftMostTarget = par;
							foundTarget = true;
						}

					}
					//particles.sort(null);

					//MWT: IN 2014 WE EXPLICITLY DIDN'T USE THE SCORES MECHANISM

					/**

					//This example only scores the largest particle. Extending to score all particles and choosing the desired one is left as an exercise
					//for the reader. Note that this scores and reports information about a single particle (single U shaped target). To get accurate information
					//you will need to evaluate the target against 1 or 2 other targets.
					scores.Aspect = getApspectScore(particles.elementAt(0));
					SmartDashboard.putNumber("Aspect", scores.Aspect);
					scores.Area = getAreaScore(particles.elementAt(0));
					SmartDashboard.putNumber("Area", scores.Area);

					foundTarget= scores.Aspect > SCORE_MIN && scores.Area > SCORE_MIN;
					distance = computeDistance(binaryFrame, particles.elementAt(0));

					**/

				}

				
				long finishTime = System.nanoTime();

				SmartDashboard.putNumber("IMAGE PROCESS TIME", ((finishTime-startTime)/1000000));

				//top left height width
				if(leftMostTarget != null){
					NIVision.Rect rect= new NIVision.Rect((int)leftMostTarget.BoundingRectLeft, (int)leftMostTarget.BoundingRectTop, 100, 100);
		    		NIVision.imaqDrawShapeOnImage(frame, frame, rect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_OVAL, 0.0f);
		    		//CameraServer.getInstance().setImage(frame);

		    		//SmartDashboard.putNumber("TARGET X1", leftMostTarget.BoundingRectLeft);
		    		//SmartDashboard.putNumber("TARGET X2", leftMostTarget.BoundingRectRight);

		    		SmartDashboard.putNumber("TARGET WIDTH",  leftMostTarget.BoundingRectRight - leftMostTarget.BoundingRectLeft);

		    		width = leftMostTarget.BoundingRectRight - leftMostTarget.BoundingRectLeft;

		    		
				}


				frame.free();
				binaryFrame.free();

				//MWT: IDEALLY WE WOULD PUT A GREEN AROUND THE IMAGE WHEN THE TARGET IS FOUND AND RED WHEN IT IS NOT
				//Send distance and target status to dashboard. The bounding rect, particularly the horizontal center (left - right) may be useful for rotating/driving towards a target
				SmartDashboard.putBoolean("Found Target", foundTarget);
				SmartDashboard.putNumber("Width", width);

				if(width > 0){
					List<Waypoint> waypoints = new ArrayList<Waypoint>();
					waypoints.add(new Waypoint(0,0,0));
					
					//use a fake 10 degree angle
					double angle = -10;
					double radians = (angle * Math.PI)/180;
					
					//0.0068x^2 - 0.989x + 35.592
		    		double distance = 0.0046*width*width - 0.7446*width + 29.212;
		    		
		    		SmartDashboard.putNumber("Distance (pre-adjust)", distance);
		    		
		    		distance = distance / Math.cos(radians);
		    		
		    		SmartDashboard.putNumber("Distance", distance);
					
					waypoints.add(new Waypoint(distance,0,radians));

					PathManager.getInstance().setWaypoints(waypoints);
				}

	    	}catch(Exception e){
	    		e.printStackTrace();
	    		System.out.println(e.getMessage());
	    	}
			}
		});

    	thread.start();


    }

    /**
    public void run(){

    	try{

    		frame = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
	    	
	    	

	    	//this.readDashboard();

	    	//width to distance (distance = 0.002x^2 - 0.558x + 46.105)

	    	NIVision.IMAQdxGrab(session, frame, 0);
	    	//camera.getImage(frame);
	    	Robot.towerCamera.gotImage = true;

	    	CameraServer.getInstance().setImage(frame);

	    	//SmartDashboard.putBoolean("GOT IMAGE", true);
			//CameraServer.getInstance().setImage(binaryFrame);

			frame.free();

    	}catch(Exception e){
    		e.printStackTrace();
    		System.out.println(e.getMessage());
    	}
    }
    
    **/

    //MWT: THIS IS NEVER REFERENCED
	//Comparator function for sorting particles. Returns true if particle 1 is larger
  	static boolean compareParticleSizes(ParticleReport particle1, ParticleReport particle2)
  	{
  		//we want descending sort order
  		return particle1.PercentAreaToImageArea > particle2.PercentAreaToImageArea;
  	}

  	/**
  	 * Converts a ratio with ideal value of 1 to a score. The resulting function is piecewise
  	 * linear going from (0,0) to (1,100) to (2,0) and is 0 for all inputs outside the range 0-2
  	 */
  	double ratioToScore(double ratio)
  	{
  		return (Math.max(0, Math.min(100*(1-Math.abs(1-ratio)), 100)));
  	}

  	double getAreaScore(ParticleReport report)
  	{
  		double boundingArea = (report.BoundingRectBottom - report.BoundingRectTop) * (report.BoundingRectRight - report.BoundingRectLeft);
  		//Tape is 7" edge so 49" bounding rect. With 2" wide tape it covers 24" of the rect.
  		return ratioToScore((49/24)*report.Area/boundingArea);
  	}

  	/**
  	 * Method to score if the aspect ratio of the particle appears to match the retro-reflective target. Target is 7"x7" so aspect should be 1
  	 */
  	double getApspectScore(ParticleReport report)
  	{
  		return ratioToScore(((report.BoundingRectRight-report.BoundingRectLeft)/(report.BoundingRectBottom-report.BoundingRectTop)));
  	}

  	/**
  	 * Computes the estimated distance to a target using the width of the particle in the image. For more information and graphics
  	 * showing the math behind this approach see the Vision Processing section of the ScreenStepsLive documentation.
  	 *
  	 * @param image The image to use for measuring the particle estimated rectangle
  	 * @param report The Particle Analysis Report for the particle
  	 * @return The estimated distance to the target in feet.
  	 */
  	double computeDistance (Image image, ParticleReport report) {
  		double normalizedWidth, targetWidth;
  		NIVision.GetImageSizeResult size;

  		size = NIVision.imaqGetImageSize(image);
  		normalizedWidth = 2*(report.BoundingRectRight - report.BoundingRectLeft)/size.width;
  		targetWidth = 7;

  		return  targetWidth/(normalizedWidth*12*Math.tan(VIEW_ANGLE*Math.PI/(180*2)));
  	}

  	private void initializeDashboard(){
  		//Put default values to SmartDashboard so fields will appear
  		SmartDashboard.putNumber("GREEN TARGET (H min)", GREEN_TARGET_H_RANGE.minValue);
  		SmartDashboard.putNumber("GREEN TARGET (H max)", GREEN_TARGET_H_RANGE.maxValue);
  		SmartDashboard.putNumber("GREEN TARGET (S min)", GREEN_TARGET_S_RANGE.minValue);
  		SmartDashboard.putNumber("GREEN TARGET (S max)", GREEN_TARGET_S_RANGE.maxValue);
  		SmartDashboard.putNumber("GREEN TARGET (V min)", GREEN_TARGET_V_RANGE.minValue);
  		SmartDashboard.putNumber("GREEN TARGET (V max)", GREEN_TARGET_V_RANGE.maxValue);

  		SmartDashboard.putNumber("PARTICLE COUNT", 0);

  		SmartDashboard.putBoolean("SHOW MASK", false);

  		//SmartDashboard.putBoolean("RED TEAM", RED_TEAM);
  		//SmartDashboard.putBoolean("BLUE TEAM", BLU_TEAM);

  		SmartDashboard.putNumber("AREA MIN %", AREA_MINIMUM);
  	}

  	private void readDashboard(){
  		//Update threshold values from SmartDashboard. For performance reasons it is recommended to remove this after calibration is finished.
  		GREEN_TARGET_H_RANGE.minValue = (int)SmartDashboard.getNumber("GREEN TARGET (H min)", GREEN_TARGET_H_RANGE.minValue);
  		GREEN_TARGET_H_RANGE.maxValue = (int)SmartDashboard.getNumber("GREEN TARGET (H max)", GREEN_TARGET_H_RANGE.maxValue);
  		GREEN_TARGET_S_RANGE.minValue = (int)SmartDashboard.getNumber("GREEN TARGET (S min)", GREEN_TARGET_S_RANGE.minValue);
  		GREEN_TARGET_S_RANGE.maxValue = (int)SmartDashboard.getNumber("GREEN TARGET (S max)", GREEN_TARGET_S_RANGE.maxValue);
  		GREEN_TARGET_V_RANGE.minValue = (int)SmartDashboard.getNumber("GREEN TARGET (V min)", GREEN_TARGET_V_RANGE.minValue);
  		GREEN_TARGET_V_RANGE.maxValue = (int)SmartDashboard.getNumber("GREEN TARGET (V max)", GREEN_TARGET_V_RANGE.maxValue);

  		SHOW_MASK = SmartDashboard.getBoolean("SHOW MASK", SHOW_MASK);

  		AREA_MINIMUM = SmartDashboard.getNumber("AREA MIN %", AREA_MINIMUM);
  	}

  	//A structure to hold measurements of a particle
  	public class ParticleReport implements Comparator<ParticleReport>, Comparable<ParticleReport>{
  		double PercentAreaToImageArea;
  		double Area;
  		double BoundingRectLeft;
  		double BoundingRectTop;
  		double BoundingRectRight;
  		double BoundingRectBottom;

  		public int compareTo(ParticleReport r)
  		{
  			return (int)(r.Area - this.Area);
  		}

  		public int compare(ParticleReport r1, ParticleReport r2)
  		{
  			return (int)(r1.Area - r2.Area);
  		}
  	};

  	//Structure to represent the scores for the various tests used for target identification
  	public class Scores {
  		double Area;
  		double Aspect;
  	};
  	
  	//@Override
  	public Map<String, Object> getCustomProperties() {
  		Map<String, Object> properties = new HashMap<String,Object>();
  		properties.put("gotImage", false);
  		return properties;
  	}

	//@Override
	public List<LoggableSensor> getSensors() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public synchronized Image getFrame() {
		return frame;
	}
	
	public synchronized void setFrame(Image frame){
		this.frame = frame;
	}
}

