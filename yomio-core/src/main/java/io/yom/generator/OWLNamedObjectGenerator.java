package io.yom.generator;

import io.yom.metamodel.MetaClass;
import io.yom.metamodel.MetaObject;
import io.yom.metamodel.Template;
import io.yom.metamodel.Variable;
import io.yom.ontmodel.OntObject;
import io.yom.parser.IOHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstact parent of all object OWL Generators
 * 
 * @author cjm
 *
 */
public abstract class OWLNamedObjectGenerator extends OWLOntologyProcessor {

	/**
	 * Logger.
	 */
	private static final Logger logger =
			LoggerFactory.getLogger(OWLNamedObjectGenerator.class);

	public enum FillerType {
		IRI, LABEL
	};



	//public abstract OWLNamedObject generate(MetaObject metaObject);
	private BidirectionalShortFormProvider bidiShortFormProvider;
	private QuotedEntityChecker entityChecker;

	public OWLNamedObjectGenerator(OWLOntology ontology) {
		super();
		this.ontology = ontology;
		init();
	}

	protected void init() {
		ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(getOWLOntologyManager(), 
				ontology.getImportsClosure(), shortFormProvider);
	}



	/**
	 * @return the iophelper
	 */
	public IOHelper getIOHelper() {
		return iohelper;
	}

	/**
	 * @param iophelper the iophelper to set
	 */
	public void setIOHelper(IOHelper iohelper) {
		this.iohelper = iohelper;
		
		entityChecker = new QuotedEntityChecker();
		entityChecker.addProperty(getOWLDataFactory().getRDFSLabel());
		entityChecker.addAll(ontology);
		entityChecker.useIOHelper(iohelper);

	}

	protected IRI generateIRI(MetaClass metaClass, OntObject ontObject) {
		String id = ontObject.getId();
		IRI iri;
		if (id != null) {
			iri = getIOHelper().createIRI(id);
		}
		else {
			iri = null;		// TODO
		}

		return iri;
	}

	public void generateLabel(IRI classIRI, MetaClass metaClass,
			OntObject ontObject) throws TemplateException {
		Template t = metaClass.getNameTemplate();
		addAnnotationAssertionAxiom(t, classIRI, getOWLDataFactory().getRDFSLabel(), ontObject);

	}
	
	protected void addAnnotationAssertionAxiom(Template t, IRI iri, OWLAnnotationProperty p, OntObject ontObject) throws TemplateException {
		if (t != null) {
			String literalValue = fillTemplate(t, ontObject, FillerType.LABEL);
			OWLDataFactory df = getOWLDataFactory();
			OWLLiteral v = df.getOWLLiteral(literalValue);
			OWLAnnotationAssertionAxiom axiom = df.getOWLAnnotationAssertionAxiom(p, iri, v);
			addAxiom(axiom);
		}
	}




	/**
	 * Takes generic template text and fills in values
	 * 
	 * Given a template, such as
	 * <pre>
	 *   text: "a %s that %s"
	 *   vars: [v1, v2]
	 * </pre>
	 * 
	 * with bindings v1=foo and v2=bar,
	 * 
	 * This will return "a foo that bar" 
	 * 
	 * @param t
	 * @param ontObject
	 * @return
	 */
	protected String fillTemplate(Template t, OntObject ontObject, FillerType fillerType) throws TemplateException {
		List<Variable> vars = t.getVariableList();
		List<String> filledValues = new ArrayList<>();
		for (Variable var : vars) {
			String val = getFilledValue(ontObject, var);
			if (fillerType.equals(FillerType.LABEL)) {
				val = getLabel(val);
			}
			filledValues.add(val);
		}
		return String.format(t.getTemplateString(),
				filledValues.toArray(new String[filledValues.size()]));
	}

	protected OWLClassExpression fillTemplateToClassExpression(Template t, OntObject ontObject, FillerType fillerType) throws TemplateException {
		String exprStr = fillTemplate(t, ontObject, fillerType);
		return parseClassExpression(exprStr);
	}


	private String getFilledValue(OntObject ontObject, Variable var) throws UnknownVariableException {
		Map<String, Object> m = ontObject.getPropertyValueMap();
		String vn = var.getName();
		if (!m.containsKey(vn)) {
			throw new UnknownVariableException(vn, ontObject);
		}
		return m.get(vn).toString();
	}

	/**
	 * Converts a string into an OWLClassExpression. If a problem is encountered, an error is thrown.
	 * @param expression
	 * @return owlClassExpression
	 * @throws ClassExpressionException 
	 */
	protected OWLClassExpression parseClassExpression(String expression) {
		logger.info("Parsing: "+expression);
		OWLClassExpression owlClassExpression = null;
		ManchesterOWLSyntaxEditorParser parser = getParser(expression);
		owlClassExpression = parser.parseClassExpression();
		return owlClassExpression;
	}

	/**
	 * Instantiate a new Manchester syntax parser.
	 * @param expression
	 * @return parser
	 */
	private ManchesterOWLSyntaxEditorParser getParser(String expression) {	
		ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(getOWLDataFactory(), expression);
		parser.setDefaultOntology(ontology);
		parser.setOWLEntityChecker(entityChecker);
		return parser;
	}


}
