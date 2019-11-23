package com.application.se2.logic;

import static com.application.se2.AppConfigurator.LoggerTopics;

import java.util.List;

import com.application.se2.components.AppComp;
import com.application.se2.components.ComponentBase;
import com.application.se2.components.ComponentIntf;
import com.application.se2.components.CustomerManagerComp;
import com.application.se2.misc.EntityProperty;
import com.application.se2.misc.EntityPropertyUpdateSet;
import com.application.se2.misc.Logger;
import com.application.se2.misc.StringProperty;
import com.application.se2.misc.Traceable;
import com.application.se2.model.Customer;
import com.application.se2.model.Entity;
import com.application.se2.model.Note;


/**
 * Class that implements the CustomerManager logic as defined in the:
 *  - CustomerManagerComp.LogicIntf
 *  with:
 *    - ComponentIntf.LogicIntf and
 *    - ComponentIntf.CRUDLogicIntf
 * 
 * @author sgra64
 *
 */
public class CustomerManager implements CustomerManagerComp.LogicIntf {
	private static Logger logger = Logger.getInstance( CustomerManager.class );

	private final CustomerManagerComp component;
	private final AppComp.LogicIntf app;


	/**
	 * Public constructor.
	 * 
	 * @param component reference to CustomerManagerComp component.
	 * @param app Application logic needed to delegate Exit-button pressed event.
	 */
	public CustomerManager( CustomerManagerComp component, AppComp.LogicIntf app ) {
		this.component = component;
		this.app = app;
	}

	
	/**
	 * Method called on startup.
	 * 
	 */
	@Override
	public void startup() {
		logger.log( LoggerTopics.Startup, component.getName() );
	}


	/**
	 * Method called on shutdown.
	 * 
	 */
	@Override
	public void shutdown() {
		logger.log( LoggerTopics.Shutdown, component.getName() );
	}


	/**
	 * Invoked on CustomerManager CRUDLogicIntf to fetch all matching Customer objects
	 * from the repository for display on the GUI.
	 * 
	 * @param match expression to match Customer objects to be returned.
	 * @param limit maximum number of returned Customer objects.
	 * @return Collection of Customer objects.
	 */
	@Override
	public Iterable<Customer> findAll( String match, long limit ) {
		// TODO: implement match, limit
		return component.invokeRepository( repository -> {
			return repository.findAll();
		});
	}


	/**
	 * Invoked on CustomerManager CRUDLogicIntf to create a new Customer instance
	 * that has an Id, but is not saved to the repository, yet.
	 * 
	 * @return newly created Customer instance (has Id, but is not saved to the repository, yet).
	 */
	@Override
	public Entity create() {
		Customer c = new Customer( "" );
		logger.log( LoggerTopics.EntityCRUD, "Create new ", Customer.class.getSimpleName(), ": ", c.getId() );
		return c;
	}


	/**
	 * Invoked on CustomerManager CRUDLogicIntf to create a new instance of an EntityProperty
	 * of a Customer instance.
	 * 
	 * @param property EntityProperty for which a new instance will be created.
	 * @return newly created instance that has not yet been assigned to the Customer property.
	 */
	@Override
	public Object createPart( EntityProperty property ) {

		if( property.getName().toLowerCase().equals( "contacts" ) ) {
			logger.log( LoggerTopics.EntityCRUD, "Create Contact for ", Customer.class.getSimpleName() );
			return new StringProperty( "" );
		}

		if( property.getName().toLowerCase().equals( "notes" ) ) {
			logger.log( LoggerTopics.EntityCRUD, "Create Note for ", Customer.class.getSimpleName() );
			return new Note( "" );
		}

		return null;
	}


	/**
	 * Invoked on CustomerManager CRUDLogicIntf to process updates of a Customer object.
	 * 
	 * @param updates EntityPropertyUpdateSet data structure to represent updated values.
	 */
	@Override
	public void update( EntityPropertyUpdateSet updates ) {
		Traceable primaryObject = updates.getRootObject();
		EntityProperty parentProperty = primaryObject.getParentProperty();
		Customer customer = (Customer)primaryObject.traverse( Customer.class );

		logger.log( LoggerTopics.EntityCRUD, "Update ", Customer.class.getSimpleName(), ": ", customer.getId() );

		updates.iterateUpdatedProperties( ( p, s ) -> {
			EntityProperty property = parentProperty==null? p : parentProperty;
			String propertyName = property.getName();
			String beforeValue = s[ 0 ];
			String newValue = s[ 1 ];

			switch( propertyName ) {

			case "contacts":
				List<String> contacts = customer.getContacts();
				// allow comma-separated contacts, e.g. "max@o2.de, 030-5697495" -> [0] max@o2.de, [1] 030-5697495
				String[] split = newValue.split( "[;,]" );
				int len = split.length;
				int i = 0;
				int j = contacts.indexOf( beforeValue );
				if( j >= 0 ) {
					for( ; i < len; i++ ) {
						if( split[i].length() > 0 ) {
							contacts.set( j , split[i] );
							break;
						}
					}
				}
				for( ; i < len; i++ ) {
					if( split[i].length() > 0 ) {
						customer.addContact( split[i] );
					}
				}
				break;

			case "notes":
				List<Note> notes = customer.getNotes();
				boolean found = false;
				for( Note note : notes ) {
					if( found = beforeValue.equals( note.getText() ) ) {
						note.setText( newValue );
						break;
					}
				}
				if( ! found ) {
					customer.addNote( newValue );
				}
				break;

			default:
				// remaining Customer fields: "id", "name", "address", "created", "status"
				p.setValue( customer, newValue );
				break;
			}
		});

		component.invokeRepository( repository -> {
			repository.save( customer );
			ComponentBase.<ComponentIntf.TableViewIntf>viewIntf( component, view -> {
				view.refreshView();
			});
			return null;
		});
	}


	/**
	 * Invoked on CustomerManager CRUDLogicIntf to delete a selection of Customer entities.
	 * 
	 * @param selection of Customer entities to be deleted.
	 */
	@Override
	public void delete( List<String> selection ) {

		component.invokeRepository( repository -> {
			if( selection.size() > 0 ) {
				logger.log( LoggerTopics.EntityCRUD, "Delete ", Customer.class.getSimpleName(), "(s): ",
						String.join( ", ", selection )
					);
					// selection.stream()
					//		.map( Object::toString )
					//		.collect( Collectors.joining( ", ") )
					// );
			}
			repository.deleteAllById( selection );
			ComponentBase.<ComponentIntf.TableViewIntf>viewIntf( component, view -> {
				view.refreshView();
			});
			return null;
		});
	}


	/**
	 * Invoked on CustomerManager CRUDLogicIntf that is delegated to App-instance for
	 * exiting the application.
	 * 
	 * @param msg Exit message.
	 */
	@Override
	public void exit( String msg ) {
		app.exit( msg );
	}

}
