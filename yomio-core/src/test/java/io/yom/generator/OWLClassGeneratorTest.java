package io.yom.generator;

import static org.junit.Assert.*;

import java.io.IOException;

import io.yom.metamodel.MetaClass;
import io.yom.metamodel.MetaClassTest;
import io.yom.metamodel.MetaObjectTest;
import io.yom.ontmodel.OntObject;
import io.yom.ontmodel.OntObjectTest;
import io.yom.parser.IOHelper;
import io.yom.parser.IOHelperTest;
import io.yom.parser.MetaObjectParserTest;
import io.yom.parser.OntObjectParserTest;

import org.junit.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests a simple generation from MetaClass example.
 * A single class FOO:new is created, using two 'primitive' classes FOO:1 and FOO:2
 * 
 * This module also simultaneously tests java-based and yaml-based ways of loading
 * modules:
 * 
 *  - creating MetaClass objects via java vs parsing from a pattern yaml file
 *  - creating filler OntObjects via java vs parsing from a pattern yaml file
 * 
 * @author cjm
 *
 */
public class OWLClassGeneratorTest {
	
	/**
	 * Logger.
	 */
	private static final Logger logger =
			LoggerFactory.getLogger(OWLClassGeneratorTest.class);

	@Test
	public void testUsingHardwiredMetaClass() throws Exception {
		OntObject obj = OntObjectTest.getTestOntClassObject();
		MetaClass mc = MetaClassTest.getTestMetaClass();
		generateAndValidateOWLClass(mc, obj);
	}
	
	@Test
	public void testUsingParsedMetaClass() throws Exception {
		OntObject obj = OntObjectTest.getTestOntClassObject();
		MetaClass mc = MetaObjectParserTest.getTestMetaClassFromFile();
		generateAndValidateOWLClass(mc, obj);
	}

	@Test
	public void testUsingParsedFillers() throws Exception {
		OntObject obj = OntObjectParserTest.getTestFillerOntClassObjectFromFile();
		MetaClass mc = MetaObjectParserTest.getTestMetaClassFromFile();
		generateAndValidateOWLClass(mc, obj);
	}

	public void generateAndValidateOWLClass(MetaClass mc, OntObject obj) throws Exception {

		IOHelper ioh = IOHelperTest.getTestIOHelper();
		String path = "/test-ontology.owl";
		OWLOntology ontology = ioh.loadOntology(this.getClass().getResourceAsStream(path));
		OWLClassGenerator gen = new OWLClassGenerator(ontology);
		gen.setIOHelper(ioh);
		
		IRI iri = gen.generateIRI(mc, obj);
		//System.out.println(iri.toString());
		//gen.generateLabel(iri, mc, obj);
		OWLClass c = gen.generate(mc, obj);
		iri = ioh.createIRI("FOO:new");
		assertEquals(c, gen.getOWLDataFactory().getOWLClass(iri));
		
		boolean okEquiv = false;
		for (OWLLogicalAxiom ax : ontology.getAxioms(c)) {
			logger.info(ax.toString());
			// WARNING: FRAGILE
			if (ax.toString().equals(
					"EquivalentClasses(<http://purl.obolibrary.org/obo/FOO_new> ObjectIntersectionOf(<http://purl.obolibrary.org/obo/FOO_1> ObjectSomeValuesFrom(<http://purl.obolibrary.org/obo/BFO_0000050> <http://purl.obolibrary.org/obo/FOO_2>)) )")) {
				okEquiv = true;
			}

		}
		assertTrue("wrong equivalence (but the test may be prone to false positives)", okEquiv);
		
		for (OWLAnnotation ann : c.getAnnotations(ontology)) {
			logger.info(ann.toString());
		}
		String label = gen.getLabel(c.getIRI());
		assertEquals("foo1 of foo2", label);
		//System.out.println(label);		
		
		// GCIs
		for (OWLSubClassOfAxiom ax : ontology.getAxioms(AxiomType.SUBCLASS_OF)) {
			if (ax.getSubClass().isAnonymous()) {
				logger.info("GCI: "+ax.toString());
			}
		}
	}
}
