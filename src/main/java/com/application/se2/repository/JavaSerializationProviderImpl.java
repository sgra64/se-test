package com.application.se2.repository;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.application.se2.misc.Logger;
import com.application.se2.model.Entity;


class JavaSerializationProviderImpl implements SerializationProviderIntf {
	private static final Logger log = Logger.getInstance( JavaSerializationProviderImpl.class );

	private final String dirPath;
	private final String fileName;


	/**
	 * Constructor.
	 * 
	 * @param path path to JSON file.
	 * @param clazz entity class needed for de-serialization.
	 */
	JavaSerializationProviderImpl( String path, Class<? extends Entity> clazz ) {
		path = path + "/" + clazz.getSimpleName();
		path = path.replace( '\\', '/' );
		int i2 = path.lastIndexOf( "/" ) + 1;
		this.dirPath = path.substring( 0, i2 );
		this.fileName = path.substring( i2, path.length() );
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
		if( list != null ) {
			File dir = new File( dirPath );
			if( ! dir.exists() ) {
				dir.mkdirs();	// create dataPath, if not present
				log.info( dirPath + " created." );
			}
			final String fullFilename = dirPath + fileName;
	
			final Path destination = new File( fullFilename ).toPath();
			ObjectOutputStream out = null;
			try {
				out = new ObjectOutputStream( Files.newOutputStream( destination ) );
			    out.writeObject( list );
	
			} catch( IOException ex ) {
				ex.printStackTrace();
	
			} finally {
				if( out != null ) {
					try {
						out.close();
					} catch( IOException ex2 ) {
						ex2.printStackTrace();
					}
				}
			}
		}
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
		if( collector != null ) {
			final String fullFilename = dirPath + fileName;

			final Path source = new File( fullFilename ).toPath();
			final ObjectInputStream in = new ObjectInputStream( Files.newInputStream( source ) );
			try {
				Serializable deserialized = (Serializable)in.readObject();
				if( deserialized instanceof List<?> ) {
					List<?> objL = (List<?>)deserialized; 
					for( Object obj : objL ) {
						Entity e = (Entity)obj;
						collector.collect( e );
					}
				}

			} catch( ClassNotFoundException ex ) {
				ex.printStackTrace();

			} finally {
				in.close();
			}

			log.info( "loaded (" + dirPath + fileName + ")." );
		}
	}
}
