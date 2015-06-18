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
        var posi = new ListBuffer[Int]
        ner.zipWithIndex.foreach{
          case("PERSON", i) => posi+=i
          case _ => None
        }
        var names = new ListBuffer[String]
        posi.foreach(i => {
          if (posi.contains(i+1)){names+=(tokens(i)+ " " + tokens(i+1))
            posi-=i
            posi-=(i+1)
          }
          else {
            names+= tokens(i)
            posi-=i
          }
        })
        names.toList
    }

  def dataClean() ={
    data.foreach(i => cleanNN(i._1,i._2))
    data.foreach(i => combineData(i._1,i._2))
    data.foreach(i => combineData(i._1,i._2))
    data.foreach(i => combineNN(i._1,i._2))
  }

  def combineNN (count: Int, linedata : Map[String,List[String]]) = {
    var tokens = linedata.get("tokens").get
    var ner  = linedata.get("ner").get
    var tags = linedata.get("tags").get
    var flag = true
    var a = 0
    while(flag){
      if(ner(a).equals("O") && checkNextNN(tags,a)) {
        tokens = tokens.slice(0,a) ::: List(tokens(a)+ " " + tokens(a+1)) ::: tokens.slice(a+2,tokens.length) ::: Nil
        ner = ner.slice(0,a) ::: ner.slice(a+1,ner.length) ::: Nil
        tags = tags.slice(0,a) ::: tags.slice(a+1,tags.length) ::: Nil
      }
      if(a==tokens.length-2) {flag=false}
      a+=1
    }
    val newData = Map("tokens" -> tokens, "ner" -> ner, "tags" -> tags)
    data-=count
    data(count) = newData
  }

  def checkNextNN (li :List[String], i : Int ) = {
    if(li(i+1).equals("NN") && li(i).equals("NN")) true else false
  }

  def cleanNN (count: Int, linedata : Map[String,List[String]]) = {
    val tokens = linedata.get("tokens").get
    val ner  = linedata.get("ner").get
    var tags = linedata.get("tags").get
    tags = tags.map(i => if(i.contains("NN")) "NN" else i)
    val newData = Map("tokens" -> tokens, "ner" -> ner, "tags" -> tags)
    data-=count
    data(count) = newData
  }

  def combineData (count: Int, linedata : Map[String,List[String]]) = {
    var tokens = linedata.get("tokens").get
    var ner  = linedata.get("ner").get
    var tags = linedata.get("tags").get
    var flag = true
    var a = 0
    while(flag){
      if(checkNextSame(ner,a)) {
        tokens = tokens.slice(0,a) ::: List(tokens(a)+ " " + tokens(a+1)) ::: tokens.slice(a+2,tokens.length) ::: Nil
        ner = ner.slice(0,a) ::: ner.slice(a+1,ner.length) ::: Nil
        tags = tags.slice(0,a) ::: tags.slice(a+1,tags.length) ::: Nil
      }
      if(a==tokens.length-2) {flag=false}
      a+=1
    }
    val newData = Map("tokens" -> tokens, "ner" -> ner, "tags" -> tags)
    data-=count
    data(count) = newData

  }

  def checkNextSame(li :List[String], i : Int ) = {
    if(li(i+1).equals(li(i)) && !li(i).equals("O")) true else false
  }

  def extractLineContent(linedata : Map[String,List[String]] ) = {
    val cortical = new CorticalExt
    val tokens = linedata.get("tokens").get
    val ner  = linedata.get("ner").get
    val tags = linedata.get("tags").get
    val res = ListBuffer[(String,String)]()
    for(a <- 0 until tokens.length ){
      val token = tokens(a)
      if(!ner(a).equals("O")){
        res.append((token,ner(a)))
      }
      else {
        if(!tags(a).equals("NN")){
          res.append((token,"O"))
        }
        else{
          res.append((token,cortical.findCategory(token)))
        }
      }
    }
    res.toList
  }

  def generateData = {
    dataClean()
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

  def getJSON = {
    val result = generateData
    var json = "{ "
    for(a <- 0 until result.length){
      json+="\"" + (a+1) + "\": [ "
      result(a).foreach(i => json+= "{\"token\":\"" + i._1 + "\", \"tag\":\"" + i._2 + "\"},")
      json=json.dropRight(1)
      json+="],"
    }
    json=json.dropRight(1)
    json+="}"
    json
  }
}

object Extraction{
  def apply(data :Map[Int,Map[String,List[String]]]) : Extraction = {
    val ext = new Extraction
    ext.data = data
    ext
  }

}
