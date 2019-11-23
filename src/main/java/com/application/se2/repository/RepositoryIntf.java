package com.application.se2.repository;

import java.util.Optional;

import com.application.se2.model.Entity;


/**
 * Interface for generic CRUD operations on a repository for a specific type.
 * Methods were adopted from Spring's
 * 		org.springframework.data.repository.CrudRepository<E,String>.
 * 
 * Reference:
 *   https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html
 * 
 * @author sgra64
 *
 * @param <E> generic Entity type
 */
public interface RepositoryIntf<E extends Entity> { //extends CrudRepository<E,String> {

	/**
	 * Returns whether entity with given id is present in the repository.
	 * Part of org.springframework.data.repository.CrudRepository<E,String>.
	 * 
	 * @param entity id.
	 * @return true if entity is present in the repository.
	 */
	public boolean existsById( String id );

	/**
	 * Find method that returns the repository entity with matching id or null if
	 * entity is not found.
	 * Part of org.springframework.data.repository.CrudRepository<E,String>.
	 * 
	 * @param id entity identifier.
	 * @return Optional of entity matching id.
	 */
	public Optional<E> findById( String id );

	/**
	 * Find method that returns all entities of the repository.
	 * Part of org.springframework.data.repository.CrudRepository<E,String>.
	 * 
	 * @return all entities of the repository.
	 */
	public Iterable<E> findAll();

	/**
	 * Find method that returns a set of Entities that have been found based on
	 * a set of Id's provided as input.
	 * Part of org.springframework.data.repository.CrudRepository<E,String>.
	 * 
	 * @param ids set of Id's to be looked up in the repository.
	 * @return set of found Entities.
	 */
	public Iterable<E> findAllById( Iterable<String> ids );

	/**
	 * Find method that returns entity that matches the regular expression. If more than
	 * one entity match, a random matching entity is returned.
	 * 
	 * @param regEx regular expression to match getName() property.
	 * @return Optional of entity matching name.
	 */
	public Optional<E> findByName( String regEx );

	/**
	 * Find method that returns List of entities matching the name-field.
	 * 
	 * @param regEx regular expression to match getName() property
	 * @param limit max number of matching entities returned
	 * @return List of matching entites (up to limit)
	 */
	public Iterable<E> findByName( String regEx, long limit );

	/**
	 * Save Entity to repository (update if already present, or add Entity if not yet present).
	 * Presence in the repository means equality of the Id-property, not equality of object
	 * references.
	 * Part of org.springframework.data.repository.CrudRepository<E,String> as
	 *		public <S extends E> S save( S entity );
	 *
	 * @param entity entity to be saved to the repository.
	 * @return entity that has been saved (which could be the reference to the Entity object
	 * stored in the repository that is different from the reference to the param-Entity, but
	 * has the same Id-property.
	 */
	public E save( E entity );

	/**
	 * Save list of Entities to repository and return the list of Entities that have been
	 * saved to the repository (see for differences the comment above).
	 * Part of org.springframework.data.repository.CrudRepository<E,String> as
	 *		<S extends E> Iterable<S> saveAll( Iterable<S> entities );
	 *
	 * @param entities
	 * @return
	 */
	public Iterable<E> saveAll( Iterable<E> entities );
 
	/**
	 * Returns the number of entities present in repository.
	 * Part of org.springframework.data.repository.CrudRepository<E,String>.
	 * 
	 * @return number of entities present in repository.
	 */
	public long count();

	/**
	 * Delete entity with matching id from the repository.
	 * Part of org.springframework.data.repository.CrudRepository<E,String>.
	 * 
	 * @param id id of entity to be deleted from the repository.
	 */
	public void deleteById( String id );

	/**
	 * Delete entity from repository.
	 * Part of org.springframework.data.repository.CrudRepository<E,String>.
	 * 
	 * @param entity entity to be deleted from repository.
	 */
	public void delete( E entity );

	/**
	 * Delete all entities passed as argument from repository as one atomic transaction.
	 * 
	 * @param ids list of entities to be deleted from repository.
	 */
	public void deleteAllById( Iterable<String> ids );

	/**
	 * Delete all entities from the repository as one atomic transaction.
	 * Part of org.springframework.data.repository.CrudRepository<E,String>.
	 * 
	 * @param entities list of entities to be deleted from repository.
	 */
	public void deleteAll( Iterable<E> entities );

	/**
	 * Delete all entities from repository as one atomic transaction. The result
	 * is an empty repository.
	 */
	public void deleteAll();

}
