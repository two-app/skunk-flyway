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

### 1. Installation and Configuration
#### 1.a. Include in `build.sbt`
```
"com.two" %% "skunk-flyway" % "0.1.0"
"org.tpolecat" %% "skunk-core" % "0.0.24"
```

#### 1.b. Configure Connection
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

#### 1.c. Load Configuration
Append the database configuration to the service Config loader, using the `skunk-flyway` type:
```scala
// config/Config.scala
import two.database.usr.config.DatabaseConfig

case class AppConfig(
    server: ServerConfig,
    database: DatabaseConfig // <--
)
```

#### 1.d. Manage Resources
Update the application resources to manage a database pool:

```scala
// config/Resources.scala
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

Flyway will automatically be applied as part of the `DatabaseSession.pool` resource effect.

### 2. Local Development Infrastructure
Instructions to configure Docker for local development.

#### 2.a. Initialization Script
To create the services database when the postgres docker image is brought up, create a `db/init.sql` file:
```sql
CREATE DATABASE YOUR_DATABASE_NAME;
GRANT ALL PRIVILEGES ON DATABASE YOUR_DATABASE_NAME TO postgres;
```

#### 2.b. Docker Compose Service
Create a new `postgres` service and map the ports:
```yaml
services:
  postgres:
    image: postgres:13.3
    container_name: postgres
    environment:
      - POSTGRES_HOST_AUTH_METHOD=trust
    volumes:
      - ./db:/docker-entrypoint-initdb.d/
    ports:
      - '5432:5432'
```

The `POSTGRES_HOST_AUTH_METHOD=trust` allows any client to access any database without authentication. Use only for local development.

Update the `test` service to depend on postgres:
```diff
  test:
    build: .
    container_name: test
    ports:
      - '8080'
    environment:
      - SERVER_PORT=8080
      - GITHUB_TOKEN=${GITHUB_TOKEN}
    # ...
+   depends_on:
+     - postgres
    # ...
    command: test

```

#### 2.c. Twogen Makefile
Update the Twogen Makefile to start the postgres service on `local_env_up`:
```Make
local_env:
	docker-compose up -d postgres

local_env_down:
	docker-compose rm --force --stop -v postgres
```

### 3. Database Migrations
Create some Flyway migrations! By default, migrations are sought from `src/main/resources/migration/*`. Here's an example `V0__Create_Country_Table.sql`:
```sql
CREATE TABLE country (
    id         uuid        CONSTRAINT country_id PRIMARY KEY,
    name       varchar(40) NOT NULL,
    created_at date        NOT NULL
);
```

At this point, have a go at bringing your service up and checking the tables...
```sh
make local_env
# wait for postgres to start
sbt run
# check database tables
make local_env_down
```

### 4. Production Infrastructure
Remember to create your database in the production cluster!
