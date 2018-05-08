package dosumis.brainscowl.obo_style


case class defn(
    text: String,
    xrefs: Set[String]
    )
    
case class synonym(
    text: String,
    xrefs: Set[String],
    typ: String,
    scope: String
    )

case class term(
    // Could make a label object with a language attribute, but this would not be obo-ish
    labels: Array[String],
    iri: String,
    defn: defn,
    synonyms: Set[synonym],
    xrefs: Set[String],
    OWL_type: String, // Would be nice to specify that this must be oneof "Class", "Individual" etc
    short_form: String
    )
