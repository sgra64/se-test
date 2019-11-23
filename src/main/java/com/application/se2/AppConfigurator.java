package com.application.se2;

import java.util.EnumSet;

import com.application.se2.components.ComponentBase;
import com.application.se2.components.ComponentIntf;
import com.application.se2.misc.StringProperty;
import com.application.se2.model.Article;
import com.application.se2.model.Customer;
import com.application.se2.model.Note;


/**
 * AppConfigurator is a local singleton class. Its purpose is to set up
 * data for configuring components such as GUI views. It only creates
 * configuration data structures and executes no logic.
 * 
 * Data structures are stored as arrays of key-value pairs as Object[][][],
 * from which Array/HashMap structures are produced later during build.
 * 
 * The GUI is assumed as a number of Tabs, each representing a view of an
 * application component (e.g. for Calculator, Customers...).
 * Each view requires configuration data from component key-value pairs.
 * 
 * @author sgra64
 *
 */
public class AppConfigurator implements ComponentIntf {
	private static AppConfigurator instance = null;

	public enum LoggerTopics {
		Always,
		Info, Warn, Error,
		EntityCRUD,
		Startup,
		Shutdown,
		PropertiesAltered,
		FieldAccessAltered,
		RepositoryLoaded,
		CSSLoaded
	}

	public static final EnumSet<LoggerTopics> LoggerConfig = EnumSet.of(
		LoggerTopics.Info,
		LoggerTopics.Warn,
		LoggerTopics.Error,
		LoggerTopics.EntityCRUD,
		LoggerTopics.Startup, LoggerTopics.Shutdown,
		//LoggerTopics.PropertiesAltered,
		//LoggerTopics.FieldAccessAltered,
		//LoggerTopics.RepositoryLoaded,
		//LoggerTopics.CSSLoaded,
		LoggerTopics.Always
	); 


	/*
	 * Keys used in component configurations.
	 */
	public static class Key extends ComponentBase.Key {
		public final static String Label = KEY( AppConfigurator.class, "label:" );
		public final static String TableView = KEY( AppConfigurator.class, "table_view:" );
		public final static String Descriptor = KEY( AppConfigurator.class, "descriptor:" );
	}

	/*
	 * Keys used in TableView configurations.
	 */
	public static class Table extends ComponentBase.Key {
		public static final String CLASS = KEY( AppConfigurator.Table.class, "class:" );
		public static final String LABEL = KEY( AppConfigurator.Table.class, "label:" );
		public static final String CSSID = KEY( AppConfigurator.Table.class, "cssId:" );
		public static final String CSSRESOURCE = KEY( AppConfigurator.Table.class, "cssResource:" );
		public static final String COLUMNS = KEY( AppConfigurator.Table.class, "columns:" );

		/*
		 * Within Table key space, keys for each column are used in TableView configurations.
		 */
		public static class Column extends Table {
			public static final String MATCHFIELD = "match_Field:";
			public static final String COL_LABEL = "col_Label:";
			public static final String COL_CSSID = "col_CssId:";
			public static final String COL_WIDTH = "col_Width:";
			public static final String COL_EDITABLE = "col_editable:";

			public static final String POPUP_VIEW = "popup_view:";
			public static final String POPUP_BUTTON_LABEL = "popup_button_label:";
			public static final String POPUP_BUTTON_SHOW_AS_TEXT = "popup_showastext:";

			public static final String DATEFMT = "dateFmt:";
		}
	}


	/**
	 * Private constructor according to singleton pattern.
	 */
	private AppConfigurator() { }

	/**
	 * Access method to singleton instance created when first called.
	 * @return reference to singleton view builder instance.
	 */
	public static AppConfigurator getInstance() {
		if( instance == null ) {
			instance = new AppConfigurator();
		}
		return instance;
	}


	/*
	 * Configuration data of main-App Tab-View as key-value pairs.
	 */
	public Object[][] AppView() {
		Object[][] view = new Object[][] {
			KV( Key.Label, "Main" ),
			KV( Key.Descriptor, "App.fxml" ),
		};
		return view;
	}

	/*
	 * Configuration data of Calculator Tab-View as key-value pairs.
	 */
	public Object[][] CalculatorView() {
		Object[][] view = new Object[][] {
			KV( Key.Label, "Calculator24" ),
			KV( Key.Descriptor, "Calculator.fxml" ),
		};
		return view;
	}

