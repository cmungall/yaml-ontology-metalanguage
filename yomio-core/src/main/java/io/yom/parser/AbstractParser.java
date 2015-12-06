package io.yom.parser;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

public abstract class AbstractParser {

	public Object parseFile(File file) throws IOException {
		String yamlString = FileUtils.readFileToString(file);
		Object jsonObject = new Yaml().load(yamlString);
		return jsonObject;
	}

}
