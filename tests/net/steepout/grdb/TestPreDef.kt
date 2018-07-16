package net.steepout.grdb

import kotlin.test.assertEquals

infix fun Any.shouldBe(o: Any): Unit = assertEquals(o, this)
