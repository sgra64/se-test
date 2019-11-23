package com.application.se2.fxgui;

import static com.application.se2.AppConfigurator.LoggerTopics;

import java.util.ArrayList;

import com.application.se2.components.ComponentIntf.CRUDLogicIntf;
import com.application.se2.misc.EntityProperty;
import com.application.se2.misc.Logger;
import com.application.se2.misc.StringProperty;
import com.application.se2.misc.Traceable;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


class TableViewPopUp extends Stage {
	private static Logger logger = Logger.getInstance( TableViewPopUp.class );

	private final Scene scene;
	private final TableView<TableItem> fxTableView_TableView;

	private final int rowIdx;
	private final int colIdx;
	private final TableItem parentTableItem;
	private final EntityProperty parentProperty;
	private final CRUDLogicIntf logicIntf;
	private final TableViewImpl fxTableView;


	public TableViewPopUp(
			final int rowIdx,
			final int colIdx,
			final TableItem parentTableItem,
			final EntityProperty parentProperty,
			final CRUDLogicIntf logicIntf,
			final TableViewConfig creatingViewConfig,
			final SimpleListProperty<?> values,
			final String styleId,
			final ObservableList<String> styleSheets
	) {
		this.rowIdx = rowIdx;
		this.colIdx = colIdx;
		this.parentTableItem = parentTableItem;
		this.parentProperty = parentProperty;
		this.logicIntf = logicIntf;

		String label = creatingViewConfig.getLabel();
		this.setTitle( label );
		this.setWidth( 600 );
		this.setHeight( 462 );
		this.setMinHeight( 200 );
		this.setMaxHeight( 462 );

		fxTableView_TableView = new TableView<TableItem>();

		fxTableView_TableView.getSelectionModel().setCellSelectionEnabled( false );
		fxTableView_TableView.getSelectionModel().setSelectionMode( SelectionMode.SINGLE );
		fxTableView_TableView.setEditable( true );

		fxTableView_TableView.setRowFactory( tv -> {
			TableRow<TableItem> row = new TableRow<>();
			row.setOnMouseClicked( event -> {
				if( event.getClickCount() == 2 && ( ! row.isEmpty() ) ) {
					fxTableView_Update();
				}
			});
			return row;
		});

		this.fxTableView = new TableViewImpl( creatingViewConfig, fxTableView_TableView, logicIntf );
		ObservableList<TableItem> items = FXCollections.observableArrayList( new ArrayList<TableItem>() );
		fxTableView_TableView.setItems( items );
		refreshView( values );

		VBox layout = new VBox( 2 );

		HBox footer = new HBox();
		Button btn1 = new Button( "NEW" );

		btn1.setOnMouseClicked( event -> {
			fxTableView_New( values );
		});

		btn1.setStyle( "-fx-pref-width: 44px; -fx-pref-height: 20px;" );// -fx-padding: 0 8 0 8;" );
		footer.setStyle( "-fx-padding: 0 2 2 1;" );		/* top, right, bottom, left */
		footer.getChildren().addAll( btn1 );

		layout.getChildren().add( fxTableView_TableView );
		layout.getChildren().add( footer );

		fxTableView_TableView.getStylesheets().addAll( styleSheets );

		fxTableView_TableView.setId( styleId );
		logger.log( LoggerTopics.CSSLoaded, "TableViewPopUp css-setId( " + styleId + " )" );

		this.scene = new Scene( layout );
		this.setScene( this.scene );
	}

	public boolean match( final int rowIdx, final int colIdx ) {
		return this.rowIdx==rowIdx && this.colIdx==colIdx;
	}

	public void refreshView( final SimpleListProperty<?> propertyList ) {
		ObservableList<?> items = (ObservableList<?>) propertyList.get();
		fxTableView_TableView.getItems().clear();
		for( int i=0; i < items.size(); i++ ) {
			Object item = items.get( i );
			if( item instanceof String ) {
				item = new StringProperty( (String)item );
			}
			Traceable po = new Traceable( item, parentTableItem.getPrimaryObject(), parentProperty );
			TableItem ti = new TableItem( po, fxTableView );
			fxTableView_TableView.getItems().add( ti );
		}
	}


	/*
	 * Private methods.
	 */

	private void fxTableView_New( SimpleListProperty<?> values ) {
		// 1. fetch new instance (primarily ID)
		// 2. PopUp update dialog
		// 3. save new instance with filled in properties
		Object e = parentProperty.isCollectionType()? logicIntf.createPart( parentProperty ) : logicIntf.create();
		if( e != null ) {
			Traceable po = new Traceable( e, parentTableItem.getPrimaryObject(), parentProperty );
			TableItem ti = new TableItem( po, fxTableView );
			TableViewUpdateForm dialog = new TableViewUpdateForm( ti, fxTableView, updateSet -> {
				logicIntf.update( updateSet );

			});
			dialog.show();
		}
	}

	private void fxTableView_Update() {
		TableItem ti = fxTableView_TableView.getSelectionModel().getSelectedItem();
		if( ti != null ) {
			TableViewUpdateForm dialog = new TableViewUpdateForm( ti, fxTableView, updateSet -> {

				logicIntf.update( updateSet );
			});
			dialog.show();

		} else {
			logger.log( LoggerTopics.Info, "Nothing selected." );
		}
	}

}
