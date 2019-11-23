package com.application.se2;

import static com.application.se2.AppConfigurator.LoggerTopics;

import java.util.Optional;

import com.application.se2.components.AppComp;
import com.application.se2.components.RunnerIntf;
import com.application.se2.misc.Callback;
import com.application.se2.misc.Logger;


/**
 * AppRunner is a local singleton class. Its purpose is to control the execution
 * of previously built (created, configured) application components.
 * 
 * Only one instance of AppRunner is created by the AppBuilder, which makes this
 * class an implicit singleton class (implicit because it does not implement the
 * singleton pattern such as, for example, AppBuilder).
 * 
 * @author sgra64
 *
 */
class AppRunner implements RunnerIntf, AppComp.LogicIntf {
	private static final Logger logger = Logger.getInstance( AppRunner.class );

	private final AppComp component;

	private Optional<Callback<Integer>> onStartCallback = Optional.empty();
	private Optional<Callback<String>> onExitCallback = Optional.empty();
	private Optional<Callback<String>> onErrorCallback = Optional.empty();


	/**
	 * Local Constructor invoked by AppBuilder.
	 * @param appBuilder creating app builder instance
	 * @param appConfig component with configurations to run the application
	 */
	AppRunner( final AppBuilder appBuilder, final AppComp appConfig ) {
		this.component = appConfig;
	}


	/**
	 * Component startup code called when the system is starting up.
	 */
	@Override
	public void startup() {
		logger.log( LoggerTopics.Startup, component.getName() );
	}

	/**
	 * Component shutdown code called when the system is shutting down.
	 */
	@Override
	public void shutdown() {
		logger.log( LoggerTopics.Shutdown, component.getName() );
	}

	/**
	 * RunnerIntf startup method with onStart, onExit, onError hooks called when the AppRunner
	 * instance is started.
	 * 
	 * @param onStart callback called at the end of this start method
	 * @param onExit callback called when exit(msg) method is called on AppRunner instance
	 * @param onError callback called when error(msg) method is called on AppRunner instance
	 * @return true
	 */
	@Override
	public void start( final Callback<Integer> onStart, final Callback<String> onExit, final Callback<String> onError ) {
		this.onStartCallback = Optional.of( onStart );
		this.onExitCallback = Optional.of( onExit );
		this.onErrorCallback = Optional.of( onError );

		this.onStartCallback.ifPresent( onStart2 -> {
			onStart2.apply( 0 );
		});

	}

	/**
	 * RunnerIntf method called to end AppRunner instance and exit through calling onExit callback
	 * @param msg exit message
	 */
	@Override
	public void exit( final String msg ) {
		this.onExitCallback.ifPresent( onExit -> {
			onExit.apply( msg );
		});
	}

	/**
	 * RunnerIntf method called to report an error to AppRunner instance
	 * @param msg exit message
	 */
	@Override
	public void error( final String msg ) {
		this.onErrorCallback.ifPresent( onError -> {
			onError.apply( msg );
		});
	}

}
