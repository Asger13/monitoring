import java.io.IOException

import com.app.service.RecieverCalculation
import com.rabbitmq.client.{AMQP, ConnectionFactory, DefaultConsumer, Envelope}

object Receiver {

  private val QUEUE_NAME = "telemetric-data"

  def main(args: Array[String]): Unit = {

    val factory: ConnectionFactory = new ConnectionFactory()
    factory.setHost("localhost")
    val connection = factory.newConnection
    val channel = connection.createChannel
    val receiver = new RecieverCalculation()
    channel.queueDeclare(QUEUE_NAME, true, false, false, null)

    // val myMessagges = new util.ArrayList[String]
    //true - автопринятие, false - нужно задавать принятие вручную

    channel.basicConsume(QUEUE_NAME, false, new DefaultConsumer(channel) {
      @throws[IOException]
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: Array[Byte]): Unit = {
        // myMessagges.add(new String(body))
        System.out.println("Received...")
        var s = ""
        try {
          s = receiver.main(body)
          System.out.println("Insert to DB...")
          channel.basicAck(envelope.getDeliveryTag, false)
          System.out.println(s)
        }
        catch {
          case e: IOException => e.printStackTrace
            //channel.txRollback()
            System.out.println("Error received")
            System.out.println(s)
        }

}})
  }
}
