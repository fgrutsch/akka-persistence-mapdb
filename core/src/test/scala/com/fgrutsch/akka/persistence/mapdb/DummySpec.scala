package com.fgrutsch.akka.persistence.mapdb

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers

class DummySpec extends AnyFunSuite with Matchers {

  private val dummy = new Dummy

  test("dummy test to have a test report") {
    dummy.myMethod mustBe true
  }

}
