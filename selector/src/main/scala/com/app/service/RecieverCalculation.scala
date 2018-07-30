package com.app.service

import java.nio.ByteBuffer
import java.sql.{DriverManager, PreparedStatement, Timestamp}
import java.util.{Calendar, Date, TimeZone}

import akka.util.ByteString

class RecieverCalculation {

  private val QUEUE_NAME = "telemetric-data"

  private val sizeField: Array[Int] = Array[Int](4, 2, 4, 1, 1, 1, 1, 1, 4, 4, 4, 4, 4, 2, 4, 4, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 4, 4, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 4, 2, 1, 4, 2, 2, 2, 2, 2, 1, 1, 1, 2, 4, 2, 1, 8, 2, 1, 16, 4, 2, 4, 37, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 6, 12, 24, 48, 1, 1, 1, 4, 4, 1, 4, 2, 6, 2, 6, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 1)
  private var bytefield: Array[Byte] = Array[Byte](16)
  private var receiveImei:Array[Byte] = Array[Byte](15)


  val IMEI = scala.collection.mutable.Map[String, ReceivingImei]()
  val bitfieldReceive = scala.collection.mutable.Map[String, ReceivingBitfield]()

  def toPresentableFast(s: String) = {
    val len = s.length
    val data = new Array[Byte](len / 2)
    var i = 0

    while (i < len) {
      val b = (Character.digit(s.charAt(i), 16) << 4) +
        Character.digit(s.charAt(i + 1), 16)
      data(i / 2) = b.asInstanceOf[Byte]
      i += 2
    }
    data
  }
  def getFlexMessage(mass: Array[Byte], size: Int = 1): List[(Int, Array[Byte])] = {
    var result: List[(Int, Array[Byte])] = Nil
    if (size == 1)
      result = (1 -> mass) :: result
    else {
      var count: Int = 1
      var i: Int = 0
      while (i < mass.length) {
        result = (count -> mass.drop(i).take(mass.length / size)) :: result
        count += 1
        i += mass.length / size
      }
    }
    result
  }

   def toAscii(hex: String) = {
    val sb = new StringBuilder
    for (i <- 0 until hex.size by 2) {
      val str = hex.substring(i, i + 2)
      sb.append(Integer.parseInt(str, 16).toChar)
    }
    sb.toString
  }

  def byteArray2BitArray(bytes: Array[Byte]) = {
    val bits = new Array[Boolean](bytes.length * 8)
    var i = 0

    while (i < bytes.length * 8) {
      if ((bytes(i / 8) & (1 << (7 - (i % 8)))) > 0)
        bits(i) = true;
      i += 1
    }
    bits
  }

  def bytesToLong(bytes: Array[Byte]): Long = {
    val buffer: ByteBuffer = ByteBuffer.allocate(8)
    buffer.put(bytes)
    buffer.flip
    return buffer.getLong
  }


  def byteAsULong(b: Byte): Long = {
    return (b.toLong) & 0x00000000000000FFL
  }

  def getUInt32_4(bytes: Array[Byte]): Long = {
    val value: Long = byteAsULong(bytes(0)) | (byteAsULong(bytes(1)) << 8) | (byteAsULong(bytes(2)) << 16) | (byteAsULong(bytes(3)) << 24)
    return value
  }

