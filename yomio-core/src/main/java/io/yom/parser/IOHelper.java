package io.yom.parser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jsonldjava.core.Context;
import com.github.jsonldjava.core.JsonLdApi;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.opencsv.CSVReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.yaml.snakeyaml.Yaml;

/**
 * Provides convenience methods for working with ontology and term files.
 *
 * Taken from ROBOT
 * @author <a href="mailto:james@overton.ca">James A. Overton</a>
 */
public class IOHelper {
    /**
     * Logger.
     */
    private static final Logger logger =
        LoggerFactory.getLogger(IOHelper.class);

    /**
     * RDF literal separator.
     */
    private static String seperator = "\"^^";

    /**
     * Path to default context as a resource.
     */
    private static String defaultContextPath = "/obo_context.jsonld";

    /**
     * Store the current JSON-LD context.
     */
    private Context context = new Context();

    /**
     * Create a new IOHelper with the default prefixes.
     */
    public IOHelper() {
        try {
            setContext(getDefaultContext());
        } catch (IOException e) {
            logger.warn("Could not load default prefixes.");
            logger.warn(e.getMessage());
        }
    }

    /**
     * Create a new IOHelper with or without the default prefixes.
     *
     * @param defaults false if defaults should not be used
     */
    public IOHelper(boolean defaults) {
        try {
            if (defaults) {
                setContext(getDefaultContext());
            } else {
                setContext();
            }
        } catch (IOException e) {
            logger.warn("Could not load default prefixes.");
            logger.warn(e.getMessage());
        }
    }

    /**
     * Create a new IOHelper with the specified prefixes.
     *
     * @param map the prefixes to use
     */
    public IOHelper(Map<String, Object> map) {
        setContext(map);
    }

    /**
     * Create a new IOHelper with prefixes from a file path.
     *
     * @param path to a JSON-LD file with a @context
     */
    public IOHelper(String path) {
        try {
            String jsonString = FileUtils.readFileToString(new File(path));
            setContext(jsonString);
        } catch (IOException e) {
            logger.warn("Could not load prefixes from " + path);
            logger.warn(e.getMessage());
        }
    }

    /**
     * Create a new IOHelper with prefixes from a file.
     *
     * @param file a JSON-LD file with a @context
     */
    public IOHelper(File file) {
        try {
            String jsonString = FileUtils.readFileToString(file);
            setContext(jsonString);
        } catch (IOException e) {
            logger.warn("Could not load prefixes from " + file);
            logger.warn(e.getMessage());
        }
    }

    /**
     * Try to guess the location of the catalog.xml file.
     * Looks in the directory of the given ontology file for a catalog file.
     *
     * @param ontologyFile the
     * @return the guessed catalog File; may not exist!
     */
    public File guessCatalogFile(File ontologyFile) {
        String path = ontologyFile.getParent();
        String catalogPath = "catalog-v001.xml";
        if (path != null) {
            catalogPath = path + "/catalog-v001.xml";
        }
        return new File(catalogPath);
    }

    /**
     * Load an ontology from a String path, using a catalog file if available.
     *
     * @param ontologyPath the path to the ontology file
     * @return a new ontology object, with a new OWLManager
     * @throws IOException on any problem
     */
    public OWLOntology loadOntology(String ontologyPath)
            throws IOException {
        File ontologyFile = new File(ontologyPath);
        File catalogFile = guessCatalogFile(ontologyFile);
        return loadOntology(ontologyFile, catalogFile);
    }

    /**
     * Load an ontology from a String path, with option to use catalog file.
     *
     * @param ontologyPath the path to the ontology file
     * @param useCatalog when true, a catalog file will be used if one is found
     * @return a new ontology object, with a new OWLManager
     * @throws IOException on any problem
     */
    public OWLOntology loadOntology(String ontologyPath, boolean useCatalog)
            throws IOException {
        File ontologyFile = new File(ontologyPath);
        File catalogFile = null;
        if (useCatalog) {
            catalogFile = guessCatalogFile(ontologyFile);
        }
        return loadOntology(ontologyFile, catalogFile);
    }

