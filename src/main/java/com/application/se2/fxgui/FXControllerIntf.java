package com.application.se2.fxgui;

import com.application.se2.components.ComponentBase;

import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;


/**
 * Internal interface for FXML controllers.
 *
 */
interface FXControllerIntf {

	public void inject( final ComponentBase component );

	public void inject( final Tab parentTab );

	public AnchorPane getAnchorPane();

	public void start();

}
