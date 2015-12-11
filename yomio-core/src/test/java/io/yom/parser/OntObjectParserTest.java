package io.yom.parser;

import static org.junit.Assert.*;
import io.yom.metamodel.MetaClass;
import io.yom.ontmodel.OntObjectModule;
import io.yom.ontmodel.OntObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class OntObjectParserTest {

	/**
	 * Logger.
	 */
	private static final Logger logger =
			LoggerFactory.getLogger(OntObjectParserTest.class);

	@Test
	public void testBackgroundOntology() throws Exception {
		OntObjectModule m = getTestBackgroundOntModuleFromFile();
		List<OntObject> cList = m.getClassList();
		for (OntObject obj : cList) {
			logger.info(" CLS: "+obj);
		}
		
	}

	@Test
	public void testFillerOntology() throws Exception {
		OntObjectModule m = getTestFillerOntModuleFromFile();
		List<OntObject> cList = m.getClassList();
		for (OntObject obj : cList) {
			logger.info(" FCLS: "+obj);
		}
		
	}

	public static OntObjectModule getOntModuleFromFile(String path) throws Exception {
		InputStream s = OntObjectParserTest.class.getResourceAsStream(path);
		Object yamlObj = ParserUtils.parseStream(s);
		OntObjectParser p = new OntObjectParser();
		Map ymap = (Map)yamlObj;
		OntObjectModule m = p.translateYAMLToOntModule(ymap);
		return m;
	}

	public static OntObjectModule getTestBackgroundOntModuleFromFile() throws Exception {
		return getOntModuleFromFile("/test-ontology.yaml");
	}
	
	public static OntObjectModule getTestFillerOntModuleFromFile() throws Exception {
		return getOntModuleFromFile("/test-filler-ontology.yaml");
	}

	public static OntObject getTestFillerOntClassObjectFromFile() throws Exception {
		OntObjectModule m = getOntModuleFromFile("/test-filler-ontology.yaml");
		return m.getClassList().get(0);
	}
}
