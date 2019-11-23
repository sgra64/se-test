package com.application.se2.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.application.se2.model.Entity;


/**
 * Local implementation class that implements the RepositoryIntf<E> interface based
 * on a simple transient (in-memory) List<E> implementation.
 * 
 * @author sgra64
 *
 * @param <E> gneric entity type defined as sub-type of EntityIntf.
 */
class SimpleRepositoryImpl<E extends Entity> implements RepositoryIntf<E> {

	/*
	 * Internal list that represents the repository.
	 */
	private final List<E> list;


	/**
	 * Public constructor.
	 * 
	 * @param list list<E> that is associated with the repository.
	 */
	public SimpleRepositoryImpl( List<E> list ) {
		this.list = list;
	}


	/**
	 * Returns whether entity with given id is present in the repository.
	 * 
	 * @param entity id.
	 * @return true if entity is present in the repository.
	 */
	@Override
	public boolean existsById( String id ) {
		return findById( id ).isPresent();
	}


	/**
	 * Find method that returns the repository entity with matching id or null if
	 * entity is not found.
	 * 
	 * @param id entity identifier.
	 * @return Optional of entity matching id.
	 */
	@Override
	public Optional<E> findById( String id ) {
		E e = findById( list, id );
		return e != null? Optional.of( e ) : Optional.empty();
	}


	/**
	 * Find method that returns all entities of the repository.
	 * 
	 * @return all entities of the repository.
	 */
	@Override
	public Iterable<E> findAll() {
		return list;
	}


	/**
	 * Find method that returns a set of Entities that have been found based on
	 * a set of Id's provided as input.
	 * 
	 * @param ids set of Id's to be looked up in the repository.
	 * @return set of found Entities.
	 */
	@Override
	public Iterable<E> findAllById( Iterable<String> ids ) {
		List<E> collection = new ArrayList<E>();
		for( String id : ids ) {
			E e = findById( list, id );
			if( e != null ) {
				collection.add ( e );
			}
		}
		return collection;
	}


	/**
	 * Find method that returns List of entities matching the name-field.
	 * 
	 * @param regEx regular expression to match getName() property.
	 * @return Optional of entity matching name.
	 */
	@Override
	public Optional<E> findByName( String regEx ) {
		List<E> resultList = findByName( regEx, 1 );
		return resultList.size() > 0? Optional.of( resultList.get( 0 ) ) : Optional.empty();
	}


	/**
	 * Find method that returns List of entities matching the name-field.
	 * 
	 * @param regEx regular expression to match getName() property
	 * @param limit max number of matching entities returned
	 * @return List of matching entites (up to limit)
	 */
	@Override
	public List<E> findByName( String regEx, long limit ) { 
		Pattern p = Pattern.compile( regEx );
		/*
		 * Alternative approach to match and collect entities using Java 8's streaming interface.
		 */
		List<E> result = list.stream()
				.filter( e -> p.matcher( e.getName() ).matches() )
				.limit( limit )
                .collect( Collectors.toList() );

		return result;
	}


	/**
	 * Save Entity to repository (update if already present, or add Entity if not yet present).
	 * Presence in the repository means equality of the Id-property, not equality of object
	 * references.
	 * 
	 * @param entity entity to be saved to the repository.
	 * @return entity that has been saved (which could be the reference to the Entity object
	 * stored in the repository that is different from the reference to the param-Entity, but
	 * has the same Id-property.
	 */
	@Override
	public E save( E entity ) {
		E e2 = update( entity, true );
		return e2;
	}


	/**
	 * Save list of Entities to repository and return the list of Entities that have been
	 * saved to the repository (see for differences the comment above).
	 * Part of org.springframework.data.repository.CrudRepository<E,String> as
	 *		<S extends E> Iterable<S> saveAll( Iterable<S> entities );
	 * @param entities
	 * @return
	 */
	@Override
	public Iterable<E> saveAll( Iterable<E> entities ) {
		List<E> res = new ArrayList<E>();
		for( E e : entities ) {
			E e2 = save( e );
			res.add( e2 );
		}
		return res;
	}


	/**
	 * Returns the number of entities present in repository.
	 * 
	 * @return number of entities present in repository.
	 */
	@Override
	public long count() {
		return list.size();
	}


	/**
	 * Delete entity with matching id from the repository.
	 * 
	 * @id id of entity to be deleted from the repository.
	 */
	@Override
	public void deleteById( String id ) {
		E e = findById( list, id );
		delete( e );
	}


	/**
	 * Delete entity from repository.
	 *  
	 * @entity entity to be deleted from repository.
	 */
	@Override
	public void delete( E entity ) {
		if( entity != null ) {
			String id = entity.getId();
			deleteById( id );
		}
	}


	/**
	 * Delete all entities passed as argument from repository as one atomic transaction.
	 * 
	 * @ids list of entities to be deleted from repository.
	 */
	@Override
	public void deleteAllById( Iterable<String> ids ) {
		for( String id : ids ) {
			E entity = findById( list, id );
			if( entity != null ) {
				list.remove( entity );
			}
		}
	}


	/**
	 * Delete all entities from the repository as one atomic transaction.
	 * 
	 * @entities list of entities to be deleted from repository.
	 */
	@Override
	public void deleteAll( Iterable<E> entities ) {
		for( E entity : entities ) {
			list.remove( entity );
		}
	}


	/**
	 * Delete all entities from repository as one atomic transaction. The result
	 * is an empty repository.
	 */
	@Override
	public void deleteAll() {
			list.clear();
	}


	/*
	 * Private methods.
	 */

	private E findById( Collection<E> collection, String id ) {
		for( E e : collection ) {
			if( e.getId().equals( id ) ) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Update method that sets values of entity passed as argument to an entity
	 * found in the repository with same id. If no entity with matching id is found,
	 * the entity passed as argument is inserted into the repository if the insert
	 * flag is set to true. If set to false, no update is performed.
	 * 
	 * @entity entity to update values of repository entity with matching id.
	 * @insert if true, entity is inserted if no entity with matching id exists.
	 * @return reference to updated entity.
	 */

	private E update( E entity, boolean insert ) {
		E e1 = findById( list, entity.getId() );
		if( e1 != null ) {
			if( e1 != entity ) {
				//logger.error( "==> duplicate instance update(" + entity.getId() + ").", null );
				entity = e1;

			} else {
				//log.info( "==> updated(" + entity.getId() + ")" );
			}

		} else {
			if( insert ) {
				//log.info( "==> inserted(" + entity.getId() + ")" );
				list.add( entity );
			}
		}
		return entity;
	}

}
