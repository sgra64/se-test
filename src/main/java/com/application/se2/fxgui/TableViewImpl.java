package com.application.se2.fxgui;

import static com.application.se2.AppConfigurator.LoggerTopics;

import java.util.HashSet;
import java.util.Set;

import com.application.se2.AppConfigurator.Table.Column;
import com.application.se2.components.ComponentIntf.CRUDLogicIntf;
import com.application.se2.misc.EntityProperty;
import com.application.se2.misc.Logger;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;


class TableViewImpl {
	private static Logger logger = Logger.getInstance( TableViewImpl.class );

	private final TableViewConfig tableViewConfig;
	private final Set<TableViewPopUp> openPopUps = new HashSet<TableViewPopUp>();


	@SuppressWarnings({ "unchecked", "rawtypes" })
	TableViewImpl( final TableViewConfig config, final TableView<TableItem> fxTableView_TableView, final CRUDLogicIntf logicIntf ) {
		this.tableViewConfig = config;
		int columns = tableViewConfig.getProperties().size();
		fxTableView_TableView.setId( tableViewConfig.getCssId() );	// set .css-#id for TableView
		logger.log( LoggerTopics.CSSLoaded, "TableView css-setId( " + tableViewConfig.getCssId() + " )");

		for( int i = 0; i < columns; i++ ) {
			EntityProperty prop = tableViewConfig.getProperties().getProperty( i );	//config.getPropertyConfig( i );
			TableColumnProperty tableColProp = new TableColumnProperty( config, prop );

			TableColumn<TableItem,String> tableCol = new TableColumn<>();
			fxTableView_TableView.getColumns().add( tableCol );
			tableCol.setText( tableColProp.getLabel() );
			tableCol.setId( tableColProp.getCssId() );		// set .css-#id for table column
			logger.log( LoggerTopics.CSSLoaded, "TableCol css-setId( " + tableColProp.getCssId() + " )" );

			//if( ep.style != null && ep.style.length() > 0 ) {
			//	tableCol.getStyleClass().add( ep.style_cls );
			//}
			//tableCol.prefWidthProperty().bind( scene.widthProperty().divide(3).subtract(2.1/3));
			//tableCol.maxWidthProperty().bind( tableCol.prefWidthProperty() );
			//tableCol.prefWidthProperty().set( ep.maxWidth );
			//tableCol.prefWidthProperty().set( (ep.minWidth + ep.maxWidth) / 2 );
			//tableCol.setStyle( "-fx-padding: 0 0 0 8;" );
			//tableCol.setResizable( ep.minWidth < ep.maxWidth );

			tableCol.prefWidthProperty().bind( fxTableView_TableView.widthProperty().multiply( 0.25 ) );

			tableCol.minWidthProperty().set( tableColProp.getMinColumnWidth() );
			tableCol.maxWidthProperty().set( tableColProp.getMaxColumnWidth() );

			int colIdx = i;
			TableViewConfig popUpConfig = tableColProp.getPopUpTableViewConfig();

			if( popUpConfig == null ) {

				tableCol.setCellValueFactory( cellData -> {
					// TableColumn.CellDataFeatures<S,T> cellData
					// https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TableColumn.CellDataFeatures.html
					TableItem fxTableItem = cellData.getValue();

					javafx.beans.property.Property ri = fxTableItem.getProperty( colIdx );
					return ri;
				});

			} else {

				tableCol.setCellFactory( column -> {
					// TableColumn<S,T> column
					// https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TableColumn.html
					TableCell<TableItem, String> tCell2 = new TableCell<TableItem, String>() {

						@Override
						protected void updateItem( String item, boolean empty ) {
							super.updateItem( item, empty );
							int rowIdx = getIndex();
							ObservableList<TableItem> items = this.getTableView().getItems();

							if( rowIdx >= 0 && rowIdx < items.size() ) {
								TableItem fxTableItem = items.get( rowIdx );

								final SimpleListProperty<?> propertyList = (SimpleListProperty<?>)fxTableItem.getProperty( colIdx );

								// update all popups on update events
								for( TableViewPopUp p : openPopUps ) {
									if( p.match( rowIdx, colIdx ) ) {
										p.refreshView( propertyList );
									}
								}

								Button btn = null;
								//btn.getStyleClass().add( "tableview-customer" + "-column-button" );
								String button_label = (String)prop.getConfig( Column.POPUP_BUTTON_LABEL );
								int listSize = propertyList.getSize();
								Object no = prop.getConfig( Column.POPUP_BUTTON_SHOW_AS_TEXT );
								int n = no != null? (int)no : 0;
								int nListSize = listSize;
								String showDirect = "";
								if( n >= 0 ) {
									nListSize = n > 0? Math.max( nListSize - n, 0 ) : listSize;
									showDirect = getFirstNElementsAsString( propertyList, n );
								}

								int showDirectLimitLength = 26;
								if( nListSize > 0 || n == -1 ) {
									btn = new Button();
									showDirectLimitLength -= 6;
								}

								if( btn != null ) {
									btn.setText( button_label != null?
										button_label.replaceAll( "#", String.valueOf( nListSize ) )
											: tableColProp.getLabel() + ": +" + n );

									btn.setOnMouseClicked( event -> {
										TableViewPopUp popupList = null;
										// Collect style sheets (.css files) that have been loaded and attached to parent nodes
										// to make them available in unattached popups.
										ObservableList<String> styleSheets = FXCollections.observableArrayList();
										for( Parent p = fxTableView_TableView; p != null; p = p.getParent() ) {
											styleSheets.addAll( p.getStylesheets() );
											if( p instanceof AnchorPane )
												break;
										}

										String styleId = tableColProp.getCssId();

										popupList = new TableViewPopUp(
											rowIdx, colIdx, fxTableItem, prop, logicIntf, popUpConfig, propertyList, styleId, styleSheets
										);

										openPopUps.add( popupList );	// add to set of open popups (no duplicates)
										popupList.show();

										popupList.setOnCloseRequest( evt3 -> {
											for( TableViewPopUp p : openPopUps ) {
												p.close();
											}
											openPopUps.clear();
										});

										if( popupList != null ) {
											popupList.requestFocus();
										}
									});
								}

								HBox hBox = new HBox();
								//hBox.setStyle( "-fx-padding: 0 0 4 0;" );	/* top, right, bottom, left */
								//hBox.setAlignment( Pos.CENTER );

								if( showDirect != null ) {
									// limit String length
									showDirect = showDirect.substring( 0, Math.min( showDirect.length(), showDirectLimitLength) );

									Label asText = new Label( showDirect );
									asText.setStyle( "-fx-padding: 0 4 0 0;" );	/* top, right, bottom, left */
									hBox.getChildren().add( asText );
								}

								if( btn != null ) {
									hBox.getChildren().add( btn );
								}

								setGraphic( hBox );
							}
						}
					};

					return tCell2;
				});
			}
		}
	}

	public TableViewConfig getTableViewConfig() {
		return tableViewConfig;
	}


	/*
	 * Private methods.
	 */

	private String getFirstNElementsAsString( SimpleListProperty<?> propertyList, int n ) {
		StringBuffer sBuffer = new StringBuffer();
		int size = Math.max( 0, Math.min( n, propertyList.getSize() ) );
		for( int i=0; i < size; i++ ) {
			if( i > 0 ) {
				sBuffer.append( ", " );
			}
			sBuffer.append( propertyList.get( i ) );
		}
		return sBuffer.toString();
	}

}
