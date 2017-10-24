import scala.xml._

object LexiconService
{
  case class LexiconService(serviceURL: String, database: String)
  {
    def getWordforms(lemma: String):List[String] =
    {
      (XML.load(s"$serviceURL/get_wordforms?database=$database&lemma="
        + lemma.toLowerCase)
        \\ "found_wordforms").toList.map(_.text.toLowerCase).filter(_.matches("^(\\p{L})*$"))
    }
  
    def getLemmata(wordform: String):List[(String,String)] =
    {
      (XML.load(s"$serviceURL/get_lemma?database=$database&wordform=" + wordform.toLowerCase)
        \\ "found_lemmata").toList.map(e => ((e \ "lemma").text, (e \ "pos").text))
    }
  }
  val serviceURL = "http://sk.taalbanknederlands.inl.nl/LexiconService/lexicon"
  val database = "lexicon_service_db"

  def main(args: Array[String]) : Unit =
  {
    val ls = new LexiconService(serviceURL, database)
    ls.getWordforms("wereld").foreach(println)
    ls.getLemmata("ook").foreach(println)
  }
}


