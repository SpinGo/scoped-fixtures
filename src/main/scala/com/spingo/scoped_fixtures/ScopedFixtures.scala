package com.spingo.scoped_fixtures

import scala.util.DynamicVariable
import org.scalatest.{Suite, Status, Args}

trait ScopedFixtures extends Suite {
  type ScopedFn[T] = (T => Status) => Status
  type AroundFn = (() => Status) => Status
  var aroundFns: List[AroundFn] = Nil

  private def pushAroundFn(fn: AroundFn): Unit =
    aroundFns = aroundFns :+ fn

  val insideTestScope = new DynamicVariable(false)

  trait TestFixture[T] {
    protected val binding = new DynamicVariable[Option[T]](None)
    protected def resolveValue: T
    def instance =
      binding.value.getOrElse { resolveValue }
    def apply() =
      instance
    if (insideTestScope.value)
      throw(new Exception("Defining scoped variables inside a test is not allowed."))
  }

  class ScopedFixture[T]( scopedGetter: ScopedFn[T] ) extends TestFixture[T] {
    def resolveValue =
      if ( insideTestScope.value )
        throw(new Exception("Eager access a ScopedFixture outside of order not allowed."))
      else
        throw(new Exception("Tried to access value of ScopedFixture outside of test"))

    pushAroundFn((body: () => Status) => {
      scopedGetter({ value: T =>
        binding.withValue(Some(value)) {
          body()
        }
      })
    })
  }

  object ScopedFixture {
    def apply[T]( fn: ScopedFn[T] ) = {
      new ScopedFixture[T](fn)
    }
  }

  class LazyFixture[T]( getter: => T) extends TestFixture[T] {
    val scopeInstantiated = new DynamicVariable(false)
    def resolveValue =
      if (! insideTestScope.value)
        throw(new Exception("Tried to access a LazyFixture outside of test"))
      else if ( ! scopeInstantiated.value )
        throw(new Exception("Eager resolve LazyFixture outside of order not allowed"))
      else {
        val value = getter
        binding.value = Some(value)
        value
      }

    pushAroundFn((body: () => Status) => {
      scopeInstantiated.withValue(true) {
        binding.withValue(None) { body() }
      }
    })
  }

  object LazyFixture {
    def apply[T]( getter: => T) = {
      new LazyFixture[T](getter)
    }
  }

  class EagerFixture[T]( getter: => T) extends TestFixture[T] {
    def resolveValue = if (insideTestScope.value)
      throw(new Exception("Attempt to eagerly reference an EagerFixture outside of order not allowed."))
    else
      throw(new Exception("Tried to access Eager value outside of test"))

    pushAroundFn((body: () => Status) => {
      binding.withValue(Some(getter)) { body() }
    })
  }
  object EagerFixture {
    def apply[T]( fn: => T) = {
      new EagerFixture[T](fn)
    }
  }

  abstract protected override def runTest(testName: String, args: Args): Status = {
    def iterate(fns: List[AroundFn])(): Status = {
      fns match {
        case head :: tail => head(iterate(tail)_)
        case Nil => super.runTest(testName, args)
      }
    }
    insideTestScope.withValue(true) {
      iterate(aroundFns)
    }
  }
}
