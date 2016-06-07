package org.springfield.lou.application.types;

/**
 * @author Niels Bubel, Rundfunk Berlin-Brandenburg (RBB), Innovationsprojekte
 * @version 7.2 - final version, 31.05.2016
 * 
 * Helperclass to bind an object on a path location
 *
 */
public class PathBindObject {
	public String method;
	public Object object;
	
	/**
	 * Binds the object on the path 
	 * 
	 * @param m method
	 * @param o object
	 */
	public PathBindObject(String m,Object o) {
		this.method = m;
		this.object = o;
	}
}
