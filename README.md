# se2-Application, v2.0
Demo application of the Software Engineering 2 course.


SE-Application has a <b>modular structure</b> that is comprised of components that follow certain software engineering principles.

 <b>Components</b> are basic building blocks of an application that represent a coarser level of abstraction than classes. Components often consist of multiple parts such as parts for performing the logic (e.g. transactions or calculations), parts that create views of a GUI (e.g. a web-GUI or a JavaFX-GUI) and parts that configure and control other parts.
It has become good software engineering practice to decouple interfaces from implementations, including user interfaces (views) from performing functions (logic). This principle is called <b>separation of concerns</b> and is an essential software engineering method to introduce structure and clarity in software systems.

In this code base, <b>"Views"</b> are used to implement GUI parts of an application component and <b>"Logic"</b> is referred to implementing functionality.

The first application component is a <b>Tax Calculator</b>. Its view-part is responsible for displaying the key panel, receiving key/mouse input and displaying calculation results. The Tax Calculator's

* GUI-component is comprised of files from the fxgui-package: _Calculator.fxml_, _Calculator.css_, _CalculatorFXMLController.java_.

* Actual calculations are performed in the logic-part located in the logic-package: _CalculatorLogic.java_.

Both parts are implemented in separate Java packages and classes. Both are connected by interfaces (<b>ViewIntf</b> and <b>LogicIntf</b>) that connect both parts to each other.

<b>Interfaces</b> consist of <i>Interface definitions</i> (public Java Intf-interfaces) and <i>references to instances</i> (Java objects) that implement interfaces. Those are often instances of private (non-public) Impl-classes of an interface. Instances often exist as <i>singletons</i> meaning that only one instance of a class exists and distributed (<i>"wired"</i>) to other parts that need to access them by <i>injecting their references</i>. "Wiring" is a complex <i>configuration</i> task.

In this code base, component interfaces are defined in a "component"-class located in the components-package. _components.Calculator.java_ contains interface definitions for the Tax Calculator:

  * _Calculator.ViewIntf_ - implemented by CalculatorFXMLController.java in the GUI and

  * _Calculator.LogicIntf_ - implemented by CalculatorLogic.java. Both parts have no other connections, dependencies, imports, references other than 
  those two interfaces.

<b>Wiring</b> is the process of connecting instances that implement certain interfaces to component parts that need to access interfaces. An interface cannot be accessed with no implementation (instance) behind. Hence, the reference to an instance that implements an interface must be bound ("wired") into the component where it is accessed. It is important that only the interface definition is known in the code where the interface is used. The implementation class of the interface should be hidden (non-public) to the code making sure that the interface is the only dependency and no other "import" exists other than the interface.

Wiring can be performed by explicitly injecting a reference into a component by invoking a method called _inject( Intf ref );_ during its configuration. Frameworks such as _Spring_ perform wiring automatically.

<b>Lifecycle</b>. Since components are comprised of multiple parts, they can be complex and their "lifecycle" needs to be carefully designed and implemented. Lifecycle operations span from creation (instantiation) and configuration to start, operation, shut down and destruction - all performed in an explicitly controlled, orderly manner. Lifecycle operations are important for components and should explicitly be defined by lifecycle interfaces that include operations such as create(), startup(), shutdown() and destroy().

This leads to the need for other components that are not concerned with performing application logic or the GUI, but with the "managing" and "operating" the other components by executing their lifecycle operations. Those components include:

* _Configurators_ as special components that provide configuration information (_AppConfigurator.java_ is an example).

* _Builders_ are special components that build (create, configure, wire) other components.

* _Runners_ are special components that execute (start, stop) other components.

Most of these components exist throughout the lifetime of the application as singleton instances. Since they control other components, they must be created first and also shut down last.
