package io.yom.metamodel;

/*

YAML Ontology Datamodel: Design Patterns

A MataClass is a Class-like entity that specifies a particular pattern for
all OWLClasses that implement it. Analogously, MetaObjectProperty and
MetaIndividual specify patterns for implementation of the corresponding
OWLObjects.

A MetaObject has one or more {@link Variable}s, and or more
Templates, for different axiom types. Each Template takes a list of
variables as input.

When an OWLEntity instantiates a MetaObject, it specifies the
values of the Variables in the MetaObject. A Generator can use
these to create the OWLEntity and associated axioms.

 */
