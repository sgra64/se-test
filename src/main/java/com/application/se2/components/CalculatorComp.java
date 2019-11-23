package com.application.se2.components;


/**
 * Calculator is a simple tax calculator application component featuring
 * a view part (Tab on the GUI) and logic (CalculatorLogic).
 * 
 * @author sgra64
 *
 */
public class CalculatorComp extends ComponentBase {

	public static final String Calculator = "Calculator";
	public static final int DISPLAY_MAXDIGITS = 16;

	/*
	 * Tokens passed from Calculator view/GUI to Calculator logic.
	 */
	public enum Token {
		K_VAT,	K_CE,	K_C,	K_BACK,
		K_MPLUS,K_MR,	K_MC,	K_DIV,
		K_7,	K_8,	K_9,	K_MUL,
		K_4,	K_5,	K_6,	K_MIN,
		K_1,	K_2,	K_3,	K_PLUS,
		K_0,	K_1000,	K_DOT,	K_EQ,
	};

	/*
	 * Token mapping of the Calculator key pad.
	 */
	public final static String[] KeyLabels = new String[] {
		"MwSt",	"CE",	"C",	"<-",
		"M+",	"MR",	"MC",	"/",
		"7",	"8",	"9",	"*",
		"4",	"5",	"6",	"-",
		"1",	"2",	"3",	"+",
		"0",	"1000",	",",	"=",
	};


	/**
	 * Interface of Calculator's view/GUI part.
	 */
	public interface ViewIntf extends ComponentIntf.ViewIntf {

		/**
		 * Output text into main number display panel.
		 * @param text text to display
		 */
		public void writeDisplay( final String text );

		/**
		 * Output text into the side area shown to the right.
		 * @param text text to display
		 */
		public void writeSideArea( final String text );

	}

	/**
	 * Interface of Calculator's logic part.
	 */
	public interface LogicIntf extends ComponentIntf.LogicIntf {

		/**
		 * Pass a token for a pressed key from the key panel to the
		 * calculator logic.
		 * @param token represents a pressed key from the key panel
		 */
		public void nextToken( final Token token );

	}

}
