package com.application.se2.repository;

import java.io.IOException;
import java.util.List;

import com.application.se2.model.Entity;


/**
 * Public interface of a SerializationProvider with read/write-methods for
 * Entity collections.
 * 
 * @author Sven Graupner
 *
 */
interface SerializationProviderIntf {

	/**
	 * Serialize a collection of entities into a serial stream.
	 * 
	 * @param list list of entities to be serialized.
	 * @throws IOException IOException thrown in case of IO failure.
	 */
	public void writeSerialStream( List<? extends Entity> list ) throws IOException;


	@FunctionalInterface
	interface CollectorIntf {
		void collect( Entity e3 );
	}

	/**
	 * Deserialize a stream of serialized entities into entity objects.
	 * 
	 * @param collector functional interface called from the persistence provider
	 * 			for each deserialized entity.
	 * @throws IOException IOException thrown in case of IO failure.
	 */
	public void readSerialStream( CollectorIntf collector ) throws IOException;

}
