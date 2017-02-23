package fr.colinleverger

import org.scalatest._

class beerScraperScalaTests extends FlatSpec with Matchers {

  val beersUrl: String = "http://craftcans.com/db.php?search=all&sort=beerid&ord=desc&view=text"
  val doc = BeerScraperScala.getDocument(beersUrl)
  val beersHtml = BeerScraperScala.getBeersList(doc)
  val beers = BeerScraperScala.getBeers(beersHtml)

  "A beerScrapper" should " provide a function to get HTML from URL" in {
    assert(!BeerScraperScala.getDocument("http://colinleverger.fr").isEmpty)
    assert(BeerScraperScala.getDocument("thisIsNotAnUrl").isEmpty)
    assert(BeerScraperScala.getDocument("http://colinleverger.com").isEmpty)
    assert(!BeerScraperScala.getDocument("http://craftcans.com/db.php?search=all&sort=beerid&ord=desc&view=text").isEmpty)
    assert(!BeerScraperScala.getDocument(beersUrl).isEmpty)
  }
  it should "decompose the html in a raw non empty beer list" in {
    assert(beersHtml.getOrElse(None) != None)
  }

  "A getBeers function" should "provide a label in first value which contains BEER and LOCATION" in {
    assert(beers.head.beerName == "BEER")
    assert(beers.head.location == "LOCATION")
  }

  it should "provide a list of beers which is not empty" in {
    assert(!beers.isEmpty)
  }

  it should "find the 'Get Together' beer" in {
    val getTogether = beers.filter(_.beerName == "Get Together")
    assert(getTogether.nonEmpty)
    val theBeer = getTogether.head
    assert(theBeer.entry == "2692.")
    assert(theBeer.beerName == "Get Together")
    assert(theBeer.brewery == "NorthGate Brewing")
    assert(theBeer.location == "Minneapolis, MN")
    assert(theBeer.style == "American IPA")
    assert(theBeer.size == "16 oz.")
    assert(theBeer.abv == "4.5%")
    assert(theBeer.ibus == "50")
  }


}