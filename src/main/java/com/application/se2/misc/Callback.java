package com.application.se2.misc;


/**
 * Callback functional interface used in one-argument, void lambda functions returning.
 * Alternatively, a Consumer could be used
 *  - https://docs.oracle.com/javase/8/docs/api/java/util/function/Consumer.html
 * 
 * Java's functional interfaces are defined in
 *  - https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html
 * 
 * @author sgra64
 *
 * @param <T> type/class of argument passed to function.
 */

@FunctionalInterface
public interface Callback<T> {

	/**
	 * Method to invoke the lambda function.
	 * 
	 * @param arg argument passed to function.
	 */
	void apply( T arg );

}
