package com.application.se2.logic;

import static com.application.se2.AppConfigurator.LoggerTopics;

import com.application.se2.components.CalculatorComp;
import com.application.se2.components.ComponentBase;
import com.application.se2.components.CalculatorComp.Token;
import com.application.se2.misc.Logger;


/**
 * Class that implements the Calculator logic as defined in the:
 *  - CalculatorComp.LogicIntf
 * 
 * @author sgra64
 *
 */
public class CalculatorLogic implements CalculatorComp.LogicIntf {
	private static Logger logger = Logger.getInstance( CalculatorLogic.class );

	private final CalculatorComp component;					// reference to Calculator component
	private final StringBuffer dsb = new StringBuffer();	// buffer to collect user input
	private final double VAT_RATE = 19.0;					// tax rate


	/**
	 * Public constructor.
	 * 
	 * @param component reference to Calculator component.
	 */
	public CalculatorLogic( final CalculatorComp component ) {
		this.component = component;
	}


	/**
	 * Method called at startup.
	 * 
	 */
	@Override
	public void startup() {
		logger.log( LoggerTopics.Startup, component.getName() );
	}


	/**
	 * Method called at shutdown.
	 * 
	 */
	@Override
	public void shutdown() {
		logger.log( LoggerTopics.Shutdown, component.getName() );
	}


	/**
	 * Method called each time a user types a key (press key or mouse-click on keypad)
	 * with the token indicating the key.
	 * 
	 * @param token Token representing a key or keypad event received from the Calculator GUI.
	 */
	@Override
	public void nextToken( final Token token ) {

		System.out.println( token.name() );

		try {
			switch( token ) {
			case K_0:	appendBuffer( "0" ); break;
			case K_1:	appendBuffer( "1" ); break;
			case K_2:	appendBuffer( "2" ); break;
			case K_3:	appendBuffer( "3" ); break;
			case K_4:	appendBuffer( "4" ); break;
			case K_5:	appendBuffer( "5" ); break;
			case K_6:	appendBuffer( "6" ); break;
			case K_7:	appendBuffer( "7" ); break;
			case K_8:	appendBuffer( "8" ); break;
			case K_9:	appendBuffer( "9" );
				break;

			case K_1000:appendBuffer( "000" );
				break;

			case K_DIV:
				throw new ArithmeticException( "ERR: div by zero" );
			case K_MUL:	appendBuffer( "*" ); break;
			case K_PLUS:appendBuffer( "+" ); break;
			case K_MIN:	appendBuffer( "-" ); break;
			case K_EQ:	appendBuffer( "=" ); break;

			case K_VAT:
				writeSideArea(
					"Brutto:  1,000.00\n" +
					VAT_RATE + "% MwSt:  159.66\n" +
					"Netto:  840.34"
				);
				break;

			case K_DOT:	appendBuffer( "." );
				break;

			case K_BACK:
				dsb.setLength( Math.max( 0, dsb.length() - 1 ) );
				break;

			case K_C:
				writeSideArea( "" );
			case K_CE:
				dsb.delete( 0,  dsb.length() );
				break;

			default:
			}
			String display = dsb.length()==0? "0" : dsb.toString();
			writeDisplay( display );

		} catch( ArithmeticException e ) {
			writeDisplay( e.getMessage() );
		}
	}


	/*
	 * Private methods.
	 */

	private void appendBuffer( String d ) {
		if( dsb.length() <= CalculatorComp.DISPLAY_MAXDIGITS ) {
			dsb.append( d );
		}
	}

	private void writeDisplay( String text ) {
		ComponentBase.<CalculatorComp.ViewIntf>viewIntf( component, view -> {
			view.writeDisplay( text );
		});
	}

	private void writeSideArea( String text ) {
		ComponentBase.<CalculatorComp.ViewIntf>viewIntf( component, view -> {
			view.writeSideArea( text );
		});
	}

}
