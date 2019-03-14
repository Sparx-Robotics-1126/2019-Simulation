package simulator;

import com.jme3.bullet.control.VehicleControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class SimUtilities {
	/* This class is a bunch of general methods that I found use for in more than one class
	 * Feel free to add to it if you have other useful stuff
	 */
	
	/**
	 * Determines whether 2 Vectors (usually position or rotation) are close to each other
	 * @param loc1 - the first vector
	 * @param loc2 - the second vector
	 * @param tolerance - the maximum distance apart that they can be
	 * @return whether or not the distance between the two vectors are in the specified tolerance
	 */
	public static boolean vectorsAreSortaClose(Vector3f loc1, Vector3f loc2, float tolerance) {
		if(distanceTo(loc1, loc2) > tolerance) {
			return false;
		} else {
			return true;
		}
	}
	
	public static double getZAngle(Quaternion q) {
		float[] angles = q.toAngles(null);
		double z = 90*angles[2]/FastMath.HALF_PI;
		double x = angles[0];
		double robotAngle = 0;
		if(z <= 0 && x <= 0) {
			robotAngle = 90+z;
		} else if(x > 0) {
			robotAngle = 270 - z;
		}
		return robotAngle;
	}
	
	/**
	 * Gives Euler angles for a quaternion in string form (good for system.out printing)
	 * @param quat - Quaternion to print
	 * @return - String representation of converted quaternion
	 */
	public static String quaternionToString(Quaternion quat) {
		String resp = "";
		resp += "\nX Rot: " + quat.toAngles(null)[0];
		resp += "\nY Rot: " + quat.toAngles(null)[1];
		resp += "\nz Rot: " + quat.toAngles(null)[2];
		return resp;
	}
	
	/**
	 * Gets the vector representing a DIRECTION that is perpendicular to the robot, ***NOT A LOCATION***
	 * @param robotControl - The vehicle control that the vector is found around
	 * @param rightSide - Whether you want the direction off the right side or off the left side
	 * @return - the vector of 3 floats representing the perpendicular direction
	 */
	public static Vector3f getPerpendicularDirection(VehicleControl robotControl, boolean rightSide) {
		float itemZRot  = robotControl.getPhysicsRotation().toAngles(null)[2];
		float itemXRot = robotControl.getPhysicsRotation().toAngles(null)[0];
		float xOffset = 0;
		float yOffset = 0;

		if(itemXRot <= 0) {
			itemZRot -= FastMath.HALF_PI;
		}else {
			itemZRot += FastMath.HALF_PI;
		}
		
		if(itemZRot > 0) {
			xOffset = (float) (FastMath.sin(itemZRot));
		} else {
			xOffset = -1f * (float)(FastMath.sin(FastMath.abs(itemZRot)));
		}

		if(robotControl.getPhysicsRotation().toAngles(null)[0] > 0) {
			yOffset = -1f * (float) (FastMath.cos(itemZRot));
		} else {
			yOffset = (float) (FastMath.cos(itemZRot));
		}	
		if(rightSide)
			return new Vector3f(xOffset, yOffset, 0);
		else
			return new Vector3f(-xOffset, -yOffset, 0);
	}
	
	/**
	 * Distance between two Vector3f objects
	 * @param loc1 - vector distance 1
	 * @param loc2 - vector distance 2
	 * @return distance
	 */
	public static float distanceTo(Vector3f loc1, Vector3f loc2) {
		float xDist = (float) Math.pow(loc1.getX() - loc2.getX(), 2);
		float yDist = (float) Math.pow(loc1.getY() - loc2.getY(), 2);
		float zDist = (float) Math.pow(loc1.getZ() - loc2.getZ(), 2);
		return FastMath.sqrt(xDist + yDist + zDist);
	}
	
	/**
	 * Gets a position a specified distance in front of a specified angle of an object
	 * @param objectLoc - location of object (Vector3f)
	 * @param objectRot - rotation of object (Quaternion)
	 * @param angleChange - The angle you want the object to be held at along arc in radians
	 * @param offset - How far in front of the object the position should be
	 * @param height - the height at which the vector will be pointing to relative to objectsLoc
	 * @return - Vector3f a certain distance at the angle specified
	 */
	public static Vector3f getPointAtAngleAndOffsetOfObject(Vector3f objectLoc, Quaternion objectRot, float angleChange, float offset, float height) {
		float itemZRot  = objectRot.toAngles(null)[2];
		float itemXRot = objectRot.toAngles(null)[0];
		float xOffset = 0;
		float yOffset = 0;

		if(itemXRot <= 0) {
			itemZRot -= angleChange;
		}else {
			itemZRot += angleChange;
		}
		
		if(itemZRot > 0) {
			xOffset = (float) (FastMath.sin(itemZRot) * offset);
		} else {
			xOffset = -1f * (float)(FastMath.sin(FastMath.abs(itemZRot)) * offset);
		}

		if(objectRot.toAngles(null)[0] > 0) {
			yOffset = -1f * (float) (FastMath.cos(itemZRot) * offset);
		} else {
			yOffset = (float) (FastMath.cos(itemZRot) * offset);
		}	

		objectLoc.setX(objectLoc.getX() + xOffset);
		objectLoc.setY(objectLoc.getY() + yOffset);
		objectLoc.setZ(objectLoc.getZ() + height);

		return objectLoc;
	}
	
	/**
	 * Returns a vector with everything as 0
	 * @return vector with all 0s
	 */
	public static Vector3f zeroVec() {
		return new Vector3f(0f, 0f, 0f);
	}
	
}
