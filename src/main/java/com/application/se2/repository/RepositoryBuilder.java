package com.application.se2.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.application.se2.components.BuilderIntf;
import com.application.se2.model.Article;
import com.application.se2.model.Customer;
import com.application.se2.model.Entity;
import com.application.se2.model.Customer.Status;


/**
 * RepositoryBuilder builds repositories and provides a runner instance for repositories.
 * Repositories can be provided with initial mock data.
 *
 * After build, individual repositories are then  managed and accessed through
 * the repository runner.
 *
 * The main method of the RepositoryBuilder is:
 *  - public RunnerIntf build();
 *
 * that returns an instance with a RunnerIntf through which a repository can be
 * launched.
 *
 * @author sgra64
 *
 */


/*
 * @Component annotation causes Spring to create singleton instance of RepositoryBuilder
 * and auto-wire its reference to all variables of that class annotated with @Autowired.
 *
 * (see use in class com.application.se2.Application.java:
 *		@Autowired
 *		private RepositoryBuilder repositoryBuilder;
 */
@Component
public class RepositoryBuilder implements BuilderIntf {

	/*
	 * Optional of RepositoryRunner instance, if repository could successfully
	 * be created and initialized.
	 */
	private Optional<RepositoryRunner>repositoryRunner;

	/*
	 * How to access a value defined in the application.properties file in Spring Boot:
	 * https://stackoverflow.com/questions/30528255/how-to-access-a-value-defined-in-the-application-properties-file-in-spring-boot
	 *
	 * @Value( "${serialization.datapath}" )
	 * private String path;
	 */
	@Autowired
	private Environment env;


	/**
	 * Private constructor according to the Singleton pattern.
	 * This constructor is invoked by Spring for @Component instantiation.
	 *
	 */
	private RepositoryBuilder() {
		this.repositoryRunner = Optional.empty();
	}


	/*
	 * Using Spring's auto-wiring to create Singleton RepositoryBuilder instance
	 * and "wire" its reference to all with @Autowired annotated variables of type
	 * RepositoryBuilder replaces the need for getInstance() { ... };
	 */
	/**
	 * Access method to singleton instance created when first called.
	 * @return reference to singleton builder instance.
	 * /

	private static RepositoryBuilder _singletonInstance = null;

	public static RepositoryBuilder getInstance() {
		if( _singletonInstance == null ) {
			_singletonInstance = new RepositoryBuilder();
		}
	}
	*/

	/**
	 * Repository-build code returning a repository Runner instance.
	 *
	 * @return runner instance.
	 */
	@Override
	public RepositoryRunner build() {
		HashMap<String, RepositoryIntf<?>> repositoryMap = new HashMap<String,RepositoryIntf<?>>();

		//List<Customer>customerList = buildCustomerData_phase1();
		final List<Customer>customerList = new ArrayList<Customer>();
		final SerializationProviderIntf CustomerSerializationProvider = getSerializationProvider( Customer.class );

		final RepositoryIntf<Customer> customerRepository
			//= new SimpleRepositoryImpl<Customer>( customerList );
			= new SerializableRepositoryImpl<Customer>( customerList, CustomerSerializationProvider );

		repositoryMap.put( Customer.class.getName(), customerRepository );

		if( customerRepository.count() == 0 ) {
			customerRepository.saveAll( buildCustomerData_phase1() );
			buildCustomerData_phase2( customerRepository );
		}

		//List<Article>articleList = buildArticleData();
		final List<Article>articleList = new ArrayList<Article>();
		final SerializationProviderIntf ArticleSerializationProvider = getSerializationProvider( Article.class );

		final RepositoryIntf<Article> articleRepository
			//= new SimpleRepositoryImpl<Article>( articleList );
			= new SerializableRepositoryImpl<Article>( articleList, ArticleSerializationProvider );

		repositoryMap.put( Article.class.getName(), articleRepository );

		if( articleRepository.count() == 0 ) {
			articleRepository.saveAll( buildArticleData() );
		}

		RepositoryRunner repositoryRunner = new RepositoryRunner( repositoryMap );

		this.repositoryRunner = Optional.of( repositoryRunner );

		return repositoryRunner;
	}


	/**
	 * Component startup code called when the system is starting up.
	 */
	@Override
	public void startup() {
		repositoryRunner.ifPresent( repositoryRunner -> {
			repositoryRunner.startup();
		});
	}


	/**
	 * Component shutdown code called when the system is shutting down.
	 */
	@Override
	public void shutdown() {
		repositoryRunner.ifPresent( repositoryRunner -> {
			repositoryRunner.shutdown();
		});
	}


	/*
	 * Private methods.
	 */

