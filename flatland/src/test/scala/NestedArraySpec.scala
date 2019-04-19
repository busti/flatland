package flatland.test

import flatland._
import collection.mutable

import org.scalatest._

class NestedArraySpec extends FreeSpec with MustMatchers {

  "NestedArrayInt" - {
    "empty" in {
      val nested = NestedArrayInt(Array[Array[Int]]())
      nested.length mustEqual 0
      nested.isEmpty mustEqual true
      // assertThrows[AnyRef](nested(0)) // should be IndexOutOfBoundsException, but scalajs thows UndefinedBehaviorException
    }
    "one empty array" in {
      val nested = NestedArrayInt(Array(Array[Int]()))
      nested.length mustEqual 1
      nested.isEmpty mustEqual false
      nested.sliceStart(0) mustEqual 1
      nested.sliceLength(0) mustEqual 0
      nested(0).isEmpty mustEqual true
    }
    "one single-element array" in {
      val nested = NestedArrayInt(Array(Array(13)))
      nested.length mustEqual 1
      nested.sliceStart(0) mustEqual 1
      nested.sliceLength(0) mustEqual 1
      nested(0, 0) mustEqual 13
      nested(0)(0) mustEqual 13
    }
    "two non-empty arrays" in {
      val nested = NestedArrayInt(Array(Array(7, 8, 9), Array(1, 2, 3)))
      nested.length mustEqual 2
      nested(0, 1) mustEqual 8
      nested(0)(1) mustEqual 8
      nested(1, 1) mustEqual 2
      nested(1)(1) mustEqual 2
      nested(0).toList mustEqual List(7, 8, 9)
      nested(1).toList mustEqual List(1, 2, 3)
    }
    "many arrays, some empty" in {
      val nested = NestedArrayInt(Array(Array(3), Array[Int](), Array(0), Array[Int](), Array(0, 1)))
      nested.length mustEqual 5
      nested(0).toList mustEqual List(3)
      nested(1).toList mustEqual List()
      nested(2).toList mustEqual List(0)
      nested(3).toList mustEqual List()
      nested(4).toList mustEqual List(0, 1)
    }
    "many arrays, some empty - from builders" in {
      def builder(elems: Int*) = {
        val b = new scala.collection.mutable.ArrayBuilder.ofInt
        b ++= elems
        b
      }
      val nested = NestedArrayInt(Array(builder(3), builder(), builder(0), null, builder(0, 1)))
      nested.length mustEqual 5
      nested(0).toList mustEqual List(3)
      nested(1).toList mustEqual List()
      nested(2).toList mustEqual List(0)
      nested(3).toList mustEqual List()
      nested(4).toList mustEqual List(0, 1)
    }

    "foreachIndex" in {
      val nested = NestedArrayInt(Array(Array(7, 8, 9), Array(1, 2, 3)))
      val agg = List.newBuilder[Int]
      nested.foreachIndex(0)(agg += _)
      assert(agg.result() == List(0, 1, 2))
    }

    "foreachElement" in {
      val nested = NestedArrayInt(Array(Array(7, 8, 9), Array(1, 2, 3)))
      val agg = List.newBuilder[Int]
      nested.foreachElement(0)(agg += _)
      assert(agg.result() == List(7, 8, 9))
    }

    "anyContains" in {
      val nested = NestedArrayInt(Array(Array(7, 8, 9), Array(1, 2, 3)))
      nested.anyContains(7) mustBe true
      nested.anyContains(8) mustBe true
      nested.anyContains(3) mustBe true
      nested.anyContains(4) mustBe false
    }

    "map" in {
      val nested = NestedArrayInt(Array(Array(7, 8, 9), Array(1, 2, 3)))
      nested.map(0)(_.toString).toList mustEqual List("7", "8", "9")
      nested.map(1)(identity).toList mustEqual List(1, 2, 3)
    }

    "flatMap" in {
      val nested = NestedArrayInt(Array(Array(701, 802, 903), Array(1, 2, 3)))
      nested.flatMap(0)(_.toString.split("0")).toList mustEqual List("7", "1", "8", "2", "9", "3")
      nested.flatMap(1)(i => Array(i, i + 1)).toList mustEqual List(1, 2, 2, 3, 3, 4)
    }

    "count" in {
      val nested = NestedArrayInt(Array(Array(1, 2, 3), Array(1, 2, 3, 4), Array(1)))
      nested.count(0)(_ < 3) mustEqual 2
      nested.count(1)(_ > 3) mustEqual 1
      nested.count(2)(_ > 3) mustEqual 0
    }

    "forall" in {
      val nested = NestedArrayInt(Array(Array(1, 2, 3), Array(1, 2, 3, 4), Array(1)))
      nested.forall(0)(_ < 4) mustEqual true
      nested.forall(1)(_ < 4) mustEqual false
      nested.forall(2)(_ < 4) mustEqual true
    }
    "exists" in {
      val nested = NestedArrayInt(Array(Array(1, 2, 3), Array(1, 2, 3, 4), Array(1)))
      nested.exists(0)(_ > 3) mustEqual false
      nested.exists(1)(_ > 3) mustEqual true
      nested.exists(2)(_ > 3) mustEqual false
    }
    "foldLeft" in {
      val nested = NestedArrayInt(Array(Array(1, 2, 3, 4), Array[Int]()))
      nested.foldLeft(0)(0)((agg,e) => agg + e) mustEqual 10
      nested.foldLeft(1)(7)((agg,e) => agg + e) mustEqual 7
    }
    "minByInt" in {
      val nested = NestedArrayInt(Array(Array(8, 1, 2, 3, 4), Array[Int]()))
      nested.minByInt(0)(19)(e => e+1) mustEqual 2
      nested.minByInt(1)(13)(e => e+1) mustEqual 13
    }
    "maxByInt" in {
      val nested = NestedArrayInt(Array(Array(1, 2, 3, 4, 2), Array[Int]()))
      nested.maxByInt(0)(-19)(e => e+1) mustEqual 5
      nested.maxByInt(1)(13)(e => e+1) mustEqual 13
    }

    "collect" in {
      val nested = NestedArrayInt(Array(Array(701, 802, 903), Array(1, 2, 3)))
      nested.collect[String](0) { case x if x < 900 => x.toString }.toList mustEqual List("701", "802")
      nested.collect[Int](1) { case 2 => 0 }.toList mustEqual List(0)
    }

    "collectFirst" in {
      val nested = NestedArrayInt(Array(Array(701, 802, 903), Array(1, 2, 3)))
      nested.collectFirst[String](0) { case x if x < 900 => x.toString } mustEqual Some("701")
      nested.collectFirst[Int](1) { case 2 => -1 } mustEqual Some(-1)
    }

    "toArraySet" in {
      val nested = NestedArrayInt(Array(Array(0, 1), Array(0)))
      nested.toArraySet(0).collectAllElements.toList mustEqual List(0, 1)
      nested.toArraySet(1).collectAllElements.toList mustEqual List(0)
    }

    "transpose" in {
      val nested = NestedArrayInt(Array(
        /* 0 */ Array[Int](1, 2),
        /* 1 */ Array[Int](2),
        /* 2 */ Array[Int](),
      ))
      val transposed = nested.transposed
      transposed(0).toList mustEqual List()
      transposed(1).toList mustEqual List(0)
      transposed(2).toList mustEqual List(0, 1)
    }
  }
}