	/*
	 * Configuration data of a Customer TableView as key-value pairs (kv-pairs)
	 * for the Table view as whole and for each table column.
	 * KV-pairs are created using helper methods.
	 * 
	 * View configurations may contain other view configurations such as here with
	 * configurations for views for columns Contacts (later rendered as Popup with
	 * contacts list) and Notes (rendered as Popup with note entries).
	 */
	public Object[][] CustomerTableView_1() {
		//
		Object[][] view = new Object[][] {
			KV( Key.Label, "Kunden" ),
			KV( Key.Descriptor, "TableView.fxml" ),

			KV( Table.CLASS, Customer.class ),
			KV( Table.LABEL, "Kunde" ),
			KV( Table.CSSID, "customer" ),
			KV( Table.CSSRESOURCE, "CustomerView.css" ),
			KV( Table.COLUMNS, new Object[][][] {
				//		match-field:,	label:,		css-id,		width:,				more:...
				COLUMN( "[Ii]d",		"Kund.-Id",		"id",		"min: 70; max: 70;", KV( Table.Column.COL_EDITABLE, false ) ),
				COLUMN( "[Nn]ame",		"Name",		"name",		"min: 50; max: 120;" ),
				COLUMN( "[Aa]ddress",	"Adresse",	"address",	"min: 240; max: 300;" ),
				COLUMN( "[Ss]tatus",	"Status",	"status",	"min: 56; max: 60;" ),
				COLUMN( "[Cc]ontacts",	"Kontakte","customer-contacts", "min: 156; max: 160;",
					KV( Table.Column.POPUP_VIEW, build_CustomerTableView_ContactsView() ),	/* Contacts View configurations */
					KV( Table.Column.POPUP_BUTTON_LABEL, "+#" ),
					KV( Table.Column.POPUP_BUTTON_SHOW_AS_TEXT, 1 )
				),
				COLUMN( "[Nn]otes",		"Notizen", "customer-notes", "min: 54; max: 58;",
					KV( Table.Column.POPUP_VIEW, build_CustomerTableView_NotesView() ),	/* Notes View configurations */
					//KV( Table.Column.POPUP_BUTTON_LABEL, "Notes: #" )
					KV( Table.Column.POPUP_BUTTON_LABEL, "+#" )
				),
//				COLUMN( "[Cc]reated",	"Erstellungsdatum", "created", "min: 50; max: 120;",
//					KV( Table.Column.DATEFMT, "d. MMM. yyyy" )
//				),
			} )
		};
		return view;
	}

	/*
	 * Configuration data of Customer-Contacts-View as key-value pairs that is later rendered as a Popup.
	 */
	private Object[][] build_CustomerTableView_ContactsView() {
		//
		Object[][] view = new Object[][] {
			KV( Table.CLASS, StringProperty.class ),
			KV( Table.LABEL, "Kontakte" ),
			KV( Table.CSSID, "contacts" ),
			KV( Table.COLUMNS, new Object[][][] {
				//		match_field:,	label:,		css-id,		width:,		more:...
				COLUMN( "[Ss]tr",		"Kontakt",	"text",		"min: 1200; max: 1200;" ),
			} )
		};
		return view;
	}

	/*
	 * Configuration data of Customer-Notes-View as key-value pairs that is later rendered as a Popup.
	 */
	private Object[][] build_CustomerTableView_NotesView() {
		//
		Object[][] view = new Object[][] {
			KV( Table.CLASS, Note.class ),
			KV( Table.LABEL, "Notizen" ),
			KV( Table.CSSID, "notes" ),
			KV( Table.COLUMNS, new Object[][][] {
				//		match-field:,		label:,			css-id,			width:,					more:...
				COLUMN( "[Tt]ime[Ss]tamp",	"Datum, Zeit",	"timestamp",	"min: 120; max: 120;", KV( Table.Column.COL_EDITABLE, false ),
					KV( Table.Column.DATEFMT, "dd-MMM-yyyy HH:mm" )		// "2018-04-02 10:16:24"
				),
				COLUMN( "[Nn]ote[Tt]ext",		"Notiz",		"text",		"min: 1200; max: 1200;" ),
			} )
		};
		return view;
	}


	/*
	 * Configuration data of a ArticleCatalog TableView as key-value pairs (kv-pairs)
	 * for the Table view as whole and for each table column.
	 * KV-pairs are created using helper methods.
	 */
	public Object[][] ArticleCatalogTableView_1() {
		//
		Object[][] view = new Object[][] {
			KV( Key.Label, "Artikelkatalog" ),
			KV( Key.Descriptor, "TableView.fxml" ),

			KV( Table.CLASS, Article.class ),
			KV( Table.LABEL, "Artikel" ),
			KV( Table.CSSID, "article" ),
			KV( Table.CSSRESOURCE, "ArticleView.css" ),
			KV( Table.COLUMNS, new Object[][][] {
				//		match-field:,	label:,		css-id,		width:,				more:...
				COLUMN( "[Ii]d",	"Artikel-Id",	"id",		"min: 86; max: 86;", KV( Table.Column.COL_EDITABLE, false ) ),
				COLUMN( "[Nn]ame",		"Name",		"name",		"min: 360; max: 400;" ),
				COLUMN( "[Pp]rice",		"Preis",	"price",	"min: 86; max: 90;" ),
				//COLUMN( "[Ss]tatus",	"Status",	"status",	"min: 56; max: 60;" ),
			} )
		};
		return view;
	}


	/*
	 * Private helper methods to create key-value pairs.
	 */

	private Object[] KV( String key, Object val ) { return new Object[] { key, val }; }
	private String[] KV( String key, String val ) { return new String[] { key, val }; }

	private Object[][] COLUMN( String match, String label, String css_id, String width, Object[]... props ) {
		int size = 4 + ( props==null? 0 : props.length );
		Object [][] res = new Object[ size ][];
		res[ 0 ] = KV( Table.Column.MATCHFIELD, match );
		res[ 1 ] = KV( Table.Column.COL_LABEL, label );
		res[ 2 ] = KV( Table.Column.COL_CSSID, css_id );
		res[ 3 ] = KV( Table.Column.COL_WIDTH, width );
		for( int i = 4; i < size; i++ ) {
			res[ i ] = props[ i - 4 ];
		}
		return res;
	}

}
