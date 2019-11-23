package com.application.se2.fxgui;

import com.application.se2.components.BuilderIntf;
import com.application.se2.components.ComponentBase;
import com.application.se2.components.RunnerIntf;
import com.application.se2.misc.Callback;


/**
 * Public interface of FX/GUI-layer.
 * 
 * @author sgra64
 *
 */
public interface FXInterface {

	/**
	 * Access method to (hidden) FX-builder instance that implements the BuilderIntf.
	 * 
	 * @return (hidden) FX-builder instance.
	 */
	public static BuilderIntf getBuilder() {
		return FXBuilder.getInstance();
	}

	/**
	 * Sub-interface that extends RunnerIntf-methods by specific operations.
	 *
	 */
	interface FXRunnerIntf extends RunnerIntf {

		public void injectView( final ComponentBase component );

		public void show( final int selectTab );

		public void exitPlatform( final Callback<String> onExit );

	}

}
