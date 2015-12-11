package io.yom.generator;

import static org.junit.Assert.*;

import java.io.IOException;

import io.yom.metamodel.MetaClass;
import io.yom.metamodel.MetaClassTest;
import io.yom.metamodel.MetaIndividual;
import io.yom.metamodel.MetaIndividualTest;
import io.yom.metamodel.MetaObjectTest;
import io.yom.ontmodel.OntObject;
import io.yom.ontmodel.OntObjectTest;
import io.yom.parser.IOHelper;
import io.yom.parser.IOHelperTest;
import io.yom.parser.MetaObjectParserTest;

import org.junit.Test;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OWLIndividualGeneratorTest {

	/**
	 * Logger.
	 */
	private static final Logger logger =
			LoggerFactory.getLogger(OWLIndividualGeneratorTest.class);

	@Test
	public void test1() throws IOException, TemplateException {
		MetaIndividual mc = MetaIndividualTest.getTestMetaIndividual();
		test(mc);
	}

	/*
	@Test
	public void test2() throws IOException, TemplateException {
		MetaClass mc = MetaObjectParserTest.getTestMetaClassFromFile();
		test(mc);
	}
	 */

	public void test(MetaIndividual mi) throws IOException, TemplateException {

		OntObject obj = OntObjectTest.getTestOntIndividualObject();
		IOHelper ioh = IOHelperTest.getTestIOHelper();
		String path = "/test-ontology.owl";
		OWLOntology ontology = ioh.loadOntology(this.getClass().getResourceAsStream(path));
		OWLIndividualGenerator gen = new OWLIndividualGenerator(ontology);
		gen.setIOHelper(ioh);

		IRI iri = gen.generateIRI(mi, obj);
		//System.out.println(iri.toString());
		//gen.generateLabel(iri, mc, obj);
		OWLNamedIndividual i = gen.generate(mi, obj);
		iri = ioh.createIRI("FOO:new-individual");
		assertEquals(i, gen.getOWLDataFactory().getOWLNamedIndividual(iri));

		boolean okEquiv = false;
		for (OWLLogicalAxiom ax : ontology.getAxioms(i)) {
			logger.info(ax.toString());
			// WARNING: FRAGILE
			if (ax.toString().equals(
					"ClassAssertion(ObjectIntersectionOf(<http://purl.obolibrary.org/obo/FOO_1>" + 
							" ObjectSomeValuesFrom(<http://purl.obolibrary.org/obo/BFO_0000050>" + 
							" <http://purl.obolibrary.org/obo/FOO_2>)) <http://purl.obolibrary.org/obo/FOO_new-individual>)")) {
			
				okEquiv = true;
			}

		}
		assertTrue("wrong equivalence (but the test may be prone to false positives)", okEquiv);

		for (OWLAnnotation ann : i.getAnnotations(ontology)) {
			logger.info(ann.toString());
		}
		String label = gen.getLabel(i.getIRI());
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
