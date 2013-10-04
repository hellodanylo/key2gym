package org.key2gym.business.entities

import org.scalatest._
import org.joda.time._
import scala.math.BigDecimal

class ClientSuite extends FunSuite with BeforeAndAfter {
 
  var monthSubscription: ItemSubscription = _
  var daySubscription: ItemSubscription = _

  var item: Item = _
  var discount: Discount = _

  before {
    monthSubscription = new ItemSubscription
    monthSubscription.setTermMonths(1)
    monthSubscription.setTermYears(0)
    monthSubscription.setTermDays(0)
    monthSubscription.setUnits(5)

    daySubscription = new ItemSubscription
    daySubscription.setTermMonths(0)
    daySubscription.setTermYears(0)
    daySubscription.setTermDays(1)
    daySubscription.setUnits(1)

    item = new Item
    item.setPrice(new java.math.BigDecimal(100))

    discount = new Discount
    discount.setPercent(50)
  }
  
  test("activate, (1 month, 5 units), (expired, 5 attandances)") {
    val client = new Client
    client.setExpirationDate(new DateMidnight().minusDays(5).toDate)
    client.setAttendancesBalance(5)

    client.activate(monthSubscription)

    // Checks the expiration date
    assert(new DateMidnight().plusMonths(1).toDate() === client.getExpirationDate())

    // Checks the attendances balance
    assert(5 === client.getAttendancesBalance)
  }

  test("activate, (1 month, 5 units), (unexpired, 5 attendances)") {
 
    val baseDate = new DateMidnight().plusDays(5)
    val client = new Client
    client.setExpirationDate(baseDate.toDate)
    client.setAttendancesBalance(5)

    client.activate(monthSubscription)

    // Checks the expiration date
    assert(baseDate.plusMonths(1).toDate() === client.getExpirationDate())

    // Checks the attendances balance
    assert(5+5 === client.getAttendancesBalance)
  }

  test("activate, (1 day, 1 unit), (expired, 5 attendances)") {
 
    val client = new Client
    client.setExpirationDate(new DateMidnight().minusDays(5).toDate)
    client.setAttendancesBalance(5)

    client.activate(daySubscription)

    // Checks the expiration date
    assert(new DateMidnight().plusDays(1).toDate() === client.getExpirationDate())

    // Checks the attendances balance
    assert(1 == client.getAttendancesBalance)
  }

  test("activate, (1 day, 1 units), (unexpired, 5 attendances)") {
 
    val baseDate = new DateMidnight().plusDays(5)

    val client = new Client
    client.setExpirationDate(baseDate.toDate)
    client.setAttendancesBalance(5)

    client.activate(daySubscription)

    // Checks the expiration date
    assert(baseDate.plusDays(1).toDate() === client.getExpirationDate())

    // Checks the attendances balance
    assert(5+1 === client.getAttendancesBalance)
  }

  test("deactivate, (1 month, 5 units), (unexpired, 10 attendances)") {
 
    val client = new Client
    client.setExpirationDate(new DateMidnight().plusMonths(1).toDate)
    client.setAttendancesBalance(10)

    client.deactivate(monthSubscription)

    // Checks the expiration date
    assert(new DateMidnight().toDate === client.getExpirationDate)

    // Checks the attendances balance
    assert(10-5 === client.getAttendancesBalance)
  }

  test("isExpired, (unexpired)") {
 
    val client = new Client
    client.setExpirationDate(new DateMidnight().plusDays(1).toDate)

    // Checks the expiration flag
    assert(false === client.isExpired)
  }

  test("isExpired, (expired)") {
 
    val client = new Client
    client.setExpirationDate(new DateMidnight().minusDays(1).toDate)

    // Checks the expiration flag
    assert(true === client.isExpired)
  }

  test("charge, $100, 3 x $100, 50%") {
 
    val client = new Client
    client.setMoneyBalance(BigDecimal(100))

    client.charge(item, 3, discount)

    // Checks the client's balance
    assert(BigDecimal(100 - 100 * 0.5 * 3) === client.getMoneyBalance)
  }

  test("uncharge, -$50, 2 x $100, 50%") {
 
    val client = new Client
    client.setMoneyBalance(BigDecimal(-50))

    client.uncharge(item, 2, discount)

    // Checks the client's balance
    assert(BigDecimal(-50 + 100 * 0.5 * 2) === client.getMoneyBalance)
  }

  test("transfer, $100, -$100") {
    
    val client = new Client
    client.setMoneyBalance(BigDecimal(100))
    
    client.transfer(BigDecimal(-100))

    assert(BigDecimal(0) === client.getMoneyBalance())
  }

  test("transfer, -$100, $100") {
    
    val client = new Client
    client.setMoneyBalance(BigDecimal(-100))
      
      client.transfer(BigDecimal(100))
      
    assert(BigDecimal(0) === client.getMoneyBalance())
  }

}
