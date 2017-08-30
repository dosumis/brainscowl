package dosumis.brainscowl

import org.semanticweb.elk.owlapi.ElkReasonerFactory
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.reasoner.InferenceType
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.util.SimpleShortFormProvider
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter
import org.semanticweb.owlapi.search.EntitySearcher
import org.semanticweb.owlapi.expression.ShortFormEntityChecker
import org.semanticweb.owlapi.model._
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat
import org.semanticweb.owlapi.formats.TurtleDocumentFormat
import scala.util.control.Exception
//import org.slf4j.impl.StaticLoggerBinder // TODO: Add logger import to build.sbt

// See Brain for how to use the following to roll class expressions from MS:
// in 3.5: import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxClassExpressionParser
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser
// TODO Need to clarify plan for BrainScowl.
// EntitySearcher in OWL-API 4 has made finding things easier.

import scala.collection.JavaConversions._
import collection.JavaConverters._

import collection.mutable.ArrayBuffer
import java.io.File
import java.util.Set

//var fu = ScowlBrain() # No ontology initialised. But has factory and manager.
//var fu = BrainScowl('owl file uri', 'default base URI') Providing an owl file URI initilises ontology.
//fu.learn('URI' OR  'filename') # Should follow imports. Only one learn allowed => no opaque mashing together.  Following imports means need a concept of ownership for OWL entities and axioms.  Edits should be only to directly loaded ontology.
//
//fu.merge(bar) Where bar is another scowlbrain object. ???
//fu.getOntology()
//fu.getTypes(Ind)
//fu.getParentClasses(Ind)
//fu.getAnnotations(AP, OWLEntity)
//
//fu.save('file path')
//fu.sleep()

// Keep it simple stupid!
// Constructor takes new ontology or creates one blank one. 
// .learn method allows ontology to be loaded from string or IRI.  Overwriting existing?
// class bar (var a: String = "", var b: String = "", var c: String = "") { def this(a: String) { this(a, "", "") }}


// TODO: Add in URI string recognition, assuming http or https. 
// => string values passed to methods may be short form or URI

// Exception spec here - should probably be pushed to a separate file.
case class UnknownOwlEntityException(message: String) extends Exception(message)

class BrainScowl (
    val file_path: String, val iri_string: String, val base_iri: String) {
    
    def this(file_path: String) {
    // Construct from file
      this(file_path = file_path, iri_string = "", base_iri = "")
    }
    
    def this(iri_string: String, base_iri: String) {
     // Construct new ontology from iri_string.
      this(file_path = "", iri_string = iri_string, base_iri = base_iri)
    }
    // Missing a constructor for loading from an iri !
    
    val factory = OWLManager.getOWLDataFactory
    val manager = OWLManager.createOWLOntologyManager
    val simple_sfp = new SimpleShortFormProvider

    
    
    // Quick and dirty job on control structure here.  Needs some work. 
    // Feels like some code should be pushed into auxiliary constructors. 
    // TODO: investigate how scope works with auxilliary constructors.
    
    // https://github.com/owlcs/owlapi/blob/version4/contract/src/test/java/org/semanticweb/owlapi/examples/Examples.java#L1072

    // Should also probably add an argument that allows loading ontology from IRI.
    var ontology = if (this.file_path.isEmpty) {
        OWLManager.createOWLOntologyManager.createOntology(IRI.create(this.iri_string))
      } else {
        manager.loadOntologyFromOntologyDocument(new File(file_path))
    }
    
    // TODO: make a lookup by label - ideally one that auto-updates
    // How to get all entities in ontology?
    
    // see https://github.com/liveontologies/elk-reasoner/wiki/ElkOwlApixs
    var reasoner = new ElkReasonerFactory().createReasoner(ontology)  //TODO: work out how to hook this up to logger.
    reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY)

    private var onts = collection.mutable.Set(ontology) 
    // scala.collection.JavaConversions._ magic takes care of casting onts 
    // to appropriate java type for the following constructor:   
    var bi_sfp = new BidirectionalShortFormProviderAdapter(manager, onts, simple_sfp)
    
    def check_then_get(short_form: String) :OWLEntity = {
      val e = this.bi_sfp.getEntity(short_form)
      if (e == null) {
        throw UnknownOwlEntityException(s"Unknown OWL Entity ${short_form}")
      }
      else {
        return e
      }
    }
      
    def add_axiom(owl_axiom: OWLAxiom) {
      this.manager.addAxiom(this.ontology, owl_axiom)
    }
    
    def annotateOntology(ann: OWLAnnotation) {
      val oa = new AddOntologyAnnotation(this.ontology, ann)
      this.manager.applyChange(oa)
    }
    
    def getClass(short_form: String): OWLClass = {
      val e = this.bi_sfp.getEntity(short_form)
      return e.asOWLClass()
    }
    
    def add_import(target: String) {
      val importDeclaration=factory.getOWLImportsDeclaration(IRI.create(target))
      manager.applyChange(new AddImport(ontology, importDeclaration))
    }
    
   def getAnnotationsOnEntity (query_short_form: String) 
   : Array[OWLAnnotation] = {
      val e = check_then_get(query_short_form)
      val annotations =  EntitySearcher.getAnnotations(e, ontology)
      return annotations.asScala.toArray      
   }
   
   def getSpecAnnotationsOnEntity (query_short_form: String, ap_short_form: String) 
   : Array[OWLAnnotation] = {
      val ap = check_then_get(ap_short_form).asOWLAnnotationProperty()
      val e = check_then_get(query_short_form)
      val annotations = EntitySearcher.getAnnotations(e, ontology, ap)
      return annotations.asScala.toArray
      }
   
   def getSpecTextAnnotationsOnEntity (query_short_form: String, ap_short_form: String) 
     : ArrayBuffer[String] = {
     /** Returns a list of strings that are the values of annotations using 
      *  the annotation property specified using ap_short_form on the owl entity 
      *  specified using query_short_form.  */
     // TODO - add test that value realy is text.
       val ann = this.getSpecAnnotationsOnEntity(query_short_form, ap_short_form)
       val out = ArrayBuffer[String]()
       for (a <- ann) {
          if (a.getValue.asLiteral.get.getDatatype.toString == "http://www.w3.org/2001/XMLSchema#string") {
              out.append(a.getValue.asLiteral.get.getLiteral) // Should probably make this a try except....
          }
       }
     return out
   }
    
  
   def ms_string_2_classExpression (ms_string: String) :OWLClassExpression = {
     val ms_parser = OWLManager.createManchesterParser()  
     ms_parser.setStringToParse(ms_string)
     ms_parser.setDefaultOntology(this.ontology)
     val ec  = new ShortFormEntityChecker(this.bi_sfp)
     ms_parser.setOWLEntityChecker(ec)
     return ms_parser.parseClassExpression()
   }
   
