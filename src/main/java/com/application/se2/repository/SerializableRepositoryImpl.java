package com.application.se2.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import com.application.se2.model.Entity;


/**
 * Local implementation class that implements the RepositoryIntf<E> interface.
 * The repository stores entities of type E in a List<E> that is persisted using
 * Json-Serialization.
 * 
 * @author sgra64
 *
 * @param <E> gneric entity type defined as sub-type of EntityIntf.
 */
class SerializableRepositoryImpl<E extends Entity> implements RepositoryIntf<E> {

	//private Optional<SerializationProviderIntf<E>> serializationProvider;
	private SerializationProviderIntf serializationProvider;

	private final ReentrantLock listUpdateLock;

	private final ReentrantLock syncLock;


	/*
	 * Internal list that represents the repository.
	 */
	private final List<E> list;

	enum ReadWrite { READ, WRITE };
	enum Status { initial, insync, altered };

	private Status status;


	/**
	 * Public constructor.
	 * 
	 * @param list list<E> that is associated with the repository.
	 */
	SerializableRepositoryImpl( List<E> list, SerializationProviderIntf serializationProvider ) {
		//this.serializationProvider = Optional.empty();
		this.serializationProvider = serializationProvider;
		this.syncLock = new ReentrantLock();
		this.listUpdateLock = new ReentrantLock();
		this.list = list;
		this.status = Status.initial;
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
		E e = _findById( list, id );
		return e != null? Optional.of( e ) : Optional.empty();
	}


	/**
	 * Find method that returns all entities of the repository.
	 * 
	 * @return all entities of the repository.
	 */
	@Override
	public Iterable<E> findAll() {
		final List<E> resultList = new ArrayList<E>( list.size() );
		safeguardRepository( ReadWrite.READ, () -> {

			resultList.addAll( list );
		});
		return resultList;
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
		final List<E> resultList = new ArrayList<E>();
		safeguardRepository( ReadWrite.READ, () -> {
			for( String id : ids ) {
				for( E e : list ) {
					if( e.getId().equals( id ) ) {
						resultList.add( e );
					}
				}
			}
		});
		return resultList;
	}


	/**
	 * Find method that returns List of entities matching the name-field.
	 * 
	 * @param regEx regular expression to match getName() property.
	 * @return Optional of entity matching name.
	 */
	@Override
	public Optional<E> findByName( String regEx ) {
		List<E> resultList = findByName( regEx, 1L );
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
		final List<E> resultList = new ArrayList<E>();
		safeguardRepository( ReadWrite.READ, () -> {
			Pattern p = Pattern.compile( regEx );
			/*
			 * Alternative approach to match and collect entities using Java 8's streaming interface.
			 */
			list.stream()
				.filter( e -> p.matcher( e.getName() ).matches() )
				.limit( limit )
				.forEach( e -> resultList.add( e ) );
		});
		return resultList;
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
		// final variable needed in order to access within lambda
		final Object[] result = new Object[] { entity };
		safeguardRepository( ReadWrite.WRITE, () -> {

			result[ 0 ] = __update( entity, true );
		});
		return (E)result[ 0 ];
	}


	/**
	 * Save list of Entities to repository and return the list of Entities that have been
	 * saved to the repository (see for differences the comment above).
	 * Part of org.springframework.data.repository.CrudRepository<E,String> as
	 *		<S extends E> Iterable<S> saveAll( Iterable<S> entities );
	 * @param entities
	 * @return list of saved entities.
	 */
	@Override
	public Iterable<E> saveAll( Iterable<E> entities ) {
		return _update( entities );
	}


	/**
	 * Returns the number of entities present in repository.
	 * 
	 * @return number of entities present in repository.
	 */
	@Override
	public long count() {
		final long[] result = new long[ 1 ];
		result[ 0 ] = 0L;
		safeguardRepository( ReadWrite.READ, () -> {
			result[ 0 ] = list.size();
		});
		return result[ 0 ];
	}


