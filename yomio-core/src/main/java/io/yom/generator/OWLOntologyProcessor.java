package io.yom.generator;

import io.yom.parser.IOHelper;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An object that takes as input an ontology and performs operations that modify
 * that ontology
 * 
 * @author cjm
 *
 */
public abstract class OWLOntologyProcessor {

	/**
	 * Logger.
	 */
	private static final Logger logger =
			LoggerFactory.getLogger(OWLOntologyProcessor.class);

	protected OWLOntology ontology;
	protected IOHelper iohelper;
	

	protected void addAxiom(OWLAxiom axiom) {
		getOWLOntologyManager().addAxiom(ontology, axiom);
	}

	/**
	 * @return
	 * @see org.semanticweb.owlapi.model.OWLOntology#getOWLOntologyManager()
	 */
	public OWLOntologyManager getOWLOntologyManager() {
		return ontology.getOWLOntologyManager();
	}

	public OWLDataFactory getOWLDataFactory() {
		return getOWLOntologyManager().getOWLDataFactory();
	}

	public String getLabel(String id) {
		IRI iri = iohelper.createIRI(id);
		return getLabel(iri);
	}

	/**
	 * Get the rdf:label
	 * 
	 * @param iri
	 * @return label
	 */
	public String getLabel(IRI iri) {
		return getAnnotation(iri, getOWLDataFactory().getRDFSLabel());
	}

	/**
	 * Retrieve the literal value of an annotation.
	 * @param iri
	 * @param annotationProperty
	 * @return literal value
	 */
	private String getAnnotation(IRI iri, OWLAnnotationProperty annotationProperty)  {
		
		for (OWLAnnotationAssertionAxiom aaa : ontology.getAnnotationAssertionAxioms(iri)) {
			if (aaa.getProperty().equals(annotationProperty)) {
				OWLAnnotationValue v = aaa.getValue();
				if (v instanceof OWLLiteral) {
					OWLLiteral val = (OWLLiteral) v;
					return val.getLiteral();
				}
				
			}
		}
		return null;
	}
}
