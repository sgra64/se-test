package com.application.se2.repository;

import java.util.HashMap;
import java.util.Optional;

import com.application.se2.AppConfigurator.LoggerTopics;
import com.application.se2.components.RunnerIntf;
import com.application.se2.misc.Callback;
import com.application.se2.misc.Logger;
import com.application.se2.model.Entity;


/**
 * RepositoryRunner is a singleton class that manages and provides repositories
 * for individual entity classes.
 * 
 * @author sgra64
 *
 */
public class RepositoryRunner implements RunnerIntf {
	private static Logger logger = Logger.getInstance( RepositoryBuilder.class );

	private final HashMap<String, RepositoryIntf<?>> repositoryMap;


	/**
	 * Protected constructor only invoked by RepositoryBuilder.
	 * 
	 * @param repositoryMap
	 */
	RepositoryRunner( HashMap<String, RepositoryIntf<?>> repositoryMap ) {
		this.repositoryMap = repositoryMap;
	}


	/**
	 * Returns a repository for a given entity class.
	 * 
	 * @param <T> entity class used as a generic.
	 * @param clazz specific entity class.
	 * @return repository of entity class.
	 */
	public <T extends Entity> Optional<RepositoryIntf<T>> getRepository( Class<T> clazz ) {
		@SuppressWarnings("unchecked")
		RepositoryIntf<T> repository = (RepositoryIntf<T>)repositoryMap.get( clazz.getName() );
		return repository != null? Optional.of( repository ) : Optional.empty();
	}


	/**
	 * Component startup code called when the system is starting up.
	 */
	@Override
	public void startup() {
		logger.log( LoggerTopics.Startup, this.getClass().getSimpleName() );
	}


	/**
	 * Component shutdown code called when the system is shutting down.
	 */
	@Override
	public void shutdown() {
		logger.log( LoggerTopics.Shutdown, this.getClass().getSimpleName() );
	}


	/**
	 * Invoked to start a component with three callouts for onStart, onExit and onErorr.
	 * 
	 * @param onStart callback invoked when component starts
	 * @param onExit callback invoked when component exits
	 * @param onError callback invoked when an error is reported for component
	 */
	@Override
	public void start( Callback<Integer> onStart, Callback<String> onExit, Callback<String> onError ) {
		// not used.
	}


	/**
	 * Invoked to exit the component, triggers onExit callout to be invoked.
	 * 
	 * @param msg message passed on exit.
	 */
	@Override
	public void exit( String msg ) {
		// not used.
	}


	/**
	 * Invoked to report an error during component execution, triggers onError callout.
	 * 
	 * @param msg error message.
	 */
	@Override
	public void error( String msg ) {
		// not used.
	}

}
