package com.application.se2.misc;

import static com.application.se2.AppConfigurator.LoggerTopics.FieldAccessAltered;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Stream;

import com.application.se2.AppConfigurator.Table;


/**
 * Class to wrap properties of Entity classes. It is used to extract values from
 * Entity properties and setting them using Reflection and taking care of basic
 * type conversion. It furthermore wraps configuration K/V-pairs that are applicable
 * to the EntityProperty.
 * 
 * @author sgra64
 *
 */
public class EntityProperty {
	private static Logger logger = Logger.getInstance( EntityProperty.class );

	public static final SimpleDateFormat DF_dd_MM_yyyy_HH_mm_ss
		= new SimpleDateFormat( "dd.MM.yyyy, HH:mm:ss" );			// "16.06.2019 21:25:36"

	public static final SimpleDateFormat DF_yyyy_MM_dd_HH_mm_ss_SSS
		= new SimpleDateFormat( "yyyy-MM-dd, HH:mm:ss.SSS" );		// "2018-04-02 10:16:24:868"

	/*
	 * Java Field associated with the Entity property.
	 */
	private final Field field;

	/*
	 * Configuration K/V-pairs associated with the EntityProperty.
	 */
	private final HashMap<String,Object>configs;


	/**
	 * Public constructor.
	 * 
	 * @param field field to be wrapped by EntityProperty.
	 */
	public EntityProperty( final Field field ) {
		this.field = field;
		this.configs = new HashMap<String,Object>();
	}


	/**
	 * Public copy constructor.
	 * 
	 * @param copy original EntityProperty of which a copy is made.
	 */
	public EntityProperty( final EntityProperty copy ) {
		this.field = copy.field;
		this.configs = copy.configs;
	}


	/**
	 * Only a set of Field base types can be altered (set) by EntityProperty.
	 * 
	 * @return true, if type of underlying Field is an alterable base type.
	 */
	public boolean isAlterableBaseType() {
		Class<?> ft = field.getType();
		boolean isAlterableBaseType = false
				|| ft == String.class
				|| ft.isPrimitive()		// byte, char, short, int, long, float, double, boolean
				|| Number.class.isAssignableFrom( ft )	// Byte, Double, Float, Integer, Long, Short, AtomicInteger, AtomicLong, BigDecimal, BigInteger
				|| Enum.class.isAssignableFrom( ft )
				|| Date.class.isAssignableFrom( ft )
			;
		return isAlterableBaseType;
	}


	/**
	 * Returns true if underlying Field is of a Collection type.
	 * 
	 * @return true, if underlying Field is of a Collection type.
	 */
	public boolean isCollectionType() {
		Class<?> fieldType = field.getType();
		boolean isCollectionType = Collection.class.isAssignableFrom( fieldType );	// alt. List.class
		return isCollectionType;
	}


	/**
	 * Returns name of underlying Field.
	 * 
	 * @return name of underlying Field.
	 */
	public String getName() {
		return field.getName();
	}


	/**
	 * Returns value of the underlying Field in the object passed as argument.
	 * 
	 * @param obj of which the value of the underlying Field will be returned.
	 * @return value of the underlying object Field.
	 */
	public Object getValue( final Object obj ) {
		Object val = null;
		try {
			val = field.get( obj );

			if( val != null && val instanceof Date ) {
				String df = (String)getConfig( Table.Column.DATEFMT );
				SimpleDateFormat dateFormat = df == null? EntityProperty.DF_dd_MM_yyyy_HH_mm_ss : new SimpleDateFormat( df );
				val = dateFormat.format( val );
			}

		} catch( IllegalAccessException ex1 ) {
			/*
			 * https://www.concretepage.com/java/how-to-access-all-private-fields-methods-and-constructors-using-java-reflection-with-example
			 */
			if( Modifier.isPrivate( field.getModifiers() ) ) {
				field.setAccessible( true );
				logger.log( FieldAccessAltered, "Field '" + field.getName() + "' set accessible." );
				//System.out.println( "Field '" + p.field.getName() + "' set accessible." );
				try {
					return getValue( obj );

				} catch( Exception ex2 ) {
					ex2.printStackTrace();
				}
			}

		} catch( IllegalArgumentException ex2 ) {

		}
		return val;
	}


