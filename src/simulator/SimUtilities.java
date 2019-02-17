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
	
	public static Vector3f getPerpendicularDirection(VehicleControl robotControl, boolean rightSide) {
		if(rightSide)
			return Griebel_DeweyMethod(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), 1.57f, .25f, 0);
		return Griebel_DeweyMethod(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), FastMath.HALF_PI, .25f, 0);
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
	 * Gets a position a certain distance in front of the "head" of an object
	 * @param objectLoc - location of object (Vector3f)
	 * @param objectRot - rotation of object (Quaternion)
	 * @param angleChange - The angle you want the object to be held at along arc in radians
	 * @param offset - How far in front of the object the position should be
	 * @param height - the height at which the vector will be pointing to relative to objectsLoc
	 * @return - Vector3f a certain distance at the angle specified
	 */
	public static Vector3f Griebel_DeweyMethod(Vector3f objectLoc, Quaternion objectRot, float angleChange, float offset, float height) {
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
}
