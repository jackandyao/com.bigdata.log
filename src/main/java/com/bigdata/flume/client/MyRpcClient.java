package com.bigdata.flume.client;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;
import java.nio.charset.Charset;
/**
 * 创建flume的远程客户端
 * @author 贾红平
 *
 */
public class MyRpcClient {
  public static void main(String[] args) {
	  
    MyRpcClientFacade client = new MyRpcClientFacade();
    
    client.init("192.168.1.129", 41414);

   
    String sampleData = "Hello Flume!";
    for (int i = 0; i < 10; i++) {
      client.sendDataToFlume(sampleData);
    }

    client.cleanUp();
  }
}

class MyRpcClientFacade {
  private RpcClient client;
  private String hostname;
  private int port;

  public void init(String hostname, int port) {
   
    this.hostname = hostname;
    this.port = port;
    this.client = RpcClientFactory.getDefaultInstance(hostname, port);
    
  }

  public void sendDataToFlume(String data) {
  
    Event event = EventBuilder.withBody(data, Charset.forName("UTF-8"));

  
    try {
      client.append(event);
    } catch (EventDeliveryException e) {
      
      client.close();
      client = null;
      client = RpcClientFactory.getDefaultInstance(hostname, port);
     
    }
  }

  public void cleanUp() {
    
    client.close();
  }

}