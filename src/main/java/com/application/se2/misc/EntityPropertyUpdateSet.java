package com.application.se2.misc;

import static com.application.se2.AppConfigurator.LoggerTopics.PropertiesAltered;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;


/**
 * Class to represent a set of EntityProperties that have been updated, e.g. by a GUI.
 * Stored updates can be validated and compared to previous values before being applied
 * to the underlying Entity.
 * 
 * @author sgra64
 *
 */
public class EntityPropertyUpdateSet {
	private static final Logger logger = Logger.getInstance( EntityPropertyAccessor.class );

	private final Traceable primaryObject;

	private final List<EntityProperty> property = new ArrayList<EntityProperty>();

	private final List<String> valueBeforeUpdate = new ArrayList<String>();

	private final List<String> valueAfterUpdate = new ArrayList<String>();

	private final List<Integer> alteredIndex = new ArrayList<Integer>();


	/**
	 * Public constructor.
	 * 
	 * @param primaryObject underlying Entity to which updates belong.
	 */
	public EntityPropertyUpdateSet( final Traceable primaryObject ) {
		this.primaryObject = primaryObject;
	}


	/**
	 * Record original property value.
	 * 
	 * @param prop reference to EntityProperty.
	 * @param value original value.
	 */
	public void before( EntityProperty prop, String value ) {
		this.property.add( prop );
		this.valueBeforeUpdate.add( value != null? value.trim() : "" );
	}


	/**
	 * Record updated value for property indexed by i.
	 * @param i index of EntityProperty
	 * @param value update value
	 */
	public void after( int i, String value ) {
		if( i >= 0 && i < property.size() ) {
			String val = value != null? value.trim() : "";
			String valBefore = valueBeforeUpdate.get( i );
			if( ! valBefore.equals( val ) ) {
				alteredIndex.add( i );
				// fill gaps with unaltered values to keep list indices in sync.
				while( valueAfterUpdate.size() < i ) {
					valueAfterUpdate.add( "" );
				}
				valueAfterUpdate.add( value );
				logger.log( PropertiesAltered, "altered: (\"" + valBefore +  "\" --> \"" + val + "\")." );
			}
		}
	}


	/**
	 * Returns true is at least one EntityProperty of the set has been altered.
	 * 
	 * @return true is at least one EntityProperty of the set has been altered.
	 */
	public boolean isAltered() {
		return alteredIndex.size() > 0;
	}


	/**
	 * Return root of object tree, hence the top-most Entity to which the update applies.
	 * @return
	 */
	public Traceable getRootObject() {
		return primaryObject;
	}


	/**
	 * Iterator method over EntityProperty's with updates.
	 * @param callback invoked for each EntityProperty with updates.
	 */
	public void iterateUpdatedProperties( BiConsumer<EntityProperty,String[]> callback ) {
		for( Integer i : alteredIndex ) {
			EntityProperty p = property.get( i );
			String[] val = new String[ 2 ];
			val[ 0 ] = valueBeforeUpdate.get( i );
			val[ 1 ] = valueAfterUpdate.get( i );
			callback.accept( p, val );
			val[0] = val[1] = null;
		}
	}


	/**
	 * Clear EntityPropertyUpdateSet and release all references from lists.
	 */
	public void clear() {
		property.clear();
		valueBeforeUpdate.clear();
		valueAfterUpdate.clear();
		alteredIndex.clear();
	}

}
