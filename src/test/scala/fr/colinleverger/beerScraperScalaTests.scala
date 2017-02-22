package fr.colinleverger

import org.scalatest._


class beerScraperScalaTests extends FlatSpec with Matchers {

  "A document" should "not be empty and should contain some html" in {
    assert(!beerScraperScala.getDocument("http://colinleverger.fr").isEmpty)
    assert(beerScraperScala.getDocument("thisIsNotAnUrl").isEmpty)
    assert(beerScraperScala.getDocument("http://colinleverger.com").isEmpty)
  }

}