	/**
	 * Delete entity with matching id from the repository.
	 * 
	 * @id id of entity to be deleted from the repository.
	 */
	@Override
	public void deleteById( String id ) {
		safeguardRepository( ReadWrite.WRITE, () -> {
			__deleteById( id );
		});
	}


	/**
	 * Delete entity from repository.
	 *  
	 * @entity entity to be deleted from repository.
	 */
	@Override
	public void delete( E entity ) {
		safeguardRepository( ReadWrite.WRITE, () -> {
			__delete( entity );
		});
	}


	/**
	 * Delete all entities passed as argument from repository as one atomic transaction.
	 * 
	 * @ids list of entities to be deleted from repository.
	 */
	@Override
	public void deleteAllById( Iterable<String> ids ) {
		safeguardRepository( ReadWrite.WRITE, () -> {
			for( String id : ids ) {
				__deleteById( id );
			}
		});
	}


	/**
	 * Delete all entities from the repository as one atomic transaction.
	 * 
	 * @entities list of entities to be deleted from repository.
	 */
	@Override
	public void deleteAll( Iterable<E> entities ) {
		safeguardRepository( ReadWrite.WRITE, () -> {
			for( E e : entities ) {
				__delete( e );
			}
		});
	}


	/**
	 * Delete all entities from repository as one atomic transaction. The result
	 * is an empty repository.
	 */
	@Override
	public void deleteAll() {
		safeguardRepository( ReadWrite.WRITE, () -> {
			list.clear();
			status = Status.altered;
		});
	}


	/*
	 * Private methods.
	 */

	private E _findById( Iterable<E> collection, String id ) {
		// final variable needed in order to access within lambda
		final Object[] result = new Object[] { null };
		safeguardRepository( ReadWrite.READ, () -> {

			for( E e : collection ) {
				if( e.getId().equals( id ) ) {
					result[ 0 ] = e;
				}
			}
		});
		return (E)result[ 0 ];
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
	private E __update( E entity, boolean insert ) {
		E e1 = null;
		for( E e : list ) {
			if( e.getId().equals( entity.getId() ) ) {
				e1 = e;
				break;
			}
		}
		if( e1 != null ) {
			if( e1 != entity ) {
				//log.error( "==> duplicate instance update(" + entity.getId() + ").", null );
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
		status = Status.altered;
		return entity;
	}

	private Iterable<E> _update( Iterable<E> entities ) {
		final List<E> resultList = new ArrayList<E>();
		safeguardRepository( ReadWrite.WRITE, () -> {

			for( E e : entities ) {
				E e1 = __update( e, true );
				if( ! resultList.contains( e1 ) ) {
					resultList.add( e1 );
				}
			}
		});
		return resultList;
	}

	private void __deleteById( String id ) {
		E e1 = null;
		for( E e : list ) {
			if( e.getId().equals( id ) ) {
				e1 = e;
				break;
			}
		}
		__delete( e1 );
	}

	private void __delete( E entity ) {
		if( entity != null && list.contains( entity ) ) {
			list.remove( entity );
			status = Status.altered;
		}
	}


	@FunctionalInterface
	private interface NoArgLambda {
		void invoke();
	}

	private void safeguardRepository( ReadWrite operationType, NoArgLambda operation ) {

		listUpdateLock.lock();
		try {

			if( status == Status.initial ) {
				syncRead();
			}
			operation.invoke();

			if( status == Status.altered ) {
				syncWrite();
			}

		} catch( Exception e ) {
			e.printStackTrace();

		} finally {
			listUpdateLock.unlock();
		}
	}

	private void syncRead() {
		syncLock.lock();
		try {
			serializationProvider.readSerialStream( e -> {
				this.list.add( (E) e );
			});
			status = Status.insync;

		} catch( IOException e ) {
			//e.printStackTrace();

		} finally {
			syncLock.unlock();
		}
	}

	private void syncWrite() {
		syncLock.lock();
		try {
			serializationProvider.writeSerialStream( list );
			status = Status.insync;

		} catch( IOException e ) {

		} finally {
			syncLock.unlock();
		}
	}

}
