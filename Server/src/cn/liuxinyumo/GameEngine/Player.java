package cn.liuxinyumo.GameEngine;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.json.JSONObject;

class Player {
	private Socket socket;
	
	private Thread thread;	//接收线程
	private Thread SendThread; //发送线程
	
	private InputStream in;
	private OutputStream out;
	
	private RoomConsole roomConsole;
	
	private Room room;
	
	public Player(Socket s,RoomConsole roomConsole){
		socket = s;
		this.roomConsole = roomConsole;
		thread = new Thread(new Self());
		try{
			in = s.getInputStream();
			out = s.getOutputStream();
		}catch(Exception e){
			e.printStackTrace();
		}
		thread.start();
	}
	
	private void ParserData(String data){
		//解析DATA
		JSONObject json;
		try{
			json = new JSONObject(data);
			switch(json.getString("ac")){
				case "ApplyRoom":
					//申请好友同房
					this.ClientApplyRoom(json.getString("key"));
				break;
				case "Ready":
					//System.out.println(data);
					this.ClientSetReady(json.getString("mine"));
				break;
				case "Down":
					int x = Integer.parseInt(json.getString("x"));
					int y = Integer.parseInt(json.getString("y"));
					this.ClientDownPiece(x,y);
				break;
			}
			
		}catch(Exception e){
			//未知类型数据
		}
	}
	
	class Self implements Runnable{	//接收用户动作线程
		public void run() {
			Input input = new Input(in);
			try{
				while(input.HasNext()){
					ParserData(input.GetNext());
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	class Send implements Runnable{
		private String Message;
		public Send(String Message){
			this.Message = Message;
		}
		public void run() {
			try{
				Output output = new Output(out);
				output.Sent(Message);
			}catch(Exception e){
				Destructor();
				e.printStackTrace();
			}
		}
	}
	
	private void SentClient(String data){
		new Thread(new Send(data)).start();;
	}
	
	private void Destructor(){ //析构连接
		
	}
	
	public void SetRoom(Room r){
		room = r;
	}
	
	//用户实义信息
	private int Scene = 0;	//用户场景 0 为暂无目的 1 在房间内
	
	public void EnterRoom(Room room){ //进入房间 － 0
		//告诉客户端进入房间
		this.SentClient("{\"ac\":\"EnterRoom\"}");
	}
	private void ExitRoom(){ //离开房间 － 1
		
	}
	private void Readyed(){ //准备
		
	}
	private void UnReadyed(){ //取消准备
		
	}
	private void ClientApplyRoom(String key){
		//去RoomConsole发送 key，如果没有房间，创建等待队列，如果有房间则进入房间。
		synchronized(roomConsole){
			roomConsole.ApplyRoom(this, key);
			this.SentClient("{\"ac\":\"ApplyRoomResult\",\"value\":1}");
		}
	}
	private void ClientSetReady(String v){	//Client是来自客户的
		if(v.equals("1")){
			//变更为准备
			room.Ready(this, true);
		}else{
			//取消准备
			room.Ready(this, false);
		}
	}
	private void ClientDownPiece(int x,int y){
		room.DownPiece(this, x, y);
	}
	
	public void RefalshReady(boolean Mine,boolean Fri){
		String a = Mine == true ? "1" : "0";
		String b = Fri == true ? "1" : "0";
		this.SentClient("{\"ac\":\"ReadyResult\",\"mine\":"+a+",\"friend\":"+b+"}");
	}
	public void RefalshColor(int c){ //设置颜色代表游戏开始
		this.SentClient("{\"ac\":\"ColorResult\",\"mine\":"+c+"}");
	}
	public void RefalshStep(int[] r){
		this.SentClient("{\"ac\":\"Down\",\"x\":"+r[1]+",\"y\":"+r[2]+",\"c\":"+r[0]+"}");
	}
	public void RefalshWinner(int r){
		this.SentClient("{\"ac\":\"Winner\",\"color\":"+r+"}");
	}
}
