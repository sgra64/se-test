package com.application.se2.model;

import java.io.Serializable;


/**
 * Public interface to mark Entity classes. Each Entity-class must have a
 *  - unique, unmodifiable String id and a
 *  - String name property.
 * 
 * Implementing java.io.Serializable is required for Entity serialization
 * and de-serialization.
 * 
 * @author sgra64
 *
 */
public interface Entity extends Serializable {

	/**
	 * Get unique, unmodifiable Entity id.
	 * 
	 * @return unique, unmodifiable Entity id.
	 */
	public String getId();


	/**
	 * Get Entity name.
	 * 
	 * @return Entity name.
	 */
	public String getName();

}
