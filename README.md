## Skunk-Flyway
Effectful database access via Skunk with Flyway migrations.

### Installation
```
"com.two" %% "skunk-flyway" % "0.1.0"
```

### Usage with `twogen` Services
:heavy_exclamation_mark: These manual steps will soon be _less manual!_ An sbt plugin is being worked on to generate the docker related configuration. 

[TwoGen-Service](https://github.com/two-app/twogen-service) is a Scala service generator that assumes a structure for application configuration, resource management, and service architecture. The following instructions offer a quickstart to configure `skunk-flyway` for `twogen-service` specifically.

**Note** This library can still be used without twogen!

#### 1. Include in `build.sbt`
```
"com.two" %% "skunk-flyway" % "0.1.0"
"org.tpolecat" %% "skunk-core" % "0.0.24"
```

#### 2. Configure Connection
Inside `application.conf`, provide connection [configuration](#configuration). For example:
```hocon
database {
    skunk = {
        host = "0.0.0.0"
	host = ${?DATABASE_HOST}
	database = "YOUR_DATABASE_NAME_HERE"
	user = "postgres"
	user = ${?DATABASE_USER}
	password = ${?DATABASE_PASSWORD}
    }
}
```

#### 3. Load Configuration
Append the database configuration to the service Config loader, using the `skunk-flyway` type:
```scala
// config/Config.scala
import two.database.usr.config.DatabaseConfig

case class AppConfig(
    server: ServerConfig,
    database: DatabaseConfig // <--
)
```

### Configuration
Update the application resources to manage a database pool:

```scala
import cats.effect.{Blocker, Concurrent, ContextShift, Resource}
import two.database.usr.session.DatabaseSession
import natchez.Trace.Implicits.noop
import skunk.Session

case class AppResources[F[_]](
    config: AppConfig,
    databasePool: Resource[F, Session[F]]
)

object AppResources {
  def create[F[_]: Concurrent: ContextShift](
      blocker: Blocker
  ): Resource[F, AppResources[F]] = {
    for {
      config <- AppConfig.load(blocker)
      pool <- DatabaseSession.pool(config.database)
    } yield AppResources(config, pool)
  }
}
```

Flyway will automatically be applied as part of the `DatabaseSession.pool` resource.
