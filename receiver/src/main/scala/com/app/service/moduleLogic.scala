package com.app.service

import java.nio.ByteBuffer
import java.util.Date

import akka.util.ByteString
import com.rabbitmq.client.{Channel, Connection, ConnectionFactory}

import scala.collection.mutable.ArrayBuffer

class moduleLogic {

    private val crc8_table: Array[Int] = Array[Int](0x00, 0x31, 0x62, 0x53, 0xC4, 0xF5, 0xA6, 0x97, 0xB9, 0x88, 0xDB, 0xEA, 0x7D, 0x4C, 0x1F, 0x2E, 0x43, 0x72, 0x21, 0x10, 0x87, 0xB6, 0xE5, 0xD4, 0xFA, 0xCB, 0x98, 0xA9, 0x3E, 0x0F, 0x5C, 0x6D, 0x86, 0xB7, 0xE4, 0xD5, 0x42, 0x73, 0x20, 0x11, 0x3F, 0x0E, 0x5D, 0x6C, 0xFB, 0xCA, 0x99, 0xA8, 0xC5, 0xF4, 0xA7, 0x96, 0x01, 0x30, 0x63, 0x52, 0x7C, 0x4D, 0x1E, 0x2F, 0xB8, 0x89, 0xDA, 0xEB, 0x3D, 0x0C, 0x5F, 0x6E, 0xF9, 0xC8, 0x9B, 0xAA, 0x84, 0xB5, 0xE6, 0xD7, 0x40, 0x71, 0x22, 0x13, 0x7E, 0x4F, 0x1C, 0x2D, 0xBA, 0x8B, 0xD8, 0xE9, 0xC7, 0xF6, 0xA5, 0x94, 0x03, 0x32, 0x61, 0x50, 0xBB, 0x8A, 0xD9, 0xE8, 0x7F, 0x4E, 0x1D, 0x2C, 0x02, 0x33, 0x60, 0x51, 0xC6, 0xF7, 0xA4, 0x95, 0xF8, 0xC9, 0x9A, 0xAB, 0x3C, 0x0D, 0x5E, 0x6F, 0x41, 0x70, 0x23, 0x12, 0x85, 0xB4, 0xE7, 0xD6, 0x7A, 0x4B, 0x18, 0x29, 0xBE, 0x8F, 0xDC, 0xED, 0xC3, 0xF2, 0xA1, 0x90, 0x07, 0x36, 0x65, 0x54, 0x39, 0x08, 0x5B, 0x6A, 0xFD, 0xCC, 0x9F, 0xAE, 0x80, 0xB1, 0xE2, 0xD3, 0x44, 0x75, 0x26, 0x17, 0xFC, 0xCD, 0x9E, 0xAF, 0x38, 0x09, 0x5A, 0x6B, 0x45, 0x74, 0x27, 0x16, 0x81, 0xB0, 0xE3, 0xD2, 0xBF, 0x8E, 0xDD, 0xEC, 0x7B, 0x4A, 0x19, 0x28, 0x06, 0x37, 0x64, 0x55, 0xC2, 0xF3, 0xA0, 0x91, 0x47, 0x76, 0x25, 0x14, 0x83, 0xB2, 0xE1, 0xD0, 0xFE, 0xCF, 0x9C, 0xAD, 0x3A, 0x0B, 0x58, 0x69, 0x04, 0x35, 0x66, 0x57, 0xC0, 0xF1, 0xA2, 0x93, 0xBD, 0x8C, 0xDF, 0xEE, 0x79, 0x48, 0x1B, 0x2A, 0xC1, 0xF0, 0xA3, 0x92, 0x05, 0x34, 0x67, 0x56, 0x78, 0x49, 0x1A, 0x2B, 0xBC, 0x8D, 0xDE, 0xEF, 0x82, 0xB3, 0xE0, 0xD1, 0x46, 0x77, 0x24, 0x15, 0x3B, 0x0A, 0x59, 0x68, 0xFF, 0xCE, 0x9D, 0xAC)
    private val sizeField: Array[Int] = Array[Int](4, 2, 4, 1, 1, 1, 1, 1, 4, 4, 4, 4, 4, 2, 4, 4, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 4, 4, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 4, 2, 1, 4, 2, 2, 2, 2, 2, 1, 1, 1, 2, 4, 2, 1, 8, 2, 1, 16, 4, 2, 4, 37, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 6, 12, 24, 48, 1, 1, 1, 4, 4, 1, 4, 2, 6, 2, 6, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 1)
    private var bitfield: Array[Byte] = Array[Byte](16)
    private var receiveImei:Array[Byte] = Array[Byte](15)

    private val QUEUE_NAME = "telemetric-data"

    private val factory = new ConnectionFactory
    factory.setHost("localhost")


