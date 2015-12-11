package io.yom.parser;

public class UnknownTagException extends Exception {

	public UnknownTagException(Object key) {
		super("Tag not known "+key);
	}

}
