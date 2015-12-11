package io.yom.metamodel;

import java.util.HashSet;
import java.util.Set;

/**
 * A MetaObject that specifies a design pattern for an implementing OWLIndividual
 * 
 * @author cjm
 *
 */
public class MetaIndividual extends MetaObject {
	
	private Set<Template> typeTemplates;
	private Set<Template> factTemplates;
	/**
	 * @return the typeTemplates
	 */
	public Set<Template> getTypeTemplates() {
		return typeTemplates;
	}
	/**
	 * @param typeTemplates the typeTemplates to set
	 */
	public void setTypeTemplates(Set<Template> typeTemplates) {
		this.typeTemplates = typeTemplates;
	}
	/**
	 * @return the factTemplates
	 */
	public Set<Template> getFactTemplates() {
		return factTemplates;
	}
	/**
	 * @param factTemplates the factTemplates to set
	 */
	public void setFactTemplates(Set<Template> factTemplates) {
		this.factTemplates = factTemplates;
	}
	
	/**
	 * @param typeTemplate the typeTemplate to add
	 */
	public void addTypeTemplate(Template typeTemplate) {
		if (typeTemplates == null) {
			typeTemplates = new HashSet<>();
		}
		this.typeTemplates.add(typeTemplate);
	}

	/**
	 * @param factTemplate the factTemplate to add
	 */
	public void addFactTemplate(Template factTemplate) {
		if (factTemplates == null) {
			factTemplates = new HashSet<>();
		}
		this.factTemplates.add(factTemplate);
	}	
	

}
