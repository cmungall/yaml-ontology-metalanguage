package io.yom.parser;

import static org.junit.Assert.*;

import org.junit.Test;

public class IOHelperTest {

	@Test
	public void test() {
		IOHelper ioh = getTestIOHelper();
		assertEquals(ioh.createIRI("FOO:1").toString(), "http://purl.obolibrary.org/obo/FOO_1");
		assertEquals(ioh.createIRI("GO:1").toString(), "http://purl.obolibrary.org/obo/GO_1");
		assertEquals(ioh.createIRI(":BAR_1").toString(), "http://purl.obolibrary.org/obo/BAR_1");
		assertEquals(ioh.createIRI("http://purl.obolibrary.org/obo/BAR_1").toString(), "http://purl.obolibrary.org/obo/BAR_1");
		assertEquals(ioh.createIRI("wibble").toString(), "http://example.org/wibble");
		//assertEquals(ioh.createIRI("BAR_1").toString(), "http://purl.obolibrary.org/obo/BAR_1");
	}

	public static IOHelper getTestIOHelper() {
	    IOHelper ioh = new IOHelper();
	    ioh.addPrefix("FOO", "http://purl.obolibrary.org/obo/FOO_");
	    ioh.addPrefix("", "http://purl.obolibrary.org/obo/");
	    ioh.addPrefix("wibble", "http://example.org/wibble");
	    //ioh.getContext().put("@base", "http://purl.obolibrary.org/obo/");
	    return ioh;
	
	}

}
