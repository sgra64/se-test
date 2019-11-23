package com.application.se2;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;

import com.application.se2.components.BuilderIntf;
import com.application.se2.components.ComponentBase;
import com.application.se2.components.ComponentIntf;
import com.application.se2.components.RunnerIntf;
import com.application.se2.fxgui.FXInterface;
import com.application.se2.misc.Logger;
import com.application.se2.repository.RepositoryBuilder;

import static com.application.se2.AppConfigurator.LoggerTopics;


/**
 * SpringBoot's main Application class containing Java's main() method with invocation
 * and launch of Spring's run time.
 *
 * @author sgra64
 *
 */
@SpringBootApplication	// same as @Configuration @EnableAutoConfiguration @ComponentScan
public class Application {
	private static Logger logger = Logger.getInstance( Application.class );

	/*
	 * Using Spring's auto-wiring to create Singleton RepositoryBuilder instance
	 * and "wire" its reference to all with @Autowired annotated variables of type
	 * RepositoryBuilder.
	 */
	@Autowired
	private RepositoryBuilder repositoryBuilder;


	@Autowired
	private ApplicationContext applicationContext;


	/**
	 * Protected constructor for Spring Boot to create an Application instance.
	 * Application instances must be created by Spring, never by "new".
	 * @param args arguments passed from main()
	 */
	Application( String[] args ) {
		System.out.println( "2. Hello SpringApplication, Constructor called." );
	}


	/**
	 * Entry point after Spring Boot has initialized the application instance.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		System.out.println( "3. Hello SpringApplication, doSomethingAfterStartup() called." );

		/*
		 * Check whether Applications runs normal from main() (with GUI)
		 * or as Unit-Test launch (no GUI).
		 */
		boolean isRunningTest = true;
		try {
			Class.forName("org.junit.Test");
		} catch (ClassNotFoundException e) {
			isRunningTest = false;
		}

		launchAfterSpringRunTime( isRunningTest );
	}


	/**
	 * Java's main() method.
	 *
	 * @param args arguments passed from invoking command.
	 */
	public static void main( final String ... args ) {
		System.out.println( "1. Hello SpringApplication!" );
		System.out.print( "   Initialize Spring Boot and create Application instance." );

		// Initialize Spring Boot and create Application instance.
		@SuppressWarnings("unused")
		ApplicationContext applicationContext = SpringApplication.run( Application.class, args );

	};


	/**
	 * Private method to launch Application after Spring's run-time has been initialized.
	 *
	 * @param isRunningAsTest indicates whether Applications runs normal from main();
	 * or as Unit-Test launch.
	 */
	private void launchAfterSpringRunTime( boolean isRunningAsTest ) {

		CountDownLatch waitForExit = new CountDownLatch(
			isRunningAsTest? 0 : 2		// Test runs have no GUI, hence no need to wait before exit.
		);

		/*
		 * RepositoryBuilder is a Spring @Component that builds repositories.
		 *
		 * Using Spring's auto-wiring to obtain reference to RepositoryBuilder component.
		 */
		//final RepositoryBuilder repositoryBuilder = RepositoryBuilder.getInstance();

		/*
		 * AppBuilder builds applications components and returns a startable Runner instance.
		 */
		final AppBuilder appBuilder = AppBuilder.getInstance();
		appBuilder.inject( repositoryBuilder );

		final RunnerIntf appRunner = appBuilder.build();

		/*
		 * Use (hidden) JavaFX Builder to build the JavaFX GUI also returning a startable FXRunner instance.
		 */
		BuilderIntf fxBuilder = FXInterface.getBuilder();
		Optional<FXInterface.FXRunnerIntf> gui =
			isRunningAsTest?
				Optional.empty() :
				Optional.of( (FXInterface.FXRunnerIntf)fxBuilder.build() );

		ComponentBase appComponent = appBuilder.getComponent( 0 );	// first component
		String appName = appComponent.getName();

		/*
		 * Start AppRunner instance.
		 */
		appRunner.start(

			onStart -> {
				logger.log( LoggerTopics.Info, appName + " starting..." );

				repositoryBuilder.startup();

				/*
				 * During AppRunner-start, the JavaFX GUI is started. The JavaFX GUI launches
				 * with its own thread(s) such that invoking main thread will run through fast
				 * and needs to wait for application exit at the waitForExit latch at the end
				 * before leaving the main function terminating the JVM process.
				 */
				gui.ifPresent( gui2 -> {

					gui2.start(

						onGUIStart -> {
							logger.log( LoggerTopics.Startup, "JavaFX-GUI." );

							/*
							 * Build JavaFX GUI elements (Tabs) for each application component.
							 */
							appBuilder.iterateComponents( component -> {
								gui2.injectView( component );	// inject component into JavaFX GUI.
								ComponentBase.<ComponentIntf.LogicIntf>logicIntf( component, logic -> {
									logic.startup();	// start component logic after linked with GUI.
								});
							});
						},

						onGUIExit -> {
							appRunner.exit( onGUIExit );		// Propagate exit from GUI to AppRunner instance.
						},

						onGUIError -> {
							appRunner.error( onGUIError );	// Propagate GUI error to AppRunner instance.
						}
					);

					gui2.show( 2 );	// Select i-th Tab as visible.
				});

			},

			/*
			 * Called to exit AppRunner instance, e.g. from gui.onGUIExit: app.exit(msg).
			 */
			onExit -> {
				System.out.println( onExit + "\n" + "shutting down components..." );
				AppBuilder.getInstance().iterateComponentsReverseOrder( component -> {
					ComponentBase.<ComponentIntf.LogicIntf>logicIntf( component, logic -> {
						logic.shutdown();
					});
				});

				gui.ifPresent( gui2 -> {
					gui2.exitPlatform( onExit2 -> {
						logger.log( LoggerTopics.Shutdown, "JavaFX-GUI." );
						waitForExit.countDown();	// unblock main-thread to leave main()
					});
				});

				waitForExit.countDown();	// unblock main-thread to leave main()

			},

			/*
			 * Called to report error during AppRunner execution.
			 */
			onError -> {
				logger.log( LoggerTopics.Error, Application.class.getName() + ".( " + onError + " )." );
				appRunner.exit( onError );
			}
		);

		/*
		 * Make main-thread wait for AppRunner's onExit before leaving the main() function
		 * terminating the JVM process.
		 */
		try {
			logger.log( LoggerTopics.Info, appName + " running..." );
			waitForExit.await();

			repositoryBuilder.shutdown();

		} catch( InterruptedException e ) {

		} finally {
			logger.log( LoggerTopics.Info, appName + " exited." );
		}

		System.out.println( "4. Bye, SpringApplication, " + applicationContext.getId() + "!" );
	}


	/**
	 * Return the Application name.
	 *
	 * @return Application name.
	 */
	public String getName() {
		return "se2-application";
	}

}
