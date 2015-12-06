package io.yom.generator;

import io.yom.parser.IOHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.ReferencedEntitySetProvider;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

/**
 * A custom OWLEntityChecker that also resolves names
 * inside single and double quotation marks.
 *
 * Taken from ROBOT
 * @author <a href="mailto:james@overton.ca">James A. Overton</a>
 */
public class QuotedEntityChecker implements OWLEntityChecker {
    /**
     * Logger.
     */
    private static final Logger logger =
        LoggerFactory.getLogger(QuotedEntityChecker.class);

    /**
     * Shared DataFactory.
     */
    private static OWLDataFactory dataFactory = new OWLDataFactoryImpl();

    /**
     * Shared IOHelper.
     */
    private static IOHelper ioHelper = null;

    /**
     * Optional short form providers for additional names.
     */
    private List<ShortFormProvider> providers =
        new ArrayList<ShortFormProvider>();

    /**
     * List of annotation properties to use for finding entities.
     */
    private List<OWLAnnotationProperty> properties =
        new ArrayList<OWLAnnotationProperty>();

    /**
     * Map from names to IRIs of annotation properties.
     */
    private Map<String, IRI> annotationProperties = new HashMap<String, IRI>();

    /**
     * Map from names to IRIs of classes.
     */
    private Map<String, IRI> classes = new HashMap<String, IRI>();

    /**
     * Map from names to IRIs of data properties.
     */
    private Map<String, IRI> dataProperties = new HashMap<String, IRI>();

    /**
     * Map from names to IRIs of datatypes.
     */
    private Map<String, IRI> datatypes = new HashMap<String, IRI>();

    /**
     * Map from names to IRIs of named individuals.
     */
    private Map<String, IRI> namedIndividuals = new HashMap<String, IRI>();

    /**
     * Map from names to IRIs of object properties.
     */
    private Map<String, IRI> objectProperties = new HashMap<String, IRI>();

    /**
     * Add an IOHelper for resolving names to IRIs.
     *
     * @param ioHelper the IOHelper to use
     */
    public void useIOHelper(IOHelper ioHelper) {
        this.ioHelper = ioHelper;
    }

    /**
     * Add a short form providers for finding names.
     *
     * @param provider the short form provider to add
     */
    public void addProvider(ShortFormProvider provider) {
        providers.add(provider);
    }

    /**
     * Add an annotation property for finding names.
     *
     * @param property the property to add
     */
    public void addProperty(OWLAnnotationProperty property) {
        properties.add(property);
    }

    /**
     * Escape single quotations marks inside a string.
     *
     * @param content the string to escape
     * @return the escaped string
     */
    public static String escape(String content) {
        if (content == null) {
            return null;
        }
        return content.replaceAll("'", "\\\\'");
    }

    /**
     * Given a string that may be a name or a class expression,
     * if it starts with quotation mark or parenthesis then return it,
     * if it contains no spaces then return it,
     * otherwise return it wrapped in single quotes.
     *
     * @param content the string to wrap
     * @return the wrapped string
     */
    public static String wrap(String content) {
        if (content == null) {
            return null;
        }
        if (content.trim().startsWith("'")
            || content.trim().startsWith("\"")
            || content.trim().startsWith("(")) {
            return content;
        }
        if (content.trim().matches(".*\\s.*")) {
            return "'" + escape(content) + "'";
        }
        return escape(content);
    }

    /**
     * Use annotation properties and the short form provider
     * to add mappings for all entities in the given ontology.
     *
     * @param ontology the ontology to add mappings for
     */
    public void addAll(OWLOntology ontology) {
        Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
        ontologies.add(ontology);
        ReferencedEntitySetProvider resp =
            new ReferencedEntitySetProvider(ontologies);
        for (OWLEntity entity: resp.getEntities()) {
            add(ontology, entity);
        }
    }

    /**
     * Given an entity, return the right map for it.
     *
     * @param entity the entity to find a map for
     * @return the right map for the entity, or null
     */
    private Map<String, IRI> pickMap(OWLEntity entity) {
        Map<String, IRI> map = null;
        if (entity.isOWLAnnotationProperty()) {
            map = annotationProperties;
        } else if (entity.isOWLObjectProperty()) {
            map = objectProperties;
        } else if (entity.isOWLDataProperty()) {
            map = dataProperties;
        } else if (entity.isOWLDatatype()) {
            map = datatypes;
        } else if (entity.isOWLClass()) {
            map = classes;
        } else if (entity.isOWLNamedIndividual()) {
            map = namedIndividuals;
        }
        return map;
    }

    /**
     * Use annotation properties and the short form provider
     * to add mappings for a single entity.
     *
     * @param entity the entity to add mappings for
     */
    public void add(OWLEntity entity) {
        add(null, entity);
    }

