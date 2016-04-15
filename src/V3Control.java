import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.NXTUltrasonicSensor;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.Pose;
import lejos.utility.Delay;

/**
 * The controller class for the project. Contains the flow of logic for the program. Along with
 * Sensor and movement configuration
 * @author Oliver Palmer
 *
 */
public class V3Control {

	TouchSensor touch;
	UltrasonicSensor ultrasonic;
	MovePilot pilot;
	int distance = 0;
	int xCo = 0;
	int yCo = 0;
	Pose pose;
	
	public static void main(String[] args) {
		new V3Control();
	}
	
	public V3Control(){
		pilot = getPilot();
		Brick brick = BrickFinder.getDefault();
		OdometryPoseProvider posePro = new OdometryPoseProvider(pilot);
		Mapping map = new Mapping(50);
		
		// Touch sensor config
		Port s1 = brick.getPort("S1");
		EV3TouchSensor tSensor = new EV3TouchSensor(s1);
		touch = new TouchSensor(tSensor);
		
		// Ultrasonic sensor config
		Port s2 = brick.getPort("S2");
		NXTUltrasonicSensor uSensor = new NXTUltrasonicSensor(s2);
		ultrasonic = new UltrasonicSensor(uSensor.getMode("Distance"));
		
		
		pilot.forward();
		// Start flow of logic
		while(Button.ESCAPE.isUp()) {
			Delay.msDelay(1000);
			// Records location of the EV3
			map.logPosition(posePro.getPose());
			
			// Turns 90 degrees if an object is detected less than 30cm away from the sensor
			if (ultrasonic.distance() < 0.3){
				pilot.stop();
				// Delay to make sure the robot has stopped
				Delay.msDelay(1000);
				// Records location of the object
				map.logObservation(posePro.getPose(), ultrasonic);
				pilot.rotate(-90);
				pilot.forward();
			}
			// Reverses and turns the robot when the touch sensor is pressed
			if (touch.pressed()){
				pilot.stop();
				pilot.travel(-50);
				pilot.rotate(-90);
				pilot.forward();
			}
		}
		// Code to exit the program
		pilot.stop();
		tSensor.close();
		uSensor.close();
		@SuppressWarnings("unused")
		SaveData save = new SaveData(map.getRoute());
		System.exit(0);
	}
	
	/**
	 * Creates a Move Pilot object to control the movement of the EV3
	 * @return new MovePilot object
	 */
	public MovePilot getPilot(){
		Wheel wheelL = WheeledChassis.modelWheel(Motor.A, 43.2).offset(68.4);
		Wheel wheelR = WheeledChassis.modelWheel(Motor.B, 43.2).offset(-68.4);
		Chassis chassis = new WheeledChassis(new Wheel[]{wheelL, wheelR}, WheeledChassis.TYPE_DIFFERENTIAL);
		return pilotConfig(new MovePilot(chassis));
	}
	
	/**
	 * Configuration for the Move Pilot. Sets angular and linear speed and acceleration.
	 * @param pilot the Move Pilot to be configured
	 * @return The configured Move Pilot object
	 */
	public MovePilot pilotConfig(MovePilot pilot) {
		pilot.setLinearSpeed(100);
		pilot.setLinearAcceleration(100);
		pilot.setAngularSpeed(100);
		pilot.setAngularAcceleration(100);
		return pilot;
	}
	
}
