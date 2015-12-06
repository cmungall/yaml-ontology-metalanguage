package io.yom.metamodel;

import org.semanticweb.owlapi.model.OWLClassExpression;

public class Variable {
	
	private String name;
	private String description;
	private OWLClassExpression fillerType;
	
	
	public Variable(String name) {
		super();
		this.name = name;
	}
	public Variable(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String toString() {
		return name;
	}

}
