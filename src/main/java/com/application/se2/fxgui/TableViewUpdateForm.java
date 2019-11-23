package com.application.se2.fxgui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Collection;
import java.util.Optional;

import com.application.se2.misc.Callback;
import com.application.se2.misc.EntityProperty;
import com.application.se2.misc.EntityPropertyAccessor;
import com.application.se2.misc.EntityPropertyUpdateSet;
import com.application.se2.misc.Traceable;


/**
 * Local helper class for a Popup that allows editing generic String property sets.
 * It is used when logic data, e.g. Customer data is updated or new objects are
 * created and property values need to be entered.
 * 
 * @author sgra64
 *
 */
class TableViewUpdateForm extends Stage {

	TableViewUpdateForm( final TableItem ti, final TableViewImpl fxTableView, final Callback<EntityPropertyUpdateSet> update ) {
		final Traceable updatable = ti.getPrimaryObject();
		TableViewConfig tvc = ti.getFXTableView().getTableViewConfig();
		String label = fxTableView.getTableViewConfig().getLabel();

		this.setTitle( label );
		this.initModality( Modality.WINDOW_MODAL );
		this.initStyle( StageStyle.UTILITY );

		this.setWidth( 360 );
		this.setMinWidth( 200 );
		this.setMaxWidth( 1200 );
		this.setMinHeight( 100 );
		this.setMaxHeight( 800 );

		Node focus = null;

		EntityPropertyAccessor props = tvc.getProperties();
		int len = props.size();
		TextField[] textFields = new TextField[ len ];
		GridPane gp = new GridPane();

		EntityPropertyUpdateSet updateSet = new EntityPropertyUpdateSet( ti.getPrimaryObject() );

		for( int i = 0; i < len; i++ ) {
			// show only fields of base types in update form, not List<?> fields.
			EntityProperty prop = props.getProperty( i );
			boolean showFieldForUpdate = prop.isAlterableBaseType();

			TableColumnProperty tcprop = new TableColumnProperty( tvc, prop );
			Object cellVal = props.getPropertyValue( updatable.getRootObject(), i );

			if( prop.isCollectionType() ) {
				Collection<?> list = (Collection<?>)cellVal;

				if( list != null && list.size() > 0 ) {
					Optional<?> firstElement = list.stream().findFirst();
					if( firstElement.isPresent() ) {
						cellVal = firstElement.get();
					} else {
						cellVal = "";
						showFieldForUpdate |= true;
					}
					showFieldForUpdate |= cellVal.getClass() == String.class;	// list of Strings

				} else {
					showFieldForUpdate |= true;
					cellVal = "";
				}
			}

			if( showFieldForUpdate ) {
				String dp = tcprop.getLabel() + " :";
				textFields[ i ] = new TextField();
				gp.addRow( i, new Label( dp ), textFields[ i ] );
				gp.setHgap( 10 );
				gp.setVgap( 10 );
				GridPane.setHgrow( textFields[ i ], Priority.ALWAYS );

				String textVal = cellVal != null? cellVal.toString() : "";
				textFields[ i ].setText( textVal );

				updateSet.before( prop, textFields[ i ].getText() );

				if( ! tcprop.isEditable() ) {
					textFields[ i ].setEditable( false );
					textFields[ i ].setStyle(
						"-fx-border-radius: 4px;" +
						"-fx-border-color: lightgrey;" +
						"-fx-background-color: #fafafa;"
					);

				} else {

					if( focus==null ) {
						focus = textFields[ i ];
					}
				}
			}
			this.setMinHeight( this.getMinHeight() + 30 );
		}

		Button OK = new Button( "OK" );
		OK.setDefaultButton( true );

		Button Cancel = new Button( "Cancel" );
		Cancel.setCancelButton( true );

		OK.setOnAction( e -> {

			int i = 0;
			for( Node node : gp.getChildren() ) {
				if( GridPane.getColumnIndex( node ) == 1 ) {
					if( node instanceof TextField ) {
						TextField tf = (TextField)node;
						updateSet.after( i++, tf.getText() );
					}
				}
			}

			if( updateSet.isAltered() ) {
				update.apply( updateSet );
			}

			updateSet.clear();
			this.close();	// close() fires ACTION event.
		});

		Cancel.setOnAction( e -> {
			this.close();
		});

		HBox buttons = new HBox();
		buttons.setSpacing( 10 );
		buttons.setAlignment( Pos.CENTER_RIGHT );
		buttons.getChildren().addAll( OK, Cancel );

		VBox layout = new VBox( 20 );
		VBox vspacer = new VBox( 20 );	// add more vertical space to fully show buttons
		layout.getChildren().addAll( gp, buttons, vspacer );
		layout.setPadding( new Insets( 5 ) );

		Scene scene = new Scene( layout );
		this.setScene( scene );

		if( focus != null ) {
			focus.requestFocus();
		}
	}
}
