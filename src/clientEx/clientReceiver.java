package clientEx;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class clientReceiver implements Runnable{
	private Logger log = Logger.getLogger(getClass());
	
	DataInputStream dis;
	String receiveMsg;
	client cl;
	
	public clientReceiver(DataInputStream dis,client cl) {
		// TODO Auto-generated constructor stub
		this.dis = dis;
		this.cl = cl;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		receiverMsg();
	}
	public void receiverMsg(){
		try {
			while(true){
				receiveMsg = dis.readUTF();
				
				//log.info("[clientReceiver.receiverMsg] receiverMsg : "+receiveMsg);
				
				if(receiveMsg.startsWith("[put]")){
					log.info("client put method");
					log.debug(receiveMsg);
				}else if(receiveMsg.startsWith("[get]")){
					log.info("client get method");
					log.debug(receiveMsg);
				}else if(receiveMsg.startsWith("[con]")){
					cl.isCan = true;
					log.info("client con method");
					log.debug(receiveMsg);
				}else if(receiveMsg.startsWith("[wai]")){
					log.info("client wai method");
					log.debug(receiveMsg);
				}else{
					printContent(receiveMsg);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			endConnect();
		}
	}
	public void printContent(String content){
		StringTokenizer st = new StringTokenizer(content,"/");
		while (st.hasMoreTokens()) {
			log.info(st.nextToken());
		}
	}

	public void endConnect(){
		try {
			dis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
