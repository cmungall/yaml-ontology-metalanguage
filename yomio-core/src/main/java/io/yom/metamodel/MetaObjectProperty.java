package io.yom.metamodel;

import java.util.HashSet;
import java.util.Set;

/**
* A MetaObject that specifies a design pattern for an implementing OWLObjectProperty
* 
* @author cjm
*
*/
public class MetaObjectProperty extends MetaObject {
	
	private Set<Template> subPropertyOfTemplates;
	
	private Set<Template> axiomTemplates;

	/**
	 * @return the subPropertyOfTemplates
	 */
	public Set<Template> getSubPropertyOfTemplates() {
		return subPropertyOfTemplates;
	}

	/**
	 * @param subPropertyOfTemplates the subPropertyOfTemplates to set
	 */
	public void setSubPropertyOfTemplates(Set<Template> subPropertyOfTemplates) {
		this.subPropertyOfTemplates = subPropertyOfTemplates;
	}

	/**
	 * @return the axiomTemplates
	 */
	public Set<Template> getAxiomTemplates() {
		return axiomTemplates;
	}

	/**
	 * @param axiomTemplates the axiomTemplates to set
	 */
	public void setAxiomTemplates(Set<Template> axiomTemplates) {
		this.axiomTemplates = axiomTemplates;
	}
	
	/**
	 * @param SubPropertyOfTemplate the SubPropertyOfTemplate to add
	 */
	public void addSubPropertyOfTemplate(Template subPropertyOfTemplate) {
		if (subPropertyOfTemplates == null) {
			subPropertyOfTemplates = new HashSet<>();
		}
		this.subPropertyOfTemplates.add(subPropertyOfTemplate);
	}	
		

}
