package Main;

import cn.liuxinyumo.GameEngine.Server;

public class Main {

	public static void main(String[] args) {
		System.out.println("Liuxinyumo.cn Gobang游戏服务器");
		Server s = new Server();
		s.SetPort(8888);
		try{
			s.Start();
			System.out.println("游戏服务器启动成功");
		}catch(Exception e){
			System.out.println("游戏服务器启动失败");
		}
	}

}
