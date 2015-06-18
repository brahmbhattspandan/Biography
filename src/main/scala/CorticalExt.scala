/**
 * Created by spandanbrahmbhatt on 4/3/15.
 */
import  io.cortical.services.RetinaApis
import io.cortical.rest.model.{Metric, Text}

class CorticalExt {

  val RETINA_NAME = "en_associative"
  val RETINA_IP = "api.cortical.io"
  val API_KEY = "5b9bd530-da47-11e4-a409-7159d0ac8188"
  val retinaApisInstance = new RetinaApis(RETINA_NAME, RETINA_IP, API_KEY)
  var designationList = List("ceo","cfo","co-founder","founder","trainee engineer","software engineer","programmer analyst","senior software engineer","system analyst","project lead","project manager","program manager","chief technical officer","assistant vice president","vice president","board of directors")
  var areaOfInterestList = List("virtualization", "cloud computing", "product development", "marketing", "enterprise software", "data warehousing", "business intelligence", "big data", "software development", "engineering", "product management", "infrastructure software", "architect")
  val designationListSmall = List("CEO", "CFO", "engineer", "programmer", "analyst")
  val areaofInterestListSmall = List("development", "architect", "data warehousing")
  val demoMetric = new Metric()

  def compareTerms(term1:String,term2:String) = {
    val compareApiInstance = retinaApisInstance.compareApi()
    var score = 0.0
    try {
      val metric = compareApiInstance.compare(new Text(term1), new Text(term2))
      score = rms(Seq(metric.getCosineSimilarity,1- metric.getEuclideanDistance,1 - metric.getJaccardDistance))
    }
    catch {
      case ex : Exception => {}
    }
    score
  }

  def findCategory(id:String)= {
    var category = "O"
    category = if (designationList.contains(id.toLowerCase)) "Designation" else "O"
    category = if (areaOfInterestList.contains(id.toLowerCase())) "Area" else "O"
    if(category.equals("O")){
      val desigScore = rms(designationListSmall.map(i => compareTerms(id,i)))
      val areaScore = rms(areaofInterestListSmall.map(i=> compareTerms(id,i)))
      category = if (desigScore < 0.12 && areaScore < 0.12) "O" else if (desigScore> areaScore) "Designation" else "Area"
    }
    category
  }

  def rms(nums: Seq[Double]) = math.sqrt(nums.map(math.pow(_, 2)).sum / nums.size)

  def addDesignation(term : String) : Unit = {designationList = designationList :+ term}

  def addAreaOfInterest(term : String) : Unit = {areaOfInterestList = areaOfInterestList :+ term}


}

