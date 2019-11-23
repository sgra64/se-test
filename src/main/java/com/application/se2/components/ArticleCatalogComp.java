package com.application.se2.components;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

import com.application.se2.model.Article;
import com.application.se2.repository.RepositoryIntf;


/**
 * Catalog is a simple application component to manage articles in a repository.
 * It features a view part (TableView on the GUI) and logic (ArticleCatalogManager.java).
 * 
 * @author sgra64
 *
 */
public class ArticleCatalogComp extends ComponentBase {

	/*
	 * ArticleCatalog uses an Article repository as data source.
	 */
	private Optional<RepositoryIntf<Article>> repository = Optional.empty();


	/**
	 * Interface of ArticleCatalog's view part is ComponentIntf.TableViewIntf
	 */

	/**
	 * Interface of ArticleCatalog's logic part.
	 */
	public interface LogicIntf extends ComponentIntf.LogicIntf, ComponentIntf.CRUDLogicIntf {
		/* no additions */
	}

	/**
	 * Method to inject repository instance.
	 * @param repository repository to inject.
	 */
	public void inject( final RepositoryIntf<Article> repository ) {
		this.repository = Optional.of( repository );
	}

	/**
	 * Invokes callOut on underlying repository returning the result set.
	 * @param callOut to be invoked on underlying repository.
	 * @return list of data items resulting from the invocation of the callOut.
	 */
	public Iterable<Article> invokeRepository( final Function<RepositoryIntf<Article>, Iterable<Article>> callOut ) {
		if( repository.isPresent() ) {
			return callOut.apply( repository.get() );
		}
		return Collections.emptyList();
	}

}
