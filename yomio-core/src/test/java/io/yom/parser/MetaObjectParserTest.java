package io.yom.parser;

import static org.junit.Assert.*;
import io.yom.generator.OWLClassGeneratorTest;
import io.yom.metamodel.MetaClass;
import io.yom.metamodel.MetaClassTest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class MetaObjectParserTest {

	/**
	 * Logger.
	 */
	private static final Logger logger =
			LoggerFactory.getLogger(MetaObjectParserTest.class);

	@Test
	public void test() throws IOException {

		MetaClass mc = getTestMetaClassFromFile();
		MetaClass mc2 = MetaClassTest.getTestMetaClass();
		logger.info(mc.toString());
		// TODO: test equality

	}

	public static MetaClass getTestMetaClassFromFile() throws IOException {
		//FileUtils.readFileToString(file);
		String path = "/test-metaclass.yaml";
		InputStream s = MetaObjectParserTest.class.getResourceAsStream(path);
		String yamlString = IOUtils.toString(s, "UTF-8");
		Object yamlObj = new Yaml().load(yamlString);
		MetaObjectParser p = new MetaObjectParser();
		Map ymap = (Map)yamlObj;
		MetaClass mc = p.translateYAMLToMetaClass(ymap);
		return mc;
	}

}
