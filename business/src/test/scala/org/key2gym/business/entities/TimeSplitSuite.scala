package org.key2gym.business.entities

import java.sql.Time
import org.joda.time.LocalTime
import org.key2gym.business.entities.TimeSplit
import org.scalatest._

class TimeSplitSuite extends FunSuite with BeforeAndAfter {
  
  var splits: java.util.List[TimeSplit] = _

  var morningSplit: TimeSplit = _
  var afternoonSplit: TimeSplit = _
  var eveningSplit: TimeSplit = _

  var morning: LocalTime = _
  var afternoon: LocalTime = _
  var evening: LocalTime = _
  var lateEvening: LocalTime = _

  before {

    splits = new java.util.LinkedList[TimeSplit]

    afternoonSplit = new TimeSplit
    afternoonSplit.setTime(new LocalTime(17, 0, 00)) // 17:00
    splits.add(afternoonSplit)

    eveningSplit = new TimeSplit
    eveningSplit.setTime(new LocalTime(22, 0, 0)) // 22:00
    splits.add(eveningSplit)

    morningSplit = new TimeSplit
    morningSplit.setTime(new LocalTime(12, 0, 0)) // 12:00
    splits.add(morningSplit)

    morning = new LocalTime(11, 0, 0) // 11:00
    afternoon = new LocalTime(15, 0, 0) // 15:00
    evening = new LocalTime(19, 0, 0) // 19:00
    lateEvening = new LocalTime(23, 0, 0) // 23:00
  }

  test("calculatePenalties, morning, morning") {
    assert(0 === morningSplit.calculatePenalties(splits, morning))
  }

  test("calculatePenalties, morning, afternoon") {
    assert(1 === morningSplit.calculatePenalties(splits, afternoon))
  }

  test("calculatePenalties, morning, evening") {
    assert(2 === morningSplit.calculatePenalties(splits, evening))
  }

  test("calculatePenalties, morning, lateEvening") {
    assert(3 === morningSplit.calculatePenalties(splits, lateEvening))
  }
  
  test("calculatePenalties, afternoon, morning") {
    assert(0 === afternoonSplit.calculatePenalties(splits, morning))
  }

  test("calculatePenalties, afternoon, afternoon") {
    assert(0 === afternoonSplit.calculatePenalties(splits, afternoon)) 
  }

  test("calculatePenalties, afternoon, evening") {
    assert(1 === afternoonSplit.calculatePenalties(splits, evening))  
  }

  test("calculatePenalties, evening, morning") {
    assert(0 === eveningSplit.calculatePenalties(splits, morning))
  }

  test("calculatePenalties, evening, afternoon") {
    assert(0 === eveningSplit.calculatePenalties(splits, afternoon)) 
  }

  test("calculatePenalties, evening, evening") {
    assert(0 === eveningSplit.calculatePenalties(splits, evening))  
  }

  test("calculatePenalties, evening, lateEvening") {
    assert(1 === eveningSplit.calculatePenalties(splits, lateEvening))  
  }

  test("selectTimeSplitForTime, morning") {
    assert(morningSplit === TimeSplit.selectTimeSplitForTime(splits, morning))
  }

  test("selectTimeSplitForTime, afternoon") {
      assert(afternoonSplit === TimeSplit.selectTimeSplitForTime(splits, afternoon))
  }

  test("selectTimeSplitForTime, evening") {
      assert(eveningSplit === TimeSplit.selectTimeSplitForTime(splits, evening))
  }

}
