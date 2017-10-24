import scala.xml._

object Tagger
{
  def getTextButNotIn(x:Node,tagName:String):String =
   {
       if (x.isInstanceOf[Text])
         x.text
       else 
         x.child.filter(_.label != tagName).map(getTextButNotIn(_,tagName)).mkString("")
   }

  def vertical(taggedText: Map[String,Array[String]]) =
  {
    val keys = taggedText.keys.toList
    //val triples = (0 to tokenProperties("word").size-1).map( i => keys.map(k => tokenProperties(k)(i)))
    (0 to taggedText("word").size-1).map(
        i => keys.map(k => taggedText(k)(i)).mkString("\t")
     ).mkString("\n")
  }

}

trait Tagger 
{
   import Tagger._
   
   def urlFor(text:String):String
   
   def taggedDocument(text:String):Elem = XML.load(urlFor(text))
   
   def tag(text:String):Map[String,Array[String]] =
   {
     println("Text length: " + text.length)
     val d = taggedDocument(text)
     Console.err.println(d.toString)
     val z = (d \\ "w").map(w => 
       List(
           ("word", getTextButNotIn(w,"interp")), 
           ("lemma", (w \ "@lemma").text), 
           ("pos", (w \ "@type").text)).toMap)
           
       val properties = z.flatMap(x => x.keys).toSet.toList
       val l = properties.map( (p:String) => (p -> z.map(m => m(p)).toArray)).toMap
       l  
   }
} 

object babTagger extends Tagger
{
   override def urlFor(text:String) = s"http://inl-labs.inl.nl/succeed/text?input=${java.net.URLEncoder.encode(text)}&format=text&tagger=bab-tagger&output=raw" 
   
   def main(args:Array[String]):Unit = 
    {
      val tagged = tag("""" Ghy syt een eesel, die syn sinnen hevet verloeren. Swyght!
        In den beginne schiep Godt den Hemel, ende de Aerde.
 De Aerde nu was woest ende ledich, ende duysternisse was op den afgront: ende de Geest Godts sweefde op de Wateren.
 Ende Godt seyde: Daer zy Licht: ende daer wert Licht.
Ende Godt sach het Licht, dat het goet was: ende Godt maeckte scheydinge tusschen het Licht, ende tusschen de duysternisse.  
        """ )
      println(Tagger.vertical(tagged))
    }
}

object chnTagger extends Tagger
{
   override def urlFor(text:String) = s"http://openconvert.clarin.inl.nl/openconvert/text?input=${java.net.URLEncoder.encode(text)}&format=text&tagger=chn-tagger&output=raw&to=tei" 
   
    def main(args:Array[String]):Unit = 
    {
      val tagged = tag("""Je ben een ezel die zijn verstand kwijt is. Zwijg!""" )
      println(Tagger.vertical(tagged))
    }
}
  
chnTagger.main(args) 
babTagger.main(args)
