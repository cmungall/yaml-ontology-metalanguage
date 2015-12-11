package io.yom.metamodel;

import java.util.HashSet;
import java.util.Set;

/**
 * A MetaObject that specifies a design pattern for an implementing OWLClass
 * 
 * @author cjm
 *
 */
public class MetaClass extends MetaObject {
	
	private Template equivalentToTemplate;
	private Template textDefinitionTemplate;
	private Set<Template> subClassOfTemplates;
	private Set<Template> generalClassAxioms;
	
	
	/**
	 * @return the equivalentToTemplate
	 */
	public Template getEquivalentToTemplate() {
		return equivalentToTemplate;
	}
	/**
	 * @param equivalentToTemplate the equivalentToTemplate to set
	 */
	public void setEquivalentToTemplate(Template equivalentToTemplate) {
		this.equivalentToTemplate = equivalentToTemplate;
	}
	/**
	 * @return the textDefinitionTemplate
	 */
	public Template getTextDefinitionTemplate() {
		return textDefinitionTemplate;
	}
	/**
	 * @param textDefinitionTemplate the textDefinitionTemplate to set
	 */
	public void setTextDefinitionTemplate(Template textDefinitionTemplate) {
		this.textDefinitionTemplate = textDefinitionTemplate;
	}
	/**
	 * @return the subClassOfTemplates
	 */
	public Set<Template> getSubClassOfTemplates() {
		return subClassOfTemplates;
	}
	/**
	 * @param subClassOfTemplates the subClassOfTemplates to set
	 */
	public void setSubClassOfTemplates(Set<Template> subClassOfTemplates) {
		this.subClassOfTemplates = subClassOfTemplates;
	}
	/**
	 * @return the axiomTemplates
	 */
	public Set<Template> getGeneralClassAxioms() {
		return generalClassAxioms;
	}
	/**
	 * @param axiomTemplates the axiomTemplates to set
	 */
	public void setGeneralClassAxioms(Set<Template> axiomTemplates) {
		this.generalClassAxioms = axiomTemplates;
	}
	/**
	 * @param axiomTemplates the axiomTemplates to set
	 */
	public void addGeneralClassAxiom(Template axiomTemplate) {
		if (generalClassAxioms == null) {
			generalClassAxioms = new HashSet<>();
		}
		this.generalClassAxioms.add(axiomTemplate);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MetaClass [\n equivalentToTemplate=" + equivalentToTemplate
				+ "\n textDefinitionTemplate=" + textDefinitionTemplate
				+ "\n subClassOfTemplates=" + subClassOfTemplates
				+ "\n generalClassAxioms=" + generalClassAxioms + "]";
	}

	
	

}
