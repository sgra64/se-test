package com.application.se2.fxgui;

import java.util.Optional;

import com.application.se2.components.CalculatorComp;
import com.application.se2.components.ComponentBase;
import com.application.se2.components.CalculatorComp.Token;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;


public class CalculatorFXMLController implements CalculatorComp.ViewIntf, FXControllerIntf {

	private Optional<CalculatorComp> component = Optional.empty();

	private Optional<Tab> parentTab = Optional.empty();


	private final static Object[][] ShortCutKeys = new Object[][] {
		{ "\r",	Token.K_EQ		},
		{ "\b",	Token.K_BACK	},
		{ "c",	Token.K_C		},
		{ "e",	Token.K_CE		},
		{ ".",	Token.K_DOT		},
		{ "m",	Token.K_VAT		},
		{ "t",	Token.K_1000	},
	};

	@FXML
	public AnchorPane anchorPane;

	@FXML
	private TextField displayTextField;

	@FXML
	private TextArea sideTextArea;

	@FXML
	private GridPane keypadGridPane;


	@Override
	public void writeDisplay(String text) {
		displayTextField.setText( text );
	}

	@Override
	public void writeSideArea(String text) {
		sideTextArea.setText( text );
	}

	@Override
	public void inject( ComponentBase component ) {
		if( component != null && component instanceof CalculatorComp ) {
			this.component = Optional.of( (CalculatorComp)component );
		}
	}

	@Override
	public void inject( final Tab parentTab ) {
		this.parentTab = Optional.of( parentTab );
	}

	@Override
	public AnchorPane getAnchorPane() {
		return anchorPane;
	}

	@Override
	public void start() {
		parentTab.ifPresent( parentTab -> {
			TabPane tabsPane = parentTab.getTabPane();

			// add KEY_TYPED event handler when Calculator tab is selected, and remove if unselected
			tabsPane.getSelectionModel().selectedItemProperty().addListener( ( ov, oldTab, newTab ) -> {
				if( parentTab.getId().equals( newTab.getId() ) ) {
					//pane.setOnKeyTyped( e -> {
					//});
					tabsPane.addEventHandler( KeyEvent.KEY_TYPED, KEY_TYPED_EventHandler );

				} else {
					if( parentTab.getId().equals( oldTab.getId() ) ) {
						tabsPane.removeEventHandler( KeyEvent.KEY_TYPED, KEY_TYPED_EventHandler );
					}
				}
			});

			int keyPadCols = 4;	// -- Java 9: keypadGridPane.getColumnCount();
			int i = 0;
			for( Node n : keypadGridPane.getChildren() ) {
				Button btn = (Button)n;

				btn.setOnMousePressed( ( e ) -> {
					Button bt = (Button)e.getSource();
					int row = GridPane.getRowIndex( bt );
					int col = GridPane.getColumnIndex( bt );
					int idx = row * keyPadCols + col;	// flatten grid coordinates to idx[0..n]

					component.ifPresent( component -> {
						ComponentBase.<CalculatorComp.LogicIntf>logicIntf( component, logic -> {
							logic.nextToken( Token.values()[ idx ] );
						});
					});
				});

				// button has focus after valid KeyPress-event, button then also receives KeyRelease
				// to release focus (and remove border highlighting from button)
				btn.setOnKeyReleased( ( e ) -> {
					anchorPane.requestFocus();
				});

				btn.setText( CalculatorComp.KeyLabels[ i++ ] );
			}

			// regain focus of anchorPane element to receive key events.
			anchorPane.addEventFilter( MouseEvent.ANY, (e) -> anchorPane.requestFocus() );

			displayTextField.setEditable( false );
			sideTextArea.setEditable( false );
		});
	}


	/*
	 * Private methods.
	 */

	private EventHandler<KeyEvent> KEY_TYPED_EventHandler = new EventHandler<KeyEvent>() {
		public void handle( KeyEvent e ) {
			String s = e.getCharacter();	// e.getCode() -> case UP, DOWN, SHIFT...
			int idx = -1;
			for( int j=0; idx < 0 && j < CalculatorComp.KeyLabels.length; j++ ) {
				if( CalculatorComp.KeyLabels[ j ].equals( s ) ) {
					idx = j;
				}
			}
			if( idx < 0 ) {
				for( Object[] sc : ShortCutKeys ) {
					if( s.equals( sc[0] ) ) {
						idx = ((Token)sc[1]).ordinal();
						break;
					}
				}
			}
			if( idx >= 0 ) {
				Button btn = (Button)keypadGridPane.getChildren().get( idx );
				btn.requestFocus();	// mimic mouse pressed highlighting
				btn.fireEvent(
					new MouseEvent(MouseEvent.MOUSE_PRESSED,
						btn.getLayoutX(), btn.getLayoutY(),
						btn.getLayoutX(), btn.getLayoutY(), MouseButton.PRIMARY, 1,
						true, true, true, true, true, true, true, true, true, true, null
				));
			}
		}
	};

}
