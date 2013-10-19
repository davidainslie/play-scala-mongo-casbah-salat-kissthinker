package data.mongo.example

import com.novus.salat.dao.{ModelCompanion, SalatDAO}
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.Imports._
import com.novus.salat.global._
import com.novus.salat.annotations.Key
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.exceptions.TestFailedException
import com.github.simplyscala.MongoEmbedDatabase

class MongoSpec extends FunSuite with ShouldMatchers with MongoEmbedDatabase {
  test("bad port") {
    evaluating {
      withEmbedMongoFixture(22222) { mongodProps =>
        MyModel.save(MyModel(name = "testFixture"))
        MyModel.count() should be (1)
      }
    } should produce[com.mongodb.MongoException]
  }

  test("save model and retrieve") {
    withEmbedMongoFixture(54321) { mongodProps =>
      val my: MyModel = MyModel(name = "testFixture")
      MyModel.save(my)
      MyModel.count() should be (1)
    }
  }

  test("launch fail() in fixture") {
    evaluating {
      withEmbedMongoFixture(54321) { mongodProps =>
        fail("fail")
      }
    } should produce[TestFailedException]
  }
}

case class MyModel(@Key("_id") id: ObjectId = new ObjectId, name: String)

object MyModel extends ModelCompanion[MyModel, ObjectId] {
  val dao = new SalatDAO[MyModel, ObjectId](collection = MongoConnection("localhost", 54321)("test")("model")) {}
}