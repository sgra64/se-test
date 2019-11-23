package com.application.se2;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Unit tests concerning the Application's name.
 * 
 * @author sgra64
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={com.application.se2.Application.class})
public class TestCases_ApplicationName {

	@Autowired
	private Application application;	// auto-wired reference to Application instance


	@Test
	public void applicationNameTest() {
		String name = application.getName();
		assertTrue( "se2-application".equals( name ) );
	}

	@Test
	public void applicationNameLengthTest() {
		String name = application.getName();
		assertTrue( name != null && name.length() == 15 );
	}

	@Test
	public void applicationNameStartsWithSE2Test() {
		String name = application.getName();
		assertTrue( name != null && name.startsWith( "se2" ) );
	}

}
