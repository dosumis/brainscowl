package dosumis.brainscowl.test
import dosumis.brainscowl.BrainScowl
import scala.collection.JavaConversions._
import org.phenoscape.scowl._


import org.scalatest.FunSpec

class BrainScowlSpec extends FunSpec {
  val go = new BrainScowl(file_path = "resources/go-simple.ofn")
  val newont = new BrainScowl("http://fu.bar/fubar.owl", "http://fu.bar/")
  describe("Test get ontology iri") {
    it("works"){
      assert(go.get_ontology_iri() == "http://purl.obolibrary.org/obo/go.owl" )
    }
  }
  describe("Test set ontology iri") {
    it("works"){
      assert(newont.get_ontology_iri() == "http://fu.bar/fubar.owl")
    }
  }
  describe("Test getSpecTextAnnotationsOnEntity") {
    it("works"){
      assert(go.getSpecTextAnnotationsOnEntity(query_short_form = "GO_0098953", 
       ap_short_form ="hasOBONamespace").head == "biological_process")
    }
  }
//  describe("Test add axiom to ontology") {
//     val a = Class(newont.base_iri + "fu")
//     val b = Class(newont.base_iri + "bar")
//     newont.add_axiom(a SubClassOf b)
//  }
}




