package com.application.se2.misc;

import static com.application.se2.AppConfigurator.LoggerConfig;
import static com.application.se2.AppConfigurator.LoggerTopics;

import com.application.se2.model.Entity;


/**
 * Local implementation of the Logger interface.
 * 
 * @author sgra64
 *
 */
class LoggerImpl implements Logger {
	private static LoggerImpl instance = null;


	/**
	 * Private constructor to prevent instance creation outside getInstance().
	 * @param clazz class that identifies the logger instance.
	 */
	private LoggerImpl( final Class<?> clazz ) {
		
	}

	/**
	 * Create and return logger instance for a given class.
	 * @param clazz class that identifies the logger instance.
	 * @return logger instance for the class.
	 */
	public static LoggerImpl getInstance( final Class<?> clazz ) {
		if( instance == null ) {
			instance = new LoggerImpl( clazz );
		}
		return instance;
	}


	/**
	 * Method to log a message.
	 * 
	 * @param topic logs are categorized by (String) topics.
	 * @param msg log message
	 * @param args further log information
	 */
	public void log( final LoggerTopics topic, final String msg, final Object... args ) {
		String id = "<none>";
		String indicator = " - shutdown";

		if( LoggerConfig.contains( topic ) ) {

			switch( topic ) {
			case Always:
			case Info:
			case Warn:
				System.out.println( msg );
				break;

			case Error:
				System.err.println( "ERROR: " + msg );
				break;

			case EntityCRUD:
				/*
				String cls = "";
				if( args.length > 0 ) {
					Object arg = args[ 0 ];
					arg = arg != null && arg instanceof PrimaryObject? ((PrimaryObject)arg).getObject() : arg;
					id = arg instanceof Entity? ((Entity)arg).getId() : String.valueOf( arg.hashCode() );
					cls= arg.getClass().getSimpleName();
				}
				System.out.println( msg + " " + cls + "." + id );
				*/
				StringBuffer sb = new StringBuffer( msg );
				for( Object arg : args ) {
					sb.append( arg.toString() );
				}
				System.out.println( sb.toString() );
				break;

			case Startup:
				indicator = " + startup";
			case Shutdown:
				System.out.println( indicator + ": " + msg );
				break;

			case PropertiesAltered:
			case FieldAccessAltered:
				System.out.println( msg );
				break;

			case RepositoryLoaded:
				if( args.length > 0 ) {
					Object arg = args[ 0 ];
					arg = arg != null && arg instanceof Traceable? ((Traceable)arg).getRootObject() : arg;
					id = arg instanceof Entity? ((Entity)arg).getId() : String.valueOf( arg.hashCode() );
				}
				System.out.println( "Repository: --> " + id );
				break;

			case CSSLoaded:
				System.out.println( msg );
				break;

			}
		}
	}


	/**
	 * Print info message.
	 * @param message log message
	 */
	@Override
	public void info( String message ) {
		log( LoggerTopics.Info, message );
	}


	/**
	 * Print warn message.
	 * @param message log message
	 */
	@Override
	public void warn( String message ) {
		log( LoggerTopics.Warn, message );
	}


	/**
	 * Print error message.
	 * @param message log message
	 * @param exception optional exception object to log.
	 */
	@Override
	public void error( String message, Exception exception ) {
		log( LoggerTopics.Error, message );
	}

}
