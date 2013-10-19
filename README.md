This project is a follow on from the use of <a href="https://github.com/davidainslie/play-scala-mybatis-kissthinker">Play/Scala/MyBatis</a>, which has a full description of coding with BDD.
All explanations leading up to MyBatis are relevant here.
To help you on this application, please read up to discussion of the use of MyBatis and then switch back here where Mongodb is used instead, in conjunction with Casbah and Salat.

This project gives you a way of running isolated specs with an embedded Mongodb.
Each example you write will start up an embedded Mongodb (empty of course), at which point you can "seed" the datastore, and once the example completes the embedded Mongodb shuts down.
The prime use for this is to test drive your DAOs (repositories).

The main functionality to use within examples is "MongoEmbedded" which utilises "Mongo" as shown in the following spec.
The functionality itself is fairly simple, building upon the libraries:
<a href="https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo">Flapdoodle</a>
and
<a href="https://github.com/SimplyScala/scalatest-embedmongo">Scalatest Embed Mongo</a>

Here is just one example showing the majority of what is one offer from this project:

```scala
class MongoIntegrationSpec extends PlaySpecification {
  "User" should {
    "be persisted once seeded data (infix notation)" in new WithApplication with MongoEmbedded {
      val mongo = new Mongo {}

      withMongo(mongo) seed "user" fromFilePath "./test/resources/users.json" withExtraArgs "--jsonArray" apply {
        import com.mongodb.casbah.Imports._

        val dao = new UserDAO { override val mongodb = mongo.mongodb }

        dao count() shouldEqual 3

        val user = User(username = "scooby", password = "doo")
        dao save user
        dao count() shouldEqual 4

        dao findOne DBObject("username" -> "scooby") should beSome(user)
      }
    }
  }
}

case class User(@Key("_id") id: ObjectId = new ObjectId, username: String, password: String)

trait UserDAO extends ModelCompanion[User, ObjectId] with Mongo {
  def dao = new SalatDAO[User, ObjectId](mongodb("user")) {}
}

object UserDAO extends UserDAO
```

Imports have been removed but the full code can be seen in "data.mongo.example.MongoIntegrationSpec".

Here is a brief discussion of this spec:

It is a Specs2 type of specification utilising the "PlaySpecification".
The example is run within a Play application (WithApplication) so that we can use Play's configuration.
If you take a look at "data.mongo.Mongo" you will see that this is both a wrapper to the underlying Casbah functionality (our Scala interface to Mongodb)
and the actual configuration of the "Mongodb interface" - where the configuration (as mentioned) utilises Play.

So, "data.mongo.Mongo" is only useful within a Play application.
However, this is just a trait, and with future iterations we could easily rename this to "data.mongo.MongoPlay" which would extend a more generic "data.mongo.Mongo".

Initially I only wanted to use this "embedded Mongodb" functionality within Play applications, so the use of Play's configuration was very convenient,
and as with BDD/TDD applications, you should only code what you need (and so long as your code adheres to all necessary standards/idioms, future amendments should be straight forward).

When running a "spec", Play is in "test" mode and so will use the configuration file "application.test.conf".

The next mixin to the example is "MongoEmbedded".
This trait extends "com.github.simplyscala.MongoEmbedDatabase" and essentially provides a DSL for your example.

The example starts by creating a new "Mongo".
This is only necessary within examples i.e. "non test" code would pick up the default singleton "Mongo".
The reason for creating one in the example, is to pass on its wrapped Casbah Mongo interface to the DAO (repository) that is being tested.
Again, you DAOs (repositories) will automatically use the default singleton "Mongo" if one is not provided.
So what is going on, is that the embedded Mongodb that is started up within the example will use a "port" that is available,
and if we do not then pass on that Mongodb on said port to our DAO (letting it use the default), the DAO will try to connect to the wrong Mongodb,
or more likely, one that does not exist.

The next part of the example is the available DSL:
```scala
withMongo(mongo) seed "user" fromFilePath "./test/resources/users.json" withExtraArgs "--jsonArray" apply
```

We can "seed" the embedded Mongodb by stipulating:
- the collection to seed, in this case "user"
- provide the actual seed data from a file, in this case in a JSON format.
- and any extra "args" required for the chosen formatting of the seed data.

The underlying functionality seeds the embedded Mongodb using Mongdb's "mongoimport" tool.
For more information on this such as possible file formats, see <a href="http://docs.mongodb.org/manual/reference/program/mongoimport/">mongoimport</a>

Within the example itself, note that we do indeed "pass on" the wrapped Casbah Mongodb interface to our "UserDAO",
and the rest should be self describing.