//   def getEntityByLabel (label: String) :OWLEntity = {
//   }
   
   // TODO: All of the following need to take class expressions!
    def getSubClasses(ms_class_expression: String): Set[OWLClass]  = {
      val c = this.ms_string_2_classExpression(ms_class_expression)
      return this.reasoner.getSubClasses(c, false).getFlattened
    }
    
    def getSuperClasses(ms_class_expression: String): Set[OWLClass]  = {
      val c = this.ms_string_2_classExpression(ms_class_expression)
      return this.reasoner.getSuperClasses(c, false).getFlattened
    }
    
    def getInstances(ms_class_expression: String): Set[OWLNamedIndividual]  = {
      val c = this.ms_string_2_classExpression(ms_class_expression)
      return this.reasoner.getInstances(c, false).getFlattened.asScala
    }
    
    def getTypes(iri_string :String = "", sfid :String = ""): 
      Iterable[OWLClassExpression] = {
        // TODO: Better
        /* iri_string: An iri string referencing an individual
        ont: An owlAPI ontology object for an ontology that 
        includes the referenced individual.
        Returns an iterable set of class expressions
        */
        // Relies on 'if' clause returning a value.  That value must be an OWLIndividual
        // for next part of code to work.
        // Would be nicer with one arg that worked out if short_form or iri...
        val i = 
          if (!sfid.isEmpty) {
            this.bi_sfp.getEntity(sfid).asOWLNamedIndividual
          }
          else if (!iri_string.isEmpty()) { 
            val iri = IRI.create(iri_string)
            this.factory.getOWLNamedIndividual(iri)
          }
        else {
          this.factory.getOWLNamedIndividual(IRI.create("http://fu.bar"))
          // Throw exception here!
            //warnings.warn("Method requires either iri string or shortFormID to be specified")
          }
        return EntitySearcher.getTypes(i, this.ontology).toIterable// But 
    }

    def get_version_iri(): String = {
       //* Wot it says on' tin.  Returns string*/
        val oid = this.ontology.getOntologyID()
        val version_iri = oid.getVersionIRI().get
        return version_iri.toString()    
     }
      
     def get_ontology_iri(): String = {
       //* Wot it says on' tin.  Returns string*/
        val oid = this.ontology.getOntologyID()
        return oid.getOntologyIRI().get.toString()
      }
     

   
     def save(file_path: String, syntax: String  = "ofn") {
       // TBA: method for choosing syntax
        var syn : OWLDocumentFormat =  new FunctionalSyntaxDocumentFormat()
        
        if (syntax == "ofn") {
           syn = new FunctionalSyntaxDocumentFormat()
        }
        else if (syntax == "rdfxml") {
           syn = new RDFXMLDocumentFormat()
        }
        else if (syntax == "ttl") {
           syn = new TurtleDocumentFormat()
        }
        else {
          println("Unrecognised syntax specification " +syntax + ".Defaulting to functional syntax.")
          // Probably better to raise exception here.
        }
        val f = new File(file_path);
        manager.saveOntology(ontology, syn, IRI.create(f.toURI())); 
       
     }
     
     def sleep() {
       this.reasoner.flush()
       this.reasoner.dispose()
     }
  
}

