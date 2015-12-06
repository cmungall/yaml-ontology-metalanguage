package io.yom.generator;

import java.util.List;
import java.util.Set;

import io.yom.generator.OWLNamedObjectGenerator.FillerType;
import io.yom.metamodel.MetaClass;
import io.yom.metamodel.Template;
import io.yom.metamodel.Variable;
import io.yom.ontmodel.OntObject;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OWLClassGenerator extends OWLNamedObjectGenerator {

	/**
	 * Logger.
	 */
	private static final Logger logger =
			LoggerFactory.getLogger(OWLNamedObjectGenerator.class);
	String textDefinitionProperty = "http://purl.obolibrary.org/obo/IAO_0000115";

	public OWLClassGenerator(OWLOntology ontology) {
		super(ontology);
		// TODO Auto-generated constructor stub
	}

	public OWLClass generate(MetaClass metaClass, OntObject ontObject) throws TemplateException {
		OWLDataFactory df = getOWLDataFactory();
		IRI classIRI = generateIRI(metaClass, ontObject);
		OWLClass c = df.getOWLClass(classIRI);
		generateLabel(classIRI, metaClass, ontObject);
		generateTextDefinition(classIRI, metaClass, ontObject);
		generateEquivalentTo(c, metaClass, ontObject);
		generateGeneralClassAxioms(metaClass, ontObject);
		return c;
	}

	public void generateEquivalentTo(OWLClass c, MetaClass metaClass,
			OntObject ontObject) throws TemplateException {
		Template t = metaClass.getEquivalentToTemplate();
		if (t != null) {
			OWLClassExpression x = fillTemplateToClassExpression(t, ontObject, FillerType.IRI);
			OWLDataFactory df = getOWLDataFactory();
			OWLEquivalentClassesAxiom axiom = df.getOWLEquivalentClassesAxiom(c, x);
			addAxiom(axiom);
		}
	}

	public void generateGeneralClassAxioms(MetaClass metaClass,
			OntObject ontObject) throws TemplateException {
		Set<Template> ts = metaClass.getGeneralClassAxioms();
		if (ts != null) {
			for (Template t : ts) {

				OWLDataFactory df = getOWLDataFactory();
				String axiomString = fillTemplate(t, ontObject, FillerType.IRI);
				String typeToken;
				String EQ = "EquivalentTo:";
				String SC = "SubClassOf:";
				if (axiomString.contains(EQ)) {
					typeToken = EQ;
				}
				else if (axiomString.contains(SC)) {
					typeToken = SC;
				}
				else {
					throw new UnrecognizedGeneralClassAxiom(axiomString);
				}
				String[] toks = axiomString.split(typeToken);
				if (toks.length != 2) {
					// consider allowing n-ary arguments
					throw new UnrecognizedGeneralClassAxiom(axiomString+" has wrong number of tokens: "+toks.length);
				}
				OWLClassExpression lhs = parseClassExpression(toks[0]);
				OWLClassExpression rhs = parseClassExpression(toks[1]);
				OWLLogicalAxiom axiom;
				if (typeToken.equals(EQ)) {
					axiom = df.getOWLEquivalentClassesAxiom(lhs, rhs);
				}
				else if (typeToken.equals(SC)) {
					axiom = df.getOWLSubClassOfAxiom(lhs, rhs);
				}
				else {
					throw new UnrecognizedGeneralClassAxiom("Assertion error");
				}
				addAxiom(axiom);
			}
		}
	}

	public void generateTextDefinition(IRI classIRI, MetaClass metaClass,
			OntObject ontObject) throws TemplateException {
		Template t = metaClass.getTextDefinitionTemplate();
		OWLAnnotationProperty p = getOWLDataFactory().getOWLAnnotationProperty(IRI.create(textDefinitionProperty));
		addAnnotationAssertionAxiom(t, classIRI, p, ontObject);

	}




}
