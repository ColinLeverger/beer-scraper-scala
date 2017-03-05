package fr.colinleverger

import org.scalatest._

class beerScraperScalaTests extends FlatSpec with Matchers {

  val beersUrl: String = "http://craftcans.com/db.php?search=all&sort=beerid&ord=desc&view=text"
  val doc = BeerScraperScala.getDocument(beersUrl)
  val beersHtml = BeerScraperScala.getBeersList(doc)
  val rawBeers = BeerScraperScala.getRawBeers(beersHtml)
  val breweries = BeerScraperScala.getBreweries(rawBeers)
  val beers = BeerScraperScala.getBeers(rawBeers, breweries)

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
    assert(rawBeers.head.beerName == "BEER")
    assert(rawBeers.head.location == "LOCATION")
  }

  it should "provide a list of beers which is not empty" in {
    assert(!rawBeers.isEmpty)
  }

  it should "find the 'Get Together' beer" in {
    val getTogether = rawBeers.filter(_.beerName == "Get Together")
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

  "A getBrewerie function" should "provide a list a breweries with no dooble values" in {
    assert(breweries.nonEmpty)
    val testBrewerie = breweries.filter(_.name == "NorthGate Brewing")
    assert(testBrewerie.size == 1)
    assert(testBrewerie.head.location == "Minneapolis")
    assert(testBrewerie.head.zipCode == "MN")
  }

  "A getBeers function" should "provide a list a beers" in {
    assert(beers.nonEmpty)
    assert(beers.size == 2410)
  }

  it should "should provide a list with 'Get Together' beer present and w/ good values" in {
    val getTogether = beers.filter(_.beerName == "Get Together")
    assert(getTogether.nonEmpty)
    val theBeer = getTogether.head
    assert(theBeer.entry == "2692")
    assert(theBeer.beerName == "Get Together")
    assert(theBeer.style == "American IPA")
    assert(theBeer.size == 16.0)
    assert(theBeer.abv.getOrElse(0.0) == 0.045)
    assert(theBeer.ibus == "50")
  }
}