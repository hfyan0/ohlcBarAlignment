import org.joda.time.{Period, DateTime, Duration}
import org.joda.time.format.DateTimeFormat
import org.nirvana._

object OHLCBarAlignment {

val dfmt = DateTimeFormat.forPattern("yyyy-MM-dd")

  def main(args: Array[String]) {

    var mapSymOHLCB = Map[String, List[OHLCBar]]()
    var lsDates = List[DateTime]()

    args.foreach(x => {
      val lsLines = scala.io.Source.fromFile(x).getLines.toList

      val lsOHLCB = lsLines.map(DataFmtAdaptors.parseBlmgFmt1(_, false)).filter(_ != None).map(_.get)

      if (lsOHLCB.length > 0) {
        mapSymOHLCB += lsOHLCB.head.symbol -> lsOHLCB
        lsDates = lsDates ::: lsOHLCB.map(_.dt)
      }
    })

    val lsSortedUniqDates = lsDates.sortBy(_.getMillis).distinct

    lsSortedUniqDates.map(d => {

      var lsDateExist = List[Boolean]()
      var lsClose = List[Double]()

      for ((sym, lsohlcb) <- mapSymOHLCB) {

        lsohlcb.filter(_.dt == d) match {
          case Nil => lsDateExist = lsDateExist :+ false
          case x :: xs =>
            {
              lsDateExist = lsDateExist :+ true
              lsClose = lsClose :+ x.priceBar.c
            }
        }
      }

      if (lsDateExist.forall(_ == true)) {
        print(dfmt.print(d))
        print(",")
        println(lsClose.mkString(","))
      }

    })

  }
}
