# Scoped Fixtures

Compatible with scala-test `3.0.x`; Scala `2.11` and `2.12`.

## Installing

    libraryDependencies += "com.spingo" %% "scoped-fixtures" % "2.0.0"

# About


For stateful values that re-initialize on each run.

## Fixture Types:

`ScopedFixtures` provides 3 different kinds of fixtures:

- `LazyFixture`
- `EagerFixture`
- `ScopedFixture`

### LazyFixture

This value gets instantiated on the first access for each test.

Example:

    val lazyValue = LazyFixture {
      new MutableThing
    }

### EagerFixture

Like `LazyFixture`, but is instantiated for each test whether it's accessed or not.

### ScopedFixture

Helpful for providing values that have needs setup and tear-down:

    val actorSystemFixture = ScopedFixture[ActorSystem] { provide =>
      val actorSystem = ActorSystem("test")
      val status = provide(actorSystem)
      actorSystem.shutdown
      status
    }

## Dereferencing

The value assigned to val is a container; in order to get the actual value, you must call apply(). IE:

    val actorSystemFixture = ScopedFixture[ActorSystem] { provide =>
      val actorSystem = ActorSystem("test")
      val status = provide(actorSystem)
      actorSystem.shutdown
      status
    }
    implicit def actorSystem = actorSystemFixture()
