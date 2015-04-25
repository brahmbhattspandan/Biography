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
  var designationList = List("CEO","CFO","co-founder","founder")
    var areaOfInterestList = List("Software","development")
  val demoMetric = new Metric()

  def compareTerms(term1:String,term2:String) = {
    val compareApiInstance = retinaApisInstance.compareApi()
    var score = 0.0
    try {
      val metric = compareApiInstance.compare(new Text(term1), new Text(term2))
      //compareApiInstance.com
      score = rms(Seq(metric.getCosineSimilarity,1- metric.getEuclideanDistance,1 - metric.getJaccardDistance))
    }
    catch {
      case ex : Exception => {}
    }
    score
  }

  def findCategory(id:String)= {
    val desigScore = rms(designationList.map(i => compareTerms(id,i)))
    val areaScore = rms(areaOfInterestList.map(i=> compareTerms(id,i)))
    var category = ""
    category = if (desigScore < 0.12 && areaScore < 0.12) "Garbage" else if (desigScore> areaScore) "Designation" else "Area"
    category
  }

  def rms(nums: Seq[Double]) = math.sqrt(nums.map(math.pow(_, 2)).sum / nums.size)

  def addDesignation(term : String) : Unit = {designationList = designationList :+ term}

  def addAreaOfInterest(term : String) : Unit = {areaOfInterestList = areaOfInterestList :+ term}


}

