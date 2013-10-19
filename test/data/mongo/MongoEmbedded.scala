package data.mongo

import com.github.simplyscala.MongoEmbedDatabase

trait MongoEmbedded extends MongoEmbedDatabase {
  def withMongo(mongo: Mongo) = DefaultMongoEmbedded(mongo)
}

private[mongo] case class DefaultMongoEmbedded(mongo: Mongo = Mongo) extends MongoEmbedded {
  def apply[R](f: => R) = {
    val mongodProps = mongoStart(port = mongo.port)
    val result = f
    mongoStop(mongodProps)
    result
  }

  def seed(collection: String) = SeededMongoEmbedded(collection = collection)(mongo)
}

private[mongo] case class SeededMongoEmbedded(collection: String = "", filePath: String = "", fileType: String = "json", extraArgs: List[String] = Nil)(mongo: Mongo = Mongo) extends MongoEmbedDatabase {
  def fromFilePath(filePath: String) = SeededMongoEmbedded(collection = collection, filePath = filePath)(mongo)

  def ofFileType(fileType: String) = SeededMongoEmbedded(collection = collection, filePath = filePath, fileType = fileType)(mongo)

  def withExtraArgs(extraArgs: String*) = SeededMongoEmbedded(collection = collection, filePath = filePath, fileType = fileType, extraArgs = extraArgs.toList)(mongo)

  def apply[R](f: => R) = {
    val mongodProps = mongoStart(port = mongo.port)

    doMongoImport()

    val result = f
    mongoStop(mongodProps)
    result
  }

  def mongoImport =
    s"mongoimport --host ${mongo.host} --port ${mongo.port} --db ${mongo.db} --collection $collection --type $fileType --file $filePath ${extraArgs.mkString}".trim

  def doMongoImport() = {
    import scala.sys.process._
    mongoImport.!!
  }
}