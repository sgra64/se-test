package com.application.se2.fxgui;

import java.util.List;

import static com.application.se2.AppConfigurator.LoggerTopics;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

import com.application.se2.components.ComponentBase;
import com.application.se2.components.ComponentIntf;
import com.application.se2.components.ComponentBase.Key;
import com.application.se2.misc.Logger;
import com.application.se2.misc.Traceable;
import com.application.se2.model.Entity;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;


public class TableViewFXMLController implements ComponentIntf.TableViewIntf, FXControllerIntf {
	private static Logger logger = Logger.getInstance( TableViewFXMLController.class );

	private Optional<CRUDLogicIntf> logic = Optional.empty();

	private Optional<TableViewImpl> fxTableView = Optional.empty();


	@FXML
	public AnchorPane fxTableView_AnchorPane;

	@FXML
	public GridPane fxTableView_GridPane;

	/*
	 * TableView<S>
	 * S - The type of the objects contained within the TableView items list.
	 */
	@FXML
	public TableView<TableItem> fxTableView_TableView;

	/*
	 * TableColumn<S,T>
	 * S - The type of the TableView generic type (i.e. S == TableView<S>)
	 * T - The type of the content in all cells in this TableColumn.
	 */
	@FXML
	private HBox fxTableView_HBox;	// Bottom area container for buttons, search box, etc.


	@FXML
	public void fxTableView_New() {
		// 1. fetch new instance (primarily ID)
		// 2. PopUp update dialog
		// 3. save new instance with filled in properties
		openUpdateDialog( logic -> {
			Object e = logic.create();
			TableItem ti = new TableItem( new Traceable( e ), fxTableView.get() );
			TableViewUpdateForm dialog = new TableViewUpdateForm( ti, ti.getFXTableView(), updateSet -> {
				logic.update( updateSet );
			});
			return dialog;
		});
	}

	@FXML
	public void fxTableView_Update() {
		// 1. PopUp update dialog for the (first) selected instance
		// 2. save instance, if altered
		openUpdateDialog( logic -> {
			TableItem ti = fxTableView_TableView.getSelectionModel().getSelectedItem();
			TableViewUpdateForm dialog = null;
			if( ti != null ) {
				dialog = new TableViewUpdateForm( ti, ti.getFXTableView(), updateSet -> {
					logic.update( updateSet );
				});

			} else {
				// logger.log( LoggerTopics.Info, "Nothing selected." );
			}
			return dialog;
		});
	}

	@FXML
	void fxTableView_Delete() {
		logic.ifPresent( logic -> {
			List<String> ids = new ArrayList<String>();
			ObservableList<TableItem> selection = fxTableView_TableView.getSelectionModel().getSelectedItems();
			if( selection.size() > 0 ) {
				for( TableItem ti : selection ) {
					Object obj = ti.getPrimaryObject().getRootObject();
					String id = obj instanceof Entity? ((Entity)obj).getId() : String.valueOf( obj.hashCode() );
					ids.add( id );
				}
				fxTableView_TableView.getSelectionModel().clearSelection();

			} else {
				// logger.log( LoggerTopics.Info, "Nothing selected." );
			}
			logic.delete( ids );
		});
	}

	@FXML
	public void fxTableView_Exit() {
		logic.ifPresent( logic -> {
			logic.exit( "TableView Exit Button pressed." );
		});
	}

	@Override
	public void inject( ComponentBase component ) {
		Object logic = component.get( Key.Logic );
		if( logic != null && logic instanceof CRUDLogicIntf ) {
			this.logic = Optional.of( (CRUDLogicIntf)logic );
		}
		TableViewConfig tableViewConfig = new TableViewConfig( component );
		TableViewImpl fxTableView = new TableViewImpl( tableViewConfig, fxTableView_TableView, this.logic.isPresent()? this.logic.get() : null );
		this.fxTableView = Optional.of( fxTableView );
	}

	@Override
	public void inject( Tab parentTab ) {
	}

	@Override
	public AnchorPane getAnchorPane() {
		return fxTableView_AnchorPane;
	}

	@Override
	public void start() {
		fxTableView_AnchorPane.getStyleClass().add( "tableview-anchorpane" );
		fxTableView_GridPane.getStyleClass().add( "tableview-gridpane" );
		fxTableView_TableView.getStyleClass().add( "tableview" );
		fxTableView_HBox.getStyleClass().add( "hbox" );

		fxTableView_TableView.prefWidthProperty().bind( fxTableView_AnchorPane.widthProperty().subtract( 0 ) );
		fxTableView_TableView.prefHeightProperty().bind( fxTableView_AnchorPane.heightProperty().subtract( 0 ) );

		fxTableView_GridPane.prefWidthProperty().bind( fxTableView_AnchorPane.widthProperty().subtract( 0 ) );
		fxTableView_GridPane.prefHeightProperty().bind( fxTableView_AnchorPane.heightProperty().subtract( 29 ) );

		/*
		 * Define selection model that allows to select multiple rows.
		 */
		fxTableView_TableView.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );

		/*
		 * Allow horizontal column squeeze of TableView columns. Column width can be fixed
		 * with -fx-pref-width: 80px;
		 */
		//	fxTableView_TableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );

		/*
		 * Double-click on row: open update dialog.
		 */
		fxTableView_TableView.setRowFactory( tv -> {
			TableRow<TableItem> row = new TableRow<>();
			row.setOnMouseClicked( event -> {
				if( event.getClickCount() == 2 && ( ! row.isEmpty() ) ) {
					fxTableView_Update();
				}
			});
			return row;
		});

		ObservableList<TableItem> items = FXCollections.observableArrayList();
		fxTableView_TableView.setItems( items );
		refreshView();
	}

	@Override
	public void refreshView() {
		ObservableList<TableItem> items = fxTableView_TableView.getItems();

		items.clear();
		logic.ifPresent( logic -> {
			fxTableView.ifPresent( fxTableView -> {
				for( Entity e : logic.findAll( "*", 1000 ) ) {
					TableItem ti = new TableItem( new Traceable( e ), fxTableView );
					items.add( ti );

					logger.log( LoggerTopics.RepositoryLoaded, null, e );
				}
			});
		});

		fxTableView_TableView.refresh();
	}


	/*
	 * Private methods.
	 */

	private void openUpdateDialog( Function<CRUDLogicIntf,TableViewUpdateForm> f ) {
		logic.ifPresent( logic -> {
			fxTableView.ifPresent( fxTableView -> {
				TableViewUpdateForm dialog = f.apply( logic );
				if( dialog != null ) {
					dialog.show();
				}
			});
		});
	}

}
