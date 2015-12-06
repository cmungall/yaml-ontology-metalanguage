package io.yom.generator;

import static org.junit.Assert.*;

import java.io.IOException;

import io.yom.parser.IOHelper;
import io.yom.parser.IOHelperTest;

import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;

public class QuotedEntityCheckerTest {

	/**
	 * Test wrapping and escaping names.
	 */
	@Test
	public void testEscaping() {
		assertEquals("testone", QuotedEntityChecker.wrap("testone"));
		assertEquals("'test one'", QuotedEntityChecker.wrap("test one"));
		assertEquals("5-bromo-2\\'-deoxyuridine",
				QuotedEntityChecker.wrap("5-bromo-2'-deoxyuridine"));
	}

	@Test
	public void test() throws IOException {
		IOHelper ioh = IOHelperTest.getTestIOHelper();
		String path = "/test-ontology.owl";
		OWLOntology ontology = ioh.loadOntology(this.getClass().getResourceAsStream(path));
		OWLDataFactory df = ontology.getOWLOntologyManager().getOWLDataFactory();
		
        QuotedEntityChecker checker = new QuotedEntityChecker();
        checker.addProperty(df.getRDFSLabel());
        checker.addAll(ontology);
        checker.useIOHelper(ioh);
        
        String base = "http://purl.obolibrary.org/obo/";
        IRI iri = IRI.create(base + "FOO_1");
        OWLClass cls = df.getOWLClass(iri);
        assertEquals(cls, checker.getOWLClass("foo1"));
        assertEquals(cls, checker.getOWLClass("'foo1'"));
        assertEquals(cls, checker.getOWLClass("FOO:1"));
        assertEquals(cls, checker.getOWLClass(":FOO_1"));


	}

}
