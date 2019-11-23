package com.application.se2;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * Entry unit testing class defining test suite.
 * See for details:
 * - https://stackoverflow.com/questions/43192046/test-suite-run-spring-boot-once
 * 
 * @author sgra64
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestCases_ApplicationName.class,		//test cases
	//TestCases_ApplicationLogic.class		//test cases ...
})
public class ApplicationTest {

}
