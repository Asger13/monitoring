package com.app.model;

import com.app.service.moduleLogic;
import org.apache.mina.api.IdleStatus;
import org.apache.mina.api.IoHandler;
import org.apache.mina.api.IoService;
import org.apache.mina.api.IoSession;
import org.apache.mina.util.ByteBufferDumper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;



public class ServerHandler implements IoHandler {

        	private static final Logger LOG = LoggerFactory.getLogger(ServerHandler.class);
			private moduleLogic ml = new moduleLogic();

            public void sessionOpened(IoSession session) {
        		LOG.info("server session opened {" + session + "}");
        	}


	public void sessionClosed(IoSession session) {
        	LOG.info("IP:" + session.getRemoteAddress().toString() + " close");
        	}

            public static byte[] hexStringToByteArray(String s) {
                int len = s.length();
                byte[] data = new byte[len / 2];
                for (int i = 0; i < len; i += 2) {
                    data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                            + Character.digit(s.charAt(i+1), 16));
                }
                return data;
            }

	public void sessionIdle(IoSession session, IdleStatus status) {

                	}


                	public void presentationMessage(){

                    }


	public void messageReceived(IoSession session, Object message) {

		//пакет инициализации незачем кидать в очередь
		//кидать ТОЛЬКО пакета с телематическими данными!!!

            			try {
            				LOG.info("Message:"+message.toString());
							LOG.info("HexString:"+ ByteBufferDumper.toHex((ByteBuffer)message));


                            byte[] data = hexStringToByteArray(ByteBufferDumper.toHex((ByteBuffer)message));

							LOG.info("Message received in the server..");


							byte[] test = ml.calculaction(ByteBufferDumper.toHex((ByteBuffer)message),session.getRemoteAddress().toString() );

							((ByteBuffer) message).clear();
							message = ByteBuffer.wrap(test);

							session.write(message);//отправка ответа клиенту

						} catch (Exception e) {
                				e.printStackTrace();
                			}
            		}


	public void messageSent(IoSession session, Object message) {

        		LOG.info("send message:" + message.toString());
        		System.out.println("server send message:" + message.toString());
        	}

	public void serviceActivated(IoService service) {

                	}


	public void serviceInactivated(IoService service) {

                	}


	public void exceptionCaught(IoSession session, Exception cause) {

                	}


	public void handshakeStarted(IoSession abstractIoSession) {

                	}


	public void handshakeCompleted(IoSession session) {

                	}

	public void secureClosed(IoSession session) {

                	}

	private byte[] convertToBytes(Object object) throws IOException {
				try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
					 ObjectOutput out = new ObjectOutputStream(bos)) {
					out.writeObject(object);
					return bos.toByteArray();
				}
			}

        }
