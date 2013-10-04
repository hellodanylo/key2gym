package org.key2gym.business.entities

import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuite

class AttendaceSuite extends FunSuite with BeforeAndAfter {
 
  var attendance: Attendance = _

  before {
    attendance = Attendance.apply(null)
  }
  
  test("close with isOpen") {
     attendance.close
     
    // Checks the attendance's state
    assert(false === attendance.isOpen)
  }

}
