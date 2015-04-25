/**
 * Created by spandanbrahmbhatt on 4/2/15.
 */
object Biography extends App{


  val NLP = NLPProcessing("Bill Cook is the president and chief operating officer at Pivotal. Cook brings more than 30 years of IT industry experience in enterprise software, data warehousing, business intelligence and storage. Prior to Pivotal, Cook served as president of EMC’s Greenplum division and served as CEO through EMC’s acquisition of Greenplum in 2010. Cook was instrumental in growing Greenplum from a startup to helping EMC become a leader in Big Data, including acquiring the gold standard in agile software development, Pivotal Labs. Cook has held many leadership positions, including 19 years at Sun Microsystems where he most recently served as senior vice president of US Sales.")
  val ext = Extraction(NLP.extractData())

  println(ext.getName())

  val data = ext.generateData
  data.foreach(i => {
    i.foreach(print)
    println()
  })




}
