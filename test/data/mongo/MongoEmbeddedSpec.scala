package data.mongo

import play.api.test.WithApplication
import org.specs2.mutable.Specification
import org.specs2.mock.Mockito

class MongoEmbeddedSpec extends Specification with Mockito {
  "Mongo" should {
    "import" in new WithApplication {
      val mongo = mock[Mongo]
      mongo.host returns "FAKE_HOST"
      mongo.port returns 12345
      mongo.db returns "FAKE_DB"

      val sme = new SeededMongoEmbedded()(mongo)

      sme.mongoImport shouldEqual "mongoimport --host FAKE_HOST --port 12345 --db FAKE_DB --collection  --type json --file"
    }
  }
}
