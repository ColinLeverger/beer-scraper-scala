package fr.colinleverger

import java.io.PrintWriter
import java.net._

import net.ruippeixotog.scalascraper.browser._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Document
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization.{read, write}

object BeerScraperScala extends App {
  val beersUrl: String = "http://craftcans.com/db.php?search=all&sort=beerid&ord=desc&view=text"

  /**
    * Case class defining a beer
    *
    * @param entry
    * @param beerName
    * @param brewery
    * @param location
    * @param style
    * @param size
    * @param abv
    * @param ibus
    */
  case class Beer(
    entry: String,
    beerName: String,
    brewery: String,
    location: String,
    style: String,
    size: String,
    abv: String,
    ibus: String
  )

  /**
    * Get document from URL
    *
    * @param url
    * @return Option[Document]
    */
  def getDocument(url: String): Option[Document] = {
    val browser = JsoupBrowser()
    try {
      Option(browser.get(url))
    } catch {
      case e@(_: UnknownHostException | _: IllegalArgumentException) => None
    }
  }

  /**
    * Get beer list from document
    *
    * @param doc
    * @return
    */
  def getBeersList(doc: Option[Document]): Option[List[Iterable[String]]] = {
    doc >> elementList("#content table tbody tr:nth-child(3) td table:nth-child(4) tr") >> texts("td")
  }

  /**
    * Get beers from beersHtmlList
    *
    * @param beersHtmlList
    * @return List[beer]
    */
  def getBeers(beersHtmlList: Option[List[Iterable[String]]]): List[Beer] = {
    for {
      beerHtmlIterable <- beersHtmlList.getOrElse(List());
      beerHtlm = beerHtmlIterable.toList
    } yield {
      Beer(
        entry = beerHtlm(0), beerName = beerHtlm(1), brewery = beerHtlm(2),
        location = beerHtlm(3), style = beerHtlm(4), size = beerHtlm(5),
        abv = beerHtlm(6), ibus = beerHtlm(7)
      )
    }
  }

  // Get document from URL
  val doc = getDocument(beersUrl)

  // Get a list of all the beers which are present in the HTML
  val beersHtmlList = getBeersList(doc)

  // Treat the list to create a list of 'beer' objects
  val beers = getBeers(beersHtmlList)
  implicit val formats = DefaultFormats

  // Write and print the result of experimentations in a json string
  val jsonString = write(beers)
  new PrintWriter("beers.json") {
    write(jsonString);
    close
  }

}
