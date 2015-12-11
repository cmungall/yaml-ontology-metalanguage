package io.yom.parser;

import static org.junit.Assert.*;
import io.yom.metamodel.MetaClass;
import io.yom.ontmodel.Module;
import io.yom.ontmodel.OntObjectModule;
import io.yom.ontmodel.OntObject;
import io.yom.ontmodel.OntObjectTest;
import io.yom.ontmodel.Project;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class OntProjectParserTest {

	/**
	 * Logger.
	 */
	private static final Logger logger =
			LoggerFactory.getLogger(OntProjectParserTest.class);

	@Test
	public void testBackgroundOntology() throws Exception {
		Project p = getOntProjectFromFile("/project.yaml");	
		logger.info("Roots: "+p.getRootModuleIds());
		OntObjectModule m1 = (OntObjectModule) p.getModule("test-filler-ontology");
		OntObjectModule m2 = (OntObjectModule) p.getModule("test-filler-individuals");
		logger.info("Roots: "+p.getRootModuleList());
		logger.info("Testing module: "+m1.getId());
		assertTrue("id", m1.getClassList().get(0).getId().equals(OntObjectTest.newClassId));
		logger.info("Testing module: "+m2);
		logger.info("Testing module: "+m2.getId());
		assertTrue("id", m2.getIndividualList().get(0).getId().equals(OntObjectTest.newIndividualId));

	}


	public static Project getOntProjectFromFile(String path) throws Exception {
		InputStream s = OntProjectParserTest.class.getResourceAsStream(path);
		Object yamlObj = ParserUtils.parseStream(s);
		OntProjectParser pp = new OntProjectParser();
		pp.setBaseDirectory("src/test/resources"); // TODO
		Map ymap = (Map)yamlObj;
		Project proj = pp.parseOntProject(ymap);
		
		return proj;
	}

}
