package com.application.se2.fxgui;

import com.application.se2.AppConfigurator.Table.Column;
import com.application.se2.misc.EntityProperty;


class TableColumnProperty extends EntityProperty {
	private int minColumnWidth = 50;
	private int maxColumnWidth = 200;
	private boolean editable = true;
	private TableViewConfig popUpTableViewConfig = null;


	public TableColumnProperty( TableViewConfig tv, EntityProperty property ) {
		super( property );
		setColumnWidths( (String)property.getConfig( Column.COL_WIDTH ) );
		Object val = property.getConfig( Column.COL_EDITABLE );
		if( val != null ) {
			this.editable = (boolean)val;
		}
		this.popUpTableViewConfig = (TableViewConfig)property.getConfig( Column.POPUP_VIEW );
	}

	public String getLabel() {
		return (String)getConfig( Column.COL_LABEL );
	}

	public String getCssId() {
		String cssId = (String)getConfig( Column.COL_CSSID );
		String label = getLabel();
		String ret = cssId == null? ( label == null? "default" : label ).toLowerCase() : cssId;
		return ret;
	}

	public int getMinColumnWidth() {
		return minColumnWidth;
	}

	public int getMaxColumnWidth() {
		return maxColumnWidth;
	}
	public boolean isEditable() {
		return editable;
	}

	public TableViewConfig getPopUpTableViewConfig() {
		return popUpTableViewConfig;
	}


	/*
	 * Private methods.
	 */

	private void setColumnWidths( String parse ) {
		// example parse string: "min: 50; max: 200;"
		for( String s1 : parse.split( ";" ) ) {
			String[] p = s1.split( ":" );
			if( p != null && p.length >= 2 ) {
				try {
					if( p[0].trim().toLowerCase().startsWith( "min" ) ) {
						this.minColumnWidth = Integer.parseInt( p[1].trim() );
					}
					if( p[0].trim().toLowerCase().startsWith( "max" ) ) {
						this.maxColumnWidth = Integer.parseInt( p[1].trim() );
					}

				} catch( NumberFormatException nfe ) {
					//ignore and use default values
				}
			}
		}
	}

}
