package com.application.se2.fxgui;

import java.util.ArrayList;
import java.util.List;

import com.application.se2.misc.EntityPropertyAccessor;
import com.application.se2.misc.Traceable;

import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;


class TableItem {
	private static final StringProperty defaultProperty = new SimpleStringProperty( " - " );

	private final TableViewImpl fxTableView;
	private final Traceable primary;
	private final List<Property<?>> propertyValues;


	TableItem( final Traceable primary, final TableViewImpl fxTableView ) {
		this.fxTableView = fxTableView;
		this.primary = primary;	//new PrimaryObject( e, parentTableItem == null? null : parentTableItem.getPrimaryObject() );
		this.propertyValues = new ArrayList<Property<?>>();
		serialize();
	}

	Property<?> getProperty( final int i ) {
		return indexInRange( propertyValues, i )? propertyValues.get( i ) : defaultProperty; 
	}

	public TableViewImpl getFXTableView() {
		return fxTableView;
	}

	public Traceable getPrimaryObject() {
		return primary;
	}


	/*
	 * Private methods.
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void serialize() {
		EntityPropertyAccessor properties = fxTableView.getTableViewConfig().getProperties();

		for( int i = 0; i < properties.size(); i++ ) {
			Object val = properties.getPropertyValue( primary.getRootObject(), i );

			if( propertyValues.size() <= i ) {
				propertyValues.add( List.class.isAssignableFrom( val.getClass() )?
					new SimpleListProperty<>() : new SimpleStringProperty()
				);
			}

			if( val instanceof List ) {
				ListProperty<?> list = ((SimpleListProperty<?>)propertyValues.get( i ));
				list.set( FXCollections.observableArrayList( ((List)val) ) );

			} else {
				((SimpleStringProperty)propertyValues.get( i )).setValue( val.toString() );
			}
		}
	}

	private boolean indexInRange( List<?>list, int i ) {
		return i >= 0 && i < list.size();
	}

}
