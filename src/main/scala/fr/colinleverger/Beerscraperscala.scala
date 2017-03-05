package fr.colinleverger

import java.io.PrintWriter
import java.net._

import net.ruippeixotog.scalascraper.browser._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Document
import org.json4s.DefaultFormats
import purecsv.safe._

object BeerScraperScala extends App {
  val beersUrl: String = "http://craftcans.com/db.php?search=all&sort=beerid&ord=desc&view=text"

  /**
    * Case class defining a beer data, before treatment & cleaning of the data
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
  case class rawBeer(
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
    * Case class defining a brewery
    *
    * @param id
    * @param name
    * @param location
    * @param zipCode
    */
  case class Brewery(
    id: Int,
    name: String,
    location: String,
    zipCode: String
  )

  /**
    * Case class defining a cleaned beer
    *
    * @param entry
    * @param beerName
    * @param breweryId
    * @param style
    * @param size
    * @param abv
    * @param ibus
    */
  case class Beer(
    entry: String,
    beerName: String,
    breweryId: Int,
    style: String,
    size: Float,
    abv: Option[Double],
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
  def getRawBeers(beersHtmlList: Option[List[Iterable[String]]]): List[rawBeer] = {
    for {
      beerHtmlIterable <- beersHtmlList.getOrElse(List());
      beerHtlm = beerHtmlIterable.toList
    } yield {
      rawBeer(
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
  val rawBeers = getRawBeers(beersHtmlList)

  /**
    * Clean the data
    *
    * @param rawBeers
    * @return (List[Brewery], List[Beer])
    */
  def cleanBeers(rawBeers: List[rawBeer]): (List[Brewery], List[Beer]) = {
    val breweries = getBreweries(rawBeers)
    val beers = getBeers(rawBeers, breweries)
    (breweries, beers)
  }

  /**
    * Create the list of the breweries present in this dataset.
    * Auto-increment "i" to index the breweries.
    *
    * @param beers
    * @return
    */
  def getBreweries(beers: List[rawBeer]): List[Brewery] = {
    val breweries = beers.groupBy(b => (b.brewery, b.location)).keys

    var i = 0
    for {b <- breweries.toList} yield {
      i = i + 1
      Brewery(
        i,
        b._1,
        b._2.takeWhile(c => c != ','),
        b._2.dropWhile(c => c != ',').drop(2)
      )
    }
  }

  /**
    * Create the cleaned list of beers with ref. to the breweries (breweries indexed)
    *
    * @param rawBeers
    * @param brewery
    * @return
    */
  def getBeers(rawBeers: List[rawBeer], brewery: List[Brewery]): List[Beer] = {
    for {be <- rawBeers.tail} yield {
      val abvFloat = be.abv.replaceAll("\\?", "") match {
        case str if !str.isEmpty =>
          Some(str.replaceAll("%", "").toDouble / 100)
        case _ => None
      }
      Beer(
        be.entry.replaceAll("\\.", ""),
        be.beerName,
        brewery.filter(_.location == be.location.takeWhile(c => c != ',')).head.id,
        be.style,
        be.size.split(" ")(0).toFloat,
        abvFloat,
        be.ibus
      )
    }
  }

  // Clean beer list
  val (breweries, beers) = cleanBeers(rawBeers)

  // Write and print the result of experimentations in a csv
  val csvBeers = beers.toCSV(";")
  val csvBreweries = breweries.toCSV(";")
  writeToDisk("beers.csv", csvBeers)
  writeToDisk("breweries.csv", csvBreweries)

  private def writeToDisk(name: String, csv: String): Unit = {
    new PrintWriter(name) {
      write(csv);
      close
    }
  }
}
