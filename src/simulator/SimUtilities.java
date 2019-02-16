package simulator;

import com.jme3.bullet.control.RigidBodyControl;
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
	public String quaternionToString(Quaternion quat) {
		String resp = "";
		resp += "\nX Rot: " + quat.toAngles(null)[0];
		resp += "\nY Rot: " + quat.toAngles(null)[1];
		resp += "\nz Rot: " + quat.toAngles(null)[2];
		return resp;
	}
	
	
	/**
	 * Distance between two Vector3f objects
	 * @param loc1 - vector distance 1
	 * @param loc2 - vector distance 2
	 * @return distance
	 */
	public float distanceTo(Vector3f loc1, Vector3f loc2) {
		float xDist = (float) Math.pow(loc1.getX() - loc2.getX(), 2);
		float yDist = (float) Math.pow(loc1.getY() - loc2.getY(), 2);
		float zDist = (float) Math.pow(loc1.getZ() - loc2.getZ(), 2);
		return FastMath.sqrt(xDist + yDist + zDist);
	}
	
	/**
	 * Gets a position a certain distance in front of the "head" of an object
	 * @param objectLoc - location of object (Vector3f)
	 * @param objectRot - rotation of object (Quaternion)
	 * @param offset - How far in front of the object the position should be
	 * @return - Vector3f a certain distance in front
	 */
	public Vector3f locInFrontOfObject(Vector3f objectLoc, Quaternion objectRot, float offset) {
		float itemZRot  = objectRot.toAngles(null)[2];
		float xOffset = 0;
		float yOffset = 0;

		if(itemZRot > 0) {
			xOffset = (float) (Math.sin(Math.abs(itemZRot)) * offset);
		} else {
			xOffset = -1f * (float)(Math.sin(Math.abs(itemZRot)) * offset);
		}

		if(objectRot.toAngles(null)[0] > 0) {
			yOffset = -1f * (float) (Math.cos(itemZRot) * offset);
		} else {
			yOffset = (float) (Math.cos(itemZRot) * offset);
		}	

		objectLoc.setX(objectLoc.getX() + xOffset);
		objectLoc.setY(objectLoc.getY() + yOffset);
		objectLoc.setZ(objectLoc.getZ() + 0.4826f);

		return objectLoc;
	}
	
}