	/**
	 * Create initial Customer data set.
	 *
	 * @return list container into which entities have been inserted.
	 */
	private List<Customer> buildCustomerData_phase1() {

		List<Customer> list = new ArrayList<Customer>();

		Customer c = new Customer( "Dr. Margarethe Boese" )
			.addContact( "drmb@yahoo.de" )
			.addContact( "home: 030 8266-5204" )
			.addContact( "work: 030 4504-2528" )
			.addContact( "cell: +49 170 82568462" )
			.setAddress( "Lindenweg 86, 12167 Berlin-Steglitz" )
			.setStatus( Customer.Status.SUSP )
			.addNote( "Zahlt Rechnung verspaetet." )
			.addNote( "Beschwert sich ueber Mitarbeiter." )
			.addNote( "Greift Angestellte verbal an." )
			.addNote( "Wurde aus dem Geschaeft verwiesen. Ein Zutrittsverbot wurde ausgesprochen." );
		list.add( c );

		list.add( new Customer( "Matteo Schwarz" ).setAddress( "Grossweg 4, 79805 Aschaffenburg" ).addContact( "matteo.schwarz@gmail.com" ) );
		list.add( new Customer( "Paul Neumann" ).setAddress( "Engelbert-Noack-Gasse 3, 16665 Parsberg" ).addContact( "paul.neumann@gmail.com" ) );
		list.add( new Customer( "Tom Wolf" ).setAddress( "Starkplatz 8, 79663 Wolfratshausen" ).addContact( "tom.wolf@yahoo.de" ) );
		list.add( new Customer( "Mila Sauer" ).setAddress( "Nicole-Weidner-Platz 4, 15616 Gelnhausen" ).addContact( "mila.sauer@yahoo.de" ) );
		list.add( new Customer( "Clara Richter" ).setAddress( "Ehlersplatz 59, 59965 Einbeck" ).addContact( "clara.richter@yahoo.de" ) );
		list.add( new Customer( "Henri Vogt" ).setAddress( "Kirschallee 21, 82493 Helmstedt" ).addContact( "henri.vogt@gmail.com" ) );
		list.add( new Customer( "Emily Beck" ).setAddress( "Silvio-Brand-Gasse 4/6, 54260 Hagenow" ).addContact( "emily.beck@gmail.com" ) );
		list.add( new Customer( "Tom Winter" ).setAddress( "Luzia-Geisler-Gasse 74, 33489 Soltau-Fallingbostel" ).addContact( "tom.winter@gmail.com" ) );
		list.add( new Customer( "Emilia Hartmann" ).setAddress( "Sanderring 5/2, 28072 Donaueschingen" ).addContact( "emilia.hartmann@gmx.de" ) );
/*
		list.add( new Customer( "Greta Roth" ).setAddress( "Bartschallee 999, 94748 Mallersdorf" ).addContact( "greta.roth@gmx.de" ) );
		list.add( new Customer( "Mathilda Becker" ).setAddress( "Kretschmergasse 95, 92935 Moers" ).addContact( "mathilda.becker@gmx.de" ) );
		list.add( new Customer( "Paula Keller" ).setAddress( "Heilallee 720, 66426 Melsungen" ).addContact( "paula.keller@gmx.de" ) );
		list.add( new Customer( "Rafael Schneider" ).setAddress( "Mina-Heine-Ring 9/9, 12096 Rochlitz" ).addContact( "rafael.schneider@gmx.de" ) );
		list.add( new Customer( "Mia Sommer" ).setAddress( "Lindemannplatz 6/6, 32868 Arnstadt" ).addContact( "mia.sommer@yahoo.de" ) );
		list.add( new Customer( "Karl Lang" ).setAddress( "Kloseweg 227, 91147 Wolfach" ).addContact( "karl.lang@yahoo.de" ) );
		list.add( new Customer( "Helena Horn" ).setAddress( "Stollplatz 1, 17700 Ravensburg" ).addContact( "helena.horn@yahoo.de" ) );
		list.add( new Customer( "Ella Wagner" ).setAddress( "Heinz-Dieter-Krebs-Weg 32, 82151 Grevenbroich" ).addContact( "ella.wagner@gmail.com" ) );
		list.add( new Customer( "Niklas Frank" ).setAddress( "Heinzeplatz 4, 40605 Bernburg" ).addContact( "niklas.frank@gmx.de" ) );
		list.add( new Customer( "Sophia Peters" ).setAddress( "Beierallee 1, 95612 Oschatz" ).addContact( "sophia.peters@gmx.de" ) );
		list.add( new Customer( "Emily Meier" ).setAddress( "Bernhardtallee 771, 73996 Grevenbroich" ).addContact( "emily.meier@gmail.com" ) );
		list.add( new Customer( "Lucas Berger" ).setAddress( "Rotheplatz 65, 93179 Bad Liebenwerda" ).addContact( "lucas.berger@gmail.com" ) );
		list.add( new Customer( "Linus Ziegler" ).setAddress( "Nora-Held-Platz 9, 16125 Grimma" ).addContact( "linus.ziegler@yahoo.de" ) );
		list.add( new Customer( "Klara Braun" ).setAddress( "Heinemannring 251, 45535 Starnberg" ).addContact( "klara.braun@gmx.de" ) );
		list.add( new Customer( "Marah Pfeiffer" ).setAddress( "Krollallee 997, 67917 Vilsbiburg" ).addContact( "marah.pfeiffer@yahoo.de" ) );
		list.add( new Customer( "Lotta Richter" ).setAddress( "Torsten-Stein-Gasse 61, 17594 Burglengenfeld" ).addContact( "lotta.richter@gmx.de" ) );
		list.add( new Customer( "Felix Schuster" ).setAddress( "Peterstr. 183, 89709 Niesky" ).addContact( "felix.schuster@gmx.de" ) );
		list.add( new Customer( "Luca Voigt" ).setAddress( "Schrammring 65, 77006 Eggenfelden" ).addContact( "luca.voigt@yahoo.de" ) );
		list.add( new Customer( "Maximilian Huber" ).setAddress( "Heinemannplatz 98, 23111 Parchim" ).addContact( "maximilian.huber@yahoo.de" ) );
		list.add( new Customer( "Marah Keller" ).setAddress( "Fabian-Link-Platz 72, 61964 Haldensleben" ).addContact( "marah.keller@gmail.com" ) );
		list.add( new Customer( "Oskar Braun" ).setAddress( "Fiedlerring 42, 88859 Stade" ).addContact( "oskar.braun@yahoo.de" ) );
		list.add( new Customer( "Emily Stein" ).setAddress( "Wilhelmgasse 00, 99405 Brilon" ).addContact( "emily.stein@gmx.de" ) );
		list.add( new Customer( "Simon Seidel" ).setAddress( "Ines-Schreiner-Platz 5/0, 89852 Monschau" ).addContact( "simon.seidel@yahoo.de" ) );
		list.add( new Customer( "Luisa Lehmann" ).setAddress( "Lemkeplatz 97, 63499 Donaueschingen" ).addContact( "luisa.lehmann@yahoo.de" ) );
		list.add( new Customer( "Lya Busch" ).setAddress( "Heinrichstr. 38, 45142 Bamberg" ).addContact( "lya.busch@gmail.com" ) );
		list.add( new Customer( "Tim Schmidt" ).setAddress( "Annette-Schrader-Platz 091, 64060 Parchim" ).addContact( "tim.schmidt@gmx.de" ) );
		list.add( new Customer( "Till Schmid" ).setAddress( "Ibrahim-Ernst-Gasse 0/3, 43037 Norden" ).addContact( "till.schmid@gmail.com" ) );
		list.add( new Customer( "Lukas Martin" ).setAddress( "Fuhrmannring 52, 53979 Darmstadt" ).addContact( "lukas.martin@gmx.de" ) );
		list.add( new Customer( "Linus Schumacher" ).setAddress( "Karola-Kraft-Gasse 8, 38500 Berchtesgaden" ).addContact( "linus.schumacher@gmx.de" ) );
		list.add( new Customer( "Anna Richter" ).setAddress( "Annelies-Heller-Gasse 7, 78227 Anklam" ).addContact( "anna.richter@yahoo.de" ) );
		list.add( new Customer( "Alina Huber" ).setAddress( "Ullrichweg 7/2, 80700 Schrobenhausen" ).addContact( "alina.huber@gmail.com" ) );
		list.add( new Customer( "Henri Schumacher" ).setAddress( "Rosmarie-Reich-Platz 62, 47893 Luckau" ).addContact( "henri.schumacher@gmx.de" ) );
*/
		return list;
	}

