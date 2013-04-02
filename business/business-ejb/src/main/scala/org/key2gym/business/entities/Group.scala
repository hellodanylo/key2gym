/*
 * Copyright 2012-2013 Danylo Vashchilenko
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
package org.key2gym.business.entities;

import java.io.Serializable
import java.util.List
import javax.persistence._
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlTransient
import scala.reflect._
import org.key2gym.business.api.ValidationException
import org.key2gym.business.resources.ResourcesManager

/**
 *
 * @author Danylo Vashchilenko
 */
@Entity
@Table(name = "group_grp")
@NamedQueries(Array(
    new NamedQuery(name = "Group.findAll", query = "SELECT g FROM Group g")
))
class Group {

  @Id
  @Column(name = "id_grp")
  protected var id: java.lang.Integer = _

  @Basic(optional = false)
  @Column(name = "name")
  protected var name: String = _

  @Basic(optional = false)
  @Column(name = "title")
  protected var title: String = _

  def getId(): java.lang.Integer = this.id

  def getName: String = this.name
  def setName(name: String) = this.name = name

  def getTitle: String = this.title
  def setTitle(title: String) = this.title = title
}
