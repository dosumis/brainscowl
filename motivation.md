# A facade for easy scripting with OWL in scala, inspired by Brain.

## Motivation:

Scripting to and from OWL typically requires working with short_form IDs.  [Brain](https://github.com/loopasam/Brain/wiki) provides a simple, scripty way to work with OWL that hides the complexity of object types and design patterns in the OWL-API.  But the code-base has become stale and has a number of drawbacks:

* It can be a pain to work with it with full IRIs for both the whole ontology and owl entities: it is all too easy for default values  to get in the way.
* Its methods for pulling information from OWL (e.g. returning axioms or annotations on a class) are rather limited.

In the spirit or Brain, interaction via text strings is preferred wherever possible.  This includes axioms, e.g a request for 

But:  it can be useful to have easy access to the 

## Why Scala?

[Scowl](https://github.com/phenoscape/scowl) provides another straighforward scripty way to write OWL. A mash-up with Brain-type functionality would => plenty of fleflexibility.  Other tools also being written in Scala (e.g. DOSDP parser).

Scala is a bit heavyweight though.  It's a bit too complex (& Java-like) for occasional coders.  The aim should be to simplify with wrapper functions and via consistent choice of data types for passing around collections maps etc.

### Sketch of scowlBrain

```scala
var fu = BrainScowl("file path") # Opens ontology from file.
var fu = BrainScowl("owl file uri", "default base URI") Providing an owl file URI initilises ontology.
# Option to load from full URI
fu.learn('URI') # Should follow imports. Only one learn allowed => no opaque mashing together.  Following imports means need a concept of ownership for OWL entities and axioms.  Edits should be only to directly loaded ontology.

fu.merge(bar) Where bar is another scowlbrain object. ???
fu.getOntology()
fu.getTypes(Ind)
fu.getParentClasses(Ind)
fu.getAnnotations(AP, OWLEntity)

fu.save("file path")
fu.sleep()

```

Data types:




### Exception handling

....???






