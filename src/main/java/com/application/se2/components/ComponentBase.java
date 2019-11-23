package com.application.se2.components;

import java.util.Properties;

import com.application.se2.AppConfigurator;
import com.application.se2.misc.Callback;


/**
 * Base class of all component classes providing component configuration
 * information stored as key-value tuples in the configs Properties map.
 * 
 * @author sgra64
 *
 */
public class ComponentBase {

	private final Properties configs;	// key-value configuration tuples.

	/*
	 * Class to define top-level key name space used to store config properties.
	 * 
	 * Keys Logic and View are used to inject references to instances that implement
	 * respective component Logic- and View-interfaces as they become available during
	 * the build process.
	 */
	public static class Key {
		public final static String Name = KEY( ComponentBase.class, "name" );

		public final static String Logic = KEY( ComponentBase.class, "logic" );
		public final static String View = KEY( ComponentBase.class, "view" );

		public static String KEY( final Class<?> clazz, final String key ) {
			return clazz.getName().toLowerCase() + "." + key;
		}
	}


	/**
	 * Public constructor.
	 */
	public ComponentBase() {
		this.configs = new Properties();
	}


	/**
	 * Method to inject view-part of a component stored under Key.View.
	 * @param view view component injected.
	 */
	public void inject( final ComponentIntf.ViewIntf view ) {
		configs.put( Key.View, view );
	}

	/**
	 * Method to inject logic-part of a component stored under Key.Logic.
	 * @param logic logic component injected.
	 */
	public void inject( final ComponentIntf.LogicIntf logic ) {
		configs.put( Key.Logic, logic );
	}

	/**
	 * Generic method to access view-part of a component, if present in configuration.
	 * The nature of generic methods requires the method to be static.
	 * 
	 * Rather than returning a ViewIntf instance stored under the Key.View and potentially
	 * returning null or an empty Optional if none is present, the approach here is to
	 * invoke a Callback on the interface if it is present.
	 *  
	 * @param <T> concrete ViewIntf of the interface to be invoked.
	 * @param component component on which the concrete ViewIntf is invoked.
	 * @param callOut called on ViewIntf.
	 */
	public static <T> void viewIntf( final ComponentBase component, final Callback<T> callOut ) {
		ComponentBase.<T>callIntf( component, Key.View, callOut );
	}

	/**
	 * Generic method to access logic-part of a component, if present in configuration.
	 * The nature of generic methods requires the method to be static.
	 * 
	 * @param <T> concrete LogicIntf of the interface to be invoked.
	 * @param component component on which the concrete LogicIntf is invoked.
	 * @param callOut called onLogicIntf.
	 */
	public static <T> void logicIntf( final ComponentBase component, final Callback<T> callOut ) {
		ComponentBase.<T>callIntf( component, Key.Logic, callOut );
	}

	/*
	 * Private method used by the methods above.
	 */
	private static <T> void callIntf( final ComponentBase instance, final String key, final Callback<T> callOut ) {
		@SuppressWarnings("unchecked")
		T intf = (T)instance.get( key );
		if( intf != null ) {
			callOut.apply( intf );
		}
	}

	/**
	 * Configurations are stored as key-value pairs by all components.
	 * This method stores a configuration tuple as key-value pair.
	 * 
	 * @param key key under which value is stored.
	 * @param value value stored under a key.
	 * @return this to dot-chain invocations.
	 */
	public ComponentBase configure( final String key, final Object value ) {
		if( AppConfigurator.Key.TableView.equals( key ) && value != null && value instanceof Object[][] ) {
			Object[][] args = (Object[][])value;
			for( Object[] arg : args ) {	// flatten ViewConfig-Object[][]
				if( arg != null && arg.length > 1 ) {
					configure( (String)arg[0], arg[1] );
				}
			}
		} else {
			configs.put( key, value );
		}
		return this;
	}

	/**
	 * Configurations are stored as key-value pairs by all components.
	 * This method returns a stored a configuration value from a key.
	 * 
	 * @param key key for which a value is returned
	 * @return value value, if found or null otherwise.
	 */
	public Object get( final String key ) {
		return configs.get( key );
	}

	/**
	 * Components should have names stored as Strings under keys Key.Name or Key.Label.
	 * This method returns a value (name) found under those keys or null if none is present.
	 * 
	 * @return component name stored under Key.Name or null if not present.
	 */
	public String getName() {
		String name = configs.getProperty( Key.Name );
		name = name != null? name : configs.getProperty( AppConfigurator.Key.Label );
		name = name != null? name : this.getClass().getSimpleName() + "." + this.hashCode();	// String.valueOf( this );
		return name;
	}

}
