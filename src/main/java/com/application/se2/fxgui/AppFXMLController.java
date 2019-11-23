package com.application.se2.fxgui;

import java.util.Optional;

import com.application.se2.components.AppComp;
import com.application.se2.components.ComponentBase;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;


public class AppFXMLController implements AppComp.ViewIntf, FXControllerIntf {

	private Optional<AppComp> component = Optional.empty();


	@FXML
	private TabPane fxApp_TabsPane;

	@FXML
	private Tab fxApp_MainTab;


	@Override
	public void inject( ComponentBase component ) {
		if( component != null && component instanceof AppComp ) {
			this.component = Optional.of( (AppComp)component );
		}
	}

	@Override
	public void inject( final Tab parentTab ) {
	}

	@FXML
	private void exitButton() {
		exit( "Exit by FXAppController.exitButton()" );
	}

	public TabPane getTabPane() {
		return fxApp_TabsPane;
	}

	@Override
	public void exit( final String msg ) {
		component.ifPresent( component -> {
			ComponentBase.<AppComp.LogicIntf>logicIntf( component, logic -> {
				logic.exit( msg );
			});
		});
	}

	@Override
	public AnchorPane getAnchorPane() {
		return null;
	}

	@Override
	public void start() {
	}

}
