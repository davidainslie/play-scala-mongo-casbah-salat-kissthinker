package data.mongo.example

import play.api.test._
import data.mongo.{Mongo, MongoEmbedded}

class MongoIntegrationSpec extends PlaySpecification {
  "User" should {
    "be persisted" in new WithApplication with MongoEmbedded {
      val mongo = new Mongo {}

      withMongo(mongo) {
        import com.mongodb.casbah.Imports._

        val dao = new UserDAO { override val mongodb = mongo.mongodb }

        val user = User(username = "scooby", password = "doo")
        dao save user
        dao.count() shouldEqual 1

        dao.findOne(DBObject("username" -> "scooby")) should beSome(user)
      }
    }

    "seed data before (dot notation)" in new WithApplication with MongoEmbedded {
      val mongo = new Mongo {}

      withMongo(mongo).seed("user").fromFilePath("./test/resources/users.json").ofFileType("json").withExtraArgs("--jsonArray") {
        import com.mongodb.casbah.Imports._

        val dao = new UserDAO { override val mongodb = mongo.mongodb }

        dao.count() shouldEqual 3

        val user = User(username = "scooby", password = "doo")
        dao save user
        dao.count() shouldEqual 4

        dao.findOne(DBObject("username" -> "scooby")) should beSome(user)
      }
    }

    "seed data before (infix notation)" in new WithApplication with MongoEmbedded {
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

import com.mongodb.casbah.commons.TypeImports.ObjectId
import com.novus.salat.annotations._
import com.novus.salat.dao.{SalatDAO, ModelCompanion}
import com.novus.salat.global._

case class User(@Key("_id") id: ObjectId = new ObjectId, username: String, password: String)

trait UserDAO extends ModelCompanion[User, ObjectId] with Mongo {
  def dao = new SalatDAO[User, ObjectId](mongodb("user")) {}
}

object UserDAO extends UserDAO