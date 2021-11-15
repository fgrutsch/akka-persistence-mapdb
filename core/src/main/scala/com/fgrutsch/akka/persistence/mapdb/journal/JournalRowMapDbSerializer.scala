/*
 * Copyright 2021 akka-persistence-mapdb contributors
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

package com.fgrutsch.akka.persistence.mapdb.journal

import org.mapdb.{DataInput2, DataOutput2, Serializer}

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

private[journal] object JournalRowMapDbSerializer {
  def apply(): Serializer[JournalRow] = new JournalRowMapDbSerializer
}

private[journal] class JournalRowMapDbSerializer extends Serializer[JournalRow] {

  private val byteArraySerializer = Serializer.BYTE_ARRAY

  override def serialize(out: DataOutput2, value: JournalRow): Unit = {
    val serialized = BinarySerializer.serialize(value)
    byteArraySerializer.serialize(out, serialized)
  }

  override def deserialize(input: DataInput2, available: Int): JournalRow = {
    val deserialized = byteArraySerializer.deserialize(input, available)
    BinarySerializer.deserialize(deserialized)
  }

  override def isTrusted: Boolean = true

  override def equals(first: JournalRow, second: JournalRow): Boolean = {
    val firstSerialized  = BinarySerializer.serialize(first)
    val secondSerialized = BinarySerializer.serialize(second)
    byteArraySerializer.equals(firstSerialized, secondSerialized)
  }

  override def hashCode(o: JournalRow, seed: Int): Int = {
    val serialized = BinarySerializer.serialize(o)
    byteArraySerializer.hashCode(serialized, seed)
  }

  override def compare(first: JournalRow, second: JournalRow): Int = {
    val firstSerialized  = BinarySerializer.serialize(first)
    val secondSerialized = BinarySerializer.serialize(second)
    byteArraySerializer.compare(firstSerialized, secondSerialized)
  }

  private object BinarySerializer {
    def serialize(obj: JournalRow): Array[Byte] = {
      val byteOut = new ByteArrayOutputStream()
      val objOut  = new ObjectOutputStream(byteOut)
      objOut.writeObject(obj)
      objOut.close()
      byteOut.close()
      byteOut.toByteArray
    }

    def deserialize(bytes: Array[Byte]): JournalRow = {
      val byteIn = new ByteArrayInputStream(bytes)
      val objIn  = new ObjectInputStream(byteIn)
      val obj    = objIn.readObject().asInstanceOf[JournalRow]
      byteIn.close()
      objIn.close()
      obj
    }
  }

}
