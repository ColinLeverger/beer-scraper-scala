# Beer Scraper in Scala

Simple scrapper, developed using `scala 2.12.1`, `ruippeixotog/scala-scraper and `scalatest`.

Use of `FlatSpec`s from `scalatest` to work in TDD.

Scrapping the website http://craftcans.com/db.php?search=all&sort=beerid&ord=desc&view=text.
 
Followed the tutorial http://blog.kaggle.com/2017/01/31/scraping-for-craft-beers-a-dataset-creation-tutorial/ to clean the data (work in progress)

# How does it work?

## Run it yourself!

```
git clone https://github.com/ColinLeverger/beer-scraper-scala`
sbt test
sbt run
```

## What about the process?

1. Connect to the website and download the HTML
2. Parse it with the library `scala-scraper`
3. Create a list of `Beer`s case class objects
4. Write Json
5. Done!

# Sample output

See [this file](beers-sample.json). The variables have not been cleaned yet.
