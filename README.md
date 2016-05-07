# Scoped Fixtures

Compatible with scala-test `2.2.x`; built for Scala `2.10` and Scala `2.11`.

## Installing

    libraryDependencies += "com.spingo" %% "scoped-fixtures" % "1.0.0"

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

Note, `ScopedFixtures` fixtures uses thread-local variables and does not work when a multi-threaded `ExecutionContext` is used! If you must test concurrent code, you need to either:

- A) Dereference the value in the parent test thread

- B) Use a single-threaded ExecutionContext

        object SingleThreadedExecutionContext extends scala.concurrent.ExecutionContext {
          def execute(runnable: Runnable): Unit = runnable.run
          def reportFailure(cause: Throwable): Unit = {
            println(s"SingleThreadedExecutionContext execution failed! ${cause}")
          }
        }

- C) (my least favorite) Don't use `ScopedFixtures`.
