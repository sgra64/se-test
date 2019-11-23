package com.application.se2.misc;


/**
 * Helper class to trace nodes of an object tree back to its originating
 * root object.
 * 
 * Use case: if a Customer class a property of class Contacts, then a Contact
 * object can be wrapped as a Traceable to link it back to the root Customer
 * object.
 * 
 * @author sgra64
 *
 */
public class Traceable {
	private final Object root;					// root object of object tree
	private final Traceable backLink;			// reference to predecessor in object tree
	private final EntityProperty backLinkProperty;	// Property reference to predecessor in object tree


	/**
	 * Public constructor.
	 * @param root root of object tree.
	 */
	public Traceable( final Object root ) {
		this( root, null, null );
	}


	/**
	 * Public constructor.
	 * @param root root of object tree.
	 * @param parent reference to predecessor in object tree 
	 * @param backLinkProperty Property reference to predecessor in object tree
	 */
	public Traceable( final Object root, final Traceable backLink, final EntityProperty backLinkProperty ) {
		this.root = root;
		this.backLink = backLink;
		this.backLinkProperty = backLinkProperty;
	}


	/**
	 * Return root object.
	 * @return root object.
	 */
	public Object getRootObject() {
		return root;
	}


	/**
	 * Returns true if this traceable has a predecessor in object tree.
	 * @return true if this traceable has a predecessor in object tree
	 */
	public boolean hasParent() {
		return backLink != null && backLink.root != null;
	}


	/**
	 * Return predecessor in object tree.
	 * @return predecessor in object tree. Returns null if traceable has no predecessor.
	 */
	public Traceable getParent() {
		return backLink;
	}


	/**
	 * Traverses object tree back links until a node of a matching class is found.
	 * @param clazz class to be found.
	 * @return node of a matching class, if any.
	 */
	public Object traverse( final Class<?> clazz ) {
		Object root = null;
		Traceable p1 = this;
		while( root == null && p1 != null ) {
			if( p1.root.getClass() == clazz ) {
				root = p1.root;
			} else {
				p1 = p1.hasParent()? p1.getParent() : null;
			}
		}
		return root;
	}


	/**
	 * Return reference to predecessor in object tree.
	 * @return reference to predecessor in object tree.
	 */
	public EntityProperty getParentProperty() {
		return backLinkProperty;
	}

}