  def calculaction(str: String, ahost:String):Array[Byte]={

    val dataByte = toPresentableFast(str)
    val data: ByteString = ByteString(dataByte)
    val answer = ArrayBuffer[Byte]()
    val host = ahost.drop(1)

    data.head match {
      case 0x40 => {
        //@NTC
        answer += 0x40.toByte
        answer += 0x4e.toByte
        answer += 0x54.toByte
        answer += 0x43.toByte

        answer += data.drop(8).head
        answer += data.drop(9).head
        answer += data.drop(10).head
        answer += data.drop(11).head

        answer += data.drop(4).head
        answer += data.drop(5).head
        answer += data.drop(6).head
        answer += data.drop(7).head

        //количество данных в ответе
        answer += 0x00.toByte
        answer += 0x00.toByte

        answer += 0x00.toByte //csd
        answer += 0x00.toByte //csp

        if ((data.drop(16).take(3)).utf8String == "*>S") {
          //receiveImei = (data.drop(20).take(15)).toArray
          answer(12) = 0x03.toByte
          answer(13) = 0x00.toByte

          answer += 0x2A.toByte
          answer += 0x3C.toByte
          answer += 0x53.toByte

        }
        else if ((data.drop(16).take(6)).utf8String == "*>FLEX") {

          answer(12) = 0x09.toByte
          answer(13) = 0x00.toByte

          val prot = data.drop(22).head & 0xFF
         // println("Протокол:" + prot)
          val protVers = data.drop(23).head & 0xFF
          //println("Версия протокола:" + protVers)
          val protStructVers = data.drop(24).head & 0xFF
          //println("Версия структуры протокола:" + protStructVers)
          val confField = data.drop(25).head & 0xFF
          //println("dataSize:" + confField)
          if (protVers == 10) {
          //println((data.drop(26).head&0xFF).toBinaryString +(data.drop(27).head&0xFF).toBinaryString + (data.drop(28).head&0xFF).toBinaryString + (data.drop(29).head&0xFF).toBinaryString + (data.drop(30).head&0xFF).toBinaryString + (data.drop(31).head&0xFF).toBinaryString + (data.drop(32).head&0xFF).toBinaryString + (data.drop(33).head&0xFF).toBinaryString + (data.drop(34).head&0xFF).toBinaryString)
          }
          if (protVers == 20) {
            //bitfield = (data.drop(26).take(16)).toArray
            //println((data.drop(26).head&0xFF).toBinaryString +(data.drop(27).head&0xFF).toBinaryString + (data.drop(28).head&0xFF).toBinaryString + (data.drop(29).head&0xFF).toBinaryString + (data.drop(30).head&0xFF).toBinaryString + (data.drop(31).head&0xFF).toBinaryString + (data.drop(32).head&0xFF).toBinaryString + (data.drop(33).head&0xFF).toBinaryString + (data.drop(34).head&0xFF).toBinaryString + (data.drop(35).head&0xFF).toBinaryString + (data.drop(36).head&0xFF).toBinaryString + (data.drop(37).head&0xFF).toBinaryString + (data.drop(38).head&0xFF).toBinaryString + (data.drop(39).head&0xFF).toBinaryString + (data.drop(40).head&0xFF).toBinaryString + (data.drop(41).head&0xFF).toBinaryString)
          }
          //*<FLEX ответ
          answer += 0x2a.toByte
          answer += 0x3c.toByte
          answer += 0x46.toByte
          answer += 0x4c.toByte
          answer += 0x45.toByte
          answer += 0x58.toByte

          //Flex ответ - протокол, версия протокола, структура протокола
          answer += data.drop(22).head
          answer += data.drop(23).head
          answer += data.drop(24).head
        }

        val csda = checkSum(answer.toArray.drop(16))
        answer(14) = csda.toByte
        val cspa = checkSum(answer.toArray.take(15))
        answer(15) = cspa.toByte


          val connection: Connection = factory.newConnection
          val channel: Channel = connection.createChannel
          channel.queueDeclare
          channel.basicPublish("", QUEUE_NAME, null, dataByte)
          channel.close()
          connection.close()



      }

      case 0x7e => {
        answer += 0x7e.toByte
        if ((data.drop(1).take(1)).utf8String == "A") {
          answer += 0x41.toByte
          answer += data.drop(2).head
          answer += crc8(answer.toArray).toByte
        }

        else if ((data.drop(1).take(1)).utf8String == "T") {
          answer += 0x54.toByte
          answer += data.drop(2).head
          answer += data.drop(3).head
          answer += data.drop(4).head
          answer += data.drop(5).head
          answer += crc8(answer.toArray).toByte
        }

        else if ((data.drop(1).take(1)).utf8String == "C") {
          answer += 0x43.toByte
          answer += crc8(answer.toArray).toByte
        }

        else if ((data.drop(1).take(1)).utf8String == "X") {
          answer += 0x58.toByte
          answer += data.drop(2).head
          answer += data.drop(3).head
          answer += data.drop(4).head
          answer += data.drop(5).head
          answer += crc8(answer.toArray).toByte
        }

        else if ((data.drop(1).take(1)).utf8String == "E") {
          answer += 0x45.toByte
          answer += data.drop(2).head
          answer += crc8(answer.toArray).toByte
        }

        val connection: Connection = factory.newConnection
        val channel: Channel = connection.createChannel
        channel.queueDeclare
        channel.basicPublish("", QUEUE_NAME, null, dataByte)
        channel.close()
        connection.close()
      }

      case _=>{

        }
      }
    answer.toArray
    }


    def checkSum(bytes: Array[Byte]): Int = {
      var sum = 0
      for (b <- bytes) {
        sum ^= b
      }
      return sum
    }

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

    def crc8(data: Array[Byte]): Int = {
      var crc = 0xFF
      for (b <- data) {
        crc = crc8_table(crc ^ b & 0xFF)
      }
      crc
    }

}

