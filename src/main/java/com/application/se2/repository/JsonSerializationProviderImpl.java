package com.application.se2.repository;

import java.io.IOException;
import java.util.List;

import com.application.se2.model.Entity;


/**
 * Dummy implementation of Json-Serialization Provider that has no effect.
 * 
 * @author Sven Graupner
 *
 */
class JsonSerializationProviderImpl implements SerializationProviderIntf {

	/**
	 * Constructor.
	 * 
	 * @param path path to JSON file.
	 * @param clazz entity class needed for de-serialization.
	 */
	JsonSerializationProviderImpl( String path, Class<? extends Entity> clazz ) {

	}


	/**
	 * Serialize list of entities into serial JSON stream. The full object
	 * tree is serialized.
	 * 
	 * @param list list of entities to serialize.
	 * @exception throws IOException.
	 */
	@Override
	public void writeSerialStream( List<? extends Entity> list ) throws IOException {

	}


	/**
	 * Deserialize JSON-stream into entities. The collector interface is called
	 * when an entity was deserialized passing the entity to the invoking method.
	 * 
	 * @param collector functional interface to invoke caller passing a deserialized entity.
	 * @exception throws IOException.
	 */
	@Override
	public void readSerialStream( CollectorIntf collector ) throws IOException {

	}

}
