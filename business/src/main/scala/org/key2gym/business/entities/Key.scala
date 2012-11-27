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

import java.io.Serializable;
import java.util.List;
import javax.persistence._;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import scala.reflect._;

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "key_key")
@NamedQueries(value = Array(
    new NamedQuery(name = "Key.findAll", query = "SELECT k FROM Key k ORDER BY k.id"),
    new NamedQuery(name = "Key.findById", query = "SELECT k FROM Key k WHERE k.id = :id"),
    new NamedQuery(name = "Key.findByTitle", query = "SELECT k FROM Key k WHERE k.title = :title"),
    new NamedQuery(name = "Key.findAvailable", query = "SELECT k From Key k WHERE k.id NOT IN (SELECT a.key.id FROM Attendance a WHERE a.datetimeEnd = '2004-04-04 09:00:01')"),
    new NamedQuery(name = "Key.findTaken", query = "SELECT k From Key k WHERE k.id IN (SELECT a.key.id FROM Attendance a WHERE a.datetimeEnd = '2004-04-04 09:00:01')")))
@SequenceGenerator(name="id_key_seq", allocationSize = 1)
class Key {
    
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id_key")
  private var id: Int = _

  @Basic(optional = false)
  @Column(name = "title")
  private var title: String = _

  @OneToMany(cascade = Array(CascadeType.ALL), mappedBy = "key")
  private var attendances: List[Attendance] = _

  def getId = id

  def getTitle = title
  def setTitle(title: String) = this.title = title;

  def getAttendances = attendances
}
