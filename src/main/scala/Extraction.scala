import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Map}

/**
 * Created by spandanbrahmbhatt on 4/2/15.
 */
class Extraction {
  var data : Map[Int,Map[String,List[String]]] = _
  val POSTags = List("NN","NNS")
  def getName() :String= {
        var nameList = new ListBuffer[String]
        for((k,v) <- data){
          val tokens = v.get("tokens").getOrElse(Nil)
          val ner = v.get("ner").getOrElse(Nil)
          nameList++=getPostition(ner,tokens)
        }
        val nameMap = nameList.groupBy(i=>i).mapValues(_.size)
        nameMap.find(_._2== nameMap.valuesIterator.max).getOrElse(("",-1))._1
    }

  def getPostition(ner : List[String], tokens : List[String]) = {
        var posi = new ListBuffer[Int];
        ner.zipWithIndex.foreach{
          case("PERSON", i) => posi+=i
          case _ => None
        }
        var names = new ListBuffer[String]
        posi.foreach(i => {
          if (posi.contains(i+1)){ names+=(tokens(i)+ " " + tokens(i+1))}
        })
        names.toList
    }

  def extractLineContent(linedata : Map[String,List[String]] ) = {
    var tokens = collection.mutable.Map() ++ linedata.get("tokens").get.zipWithIndex.map(i => (i._2,i._1)).toMap
    var ner  = collection.mutable.Map() ++ linedata.get("ner").get.zipWithIndex.map(i => (i._2,i._1)).toMap
    var tags = collection.mutable.Map() ++ linedata.get("tags").get.zipWithIndex.map(i => (i._2,i._1)).toMap
    val extractedData = ListBuffer[String]()
    val orgPositionList = getTokens(ner,"ORGANIZATION")
    val locPositionList = getTokens(ner,"LOCATION")
    var datePositionList = getTokens(ner,"DURATION")
    datePositionList = datePositionList ++ getTokens(ner,"DATE")
    var orgData = "Company : "
    var locData = "Location : "
    var dateData = "Date : "
    orgPositionList.foreach(i => {
      i.foreach(j => orgData+=tokens(j) + " ")
      orgData += " -- "
    })
    locPositionList.foreach(i => {
      i.foreach(j => locData+=tokens(j) + " ")
      locData += " -- "
    })
    datePositionList.foreach(i =>{
      i.foreach(j => dateData+=tokens(j) + " ")
      dateData += " -- "
    })
    /*val orgPositionFList = orgPositionList.flatten
    val locPositionFList = locPositionList.flatten
    val datePositionFList = datePositionList.flatten
    tokens = tokens.filter(i => !orgPositionFList.contains(i._1))
    tokens = tokens.filter(i => !locPositionFList.contains(i._1))
    tokens = tokens.filter(i => !datePositionFList.contains(i._1))
    ner = ner.filter(i=> !orgPositionFList.contains(i._1))
    ner = ner.filter(i=> !locPositionFList.contains(i._1))
    ner = ner.filter(i=> !datePositionFList.contains(i._1))
    tags = tags.filter(i=> !orgPositionFList.contains(i._1))
    tags = tags.filter(i=> !locPositionFList.contains(i._1))
    tags = tags.filter(i=> !datePositionFList.contains(i._1)) */
    val requiredTags = getTokens(tags,"NN")
    var nameNounTokensList = ListBuffer[String]()
    requiredTags.foreach(i => {
      var tempName = ""
      i.foreach(j => tempName+=tokens(j) + " ")
      nameNounTokensList += tempName
    })
    val cortical = new CorticalExt
    val nameNounTokensMap = nameNounTokensList.zipWithIndex.map(i => (i._2,i._1)).toMap
    val categories = nameNounTokensMap.map(i => (i._1,cortical.findCategory(i._2)))
    extractedData += orgData + " || "
    extractedData += locData + " || "
    extractedData += dateData + " || "
    val designation = "Designation : " + categories.filter(i => i._2.equals("Designation")).keys.map(i => nameNounTokensMap(i)).toList.mkString(" ")
    val areaOfWork = "Area of Work : " + categories.filter(i => i._2.equals("Area")).keys.map(i => nameNounTokensMap(i)).toList.mkString(" ")
    extractedData+=designation + " || "
    extractedData+=areaOfWork + " || "
    extractedData.toList
  }

  def generateData = {
    val genData = data.map(i => extractLineContent(i._2)).toList
    genData
  }


  def getTokens(map : Map[Int, String], term : String)  = {
    var groupedLocations =  new ListBuffer[List[Int]]()
    var locationGroup = new ListBuffer[Int]()
    for (a <- 0 until map.size) {
      val entry = (a,map(a))
      if (entry._2.contains(term)) {
        locationGroup += entry._1
      }
      else if (!locationGroup.isEmpty) {
        groupedLocations+=locationGroup.toList
        locationGroup = new ListBuffer[Int]()
      }
    }
    if (!locationGroup.isEmpty) {
      groupedLocations+=locationGroup.toList
    }
    groupedLocations.toList
  }
}

object Extraction{
  def apply(data :Map[Int,Map[String,List[String]]]) : Extraction = {
    val ext = new Extraction
    ext.data = data
    ext
  }

}