	private void buildCustomerData_phase2( RepositoryIntf<Customer> customerRepository ) {

		for( Customer c2 : customerRepository.findByName( ".* S.*", Long.MAX_VALUE ) ) {
			System.out.println( " --found--> " + c2.getName() );
			c2.setStatus( Status.TERM );
			c2.addNote( "Kunde wurde terminiert." );
		}

		customerRepository.findByName( "Matteo" ).ifPresent( c2 -> {
			c2.addContact( "matteo@yahoo.com" ).addContact( "max88@gmail.com" ).addContact( "030 3849-5039" ).addContact( "+49 170 9369224" )
				.addNote( "Kunde moechte Rechnung per Post erhalten." )
				.addNote( "Kunde hat Rechnung bezahlt." );
		});

		customerRepository.findByName( "Tom Wolf" ).ifPresent( c2 -> {
			c2.addContact( "majortom@gmail.com" )
				.addContact( "+491582341346" );
		});

		customerRepository.findByName( "Emilia Hartmann" ).ifPresent( c2 -> {
			c2.addContact( "majortom@gmail.com" )
				.addContact( "+491582341346" )
				.setStatus( Customer.Status.SUSP )
				.addNote( "Kunde hat Rechnung nicht bezahlt." )
				.addNote( "Erste Mahnung." )
				.addNote( "Zweite Mahnung." );
		});

		customerRepository.findByName( "Emily Meier" ).ifPresent( c2 -> {
			c2.addContact( "eme@yahoo.com" )
				.addContact( "meyer244@gmail.com" )
				.addContact( "+49170482395" )
				.setStatus( Customer.Status.SUSP );
		});

		customerRepository.saveAll( customerRepository.findAll() );
	}


