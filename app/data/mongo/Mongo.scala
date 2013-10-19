package data.mongo

import play.api.Play.current
import play.api.Configuration
import com.mongodb.casbah.{MongoDB, MongoConnection}
import de.flapdoodle.embed.process.runtime.Network

trait Mongo {
  val configuration: Configuration = current.configuration

  val host: String = configuration.getString("mongodb.host").getOrElse("127.0.0.1")

  val port: Int = configuration.getInt("mongodb.port").getOrElse(Network.getFreeServerPort)

  val db: String = configuration.getString("mongodb.db").getOrElse("kissthinker")

  val mongodb: MongoDB = MongoConnection(host, port)(db)

  override def toString = s"$host:$port/$db"
}

object Mongo extends Mongo