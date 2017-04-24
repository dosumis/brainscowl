package org.dosumis.brainscowl.test
import main.scala.org.dosumis.brainscowl.BrainScowl
import scala.collection.JavaConversions._
import org.scalatest._
import org.phenoscape.scowl._

// Quick and dirty tests.  
// TODO move to full unit testing.

object brainscowltest extends(App) {
  
  def test_load_ont() { 
   val bs = new BrainScowl(file_path = "resources/go-simple.owl")
   println(bs.get_ontology_iri())
   println(bs.get_version_iri())
   println(bs.getSpecTextAnnotationsOnEntity(query_short_form = "GO_0098953", 
       ap_short_form ="hasOBONamespace").head)
   bs.sleep()
  }
   
   def test_new_ont(iri_string: String, base_iri :String ) { 
     val bs = new BrainScowl(iri_string = iri_string, base_iri = base_iri )
     println(bs.get_ontology_iri())
     val a = Class(bs.base_iri + "fu")
     val b = Class(bs.base_iri + "bar")
     bs.add_axiom(a SubClassOf b)
     println(bs.get_ontology_iri())
     bs.save("bash.owl")
     bs.sleep()
   }
   
  test_load_ont()
  test_new_ont( "http://fu.bar/bash.owl", "http://fu.bar/")
}


