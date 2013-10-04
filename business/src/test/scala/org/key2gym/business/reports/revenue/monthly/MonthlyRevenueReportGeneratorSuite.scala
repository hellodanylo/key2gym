package org.key2gym.business.reports.revenue.monthly

import org.scalatest._
import org.joda.time._
import org.key2gym.business.api.ValidationException

class MonthlyRevenueReportGeneratorSuite extends FunSuite with BeforeAndAfter {
 
  var validValues: Array[ReadableInterval] = _
  var invalidValues: Array[ReadableInterval] = _

  var generator: MonthlyRevenueReportGenerator = _

  before {

    generator = new MonthlyRevenueReportGenerator

    validValues = Array(
      new Interval(
	new DateMidnight(2012, 01, 01),
	new DateMidnight(2012, 03, 01)
      ),
      
      new Interval(
	new DateMidnight(2012, 05, 01),
	new DateMidnight(2013, 03, 01)
      )
    )
      
    invalidValues = Array(
      new Interval(
	new DateMidnight(2012, 1, 5),
	new DateMidnight(2012, 3, 1)
      ),
      
      new Interval(
	new DateMidnight(2012, 1, 1),
	new DateMidnight(2012, 3, 31)
      )
    )
  }
  
  test("validateInput, validValues") {
    
    try {
      validValues.foreach(v => generator.validateInput(v, null))
    } catch {
      case _: ValidationException => fail
    }

  }

  test("validateInput, invalidValues") {
    
    invalidValues.foreach(v => {
      intercept[ValidationException] {
	generator.validateInput(v, null)
      }
    })

  }
}
