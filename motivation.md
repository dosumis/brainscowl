# A facade for easy scripting with OWL in scala, inspired by Brain.

## Motivation:

Scripting to and from OWL typically requires working with short_form IDs.  Brain provides a simple, scripty way to work with OWL that hides the complexity of object types and design patterns in the OWL-API.  But the code-base has become stale and has a number of drawbacks:

It can be a pain to work with it with full IRIs for both file.  All too easy for defaults to get in the way.

Its methods for pulling information from OWL are rather limited.

## Why Scala?

Scowl provides another straighforward scripty way to write OWL. A mash-up with Brain-type functionality would => plenty of fleflexibility.  Other tools also being written in Scala (e.g. DOSDP parser).

Scala is a bit heavyweight though.  It's a bit too complex (& Java-like) for occasional coders.

### Sketch of scowlBrain

var fu = ScowlBrain() # No ontology initialised. But has factory and manager.
var fu = BrainScowl('owl file uri', 'default base URI') Providing an owl file URI initilises ontology.
fu.learn('URI' OR  'filename') # Should follow imports. Only one learn allowed => no opaque mashing together.  Following imports means need a concept of ownership for OWL entities and axioms.  Edits should be only to directly loaded ontology.

fu.merge(bar) Where bar is another scowlbrain object. ???
fu.getOntology()
fu.getTypes(Ind)
fu.getParentClasses(Ind)
fu.getAnnotations(AP, OWLEntity)

fu.save('file path')
fu.sleep()

Options for arg:  short_form or owlEntity or IRI string (or IRI?) - Also allow obo short_form?

How to achieve such flexibility?

1. Scala - Look up how to make multiple constructors
2. Control of casting for output?
   Return object, then use methods on object to get back IRIs, short_form, strings etc:

scala> val c = Class("http://fu.bar/bash")
c: org.semanticweb.owlapi.model.OWLClass = <http://fu.bar/bash>

scala> c
res31: org.semanticweb.owlapi.model.OWLClass = <http://fu.bar/bash>

scala> c.getIRI
res32: org.semanticweb.owlapi.model.IRI = http://fu.bar/bash

scala> c.getIRI.getShortForm
res33: String = bash

scala> c.getIRI.toString
res34: String = http://fu.bar/bash

### Exception handling

....???






