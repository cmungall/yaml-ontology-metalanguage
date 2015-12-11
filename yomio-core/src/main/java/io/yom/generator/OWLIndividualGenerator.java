package io.yom.generator;

import io.yom.metamodel.MetaIndividual;
import io.yom.metamodel.Template;
import io.yom.ontmodel.OntObject;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OWLIndividualGenerator extends OWLNamedObjectGenerator {

	/**
	 * Logger.
	 */
	private static final Logger logger =
			LoggerFactory.getLogger(OWLIndividualGenerator.class);

	public OWLIndividualGenerator(OWLOntology ontology) {
		super(ontology);
	}

	public OWLNamedIndividual generate(MetaIndividual metaObject, OntObject ontObject) throws TemplateException {
		OWLDataFactory df = getOWLDataFactory();
		IRI individualIRI = generateIRI(metaObject, ontObject);
		OWLNamedIndividual i = df.getOWLNamedIndividual(individualIRI);
		generateLabel(individualIRI, metaObject, ontObject);
		generateTypes(i, metaObject, ontObject);
		return i;
	}

	public void generateTypes(OWLNamedIndividual i, MetaIndividual metaObject,
			OntObject ontObject) throws TemplateException {
		for (Template t : metaObject.getTypeTemplates()) {
			OWLClassExpression x = fillTemplateToClassExpression(t, ontObject, FillerType.IRI);
			OWLDataFactory df = getOWLDataFactory();
			OWLClassAssertionAxiom axiom = df.getOWLClassAssertionAxiom(x, i);
			addAxiom(axiom);
		}
	}


}
