/*
 * Copyright 2012 Danylo Vashchilenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.key2gym.business.entities

import java.io.Serializable
import java.sql.Time
import java.util.List
import javax.persistence._
import scala.collection.JavaConversions._

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "time_split_tsp")
@NamedQueries(Array(
    new NamedQuery(name = "TimeSplit.findAll", query = "SELECT t FROM TimeSplit t ORDER BY t.endTime ASC"),
    new NamedQuery(name = "TimeSplit.findById", query = "SELECT t FROM TimeSplit t WHERE t.id = :id")))
@SequenceGenerator(name="id_tsp_seq", allocationSize = 1)
class TimeSplit extends Serializable {
    
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id_tsp")
  protected var id: java.lang.Integer = _
  
  @Basic(optional = false)
  @Column(name = "end_time")
  protected var endTime: Time = _
  
  @Basic(optional = false)
  @Column(name = "title")
  protected var title: String = _
  
  @OneToMany(mappedBy = "timeSplit")
  protected var itemSubscriptions: List[ItemSubscription] = _
  
  def getId(): java.lang.Integer = this.id

  def getTime(): Time = this.endTime
  def setTime(endTime: Time) = this.endTime = endTime
    
  def getTitle(): String = this.title
  def setTitle(title: String) = this.title = title

  def getItemSubscriptions(): List[ItemSubscription] = this.itemSubscriptions

  /** Calculates the quantity of penalties.
    *
    * This is the quantity of time splits following
    * this timesplit that end before the given time.
    *
    * The given list should contain all time splits
    * within this time system. It does not have
    * to be ordered.
    * 
    * @param timeSplits the splits within this time system
    * @param time the time attendance takes place
    * @return the quantity of penalties
    */
  def calculatePenalties(timeSplits: List[TimeSplit], time: Time): Int = {
    
    var penalties = 0

    for (that <- timeSplits) {
      if(that.endTime.compareTo(this.endTime) >= 0 && that.endTime.compareTo(time) < 0) {
	penalties += 1
      }
    }
    
    penalties
  }

  def equals(that: TimeSplit): Boolean = that.id == this.id
}

object TimeSplit {
  
  /** Returns the time split to which the time instant belongs.
    *
    * @param timeSplits the time splits in the time system
    * @param the time instant
    * @return the time split corresponding to the time
    */
  def selectTimeSplitForTime(timeSplits: List[TimeSplit], time: Time): TimeSplit = {
    var result: TimeSplit = null

    for(timeSplit <- timeSplits) {
      if(timeSplit.getTime.compareTo(time) > 0 && (result == null || result.getTime.compareTo(timeSplit.getTime()) > 0)) {
	result = timeSplit
      }
    }
    
    result
  }
}