    /**
     * Use annotation properties and the short form provider
     * to add mappings for a single entity.
     *
     * @param parentOntology an ontology with annotations for the entity
     * @param entity the entity to add mappings for
     */
    public void add(OWLOntology parentOntology, OWLEntity entity) {
        if (entity == null) {
            return;
        }

        Map<String, IRI> map = pickMap(entity);
        if (map == null) {
            logger.info("Unknown OWL entity type for: " + entity);
            return;
        }

        if (providers != null) {
            for (ShortFormProvider provider: providers) {
                map.put(provider.getShortForm(entity), entity.getIRI());
            }
        }

        if (parentOntology != null && properties != null) {
            for (OWLAnnotationProperty property: properties) {
                Set<OWLAnnotation> annotations =
                    entity.getAnnotations(parentOntology, property);
                for (OWLAnnotation annotation: annotations) {
                	OWLAnnotationValue av = annotation.getValue();
                	if (av instanceof OWLLiteral) {
                        map.put(((OWLLiteral) av).getLiteral(), entity.getIRI());
                    }
                }
            }
        }
    }

    /**
     * Add a specific mapping to the given entity.
     *
     * @param entity the entity to add mappings for
     * @param name the name to map to this entity
     */
    public void add(OWLEntity entity, String name) {
        if (entity == null) {
            return;
        }

        Map<String, IRI> map = pickMap(entity);
        if (map == null) {
            logger.info("Unknown OWL entity type for: " + entity);
            return;
        }

        map.put(name, entity.getIRI());
    }

    /**
     * Get the IRI for the given name in a given map.
     * Quotation marks will be removed if necessary.
     *
     * @param map the map to search
     * @param name the name of the entity to find
     * @return the IRI of the entity, or null if none is found
     */
    private IRI getIRI(Map<String, IRI> map, String name) {
        if (map.containsKey(name)) {
            return map.get(name);
        }
        name = name.trim().replaceAll("^'|'$", "");
        if (map.containsKey(name)) {
            return map.get(name);
        }
        name = name.trim().replaceAll("^\"|\"$", "");
        if (map.containsKey(name)) {
            return map.get(name);
        }
        return null;
    }

    /**
     * Find an annotation property with the given name.
     * Quotation marks will be removed if necessary.
     *
     * @param name the name of the entity to find
     * @return an annotation property, or null
     */
    public OWLAnnotationProperty getOWLAnnotationProperty(String name) {
        return getOWLAnnotationProperty(name, false);
    }

    /**
     * Find an annotation property with the given name.
     * Quotation marks will be removed if necessary.
     *
     * @param name the name of the entity to find
     * @param create when true and an IOHelper is defined, create the property
     * @return an annotation property, or null
     */
    public OWLAnnotationProperty getOWLAnnotationProperty(String name,
            boolean create) {
        IRI iri = getIRI(annotationProperties, name);
        if (iri != null) {
            return dataFactory.getOWLAnnotationProperty(iri);
        }
        if (ioHelper != null) {
            iri = ioHelper.createIRI(name);
            if (iri != null) {
                return dataFactory.getOWLAnnotationProperty(iri);
            }
        }
        return null;
    }

    /**
     * Find a class with the given name, or create one.
     * Quotation marks will be removed if necessary.
     *
     * @param name the name of the entity to find
     * @return a class, or null
     */
    public OWLClass getOWLClass(String name) {
        IRI iri = getIRI(classes, name);
        if (iri != null) {
            return dataFactory.getOWLClass(iri);
        }
        if (ioHelper != null) {
            iri = ioHelper.createIRI(name);
            if (iri != null) {
                return dataFactory.getOWLClass(iri);
            }
        }
        return null;
    }

    /**
     * Find a data property with the given name.
     * Quotation marks will be removed if necessary.
     *
     * @param name the name of the entity to find
     * @return a data property, or null
     */
    public OWLDataProperty getOWLDataProperty(String name) {
        IRI iri = getIRI(dataProperties, name);
        if (iri != null) {
            return dataFactory.getOWLDataProperty(iri);
        }
        return null;
    }

    /**
     * Find a datatype with the given name.
     * Quotation marks will be removed if necessary.
     *
     * @param name the name of the entity to find
     * @return a datatype, or null
     */
    public OWLDatatype getOWLDatatype(String name) {
        return getOWLDatatype(name, false);
    }

    /**
     * Find a datatype with the given name, or create one.
     * Quotation marks will be removed if necessary.
     *
     * @param name the name of the entity to find
     * @param create when true and an IOHelper is defined, create the type
     * @return a datatype, or null
     */
    public OWLDatatype getOWLDatatype(String name, boolean create) {
        IRI iri = getIRI(datatypes, name);
        if (iri != null) {
            return dataFactory.getOWLDatatype(iri);
        }
        if (create && ioHelper != null) {
            iri = ioHelper.createIRI(name);
            if (iri != null) {
                return dataFactory.getOWLDatatype(iri);
            }
        }
        return null;
    }

    /**
     * Find a named individual with the given name.
     * Quotation marks will be removed if necessary.
     *
     * @param name the name of the entity to find
     * @return a named individual, or null
     */
    public OWLNamedIndividual getOWLIndividual(String name) {
        IRI iri = getIRI(namedIndividuals, name);
        if (iri != null) {
            return dataFactory.getOWLNamedIndividual(iri);
        }
        return null;
    }

    /**
     * Find an object property with the given name.
     * Quotation marks will be removed if necessary.
     *
     * @param name the name of the entity to find
     * @return an object property, or null
     */
    public OWLObjectProperty getOWLObjectProperty(String name) {
        IRI iri = getIRI(objectProperties, name);
        if (iri != null) {
            return dataFactory.getOWLObjectProperty(iri);
        }
        return null;
    }
}