    /**
     * Load an ontology from a String path, with optional catalog file.
     *
     * @param ontologyPath the path to the ontology file
     * @param catalogPath the path to the catalog file
     * @return a new ontology object, with a new OWLManager
     * @throws IOException on any problem
     */
    public OWLOntology loadOntology(String ontologyPath, String catalogPath)
            throws IOException {
        File ontologyFile = new File(ontologyPath);
        File catalogFile  = new File(catalogPath);
        return loadOntology(ontologyFile, catalogFile);
    }

    /**
     * Load an ontology from a File, using a catalog file if available.
     *
     * @param ontologyFile the ontology file to load
     * @return a new ontology object, with a new OWLManager
     * @throws IOException on any problem
     */
    public OWLOntology loadOntology(File ontologyFile)
            throws IOException {
        File catalogFile = guessCatalogFile(ontologyFile);
        return loadOntology(ontologyFile, catalogFile);
    }

    /**
     * Load an ontology from a File, with option to use a catalog file.
     *
     * @param ontologyFile the ontology file to load
     * @param useCatalog when true, a catalog file will be used if one is found
     * @return a new ontology object, with a new OWLManager
     * @throws IOException on any problem
     */
    public OWLOntology loadOntology(File ontologyFile, boolean useCatalog)
            throws IOException {
        File catalogFile = null;
        if (useCatalog) {
            catalogFile = guessCatalogFile(ontologyFile);
        }
        return loadOntology(ontologyFile, catalogFile);
    }

    /**
     * Load an ontology from a File, with optional catalog File.
     *
     * @param ontologyFile the ontology file to load
     * @param catalogFile the catalog file to use
     * @return a new ontology object, with a new OWLManager
     * @throws IOException on any problem
     */
    public OWLOntology loadOntology(File ontologyFile, File catalogFile)
            throws IOException {
        logger.debug("Loading ontology {} with catalog file {}",
                ontologyFile, catalogFile);

        Object jsonObject = null;

        try {
            String extension =
                FilenameUtils.getExtension(ontologyFile.getName());
            extension = extension.trim().toLowerCase();
            if (extension.equals("yml") || extension.equals("yaml")) {
                logger.debug("Converting from YAML to JSON");
                String yamlString = FileUtils.readFileToString(ontologyFile);
                jsonObject = new Yaml().load(yamlString);
            } else if (extension.equals("js")
                       || extension.equals("json")
                       || extension.equals("jsonld")) {
                String jsonString = FileUtils.readFileToString(ontologyFile);
                jsonObject = JsonUtils.fromString(jsonString);
            }

            // Use Jena to convert a JSON-LD string to RDFXML, then load it
            if (jsonObject != null) {
                logger.debug("Converting from JSON to RDF");
                jsonObject = new JsonLdApi().expand(getContext(), jsonObject);
                String jsonString = JsonUtils.toString(jsonObject);
                Model model = ModelFactory.createDefaultModel();
                model.read(IOUtils.toInputStream(jsonString), null, "JSON-LD");
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                //model.write(System.out);
                model.write(output);
                byte[] data = output.toByteArray();
                ByteArrayInputStream input = new ByteArrayInputStream(data);
                return loadOntology(input);
            }

            OWLOntologyManager manager =
                OWLManager.createOWLOntologyManager();
            if (catalogFile != null && catalogFile.isFile()) {
                manager.addIRIMapper(new CatalogXmlIRIMapper(catalogFile));
            }
            return manager.loadOntologyFromOntologyDocument(ontologyFile);
        } catch (JsonLdError e) {
            throw new IOException(e);
        } catch (OWLOntologyCreationException e) {
            throw new IOException(e);
        }
    }

    /**
     * Load an ontology from an InputStream, without a catalog file.
     *
     * @param ontologyStream the ontology stream to load
     * @return a new ontology object, with a new OWLManager
     * @throws IOException on any problem
     */
    public OWLOntology loadOntology(InputStream ontologyStream)
            throws IOException {
        OWLOntology ontology = null;
        try {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            ontology = manager.loadOntologyFromOntologyDocument(ontologyStream);
        } catch (OWLOntologyCreationException e) {
            throw new IOException(e);
        }
        return ontology;
    }