	/**
	 * Sets the value of the underlying Field in the object passed as argument.
	 * 
	 * @param obj of which the value of the underlying Field will be set.
	 * @return value set to the underlying object Field.
	 */
	public void setValue( final Object obj, Object value ) {
		try {
			Class<?>ft = field.getType();
			if( value != null && value instanceof String ) {
				String strVal = (String)value;

				// byte, char, short, int, long, float, double, boolean
				if( ft.isPrimitive() ) {
					if( ft == int.class ) { value = Integer.parseInt( strVal ); } else {
					if( ft == long.class ) { value = Long.parseLong( strVal ); } else {
					if( ft == boolean.class ) { value = Boolean.parseBoolean( strVal ); } else {
					if( ft == char.class ) { value = strVal.charAt( 0 ); } else {
					if( ft == float.class ) { value = Float.parseFloat( strVal ); } else {
					if( ft == double.class ) { value = Double.parseDouble( strVal ); } else {
					if( ft == short.class ) { value = Short.parseShort( strVal ); } else {
					if( ft == byte.class ) { value = Byte.parseByte( strVal ); } else {
					}}}}}}}}

				} else {
					// Byte, Double, Float, Integer, Long, Short, AtomicInteger, AtomicLong, BigDecimal, BigInteger
					if( Number.class.isAssignableFrom( ft ) ) {
						if( ft.equals( Integer.class ) ) { value = Integer.parseInt( strVal ); } else {
						if( ft.equals( Long.class ) ) { value = Long.parseLong( strVal ); } else {
						if( ft.equals( Double.class ) ) { value = Double.parseDouble( strVal ); } else {
						if( ft.equals( Float.class ) ) { value = Float.parseFloat( strVal ); } else {
						if( ft.equals( Short.class ) ) { value = Short.parseShort( strVal ); } else {	
						if( ft.equals( Byte.class ) ) { value = Byte.parseByte( strVal ); } else {
						}}}}}}

					} else {
						if( Enum.class.isAssignableFrom( ft ) ) {
							value = Stream.of( field.getType().getEnumConstants() ).filter(
									// find first enum constant matching update value
									st -> st.toString().toUpperCase().contains( strVal.toUpperCase() )
								).findFirst().get();

						} else {
							if( Date.class.isAssignableFrom( ft ) ) {
								/*
								 * https://stackoverflow.com/questions/4216745/java-string-to-date-conversion
								 */
								String df = (String)getConfig( Table.Column.DATEFMT );
								SimpleDateFormat dateFormat = df == null? EntityProperty.DF_dd_MM_yyyy_HH_mm_ss : new SimpleDateFormat( df );
								value = (Date)dateFormat.parse( (String)value );

							} else {
								if( ft.equals( Boolean.class ) ) { value = Boolean.parseBoolean( strVal ); } else {
								if( ft == Character.class ) { value = strVal.charAt( 0 ); } else {
								}}
							}
						}
					}
				}

				field.set( obj, value );
			}

		} catch( IllegalArgumentException e ) {
			// TODO: handle access exceptions
			e.printStackTrace();

		} catch( IllegalAccessException e ) {
			// TODO: handle access exceptions
			e.printStackTrace();

		} catch( ParseException e ) {
			// thrown by String -> Date conversion, ignore, no value changed
		}
	}


	/**
	 * Returns value of a configuration associated with a key or null.
	 * @param key of the configuration.
	 * @return value of the configuration identified by the key.
	 */
	public Object getConfig( final String key ) {
		return configs.get( key );
	}


	/**
	 * Puts a K/V-pair into configuration.
	 * @param key of the configuration.
	 * @param config value of the configuration.
	 */
	public void putConfig( final String key, final Object value ) {
		configs.put( key, value );
	}

}
