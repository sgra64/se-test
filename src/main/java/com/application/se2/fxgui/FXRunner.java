package com.application.se2.fxgui;

import static com.application.se2.AppConfigurator.LoggerTopics;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import com.application.se2.AppConfigurator;
import com.application.se2.components.ComponentBase;
import com.application.se2.components.ComponentIntf;
import com.application.se2.misc.Callback;
import com.application.se2.misc.Logger;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/**
 * JavaFX/GUI Runner component as local component singleton class.
 * 
 * @author sgra64
 *
 */
class FXRunner extends javafx.application.Application implements FXInterface.FXRunnerIntf {
	private static Logger logger = Logger.getInstance( FXRunner.class );
	private static FXRunner instance = null;

	private Optional<Callback<Integer>> onStartCallback = Optional.empty();
	private Optional<Callback<String>> onExitCallback = Optional.empty();
	private Optional<Callback<String>> onErrorCallback = Optional.empty();

	private final List<ComponentBase>launchables = new ArrayList<ComponentBase>();
	private boolean error = false;
	private Optional<Stage> stage = Optional.empty();

	private Parent root = null;
	private TabPane tabsPane = null;


	/**
	 * Private constructor according to singleton pattern.
	 */
	private FXRunner() { }

	/**
	 * Access method to singleton instance created when first called.
	 * @return reference to singleton runner instance.
	 */
	public static FXRunner getInstance() {
		if( FXRunner.instance == null ) {
			FXRunner.instance = new FXRunner();
		}
		return FXRunner.instance;
	}


	/**
	 * Component startup code called when the system is starting up.
	 */
	@Override
	public void startup() {
		logger.log( LoggerTopics.Startup, this.getClass().getName() );
	}

	/**
	 * Component shutdown code called when the system is shutting down.
	 */
	@Override
	public void shutdown() {
		logger.log( LoggerTopics.Shutdown, this.getClass().getName() );
	}

	/**
	 * RunnerIntf startup method with onStart, onExit, onError hooks called when FXRunner instance is started.
	 * @param onStart callback called at the end of this start method
	 * @param onExit callback called when exit(msg) method is called on AppRunner instance
	 * @param onError callback called when error(msg) method is called on AppRunner instance
	 * @return true
	 */
	@Override	// from application.components.RunnableIntf
	public void start( final Callback<Integer> onStart, final Callback<String> onExit, final Callback<String> onError ) {
		this.onStartCallback = Optional.of( onStart );
		this.onExitCallback = Optional.of( onExit );
		this.onErrorCallback = Optional.of( onError );

		CountDownLatch waitForFXGUIReady = new CountDownLatch( 1 );

		/*
		 * FXBuilder instance must be created by (within) FX-Platform thread.
		 */
		Platform.startup( () -> {

			Stage stage = new Stage();
			this.stage = Optional.of( stage );

			/*
			 * Invoke public start()-method inherited from Application.java.
			 * This method would directly be called when Application would be
			 * started as JavaFX-application.
			 */
			this.stage.ifPresent( stage2 -> {
				start( stage2 );
			});

			waitForFXGUIReady.countDown();
		});

		/*
		 * Invoking thread of getInstance() should not leave before FXBuilder-instance
		 * has been created by FX-Platform thread.
		 */

		try {
			waitForFXGUIReady.await();
			//error = true;

		} catch( InterruptedException e ) {

			onErrorCallback.ifPresent( errorCode -> {
				errorCode.apply( e.getMessage() );
			});
		}

		if( error && onErrorCallback.isPresent() ) {
			onErrorCallback.get().apply( "error caused in " + FXRunner.class.getSimpleName() + ".start()" );
		}

		this.onStartCallback.ifPresent( onStart2 -> {
			onStart2.apply( 0 );
		});

	}

	/**
	 * RunnerIntf method called to end FXRunner instance and exit through calling onExit callback
	 * @param msg exit message
	 */
	@Override
	public void exit( final String msg ) {
		this.onExitCallback.ifPresent( onExit -> {
			onExit.apply( msg );
		});
	}

	/**
	 * FXRunnerIntf method called to end FXRunner instance and exit through
	 * calling onExit callback after closing FX stage.
	 * @param onExit callback called when exit(msg) method is called on AppRunner instance
	 */
	@Override
	public void exitPlatform( final Callback<String> onExit ) {
		Platform.runLater( ()-> {

			stage.ifPresent( stage -> {
				stage.close();
			});

			Platform.exit();	// exits FX-platform
			onExit.apply( "FX-Platform exited." );	// callback still executed by FX-platform
		});
	}

	/**
	 * RunnerIntf method called to report an error to AppRunner instance
	 * @param msg exit message
	 */
	@Override
	public void error( final String msg ) {
		this.onErrorCallback.ifPresent( onError -> {
			onError.apply( msg );
		});
	}