    /**
     * Given the name of a file format, return an instance of it.
     *
     * Suported formats:
     *
     * <ul>
     * <li>OBO as 'obo'
     * <li>RDFXML as 'owl'
     * <li>Turtle as 'ttl'
     * <li>OWLXML as 'owx'
     * <li>Manchester as 'omn'
     * <li>OWL Functional as 'ofn'
     * </ul>
     *
     * @param formatName the name of the format
     * @return an instance of the format
     * @throws IllegalArgumentException if format name is not recognized
     */
    public static OWLOntologyFormat getFormat(String formatName)
          throws IllegalArgumentException {
        formatName = formatName.trim().toLowerCase();
        if (formatName.equals("obo")) {
            return new org.coode.owlapi.obo.parser.OBOOntologyFormat();
        } else if (formatName.equals("owl")) {
            return new org.semanticweb.owlapi.io.RDFXMLOntologyFormat();
        } else if (formatName.equals("ttl")) {
            return new org.coode.owlapi.turtle.TurtleOntologyFormat();
        } else if (formatName.equals("owx")) {
            return new org.semanticweb.owlapi.io.OWLXMLOntologyFormat();
        } else if (formatName.equals("omn")) {
            return new org.coode.owlapi.manchesterowlsyntax
                .ManchesterOWLSyntaxOntologyFormat();
        } else if (formatName.equals("ofn")) {
            return new org.semanticweb.owlapi.io
                .OWLFunctionalSyntaxOntologyFormat();
        } else {
            throw new IllegalArgumentException(
                    "Unknown ontology format: " + formatName);
        }
    }

    /**
     * Save an ontology to a String path.
     *
     * @param ontology the ontology to save
     * @param ontologyPath the path to save the ontology to
     * @return the saved ontology
     * @throws IOException on any problem
     */
    public OWLOntology saveOntology(OWLOntology ontology, String ontologyPath)
            throws IOException {
        return saveOntology(ontology, new File(ontologyPath));
    }

    /**
     * Save an ontology to a File.
     *
     * @param ontology the ontology to save
     * @param ontologyFile the file to save the ontology to
     * @return the saved ontology
     * @throws IOException on any problem
     */
    public OWLOntology saveOntology(OWLOntology ontology, File ontologyFile)
            throws IOException {
        return saveOntology(ontology, IRI.create(ontologyFile));
    }

    /**
     * Save an ontology to an IRI,
     * using the file extension to determine the format.
     *
     * @param ontology the ontology to save
     * @param ontologyIRI the IRI to save the ontology to
     * @return the saved ontology
     * @throws IOException on any problem
     */
    public OWLOntology saveOntology(final OWLOntology ontology, IRI ontologyIRI)
            throws IOException {
        try {
            String formatName = FilenameUtils.getExtension(
                    ontologyIRI.toString());
            OWLOntologyFormat format = getFormat(formatName);
            return saveOntology(ontology, format, ontologyIRI);
        } catch (IllegalArgumentException e) {
            throw new IOException(e);
        }
    }

    /**
     * Save an ontology in the given format to a file.
     *
     * @param ontology the ontology to save
     * @param format the ontology format to use
     * @param ontologyFile the file to save the ontology to
     * @return the saved ontology
     * @throws IOException on any problem
     */
    public OWLOntology saveOntology(final OWLOntology ontology,
            OWLOntologyFormat format, File ontologyFile)
            throws IOException {
        return saveOntology(ontology, format, IRI.create(ontologyFile));
    }

    /**
     * Save an ontology in the given format to an IRI.
     *
     * @param ontology the ontology to save
     * @param format the ontology format to use
     * @param ontologyIRI the IRI to save the ontology to
     * @return the saved ontology
     * @throws IOException on any problem
     */
    public OWLOntology saveOntology(final OWLOntology ontology,
            OWLOntologyFormat format, IRI ontologyIRI)
            throws IOException {
        logger.debug("Saving ontology {} as {} with to IRI {}",
                ontology, format, ontologyIRI);

        //if (format instanceof PrefixOWLOntologyFormat) {
        //    ((PrefixOWLOntologyFormat) format)
        //        .copyPrefixesFrom(getPrefixManager());
        //}

        try {
            ontology.getOWLOntologyManager().saveOntology(
                    ontology, format, ontologyIRI);
        } catch (OWLOntologyStorageException e) {
            throw new IOException(e);
        }
        return ontology;
    }


