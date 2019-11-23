package com.application.se2.fxgui;

import java.util.function.Function;

import com.application.se2.AppConfigurator;
import com.application.se2.AppConfigurator.Table;
import com.application.se2.AppConfigurator.Table.Column;
import com.application.se2.components.ComponentBase;
import com.application.se2.misc.Callback;
import com.application.se2.misc.EntityProperty;
import com.application.se2.misc.EntityPropertyAccessor;


class TableViewConfig extends ComponentBase {
	private final ComponentBase parent;
	private final EntityPropertyAccessor propertyList;

	TableViewConfig( ComponentBase proto ) {
		this.parent = proto;

		Class<?> clazz = (Class<?>) get( Table.CLASS );
		if( clazz != null ) {
			Object[][][] columns = (Object[][][])get( Table.COLUMNS );
			propertyList = new EntityPropertyAccessor( clazz );

			for( Object[][] colConfigs : columns ) {
				String propertyName = (String)find( Column.MATCHFIELD, colConfigs );

				EntityProperty property = propertyList.addProperty( propertyName );

				if( property != null ) {
					traverseConfigs( colConfigs, keyFilter -> {
						String key = (String)keyFilter[ 0 ];
						Object value = keyFilter[ 1 ];

						if( key.equals( Column.POPUP_VIEW ) ) {
							Object[][] configs2 = (Object[][])value;
							ComponentBase newConfig = new ComponentBase();
							newConfig.configure( AppConfigurator.Key.TableView, configs2 );
							TableViewConfig tv2 = new TableViewConfig( newConfig );
							value = tv2;
							property.putConfig( key, value );
							return false;
						}
						return true;

					}, configs -> {
						String key = (String)configs[ 0 ];
						Object value = configs[ 1 ];
						property.putConfig( key, value );
					});
				}
			}

		} else {
			propertyList = null;
		}
	}

	public String getLabel() {
		return (String)this.parent.get( Table.LABEL );
	}

	public String getCssId() {
		return (String)this.parent.get( Table.CSSID );
	}

	public EntityPropertyAccessor getProperties() {
		return propertyList;
	}


	@Override
	public ComponentBase configure( final String key, final Object value ) {
		return this.parent.configure( key, value );
	}

	@Override
	public Object get( String key ) {
		return this.parent.get( key );
	}

	@Override
	public String getName() {
		return this.parent.getName();
	}


	/*
	 * Private methods.
	 */

	private Object find( String key, Object[][] viewConfig ) {
		for( Object[] entry : viewConfig ) {
			if( entry != null && entry.length > 1 && entry[0].equals( key ) ) {
				return entry[1];
			}
		}
		return null;
	}

	private void traverseConfigs( Object[][] configs, Function<Object[],Boolean> keyFilter, Callback<Object[]> otherwise ) {
		if( configs != null ) {
			for( Object[] config : configs ) {
				if( config != null && config.length > 1 ) {
					if( keyFilter.apply( config ) ) {
						Object value = config[ 1 ];
						if( value instanceof Object[][] ) {
							traverseConfigs( (Object[][])value, keyFilter, otherwise );
						} else {
							otherwise.apply( config );
						}
					}
				}
			}
		}
	}

}
