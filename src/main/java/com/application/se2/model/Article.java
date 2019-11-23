package com.application.se2.model;

import com.application.se2.misc.IDGenerator;


/**
 * Article is an Entity-class that represents an article.
 * 
 * @author sgra64
 * 
 */
public class Article implements Entity {
	private static final long serialVersionUID = 1L;

	private static final IDGenerator ArticleIdGenerator
		= new IDGenerator( "P", IDGenerator.IDTYPE.NUM, 8 );

	/*
	 * Entity Properties.
	 */
	private final String id;

	private String name;

	private String price;


	/**
	 * Public constructor.
	 * @param name Article name.
	 * @param price Article price.
	 */
	public Article( final String name, final String price ) {
		this( null, name, price );
	}

	/**
	 * Private constructor.
	 * @param id if null is passed as id, an ID will be generated.
	 * @param name Article name.
	 * @param price Article price.
	 */
	private Article( final String id, final String name, final String price ) {
		this.id = id == null? ArticleIdGenerator.nextId() : id;
		this.name = name;
		setPrice( price );
	}


	/**
	 * Return Article id.
	 * 
	 * @return Article id.
	 */
	public String getId() {		// No setId(). Id's cannot be altered.
		return id;
	}


	/**
	 * Return Article name.
	 * 
	 * @return Article name.
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Set Article name.
	 * 
	 * @param name Article name.
	 * @return self reference.
	 */
	public Article setName( final String name ) {
		this.name = name;
		return this;
	}


	/**
	 * Return Article price.
	 * 
	 * @return Article price.
	 */
	public String getPrice() {
		return price;
	}


	/**
	 * Set Article price.
	 * 
	 * @param name Article price.
	 * @return self reference.
	 */
	public Article setPrice( final String price ) {
		this.price = price;
		return this;
	}

}
