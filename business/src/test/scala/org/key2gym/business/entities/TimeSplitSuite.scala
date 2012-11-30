package org.key2gym.business.entities

import java.sql.Time
import org.scalatest._

class TimeSplitSuite extends FunSuite with BeforeAndAfter {
  
  var splits: java.util.List[TimeSplit] = _

  var morningSplit: TimeSplit = _
  var afternoonSplit: TimeSplit = _
  var eveningSplit: TimeSplit = _

  var morning: Time = _
  var afternoon: Time = _
  var evening: Time = _
  var lateEvening: Time = _

  before {

    splits = new java.util.LinkedList[TimeSplit]

    morningSplit = new TimeSplit
    morningSplit.setTime(new Time(12 * 60 * 60 * 1000)) // 12:00
    splits.add(morningSplit)

    afternoonSplit = new TimeSplit
    afternoonSplit.setTime(new Time(17 * 60 * 60 * 1000)) // 17:00
    splits.add(afternoonSplit)

    eveningSplit = new TimeSplit
    eveningSplit.setTime(new Time(22 * 60 * 60 * 1000)) // 22:00
    splits.add(eveningSplit)

    morning = new Time(11 * 60 * 60 * 1000) // 11:00
    afternoon = new Time(15 * 60 * 60 * 1000) // 15:00
    evening = new Time(19 * 60 * 60 * 1000) // 19:00
    lateEvening = new Time(23 * 60 * 60 * 1000) // 23:00
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
