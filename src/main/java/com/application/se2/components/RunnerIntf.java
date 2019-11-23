package com.application.se2.components;

import com.application.se2.components.ComponentIntf.LogicIntf;
import com.application.se2.misc.Callback;


/**
 * Public interface of a Runner component.
 * 
 * @author sgra64
 */
public interface RunnerIntf extends LogicIntf {

	/**
	 * Invoked to start a component with three callouts for onStart, onExit and onErorr.
	 * 
	 * @param onStart callback invoked when component starts
	 * @param onExit callback invoked when component exits
	 * @param onError callback invoked when an error is reported for component
	 */
	void start(
		final Callback<Integer> onStart,
		final Callback<String> onExit,
		final Callback<String> onError
	);

	/**
	 * Invoked to exit the component, triggers onExit callout to be invoked.
	 * 
	 * @param msg message passed on exit.
	 */
	public void  exit( final String msg );

	/**
	 * Invoked to report an error during component execution, triggers onError callout.
	 * 
	 * @param msg error message.
	 */
	public void error( final String msg );

}
