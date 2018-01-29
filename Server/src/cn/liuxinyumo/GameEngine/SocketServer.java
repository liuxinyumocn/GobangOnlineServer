package cn.liuxinyumo.GameEngine;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SocketServer {
	private ServerSocket serverSocket ;
	private Thread receiveThread;
	private int port;
	private boolean open = true;
	
	private PlayerConsole playerConsole;
	
	
	public SocketServer(PlayerConsole pc ,int port) throws Exception{
		this.playerConsole = pc;
		this.port = port;
		serverSocket = new ServerSocket(port);
		receiveThread = new Thread(new ReceiveThread(serverSocket),"接待线程");
		receiveThread.start();
	}
	
	class ReceiveThread implements Runnable{	//入口接待线程
		private ServerSocket serverSocket;
		public ReceiveThread(ServerSocket ss){
			serverSocket = ss;
		}
		public void run(){
			//开启接待
			while(open){
				try{
					//产生新的连接
					Socket socket = serverSocket.accept();
					//将该连接转移至验证线程
					new Thread(new CheckThread(socket)).start();
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
		}
	}
	
	class CheckThread implements Runnable{ //验证线程
		private Socket client;
		public CheckThread(Socket c){
			client = c;
		}
		public void run() {
			try{
				InputStream in = client.getInputStream();
				OutputStream out = client.getOutputStream();
				byte[] buff = new byte[1024];
				String req = "";
				int count = in.read(buff);
				if(count > 0){
					req = new String(buff,0,count);
					//System.out.println("握手请求："+req);
					//获取Key
					String secKey = getSecWebSocketKey(req);
					//System.out.println("secKey = "+secKey);
					String response = "HTTP/1.1 101 Switching Protocols\r\nUpgrade:"+"websocket\r\nConnection:Upgrade\r\nSec-WebSocket-Accept:"+getSecWebSocketAccept(secKey)+"\r\n\r\n";
					//System.out.println("secAccept = "+getSecWebSocketAccept(secKey));
					out.write(response.getBytes());
				}
				//握手成功 交由PlayerConsole管理
				
				synchronized(playerConsole){
					playerConsole.AddClient(client);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	private String getSecWebSocketKey(String req){
		Pattern p = Pattern.compile("^(Sec-WebSocket-Key:).+",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(req);

		if(m.find()){
			String foundstring = m.group();
			return foundstring.split(":")[1].trim();
		}
		else{
			return null;
		}
	}

	private String getSecWebSocketAccept(String key) throws Exception{
		String guid = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
		key += guid;
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(key.getBytes("utf-8"),0,key.length());
		byte[] sha1Hash = md.digest();
		Encoder base = Base64.getEncoder();
        key = base.encodeToString(sha1Hash);
		return base.encodeToString(sha1Hash);
	}
}
