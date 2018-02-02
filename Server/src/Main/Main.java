package Main;

import java.util.Scanner;

import cn.liuxinyumo.GameEngine.Server;

public class Main {

	private Scanner scan;
	private Server s;
	
	public Main(){
		scan = new Scanner(System.in);
	}
	
	public void Start(){
		System.out.println("Liuxinyumo.cn Gobang游戏服务器");
		s = new Server();
		
		//设置端口号：
		int port = 8888;
		port = GetPort();
		s.SetPort(port);
		try{
			s.Start();
			System.out.println("游戏服务器启动成功");
			this.cmd();
		}catch(Exception e){
			System.out.println("游戏服务器启动失败");
		}
	}
	
	private int GetPort(){
		try{
			System.out.print("请设置游戏端口号（通常8888）：");
			int p = scan.nextInt();
			return p;
		}catch(Exception e){
			return GetPort();
		}
	}
	
	private void cmd(){
		try{
			System.out.println("输入数字编号以获取信息：");
			System.out.println("1 当前在线人数");
			System.out.println("2 当前对局房间数目");
			int Number = scan.nextInt();
			switch(Number){
				case 1:
					ShowOnlinePlayerTotal();
				break;
				case 2:
					ShowRoomTotal();
				break;
			}
		}catch(Exception e){
			System.out.println("#异常指令重新选择#");
			cmd();
		}
	}
	
	private void ShowOnlinePlayerTotal(){
		System.out.println("当前在线人数："+s.GetOnlinePlayerTotal());
		cmd();
	}
	
	private void ShowRoomTotal(){
		System.out.println("当前游戏房间数目："+s.GetRoomTotal());
		cmd();
	}
	
	public static void main(String[] args) {
		new Main().Start();
	}

}