    /**
     * Extract a set of term identifiers from an input string
     * by removing comments, trimming lines, and removing empty lines.
     * A comment is a space or newline followed by a '#',
     * to the end of the line. This excludes '#' characters in IRIs.
     *
     * @param input the String containing the term identifiers
     * @return a set of term identifier strings
     */
    public Set<String> extractTerms(String input) {
        Set<String> results = new HashSet<String>();
        List<String> lines = Arrays.asList(
                input.replaceAll("\\r", "").split("\\n"));
        for (String line: lines) {
            if (line.trim().startsWith("#")) {
                continue;
            }
            String result = line.replaceFirst("($|\\s)#.*$", "").trim();
            if (!result.isEmpty()) {
                results.add(result);
            }
        }
        return results;
    }

    /**
     * Given a term string, use the current prefixes to create an IRI.
     *
     * @param term the term to convert to an IRI
     * @return the new IRI
     */
    public IRI createIRI(String term) {
        if (term == null) {
            return null;
        }

        try {
            // This is stupid, because better methods aren't public.
            // We create a new JSON map and add one entry
            // with the term as the key and some string as the value.
            // Then we run the JsonLdApi to expand the JSON map
            // in the current context, and just grab the first key.
            // If everything worked, that key will be our expanded iri.
            Map<String, Object> jsonMap = new HashMap<String, Object>();
            jsonMap.put(term, "ignore this string");
            Object expanded = new JsonLdApi().expand(context, jsonMap);
            String result = ((Map<String, Object>) expanded)
                .keySet().iterator().next();
            if (result != null) {
                return IRI.create(result);
            }
        } catch (Exception e) {
            logger.warn("Could not create IRI for {}", term);
            logger.warn(e.getMessage());
        }
        return null;
    }

    /**
     * Given a set of term identifier strings, return a set of IRIs.
     *
     * @param terms the set of term identifier strings
     * @return the set of IRIs
     * @throws IllegalArgumentException if term identifier is not a valid IRI
     */
    public Set<IRI> createIRIs(Set<String> terms)
            throws IllegalArgumentException {
        Set<IRI> iris = new HashSet<IRI>();
        for (String term: terms) {
            IRI iri = createIRI(term);
            if (iri != null) {
                iris.add(iri);
            }
        }
        return iris;
    }

