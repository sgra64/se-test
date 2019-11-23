package com.application.se2.misc;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import com.application.se2.model.Customer;

import java.util.Properties;
import java.util.Set;

public class Snippets {

	//public static final SimpleDateFormat DF_yyyy_MM_dd
	//	= new SimpleDateFormat( "yyyy-MM-dd", Locale.GERMANY );		// "2018-04-02"

	@Retention( RetentionPolicy.RUNTIME )
	@Target( { ElementType.FIELD } )
	public @interface ViewableProperty {
		//String id() default null;
		//String label() default DEFAULT_LABEL;
		//int field();//default -1;
	}

	public static void write( String fileName, String text ) {
		try {
			FileWriter fwriter = new FileWriter( fileName, false );
			BufferedWriter bwriter = new BufferedWriter( fwriter );
			bwriter.write( text );

			bwriter.close();
			fwriter.close();
			
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}

	public void snip1() {
		/*
		 * https://stackoverflow.com/questions/1942644/get-generic-type-of-java-util-list
		 */
		//for( Method method : Customer.class.getMethods() ) {
		for( Field field : Customer.class.getDeclaredFields() ) {
			Class<?> fieldClass = field.getType();
			System.out.println( "fieldClass: " + fieldClass.getTypeName() );
			if( Collection.class.isAssignableFrom( fieldClass ) ) {
				Type fieldType = field.getGenericType();
				System.out.println( "Collection.class.isAssignableFrom( fieldClass ): " + fieldType.getTypeName() );
				if( fieldType instanceof ParameterizedType ) {
					ParameterizedType paramType = (ParameterizedType)fieldType;
					Type[] argTypes = paramType.getActualTypeArguments();
					if( argTypes.length > 0 ) {
						System.out.println( "Generic type is " + argTypes[0] );
					}
				}
			}
		}
	}

	public void snip2() {
		Properties configs = new Properties();
		Set<Entry<Object, Object>> entries = configs.entrySet();
		for( Entry<Object, Object> entry : entries ) {
			configs.put( entry.getKey(), entry.getValue() );	    	
		}
	}

	public boolean indexInRange( List<?>list, int i ) {
		return i >= 0 && i < list.size();
	}

	public boolean containsString( final List<String> list, final String str ) {
		return list.stream()
			.filter( s -> s.equals( str ) )
				.findFirst().isPresent();
	}

	public int parseInt( Object object ) {
		int n = -1;
		if( object != null ) {
			if( object instanceof String ) {
				try {
					n = Integer.parseInt( (String)object );

				} catch( NumberFormatException e ) {}
			} else {
				if( object instanceof Integer ) {
					n = ((Integer)object).intValue();
				}
			}
		}
		return n;
	}

	void snip() {
		String[] beans = new String[] { };
        Arrays.stream( beans ).sorted().forEach( System.out::println );
	}

	static Locale locale = new Locale( "de", "GERMANY" );
	static DateFormatSymbols dateFormatSymbols = new DateFormatSymbols( locale );
	static {
		// http://tutorials.jenkov.com/java-internationalization/simpledateformat.html
		// https://www.baeldung.com/java-daylight-savings

		//TimeZone tz = TimeZone.getTimeZone( "Europe/Berlin" );
		//TimeZone.setDefault( tz );
		// static initialization code to initialize time with proper time zone.

		//df.setTimeZone( TimeZone.getTimeZone( "GMT+01" ) );
		//df_simple.setTimeZone( df.getTimeZone() );

		dateFormatSymbols.setShortMonths( new String[] {	// pattern: "MMM", long months: "MMMMM"
			"Jan", "Feb", "Mar", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dez"
		});
		dateFormatSymbols.setShortWeekdays( new String[] {	// pattern: "EEE", long months: "EEEEE"
			"Sa", "So", "Mo", "Di", "Mi", "Do", "Fr"
		});
	}

	public static final SimpleDateFormat DF_yyyy_MM_dd
		= new SimpleDateFormat( "yyyy-MM-dd", Locale.GERMANY );		// "2018-04-02"

	public static final SimpleDateFormat DF_dd_MM_yyyy_HH_mm_ss
		= new SimpleDateFormat( "dd.MM.yyyy, HH:mm:ss" );			// "16.06.2019 21:25:36"

	public static final SimpleDateFormat DF_yyyy_MM_dd_HH_mm_ss_SSS
		= new SimpleDateFormat( "yyyy-MM-dd, HH:mm:ss.SSS" );		// "2018-04-02 10:16:24:868"


	ListChangeListener listener2 = new ListChangeListener<Object>() {
		@Override
		public void onChanged(Change<? extends Object> c) {
			System.err.println( "** +@-PP-@+ **" );
		}
	};

	ChangeListener listener = new ChangeListener<Object>() {
		@Override
		public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
			// TODO Auto-generated method stub
			System.err.println( "** +@@+ **" );
		}
	};

}
