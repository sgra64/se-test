package com.application.se2.components;

import java.util.List;

import com.application.se2.misc.EntityProperty;
import com.application.se2.misc.EntityPropertyUpdateSet;
import com.application.se2.model.Entity;


/**
 * Modularizing systems into components and parts requires interfaces to be
 * defined between components and their internal and external parts.
 * 
 * Public interface ComponentIntf defines a number of interfaces for components:
 *  - LogicIntf, ViewIntf, RepositoryIntf
 * 
 * All interfaces are top-level interfaces that will be sub-typed by specific
 * component classes (refer to components such as Calculator with specific interfaces:
 * \\
 *  - Calculator.ViewIntf extends ComponentIntf.ViewIntf { ... } and
 *  - Calculator.LogicIntf extends ComponentIntf.LogicIntf { ... }.
 * 
 * as examples.
 * 
 * @author sgra64
 *
 */
public interface ComponentIntf {

	/**
	 * Public super-interface of Logic components.
	 */
	public interface LogicIntf extends ComponentIntf {

		/**
		 * Invoked on startup.
		 */
		public void startup();

		/**
		 * Invoked on shutdown.
		 */
		public void shutdown();

	}


	/**
	 * Public super-interface of View components.
	 */
	public interface ViewIntf extends ComponentIntf {

	}


	/**
	 * Public sub-interface of TableView components (views shown as tables).
	 */
	public interface TableViewIntf extends ComponentIntf.ViewIntf {

		/**
		 * Refresh table view by loading data items from data source.
		 */
		public void refreshView();

	}


	/**
	 * Public sub-interface used to facilitate CRUD operations between
	 * View/GUI components that need data sources and the associated logic.
	 * (CRUD: stands for operations to Create, Read, Update and Delete data elements).
	 */
	public interface CRUDLogicIntf extends ComponentIntf.LogicIntf {

		/**
		 * Method to return all data items that match a pattern up to a limit
		 * of items.
		 * 
		 * @param match pattern to match data items at the source. Only matching
		 * data items are returned.
		 * @param limit upper limit of data items to be returned.
		 * @return list of data items.
		 */
		public Iterable<? extends Entity> findAll( String match, long limit );

		/**
		 * Method that returns a data item that has been newly created by the logic.
		 * @return new data item.
		 */
		public Entity create();

		/**
		 * Method that returns a new property as part of an Entity object.
		 * For example, a new Contact item may be created for a Customer data item.
		 * 
		 * @param property property for which new item will be created.
		 * @return object as new instance for property
		 */
		public Object createPart( EntityProperty property );

		/**
		 * Method to process updates to a data item in the logic.
		 * @param updates structure to represent updated values.
		 */
		public void update( EntityPropertyUpdateSet updates );

		/**
		 * Method to remove a set of data items specified by their id's from the source.
		 * @param selection
		 */
		public void delete( List<String> selection );

		/**
		 * Method called to pass exit to logic.
		 * @param msg exit message.
		 */
		public void exit( String msg );

	}

}
