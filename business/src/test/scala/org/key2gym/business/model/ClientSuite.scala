package org.key2gym.business.model.ClientSuite

import org.scalatest._
import org.joda.time._
import org.key2gym.business.entities.Client
import org.key2gym.persistence.ItemSubscription

class ClientSuite extends FunSuite with BeforeAndAfter {
 
  var monthSubscription: ItemSubscription = _
  var daySubscription: ItemSubscription = _

  before {
    monthSubscription = new ItemSubscription
    monthSubscription.setTermMonths(1)
    monthSubscription.setTermYears(0)
    monthSubscription.setTermDays(0)

    daySubscription = new ItemSubscription
    daySubscription.setTermMonths(0)
    daySubscription.setTermYears(0)
    daySubscription.setTermDays(1)

  }
  
  test("rollExpirationDate forward, 1 month, expired") {
 
    val client = new Client
    client.setExpirationDate(new DateMidnight().minusDays(5).toDate)

    client.rollExpirationDate(monthSubscription, true)

    val expected = new DateMidnight().plusMonths(1).toDate()
    assert(expected === client.getExpirationDate())
  }

  test("rollExpirationDate forward, 1 month, unexpired") {
 
    val baseDate = new DateMidnight().plusDays(5)

    val client = new Client
    client.setExpirationDate(baseDate.toDate)

    client.rollExpirationDate(monthSubscription, true)

    val expected = baseDate.plusMonths(1).toDate()
    assert(expected === client.getExpirationDate())
  }

  test("rollExpirationDate forward, 1 day, expired") {
 
    val client = new Client
    client.setExpirationDate(new DateMidnight().minusDays(5).toDate)
    
    client.rollExpirationDate(daySubscription, true)

    val expected = new DateMidnight().plusDays(1).toDate()
    assert(expected === client.getExpirationDate())
  }

  test("rollExpirationDate forward, 1 day, unexpired") {
 
    val baseDate = new DateMidnight().plusDays(5)

    val client = new Client
    client.setExpirationDate(baseDate.toDate)

    client.rollExpirationDate(daySubscription, true)

    val expected = baseDate.plusDays(1).toDate()
    assert(expected === client.getExpirationDate())
  }
}
