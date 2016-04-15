import java.util.ArrayList;
import java.util.List;

import lejos.hardware.lcd.LCD;
import lejos.robotics.navigation.Pose;

/**
 * The Mapping class is responsible for holding the map data and methods to add to the map data.
 * A lot of experimentation was conducted in this class, most of which was deleted however some
 * methods and old code remain to show the thought process involved.
 * @author Oliver Palmer
 *
 */
public class Mapping {

	int mapRes;
	List<Observation> mapData;
	
	public Mapping(int resolution) {
		mapRes = resolution;
		mapData = new ArrayList<Observation>();
	}
	
	/**
	 * Adds positional data to the map log
	 * @param pose - position object from the Odometry Pose Provider
	 */
	public void logPosition(Pose pose) {
		mapData.add(new Observation(pose.getX(), pose.getY(), pose.getHeading(), 0));
	}
	
	/**
	 * Adds landmark data to the map log. Calculates the landmark position using the EV3's recorded location
	 * and the ultrasonic sensors distance reading.
	 * @param pose - position object from the Odometry Pose Provider
	 * @param distance - distance recorded by the ultrasonic sensor
	 */
	public void logObservation(Pose pose, UltrasonicSensor sensor) {
		float x = pose.getX();
		float y = pose.getY();
		float heading = pose.getHeading();
		
		// Old system for calculating the location of landmarks
		/*if (heading < 45 && heading > -45) {
			x += sensor.distance() * 1000;
		} else if (heading < -45 && heading > -135) {
			y -= sensor.distance() * 1000;
		} else if ((heading < -135 && heading > -225) || (heading > 135 && heading < 225)) {
			x -= sensor.distance() * 1000;
		} else if (heading < 135 && heading > 45){
			y += sensor.distance() * 1000;
		}*/
		x += (float)Math.cos(Math.toRadians(heading)) * sensor.distance() * 1000;
		y += (float)Math.sin(Math.toRadians(heading)) * sensor.distance() * 1000;
		mapData.add(new Observation(x, y, pose.getHeading(), 1));
	}
	
	/**
	 *  Getter for the map data
	 * @return
	 */
	public List<Observation> getRoute(){
		return this.mapData;
	}
	
	/**
	 * @deprecated
	 * Method to draw contents of map to the EV3 LCD screen.
	 * An attempt to draw to the screen of the EV3 was made but proved difficult and time consuming so
	 * was abandoned. This is some of the code used.
	 */
	public void draw(){
		LCD.clear();
		
		int centerX = mapRes/2;
		int centerY = mapRes/2;
		
		if (mapData != null) {
			int x;
			int y;
						
			for (Observation o : mapData) {
				
				if (o.getValue() == 0) {
					x = centerX + (Math.round(o.getX())) / 10;
					y = centerY + (Math.round(o.getY())) / 10;
					drawSquare(x - 1, y - 1, 2, 1);
				}
				if (o.getValue() == 1) {
					x = centerX + (Math.round(o.getX())) / 10;
					y = centerY + (Math.round(o.getY())) / 10;
					drawSquare(x - 1, y - 1, 2, 1);
				}
			}
		}
	}
	
	/**
	 * Draws a square to the EV3 screen
	 * @param x - x coordinate
	 * @param y - y coordinate
	 * @param size - size of square to draw
	 * @param value - colour of square
	 */
	public void drawSquare(int x, int y, int size, int value){
		// Set s to square size in pixels
		int s = size;
		for (int i = 0; i < s; i++){
			for (int j = 0; j < s; j++){
				LCD.setPixel(x, y, value);
				x++;
			}
			y++;
			x -= s;
		}
	}
}
