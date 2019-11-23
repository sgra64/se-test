package com.application.se2.misc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * Helper class to represent the properties of an Entity class wrapped as EntityProperties.
 * 
 * @author sgra64
 *
 */
public class EntityPropertyAccessor {

	private final Class<?> clazz;

	private final List<EntityProperty> entityPropertyList = new ArrayList<EntityProperty>();


	/**
	 * Public constructor.
	 * @param clazz class of Entity
	 */
	public EntityPropertyAccessor( final Class<?> clazz ) {
		this.clazz = clazz;
	}


	/**
	 * Returns list of wrapped as EntityProperties.
	 * 
	 * @return list list of wrapped as EntityProperties.
	 */
	public List<EntityProperty> getList() {
		return entityPropertyList;
	}


	/**
	 * Returns number of wrapped EntityProperties.
	 * 
	 * @return number of wrapped EntityProperties.
	 */
	public int size() {
		return entityPropertyList.size();
	}


	/**
	 * Method attempts to locate a Field in an Entity class by name and add to the
	 * set of wrapped EntityProperties.
	 * 
	 * @param fieldName name of field.
	 * @return EntityProperty, if Field with matching name could be found, or null otherwise.
	 */
	public EntityProperty addProperty( final String fieldName ) {
		final EntityProperty p = findProperty( fieldName, foundProp -> {}, newProp -> {
			entityPropertyList.add( newProp );
		});
		return p;
	}


	/**
	 * Add K/V-pair as configuration information to a EntityProperty with matching name.
	 * @param fieldName name of field.
	 * @param key key of K/V-pair.
	 * @param value value of K/V-pair.
	 * @return this
	 */
	public EntityPropertyAccessor addConfig( final String fieldName, final String key, final String value ) {
		findProperty( fieldName, foundProp -> {
			foundProp.putConfig( key, value );
		});
		return this;
	}


	/**
	 * Return i-th EntityProperty of list.
	 * 
	 * @param i index.
	 * @return i-th EntityProperty of list.
	 */
	public EntityProperty getProperty( final int i ) {
		EntityProperty prop = null;
		if( indexInRange( entityPropertyList, i ) ) {
			prop = entityPropertyList.get( i );
		}
		return prop;
	}


	/**
	 * Return value of i-th EntityProperty in underlying object.
	 * 
	 * @param object underlying object.
	 * @param i index in entityPropertyList.
	 * @return value of i-th property in underlying object.
	 */
	public Object getPropertyValue( final Object object, final int i ) {
		Object val = null;
		if( indexInRange( entityPropertyList, i ) ) {
			EntityProperty p = entityPropertyList.get( i );
			val = p.getValue( object );
		}
		return val;
	}


	/*
	 * Private methods.
	 */

	private EntityProperty findProperty( final String fieldnamePattern, final Callback<EntityProperty>... propArg ) {

		for( EntityProperty p : entityPropertyList ) {
			if( p.getName().matches( fieldnamePattern ) ) {
				if( propArg.length > 0 ) {
					propArg[ 0 ].apply( p );
				}
				return p;
			}
		}
		//Field[] fields = this.clazz.getFields();	// including inherited fields
		Field[] fields = this.clazz.getDeclaredFields();	// excluding inherited fields

		for( int i=0; i < fields.length; i++ ) {
			Field field = fields[ i ];
			// base.ViewableProperty[] anotations = field.getAnnotationsByType( base.ViewableProperty.class );
			// if( anotations != null && anotations.length > 0 ) {
				if( field.getName().matches( fieldnamePattern ) ) {
					//TODO: Lookup rather than new Property.
					// if multiple PropertyLists exist, Properties should only exist once per field.
					EntityProperty p = new EntityProperty( field );

					for( int j = 0; j < Math.min( propArg.length, 2 ); j++ ) {
						propArg[ j ].apply( p );	// call for: foundProp -> {}, newProp -> {}
					}
					return p;
				}
			//}
		}
		return null;
	}

	private boolean indexInRange( List<?>list, int i ) {
		return i >= 0 && i < list.size();
	}

}
