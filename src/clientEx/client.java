/*
 * Client
 * 
 * ver0.1
 * 
 * 2016년 5월 2일
 * 
 * 
 */
package clientEx;

import java.io.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;


import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
public class client {
	private Logger log = Logger.getLogger(getClass());
	
	Socket soc;
	DataOutputStream dos;
	DataInputStream dis;
	
	Scanner scan = new Scanner(System.in);
	String sendMsg;
	String receiveMsg;
	
	String type;
	String key;
	String value;
	
	clientReceiver cr;
	Thread tr;
	
	boolean isCan=false;
	Properties prop;
	String ip;
	int portNum;
	
	public client(){
		Config();
	}
	public void Config(){
		prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("client.properties");
			prop.load(input);
			
			portNum = Integer.parseInt(prop.getProperty("port"));
			ip = prop.getProperty("ip");
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		new client().connectServer();
	}
	public void connectServer(){
		
		try {
			soc = new Socket(ip,portNum);
			dos = new DataOutputStream(new BufferedOutputStream(soc.getOutputStream()));
			dis = new DataInputStream(new BufferedInputStream(soc.getInputStream()));
			
			cr = new clientReceiver(dis,this);
			tr = new Thread(cr);
			tr.start();
			while(true){
				if(isCan){
					System.out.println("command :");
					type = scan.nextLine();
					if(type.equals("end")){
						break;
					}else if(type.equals("getA")){
						sendMsg = makeJSONPacket (type);
					}else{
						System.out.println("key :");
						key = scan.nextLine();
						if(type.equals("put")){
							System.out.println("value :");
							value = scan.nextLine();
							sendMsg = makeJSONPacket(type, key, value);
						}else{
							sendMsg = makeJSONPacket(type, key);
						}
					}	
					dos.writeUTF(sendMsg);
					dos.flush();
					System.out.println("======전송======");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			endConnect();
		}
		log.info("연결을 종료 하였습니다.");	
	}

	
	@SuppressWarnings("unchecked")
	public String makeJSONPacket(String type){
		JSONObject packetJSON = new JSONObject();
		packetJSON.put("TYPE", type);

		return packetJSON.toJSONString();
	}
	@SuppressWarnings("unchecked")
	public String makeJSONPacket(String type,String key){
		JSONObject packetJSON = new JSONObject();
		packetJSON.put("TYPE", type);
		packetJSON.put("KEY", key);

		return packetJSON.toJSONString();
	}
	@SuppressWarnings("unchecked")
	public String makeJSONPacket(String type,String key,String value){
		JSONObject packetJSON = new JSONObject();
		packetJSON.put("TYPE", type);
		packetJSON.put("KEY", key);
		packetJSON.put("VALUE", value);

		return packetJSON.toJSONString();
	}
	public void endConnect(){
		try {
			dos.close();
			dis.close();
			soc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
