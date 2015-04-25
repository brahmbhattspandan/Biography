import edu.arizona.sista.processors.Processor

/**
 * Created by spandanbrahmbhatt on 4/2/15.
 */
class NLPProcessing {
    var inputString : String = ""

    def extractData() = {
        import  edu.arizona.sista.processors._
        import  edu.arizona.sista.processors.fastnlp.FastNLPProcessor
        import edu.arizona.sista.processors.corenlp.CoreNLPProcessor
        val proc:Processor = new CoreNLPProcessor(withDiscourse = true)
        val doc = annotateDoc(proc,inputString)
        var sentenceCount = 0
        import scala.collection.mutable.{Map,ListBuffer}
        var nlpData = Map[Int,Map[String,List[String]]]()
        for (sentence <- doc.sentences) {
            var tags = new ListBuffer[String]
            var tokens = new ListBuffer[String]
            var ner = new ListBuffer[String]
            sentence.words.toList.foreach(word => tokens+=word)
            sentence.tags.get.toList.foreach(tag => tags+=tag)
            sentence.entities.get.toList.foreach(tok => ner+=tok)
            val data = Map("tags" -> tags.toList, "tokens" -> tokens.toList, "ner" -> ner.toList)
            nlpData += (sentenceCount -> data)
            sentenceCount += 1
        }
        nlpData
    }

    def annotateDoc(proc : Processor, input : String) = {
        val doc = proc.mkDocument(input)
        proc.tagPartsOfSpeech(doc)
        proc.lemmatize(doc)
        proc.recognizeNamedEntities(doc)
        doc.clear()
        doc
    }
}

object NLPProcessing {
    def apply (input: String) : NLPProcessing = {
        val nlp = new NLPProcessing()
        nlp.inputString = input
        nlp
    }
}
