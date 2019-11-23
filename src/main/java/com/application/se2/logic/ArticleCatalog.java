package com.application.se2.logic;

import java.util.List;

import com.application.se2.AppConfigurator.LoggerTopics;
import com.application.se2.components.AppComp;
import com.application.se2.components.ArticleCatalogComp;
import com.application.se2.components.ComponentBase;
import com.application.se2.components.ComponentIntf;
import com.application.se2.misc.EntityProperty;
import com.application.se2.misc.EntityPropertyUpdateSet;
import com.application.se2.misc.Logger;
import com.application.se2.misc.StringProperty;
import com.application.se2.misc.Traceable;
import com.application.se2.model.Article;
import com.application.se2.model.Entity;
import com.application.se2.model.Note;


/**
 * Class that implements the ArticleCatalog logic as defined in the:
 *  - ArticleCatalogComp.LogicIntf
 *  with:
 *    - ComponentIntf.LogicIntf and
 *    - ComponentIntf.CRUDLogicIntf
 * 
 * @author sgra64
 *
 */
public class ArticleCatalog implements ArticleCatalogComp.LogicIntf {
	private static Logger logger = Logger.getInstance( ArticleCatalog.class );

	private final ArticleCatalogComp component;
	private final AppComp.LogicIntf app;


	/**
	 * Public constructor.
	 * 
	 * @param component reference to ArticleCatalogComp component.
	 * @param app Application logic needed to delegate Exit-button pressed event.
	 */
	public ArticleCatalog( ArticleCatalogComp component, AppComp.LogicIntf app ) {
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
	 * Invoked on ArticleCatalog CRUDLogicIntf to fetch all matching Article objects
	 * from the repository for display on the GUI.
	 * 
	 * @param match expression to match Article objects to be returned.
	 * @param limit maximum number of returned Article objects.
	 * @return Collection of Article objects.
	 */
	@Override
	public Iterable<Article> findAll( String match, long limit ) {
		// TODO: implement match, limit
		return component.invokeRepository( repository -> {
			return repository.findAll();
		});
	}


	/**
	 * Invoked on ArticleCatalog CRUDLogicIntf to create a new Article instance
	 * that has an Id, but is not saved to the repository, yet.
	 * 
	 * @return newly created Article instance (has Id, but is not saved to the repository, yet).
	 */
	@Override
	public Entity create() {
		Article c = new Article( "", "0,00 EUR" );
		logger.log( LoggerTopics.EntityCRUD, "Create new ", Article.class.getSimpleName(), ": ", c.getId() );
		return c;
	}


	/**
	 * Invoked on ArticleCatalog CRUDLogicIntf to create a new instance of an EntityProperty
	 * of a Article instance.
	 * 
	 * @param property EntityProperty for which a new instance will be created.
	 * @return newly created instance that has not yet been assigned to the Article property.
	 */
	@Override
	public Object createPart( EntityProperty property ) {
		if( property.getName().toLowerCase().equals( "contacts" ) ) {
			logger.log( LoggerTopics.EntityCRUD, "Create Contact for ", Article.class.getSimpleName() );
			return new StringProperty( "" );
		}
		if( property.getName().toLowerCase().equals( "notes" ) ) {
			logger.log( LoggerTopics.EntityCRUD, "Create Note for ", Article.class.getSimpleName() );
			return new Note( "" );
		}
		return null;
	}


	/**
	 * Invoked on ArticleCatalog CRUDLogicIntf to process updates of a Article object.
	 * 
	 * @param updates EntityPropertyUpdateSet data structure to represent updated values.
	 */
	@Override
	public void update( EntityPropertyUpdateSet updates ) {
		Traceable primaryObject = updates.getRootObject();
		//Property parentProperty = primaryObject.getParentProperty();
		Article customer = (Article)primaryObject.traverse( Article.class );

		logger.log( LoggerTopics.EntityCRUD, "Update ", Article.class.getSimpleName(), ": ", customer.getId() );

		updates.iterateUpdatedProperties( ( p, s ) -> {
			//Property property = parentProperty==null? p : parentProperty;
			//String propertyName = property.getName();
			//String beforeValue = s[ 0 ];
			String newValue = s[ 1 ];

			p.setValue( customer, newValue );
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
	 * Invoked on ArticleCatalog CRUDLogicIntf to delete a selection of Article entities.
	 * 
	 * @param selection of Article entities to be deleted.
	 */
	@Override
	public void delete( List<String> selection ) {

		component.invokeRepository( repository -> {
			if( selection.size() > 0 ) {
				logger.log( LoggerTopics.EntityCRUD, "Delete ", Article.class.getSimpleName(), "(s): ",
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
	 * Invoked on ArticleCatalog CRUDLogicIntf that is delegated to App-instance for
	 * exiting the application.
	 * 
	 * @param msg Exit message.
	 */
	@Override
	public void exit( String msg ) {
		app.exit( msg );
	}

}