	/**
	 * Method inherited from javafx.application.Application called from RunnerIntf
	 * start( onStart, onExit, onError ) method.
	 * @param stage javafx.stage.Stage as part of JavaFX GUI
	 */
	@Override
	public void start( final Stage stage ) {
		Scene scene = stage.getScene();
		root = scene==null? null : scene.getRoot();

		stage.setTitle( "App Name" );

		stage.setOnCloseRequest( e -> {
			if( e.getEventType() == WindowEvent.WINDOW_CLOSE_REQUEST ) {
				error = true;
				exit( "Exit by WINDOW_CLOSE_REQUEST" );	// callback to Application.start( Function<Integer, String> exitCode )
			}
		});

		/* Traditional way to set up an event handler:
		stage.setOnCloseRequest( (EventHandler<WindowEvent>) new EventHandler<WindowEvent>() {
		    @Override
		    public void handle( WindowEvent event ) {
		    	if( event.getEventType() == WindowEvent.WINDOW_CLOSE_REQUEST ) {
					stop();
				}
		    }
		});	*/
	}

	/**
	 * FXRunnerIntf method called on FXRunner instance.
	 * @param selectTab index of Tab to be shown after startup 
	 */
	@Override
	public void show( int selectTab ) {
		stage.ifPresent( stage -> {
			Platform.runLater( ()-> {
				for( ComponentBase component : launchables ) {
					launch( stage, component );
				}
				launchables.clear();
				tabsPane.getSelectionModel().select( selectTab );

				stage.setMaxWidth(  1400.0 );
				stage.setMinWidth(   520.0 );
				stage.setMaxHeight( 1600.0 );
				stage.setMinHeight(  420.0 );

				stage.show();
			});
		});
	}

	/**
	 * FXRunnerIntf method to inject application components into FXBuilder for
	 * which Views/Tabs will be created on the GUI.
	 * @param component application component for which a View/Tab will be created on the GUI
	 */
	@Override
	public void injectView( final ComponentBase component ) {
		launchables.add( component );
	}


	/*
	 * Private Methods.
	 */

	private void launch( final Stage stage, final ComponentBase component ) {
		Scene scene = stage.getScene();
		root = scene==null? null : scene.getRoot();
		String fxmlRes = null;

		try {
			String label = (String)component.get( AppConfigurator.Key.Label );
			fxmlRes = (String)component.get( AppConfigurator.Key.Descriptor );
			URL url = AppFXMLController.class.getResource( fxmlRes );
			FXMLLoader loader = new FXMLLoader( url );
			Pane pane = loader.load();

			if( tabsPane == null ) {
				root = pane;
				scene = new Scene( pane );
				stage.setScene( scene );
				String title = (String)component.get( ComponentBase.Key.Name );
				stage.setTitle( title == null? label : title );
			}

			Object fxmlControllerRaw = loader.getController();

			if( fxmlControllerRaw instanceof ComponentIntf.ViewIntf ) {
				/* @@ dependency injection point */
				component.inject( (ComponentIntf.ViewIntf)fxmlControllerRaw );
			}

			if( fxmlControllerRaw instanceof FXControllerIntf ) {
				FXControllerIntf fxmlController = (FXControllerIntf)fxmlControllerRaw;
				/* @@ dependency injection point */
				fxmlController.inject( component );

				if( fxmlControllerRaw instanceof AppFXMLController ) {
					AppFXMLController fxmlAppController = (AppFXMLController)fxmlControllerRaw;
					tabsPane = fxmlAppController.getTabPane();
					if( tabsPane.getTabs().size() > 0 ) {
						Tab tab = tabsPane.getTabs().get( 0 );
						tab.setText( label );
					}

					loadCss( root, component, fxmlRes );

				} else {

					AnchorPane fxAnchorPane = fxmlController.getAnchorPane();
					Tab tab = new Tab( label, fxAnchorPane );
					tab.setId( label );
					tabsPane.getTabs().add( tab );
					fxmlController.inject( tab );

					fxAnchorPane.prefWidthProperty().bind( scene.widthProperty().subtract( 0 ) );
					fxAnchorPane.prefHeightProperty().bind( scene.heightProperty().subtract( 0 ) );

					loadCss( fxAnchorPane, component, fxmlRes );

				}

				fxmlController.start();
			}

		} catch( IOException e ) {
			fxmlRes = null;
			onErrorCallback.ifPresent( onError -> {
				onError.apply( "IOException loading resource. " + e.getMessage() );
			});
		}
	}

	private void loadCss( Parent attachNode, final ComponentBase component, String fxmlRes ) {//, Function<String,Boolean> attachNode ) {
		URL cssUrl = null;
		String cssResource = (String)component.get( AppConfigurator.Table.CSSRESOURCE );
		if( cssResource != null ) {
			cssUrl = AppFXMLController.class.getResource( cssResource );
		} else {
			cssUrl = fxmlRes != null? AppFXMLController.class.getResource( fxmlRes.replace( ".fxml", ".css" ) ) : null;
		}
		if( cssUrl != null ) {
			attachNode.getStylesheets().add( cssUrl.toExternalForm() );
			logger.log( LoggerTopics.CSSLoaded, cssUrl.toExternalForm() );
		}
	}

}