  def main(body: Array[Byte]): String = {

        val data = ByteString(body)

        val preambul: Array[Byte] = Array[Byte](data.drop(0).head, data.drop(1).head, data.drop(2).head, data.drop(3).head)
        val s = new String(preambul)
        println("Преамбула: " + s)
        val poluchIdArray: Array[Byte] = Array[Byte](data.drop(4).head, data.drop(5).head, data.drop(6).head, data.drop(7).head)
        val poluchId = getUInt32_4(poluchIdArray)
        println("ID получателя:" + poluchId)
        val otpravIdArray: Array[Byte] = Array[Byte](data.drop(8).head, data.drop(9).head, data.drop(10).head, data.drop(11).head)
        val otpravId = getUInt32_4(otpravIdArray)
        println("ID отправителя:" + otpravId)
        val nData: Int = (data.drop(12).head & 0xFF) + (data.drop(13).head & 0xFF) * 256
        println("Количество байт данных:" + nData)
        val csdAll = (data.drop(14).head) & 0xFF
        println("Контрольная сумма всего пакета:" + csdAll)
        val csdHead = (data.drop(15).head) & 0xFF
        println("Контрольная сумма заголовка:" + csdHead)

        val testbool = byteArray2BitArray((data.drop(1).take(9)).toArray)


        if ((data.drop(16).take(3)).utf8String == "*>S") {
          println("handshake")
          //выводим IMEI
          if (IMEI.contains("imei")) {
            IMEI.remove("imei")
          }
          receiveImei = data.drop(20).take(15).toArray
          IMEI.put("imei",new ReceivingImei(receiveImei))

        }

        else if ((data.drop(16).take(6)).utf8String == "*>FLEX") {
          val prot = data.drop(22).head & 0xFF
          println("Протокол:" + prot)
          val protVers = data.drop(23).head & 0xFF
          println("Версия протокола:" + protVers)
          val protStructVers = data.drop(24).head & 0xFF
          println("Версия структуры протокола:" + protStructVers)
          val confField = data.drop(25).head & 0xFF
          println("dataSize:" + confField)
          if (protVers == 10) {
            //println((data.drop(26).head&0xFF).toBinaryString +(data.drop(27).head&0xFF).toBinaryString + (data.drop(28).head&0xFF).toBinaryString + (data.drop(29).head&0xFF).toBinaryString + (data.drop(30).head&0xFF).toBinaryString + (data.drop(31).head&0xFF).toBinaryString + (data.drop(32).head&0xFF).toBinaryString + (data.drop(33).head&0xFF).toBinaryString + (data.drop(34).head&0xFF).toBinaryString)
          }
          if (protVers == 20) {
            bytefield = ((data.drop(26).take(16)).toArray)
            if (bitfieldReceive.contains("bitfield")) {
              bitfieldReceive.remove("bitfield")
            }
            bitfieldReceive.put("bitfield",new ReceivingBitfield(byteArray2BitArray(bytefield)))
            //println((data.drop(26).head&0xFF).toBinaryString +(data.drop(27).head&0xFF).toBinaryString + (data.drop(28).head&0xFF).toBinaryString + (data.drop(29).head&0xFF).toBinaryString + (data.drop(30).head&0xFF).toBinaryString + (data.drop(31).head&0xFF).toBinaryString + (data.drop(32).head&0xFF).toBinaryString + (data.drop(33).head&0xFF).toBinaryString + (data.drop(34).head&0xFF).toBinaryString + (data.drop(35).head&0xFF).toBinaryString + (data.drop(36).head&0xFF).toBinaryString + (data.drop(37).head&0xFF).toBinaryString + (data.drop(38).head&0xFF).toBinaryString + (data.drop(39).head&0xFF).toBinaryString + (data.drop(40).head&0xFF).toBinaryString + (data.drop(41).head&0xFF).toBinaryString)
          }
        }
         val byteArray = body//toPresentableFast(packetLongMessage)

    var imei:String = ""

    if(IMEI.contains("imei")) {
      val selectedImei = IMEI
      val hex = selectedImei("imei").data.map("%02X" format _).mkString
      imei = toAscii(hex)
    }

     var bitfield = Array[Boolean]()
    bitfieldReceive.map(
      bitfieldmessage=> {
        bitfield = bitfieldmessage._2.data
      }
    )

        var packetsIndex: List[(Int, Array[Byte])] = Nil

        //обработка сообщения А
        if (byteArray.drop(1).head == 0x41) {
          packetsIndex = getFlexMessage(byteArray.drop(3).take(byteArray.length - 4), byteArray.drop(2).head.asInstanceOf[Int])
          println("A message")
        }
        //обработка сообщения T
        else if (byteArray.drop(1).head == 0x54) {
          packetsIndex = getFlexMessage(byteArray.drop(6).take(byteArray.length - 7))
          println("T message")
        }
        else{
          "Systeem field received"
        }


      Class.forName("org.postgresql.Driver")
      val db = DriverManager.getConnection("jdbc:postgresql://localhost:5433/monitoring", "postgres", "1")
      db.setAutoCommit(false)
      System.out.println("Opened database successfully")


    val receiveTime = new Date()
    var navigationTime = new Date()
    val isCorrupted = false
    var isValid: Boolean = false
    var lattitude: Double = 0.0
    var longtitude: Double = 0.0
    var attitude: Double = 0.0
    var speed: Double = 0.0
    var power: Double = 0.0
    var fuel = 0
    var sensorData = Array[BigDecimal] {
      10000000
    }

    packetsIndex.map(
          packet => {
            val message = packet._2
            var index: Int = 0
            var indexData: Int = 0

            while (index < bitfield.length) {
              if (bitfield(index)) {
                if (index == 2) {
                  //дата
                  navigationTime = new Date((getUInt32_4((message.drop(indexData).take(4)))) * 1000)
                  println(getUInt32_4((message.drop(indexData).take(4))))
                  println("navigationTime " + navigationTime)
                }
                if (index == 4) {
                  //дата
                  val inputDataString: String = String.format("%8s", Integer.toBinaryString(message(indexData) & 0xFF)).replace(' ', '0')
                  val sensorDataValue: BigDecimal = BigDecimal(inputDataString)

                  sensorData(0) = sensorDataValue
                }
                else if (index == 18) {
                  //напряжение
                  power = (message(indexData).asInstanceOf[Int] & 0xFF + (message(indexData + 1).asInstanceOf[Int] & 0xFF) * 256) * 0.001
                  println("power " + power)
                }
                else if (index == 37) {
                  fuel = (message(indexData).asInstanceOf[Int] & 0xFF + (message(indexData + 1).asInstanceOf[Int] & 0xFF) * 256)
                  println("fuel " + fuel)
                }

                else if (index == 71) {
                  val naviState = message.drop(indexData).head & 0xFF
                  if (naviState == 2)
                    isValid = true
                  else
                    isValid = true
                  println(isValid)
                }
                else if (index == 72) {
                  //долгота и широта с вчп
                  val lattitudeLong = bytesToLong((message.drop(indexData).take(8).reverse))
                  lattitude = BigDecimal(lattitudeLong / 600000000.0).setScale(8, BigDecimal.RoundingMode.HALF_EVEN).toDouble
                  println("lattitude " + lattitude)
                  val longtitudeLong = bytesToLong((message.drop(indexData + 8).take(8).reverse))
                  longtitude = BigDecimal(longtitudeLong / 600000000.0).setScale(8, BigDecimal.RoundingMode.HALF_EVEN).toDouble
                  println("longtitude " + longtitude)
                }
                else if (index == 73) {
                  attitude = BigDecimal(getUInt32_4(message.drop(indexData).take(4)) * 0.001).setScale(3, BigDecimal.RoundingMode.HALF_EVEN).toDouble
                  println("attitude " + attitude)

                }
                else if (index == 75) {
                  //скорость
                  speed = BigDecimal(ByteBuffer.wrap((message.drop(indexData).take(4).reverse)).getFloat).setScale(1, BigDecimal.RoundingMode.HALF_EVEN).toDouble
                  println("speed " + speed)
                }
                indexData += sizeField(index)
              }
              index += 1
            }

            val st = db.createStatement
            val insertSql = """
                              |insert into point (fuel, id_agro_field, speed, power, coordinate, attitude, imei, navigationdate, receivedate)
                              |values (?,?,?,?,ST_GeomFromText('SRID=4326;POINT(' || ? || ' ' || ? || ')'),?,?,?,?);
                            """.stripMargin
            val preparedStmt: PreparedStatement = db.prepareStatement(insertSql)
            preparedStmt.setInt (1, fuel)
            preparedStmt.setInt (2, 5)
            preparedStmt.setDouble  (3, speed)//speed
            preparedStmt.setDouble (4, power)//power
            preparedStmt.setDouble (5, longtitude)//coordinate
            preparedStmt.setDouble (6, lattitude)//coordinate
            preparedStmt.setDouble   (7, attitude)//attitude
            preparedStmt.setString (8, imei)//imei
            preparedStmt.setTimestamp(9, new Timestamp(navigationTime.getTime), Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow")))
            preparedStmt.setTimestamp (10, new Timestamp(receiveTime.getTime), Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow")))//receivedate
            preparedStmt.execute
            st.close
          }
        )
    db.commit
    db.close
    "complete"
  }
}

class ReceivingImei(val data: Array[Byte]) {}

class ReceivingBitfield(val data: Array[Boolean]) {}