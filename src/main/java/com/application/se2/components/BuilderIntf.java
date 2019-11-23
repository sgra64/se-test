package com.application.se2.components;

import com.application.se2.components.ComponentIntf.LogicIntf;


/**
 * Public interface of a Builder component.
 * 
 * @author sgra64
 */
public interface BuilderIntf extends LogicIntf {

	/**
	 * Executes build process.
	 * @return built and runnable instance.
	 */
	public RunnerIntf build();

}
