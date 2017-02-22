package fr.colinleverger

import net.ruippeixotog.scalascraper.browser._
import net.ruippeixotog.scalascraper.model.Document
import java.net._

object beerScraperScala extends App {

  case class beer(
    entry: Int,
    beerName: String,
    brewery: String,
    location: String,
    style: String,
    abv: String,
    ibus: String
  )

  val browser = JsoupBrowser()

  /**
    * get document from URL
    *
    * @param url
    * @return
    */
  def getDocument(url: String): Option[Document] = {
    val browser = JsoupBrowser()
    try {
      Option(browser.get(url))
    } catch {
      case _ @ (_ :UnknownHostException |_ : IllegalArgumentException) => None
    }
  }

}
