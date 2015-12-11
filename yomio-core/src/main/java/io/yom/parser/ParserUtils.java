package io.yom.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

public class ParserUtils {

	public static Object parseFile(File file) throws IOException {
		String yamlString = FileUtils.readFileToString(file);
		Object jsonObject = new Yaml().load(yamlString);
		return jsonObject;
	}

	public static Object parseStream(InputStream s) throws IOException {

		String yamlString = IOUtils.toString(s, "UTF-8");
		Object yamlObj = new Yaml().load(yamlString);
		return yamlObj;
	}

}
