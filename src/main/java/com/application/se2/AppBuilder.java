package com.application.se2;

import static com.application.se2.AppConfigurator.LoggerTopics;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.application.se2.components.AppComp;
import com.application.se2.components.ArticleCatalogComp;
import com.application.se2.components.BuilderIntf;
import com.application.se2.components.CalculatorComp;
import com.application.se2.components.ComponentBase;
import com.application.se2.components.CustomerManagerComp;
import com.application.se2.components.RunnerIntf;
import com.application.se2.logic.ArticleCatalog;
import com.application.se2.logic.CalculatorLogic;
import com.application.se2.logic.CustomerManager;
import com.application.se2.misc.Callback;
import com.application.se2.misc.Logger;
import com.application.se2.model.Article;
import com.application.se2.model.Customer;
import com.application.se2.repository.RepositoryBuilder;
import com.application.se2.repository.RepositoryRunner;


/**
 * AppBuilder is a local singleton class. Its purpose is to build (create, configure)
 * application components, but not yet started.
 * The main method is:
 *  - public RunnerIntf build();
 * 
 * that returns an instance with a RunnerIntf through which application components can be
 * launched.
 * 
 * @author sgra64
 *
 */
class AppBuilder implements BuilderIntf {
	private static final Logger logger = Logger.getInstance( AppBuilder.class );

	private static AppBuilder instance = null;

	private final List<ComponentBase> appComponents;

	private Optional<RepositoryBuilder> repositoryBuilder;


	/**
	 * Private constructor according to the singleton pattern.
	 */
	private AppBuilder() {
		this.appComponents = new ArrayList<ComponentBase>();
		this.repositoryBuilder = Optional.empty();
	}


	/**
	 * Access method to singleton instance created when first called.
	 * 
	 * @return reference to singleton builder instance.
	 */
	public static AppBuilder getInstance() {
		if( instance == null ) {
			instance = new AppBuilder();
		}
		return instance;
	}


	/**
	 * Inject reference to RepositoryBuilder.
	 * @param repositoryBuilder reference to singleton RepositoryBuilder instance.
	 */
	public void inject( RepositoryBuilder repositoryBuilder ) {
		this.repositoryBuilder = Optional.of( repositoryBuilder );
	}


	/**
	 * Build code returning a runner instance.
	 * 
	 * @return runner instance.
	 */
	@Override
	public RunnerIntf build() {

		final AppConfigurator appConfigurator = AppConfigurator.getInstance();

		final AppComp app = (AppComp)new AppComp()
			.configure( ComponentBase.Key.Name, "SE-Application" )
			.configure( AppConfigurator.Key.TableView, appConfigurator.AppView() )
			;

		final CalculatorComp calculator = (CalculatorComp)new CalculatorComp()
			.configure( AppConfigurator.Key.TableView, appConfigurator.CalculatorView() )
			;

		final CustomerManagerComp customerManager = (CustomerManagerComp) new CustomerManagerComp()
			.configure( AppConfigurator.Key.TableView, appConfigurator.CustomerTableView_1() )
			;

		final ArticleCatalogComp articleCatalog = (ArticleCatalogComp) new ArticleCatalogComp()
			.configure( AppConfigurator.Key.TableView, appConfigurator.ArticleCatalogTableView_1() )
			;

		appComponents.add( app );
		appComponents.add( calculator );
		appComponents.add( customerManager );
		appComponents.add( articleCatalog );

		repositoryBuilder.ifPresent( repositoryBuilder -> {

			final RepositoryRunner repositoryRunner = repositoryBuilder.build();

			// "wire" customer repository into customerManager.
			repositoryRunner.<Customer>getRepository( Customer.class ).ifPresent( customerRepository -> {
				customerManager.inject( customerRepository );
			});

			repositoryRunner.<Article>getRepository( Article.class ).ifPresent( articleRepository -> {
				articleCatalog.inject( articleRepository );
			});
		});

		final AppRunner appRunner = new AppRunner( this, app );
		app.inject( appRunner );

		final CalculatorLogic calculatorLogic = new CalculatorLogic( calculator );
		calculator.inject( calculatorLogic );

		final CustomerManager customerManagerLogic = new CustomerManager( customerManager, appRunner );
		customerManager.inject( customerManagerLogic );

		final ArticleCatalog articleCatalogManager = new ArticleCatalog( articleCatalog, appRunner );
		articleCatalog.inject( articleCatalogManager );

		return appRunner;
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
	 * AppBuilder manages a list of components. Method returns i-th component or null.
	 * 
	 * @param i index in component list
	 * @return i-th component of i is in the range of the list or null otherwise
	 */
	public ComponentBase getComponent( final int i ) {
		return i >= 0 && i < appComponents.size()? appComponents.get( i ) : null;
	}

	/**
	 * Iterates over component list calling the iterator callback for each component.
	 * 
	 * @param it callback called for each component
	 */
	public void iterateComponents( final Callback<ComponentBase> it ) {
		iterateComponents( false, it );
	}

	/**
	 * Iterates over component list calling the iterator callback in reverse order.
	 * 
	 * @param it callback called for each component in reverse order.
	 */
	public void iterateComponentsReverseOrder( final Callback<ComponentBase> it ) {
		iterateComponents( true, it );
	}


	/*
	 * Private methods.
	 */

	private void iterateComponents( final boolean reverse, final Callback<ComponentBase> it ) {
		int i = reverse? appComponents.size() : -1;
		int inc = reverse? -1 : 1;
		for( int counter = 0; counter < appComponents.size(); counter++ ) {
			ComponentBase c = appComponents.get( i += inc );
			it.apply( c );
		}
	}

}
