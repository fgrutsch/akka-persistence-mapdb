/*
 * Copyright 2022 akka-persistence-mapdb contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fgrutsch.akka.persistence.mapdb.query

import com.fgrutsch.akka.persistence.mapdb.query.ReadJournalConfig.DbConfig
import com.fgrutsch.akka.persistence.mapdb.util.ConfigOps._
import com.typesafe.config.Config

import scala.concurrent.duration.FiniteDuration

object ReadJournalConfig {

  class DbConfig(config: Config) {
    val name: String              = config.getString("name")
    override def toString: String = s"${getClass.getSimpleName}($name)"
  }

}

class ReadJournalConfig(config: Config) {
  val db: DbConfig                    = new DbConfig(config.getConfig("db"))
  val refreshInterval: FiniteDuration = config.getFiniteDuration("refresh-interval")
  val maxBufferSize: Int              = config.getInt("max-buffer-size")
  override def toString: String       = s"${getClass.getSimpleName}($db,$refreshInterval,$maxBufferSize)"
}