    /**
     * Create an OWLLiteral.
     *
     * @param value the lexical value
     * @return a literal
     */
    public static OWLLiteral createLiteral(String value) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory df = manager.getOWLDataFactory();
        return df.getOWLLiteral(value);
    }

    /**
     * Create an OWLLiteral with a language tag.
     *
     * @param value the lexical value
     * @param lang the language tag
     * @return a literal
     */
    public static OWLLiteral createTaggedLiteral(String value, String lang) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory df = manager.getOWLDataFactory();
        return df.getOWLLiteral(value, lang);
    }

    /**
     * Create a typed OWLLiteral.
     *
     * @param value the lexical value
     * @param type the type IRI string
     * @return a literal
     */
    public OWLLiteral createTypedLiteral(String value, String type) {
        IRI iri = createIRI(type);
        return createTypedLiteral(value, iri);
    }

    /**
     * Create a typed OWLLiteral.
     *
     * @param value the lexical value
     * @param type the type IRI
     * @return a literal
     */
    public OWLLiteral createTypedLiteral(String value, IRI type) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory df = manager.getOWLDataFactory();
        OWLDatatype datatype = df.getOWLDatatype(type);
        return df.getOWLLiteral(value, datatype);
    }

    /**
     * Parse a set of IRIs from a space-separated string, ignoring '#' comments.
     *
     * @param input the string containing the IRI strings
     * @return the set of IRIs
     * @throws IllegalArgumentException if term identifier is not a valid IRI
     */
    public Set<IRI> parseTerms(String input) throws IllegalArgumentException {
        return createIRIs(extractTerms(input));
    }

    /**
     * Load a map of prefixes from the "@context" of a JSON-LD string.
     *
     * @param jsonString the JSON-LD string
     * @return a map from prefix name strings to prefix IRI strings
     * @throws IOException on any problem
     */
    public static Context parseContext(String jsonString) throws IOException {
        try {
            Object jsonObject = JsonUtils.fromString(jsonString);
            if (!(jsonObject instanceof Map)) {
                return null;
            }
            Map<String, Object> jsonMap = (Map<String, Object>) jsonObject;
            if (!jsonMap.containsKey("@context")) {
                return null;
            }
            Object jsonContext = jsonMap.get("@context");
            return new Context().parse(jsonContext);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * Get a copy of the default context.
     *
     * @return a copy of the current context
     * @throws IOException if default context file cannot be read
     */
    public Context getDefaultContext() throws IOException {
        InputStream stream =
            IOHelper.class.getResourceAsStream(defaultContextPath);
        String jsonString = IOUtils.toString(stream);
        return parseContext(jsonString);
    }

    /**
     * Get a copy of the current context.
     *
     * @return a copy of the current context
     */
    public Context getContext() {
        return this.context.clone();
    }

    /**
     * Set an empty context.
     */
    public void setContext() {
        this.context = new Context();
    }

    /**
     * Set the current JSON-LD context to the given context.
     *
     * @param context the new JSON-LD context
     */
    public void setContext(Context context) {
        if (context == null) {
            setContext();
        } else {
            this.context = context;
        }
    }

    /**
     * Set the current JSON-LD context to the given context.
     *
     * @param jsonString the new JSON-LD context as a JSON string
     */
    public void setContext(String jsonString) {
        try {
            this.context = parseContext(jsonString);
        } catch (Exception e) {
            logger.warn("Could not set context from JSON");
            logger.warn(e.getMessage());
        }
    }

    /**
     * Set the current JSON-LD context to the given map.
     *
     * @param map a map of strings for the new JSON-LD context
     */
    public void setContext(Map<String, Object> map) {
        try {
            this.context = new Context().parse(map);
        } catch (Exception e) {
            logger.warn("Could not set context {}", map);
            logger.warn(e.getMessage());
        }
    }

    /**
     * Make an OWLAPI DefaultPrefixManager from a map of prefixes.
     *
     * @param prefixes a map from prefix name strings to prefix IRI strings
     * @return a new DefaultPrefixManager
     */
    public static DefaultPrefixManager makePrefixManager(
            Map<String, String> prefixes) {
        DefaultPrefixManager pm = new DefaultPrefixManager();
        for (Map.Entry<String, String> entry: prefixes.entrySet()) {
            pm.setPrefix(entry.getKey() + ":", entry.getValue());
        }
        return pm;
    }

    /**
     * Get a prefix manager with the current prefixes.
     *
     * @return a new DefaultPrefixManager
     */
    public DefaultPrefixManager getPrefixManager() {
        return makePrefixManager(context.getPrefixes(false));
    }

    /**
     * Add a prefix mapping as a single string "foo: http://example.com#".
     *
     * @param combined both prefix and target
     * @throws IllegalArgumentException on malformed input
     */
    public void addPrefix(String combined) throws IllegalArgumentException {
        String[] results = combined.split(":", 2);
        if (results.length < 2) {
            throw new IllegalArgumentException(
                    "Invalid prefix string: " + combined);
        }
        addPrefix(results[0], results[1]);
    }

    /**
     * Add a prefix mapping to the current JSON-LD context,
     * as a prefix string and target string.
     * Rebuilds the context.
     *
     * @param prefix the short prefix to add; should not include ":"
     * @param target the IRI string that is the target of the prefix
     */
    public void addPrefix(String prefix, String target) {
        try {
            context.put(prefix.trim(), target.trim());
            context.remove("@base");
            setContext((Map<String, Object>) context);
        } catch (Exception e) {
            logger.warn("Could not load add prefix \"{}\" \"{}\"",
                    prefix, target);
            logger.warn(e.getMessage());
        }
    }

    /**
     * Get a copy of the current prefix map.
     *
     * @return a copy of the current prefix map
     */
    public Map<String, String> getPrefixes() {
        return this.context.getPrefixes(false);
    }

    /**
     * Set the current prefix map.
     *
     * @param map the new map of prefixes to use
     */
    public void setPrefixes(Map<String, Object> map) {
        setContext(map);
    }

    /**
     * Return the current prefixes as a JSON-LD string.
     *
     * @return the current prefixes as a JSON-LD string
     * @throws IOException on any error
     */
    public String getContextString() throws IOException {
        try {
            Object compact = JsonLdProcessor.compact(
                    JsonUtils.fromString("{}"),
                    context.getPrefixes(false),
                    new JsonLdOptions());
            return JsonUtils.toPrettyString(compact);
        } catch (Exception e) {
            throw new IOException("JSON-LD could not be generated", e);
        }
    }

    /**
     * Write the current context as a JSON-LD file.
     *
     * @param path the path to write the context
     * @throws IOException on any error
     */
    public void saveContext(String path) throws IOException {
        saveContext(new File(path));
    }

    /**
     * Write the current context as a JSON-LD file.
     *
     * @param file the file to write the context
     * @throws IOException on any error
     */
    public void saveContext(File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(getContextString());
        writer.close();
    }

    /**
     * Read comma-separated values from a path to a list of lists of strings.
     *
     * @param path file path to the CSV file
     * @return a list of lists of strings
     * @throws IOException on file or reading problems
     */
    public static List<List<String>> readCSV(String path) throws IOException {
        return readCSV(new FileReader(path));
    }

    /**
     * Read comma-separated values from a stream to a list of lists of strings.
     *
     * @param stream the stream to read from
     * @return a list of lists of strings
     * @throws IOException on file or reading problems
     */
    public static List<List<String>> readCSV(InputStream stream)
            throws IOException {
        return readCSV(new InputStreamReader(stream));
    }

    /**
     * Read comma-separated values from a reader to a list of lists of strings.
     *
     * @param reader a reader to read data from
     * @return a list of lists of strings
     * @throws IOException on file or reading problems
     */
    public static List<List<String>> readCSV(Reader reader) throws IOException {
        CSVReader csv = new CSVReader(reader);
        List<List<String>> rows = new ArrayList<List<String>>();
        String[] nextLine;
        while ((nextLine = csv.readNext()) != null) {
            rows.add(new ArrayList<String>(Arrays.asList(nextLine)));
        }
        return rows;
    }


    /**
     * Read tab-separated values from a path to a list of lists of strings.
     *
     * @param path file path to the CSV file
     * @return a list of lists of strings
     * @throws IOException on file or reading problems
     */
    public static List<List<String>> readTSV(String path) throws IOException {
        return readTSV(new FileReader(path));
    }

    /**
     * Read tab-separated values from a stream to a list of lists of strings.
     *
     * @param stream the stream to read from
     * @return a list of lists of strings
     * @throws IOException on file or reading problems
     */
    public static List<List<String>> readTSV(InputStream stream)
            throws IOException {
        return readTSV(new InputStreamReader(stream));
    }

    /**
     * Read tab-separated values from a reader to a list of lists of strings.
     *
     * @param reader a reader to read data from
     * @return a list of lists of strings
     * @throws IOException on file or reading problems
     */
    public static List<List<String>> readTSV(Reader reader) throws IOException {
        CSVReader csv = new CSVReader(reader, '\t');
        List<List<String>> rows = new ArrayList<List<String>>();
        String[] nextLine;
        while ((nextLine = csv.readNext()) != null) {
            rows.add(new ArrayList<String>(Arrays.asList(nextLine)));
        }
        return rows;
    }

    /**
     * Read a table from a path to a list of lists of strings.
     *
     * @param path file path to the CSV file
     * @return a list of lists of strings
     * @throws IOException on file or reading problems
     */
    public static List<List<String>> readTable(String path) throws IOException {
        File file = new File(path);
        String extension = FilenameUtils.getExtension(file.getName());
        extension = extension.trim().toLowerCase();
        if (extension.equals("csv")) {
            return readCSV(new FileReader(path));
        } else if (extension.equals("tsv") || extension.equals("tab")) {
            return readTSV(new FileReader(path));
        } else {
            throw new IOException("Unrecognized file type for: " + path);
        }
    }

}
