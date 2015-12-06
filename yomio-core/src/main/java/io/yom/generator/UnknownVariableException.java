package io.yom.generator;

import io.yom.ontmodel.OntObject;

public class UnknownVariableException extends TemplateException {

	public UnknownVariableException(String vn, OntObject ontObject) {
		super("variable name "+vn+" not in "+ontObject);
	}

}
