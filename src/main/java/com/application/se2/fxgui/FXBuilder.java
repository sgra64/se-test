package com.application.se2.fxgui;

import com.application.se2.AppConfigurator.LoggerTopics;
import com.application.se2.components.BuilderIntf;
import com.application.se2.misc.Logger;


/**
 * JavaFX-GUI Builder as local singleton component class.
 * 
 * @author sgra64
 *
 */
class FXBuilder implements BuilderIntf {
	private static Logger logger = Logger.getInstance( FXBuilder.class );
	private static FXBuilder instance = null;


	/**
	 * Private constructor according to singleton pattern.
	 */
	private FXBuilder() { }


	/**
	 * Access method to singleton instance created when first called.
	 * @return reference to singleton builder instance.
	 */
	public static FXBuilder getInstance() {
		if( FXBuilder.instance == null ) {
			FXBuilder.instance = new FXBuilder();
		}
		return FXBuilder.instance;
	}


	/**
	 * Build code returning a runner instance.
	 * @return runner instance.
	 */
	@Override
	public FXInterface.FXRunnerIntf build() {
		FXRunner fxr = FXRunner.getInstance();
		return fxr;
	}


	/**
	 * Component startup code called when the system is starting up.
	 */
	@Override
	public void startup() {
		logger.log( LoggerTopics.Startup, this.getClass().getName() );
	}


	/**
	 * Component shutdown code called when the system is shutting down.
	 */
	@Override
	public void shutdown() {
		logger.log( LoggerTopics.Shutdown, this.getClass().getName() );
	}

}
