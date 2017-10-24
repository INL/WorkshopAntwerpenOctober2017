import scala.xml._

object Tagger
{
   def bab_tagger_url(text:String):String = s"http://inl-labs.inl.nl/succeed/text?input=${java.net.URLEncoder.encode(text)}&format=text&tagger=bab-tagger&output=raw"
   def chn_tagger_url(text:String):String = s"http://openconvert.clarin.inl.nl/openconvert/text?input=${java.net.URLEncoder.encode(text)}&format=text&tagger=chn-tagger&output=raw&to=tei"

   def getTextButNotIn(x:Node,tagName:String):String =
       if (x.isInstanceOf[Text])
         x.text
       else
         x.child.filter(_.label != tagName).map(getTextButNotIn(_,tagName)).mkString("")

   def tag(f:String=>String, text:String) =
   {
     val d = XML.load(f(text))
     val z = (d \\ "w").map(w =>
       List(
           ("word", getTextButNotIn(w,"interp")),
           ("lemma", (w \ "@lemma").text),
           ("pos", (w \ "@type").text)))
     z.foreach(println)
   }
}

Tagger.tag(Tagger.bab_tagger_url,"De Aerde nu was woest ende ledich, ende duysternisse was op den afgront: ende de Geest Godts sweefde op de Wateren.")
