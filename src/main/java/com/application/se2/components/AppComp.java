package com.application.se2.components;

import com.application.se2.misc.Callback;


/**
 * App is the minimally required application component featuring
 * a view part (Tab on the GUI) and logic (AppRunner).
 * 
 * @author sgra64
 *
 */
public class AppComp extends ComponentBase {

	/**
	 * Interface of App's view/GUI part.
	 */
	public interface ViewIntf extends ComponentIntf.ViewIntf {

		public void exit( final String msg );

	}

	/**
	 * Interface of App's logic part.
	 */
	public interface LogicIntf extends RunnerIntf, ComponentIntf.LogicIntf {

		/**
		 * Invoked to start a component with three callouts for onStart, onExit and onErorr.
		 * @param onStart callback invoked when component starts
		 * @param onExit callback invoked when component exits
		 * @param onError callback invoked when an error is reported for component
		 */
		@Override	// from RunnerIntf
		void start(
			final Callback<Integer> onStart,
			final Callback<String> onExit,
			final Callback<String> onError
		);

		/**
		 * Invoked to exit the component, triggers onExit callout to be invoked.
		 * @param msg message passed on exit.
		 */
		@Override	// from RunnerIntf
		public void  exit( final String msg );

		/**
		 * Invoked to report an error during component execution, triggers onError callout.
		 * @param msg error message.
		 */
		@Override	// from RunnerIntf
		public void error( final String msg );

	}

}
