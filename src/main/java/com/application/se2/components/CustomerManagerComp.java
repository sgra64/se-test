package com.application.se2.components;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

import com.application.se2.model.Customer;
import com.application.se2.repository.RepositoryIntf;


/**
 * CustomerManager is a simple application component to manage customers in a repository.
 * It features a view part (TableView on the GUI) and logic (CustomerManager.java).
 * 
 * @author sgra64
 *
 */
public class CustomerManagerComp extends ComponentBase {

	/*
	 * CustomerManager uses a Customer repository as data source.
	 */
	private Optional<RepositoryIntf<Customer>> repository = Optional.empty();


	/**
	 * Interface of UserManager's view part is ComponentIntf.TableViewIntf
	 */

	/**
	 * Interface of UserManager's logic part.
	 */
	public interface LogicIntf extends ComponentIntf.CRUDLogicIntf {
		/* no additions */
	}

	/**
	 * Method to inject repository instance.
	 * @param repository repository to inject.
	 */
	public void inject( final RepositoryIntf<Customer> repository ) {
		this.repository = Optional.of( repository );
	}

	/**
	 * Invokes callOut on underlying repository returning the result set.
	 * @param callOut to be invoked on underlying repository.
	 * @return list of data items resulting from the invocation of the callOut.
	 */
	public Iterable<Customer> invokeRepository( final Function<RepositoryIntf<Customer>, Iterable<Customer>> callOut ) {
		if( repository.isPresent() ) {
			return callOut.apply( repository.get() );
		}
		return Collections.emptyList();
	}

}