	/**
	 * Create initial Article data set.
	 *
	 * @return list container into which entities have been inserted.
	 */
	private List<Article> buildArticleData() {

		List<Article> list = new ArrayList<Article>();

		list.add( new Article( "Canon Objektiv EF 50mm f/1.2L USM", "1.549,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 50mm f/1.4 USM", "449,00 EUR" ) );

		list.add( new Article( "Canon Objektiv EF 40mm f/2.8 STM", "239,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 50mm f/1.8 STM", "139,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 24-70mm f/4L IS USM", "929,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 24-105mm f/4L IS II USM", "1.199,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 24-70mm f/2.8L II USM", "2.019,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF-S 18-55mm f/4-5.6 IS STM", "249,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF-S 18-55mm f/3.5-5.6 IS II", "199,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF-S 18-55mm f/3.5-5.6 IS STM", "249,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 24-105mm f/3.5-5.6 IS STM", "479,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF-S 18-135mm f/3.5-5.6 IS STM + EW 73B + LC Kit", "499,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF-S 18-200mm f/3.5-5.6 IS ", "585,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF-S 18-135mm f/3.5-5.6 IS STM", "499,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF-S 15-85mm f/3.5-5.6 IS USM", "799,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF-S 18-135mm f/3.5-5.6 IS USM", "549,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF-S 17-55mm f/2.8 IS USM", "919,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 400mm f/4 DO IS II USM", "7.029,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 800mm f/5.6L IS USM ", "14.149,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 300mm f/4L IS USM", "1.469,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 200mm f/2.8L II USM ", "829,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 400mm f/5.6L USM", "1.449,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 135mm f/2L USM ", "1.109,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 85mm f/1.8 USM", "479,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 100mm f/2 USM ", "529,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 200mm f/2L IS USM", "6.309,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 85mm f/1.2L II USM", "2.239,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 300mm f/2.8L IS II USM", "6.499,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 400mm f/2.8L IS II USM", "11.019,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 85mm f/1.4L IS USM", "1.599,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 500mm f/4L IS II USM", "9.979,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 600mm f/4L IS II USM", "12.639,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 70-300mm f/4-5.6 IS II USM", "539,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 70-200mm f/4L IS USM", "1.409,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 28-300mm f/3.5-5.6L IS USM", "2.659,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 100-400mm f/4.5-5.6L IS II USM ", "2.379,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 75-300mm f/4-5.6 III USM", "369,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 75-300mm f/4-5.6 III", "299,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 70-300mm f/4-5.6L IS USM", "1.429,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF-S 55-250mm f/4-5.6 IS STM + ET 63 + LC Kit", "349,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF-S 55-250mm f/4-5.6 IS STM", "349,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 70-200mm f/2.8L IS II USM", "2.299,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 70-200mm f/4L USM", "689,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 70-200mm f/2.8L USM", "1.579,00 EUR" ) );
		list.add( new Article( "Canon Objektiv EF 200-400mm f/4L IS USM + Extender 1.4x", "11.699,00 EUR" ) );

		return list;
	}


	private SerializationProviderIntf getSerializationProvider( Class<? extends Entity> clazz ) {
		SerializationProviderIntf serializationProvider = null;
		String format = env.getProperty( "serialization.format" );
		String path = env.getProperty( "serialization.datapath" );
		if( path == null ) {
			path = "";
		}
		if( format != null ) {
			format = format.toLowerCase().trim();
			// found Java-Serialization configuration
			if( format.contains( "java" ) ) {
				serializationProvider = new JavaSerializationProviderImpl( path, clazz );
			} else {
				// found Json-Serialization configuration
				if( format.contains( "json" ) ) {
					serializationProvider = new JsonSerializationProviderImpl( path, clazz );
				}
			}
		}
		if( serializationProvider == null ) {
			serializationProvider = new NullSerializationProviderImpl( path, clazz );
		}
		return serializationProvider;
	}

}
