package org.key2gym.business.entities

import java.math.BigDecimal
import org.scalatest._
import org.key2gym.business.api.ValidationException

class OrderSuite extends FunSuite with BeforeAndAfter {
 
  test("recordPayment, valid scale, valid precision") {
    val order = OrderEntity()
    val amount = new BigDecimal("9876.54")

    order.recordPayment(amount)

    assert(amount === order.getPayment)
  }

  test("recordPayment, invalid scale, valid precision") {
    val order = OrderEntity()

    intercept[ValidationException] { 
      order.recordPayment(new BigDecimal("876.543"))
    }

    assert(BigDecimal.ZERO === order.getPayment)
  }

  test("recordPayment, valid scale, invalid precision") {
    val order = OrderEntity()

    intercept[ValidationException] { 
      order.recordPayment(new BigDecimal("98765.43"))
    }

    assert(BigDecimal.ZERO === order.getPayment)
  }

  test("recordPayment, invalid scale, invalid precision") {
    val order = OrderEntity()

    intercept[ValidationException] { 
      order.recordPayment(new BigDecimal("98765.435"))
    }

    assert(BigDecimal.ZERO === order.getPayment)
  }

}
