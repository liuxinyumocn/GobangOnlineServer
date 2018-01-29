package cn.liuxinyumo.GameEngine;

import java.util.ArrayList;
import java.util.List;

class Room {
	/*
	 *  房间类
	 *  
	 * */
	
	private int RoomID; //房间号
	
	private PlayerInfo A = null;
	private PlayerInfo B = null;
	
	private int Count; //连续对局标记
	
	private boolean Started; //游戏是否开始标志
	//private List<Player> Visited = null;	//观战列表
	
	private GameCore gameCore = null;	//游戏逻辑 每次对局均会重新被创建
	
	public Room(Player A,Player B){
		this.A = new PlayerInfo(A,this);
		this.B = new PlayerInfo(B,this);
		Count = 0; 
		Started = false; 
		//RoomID = roomID;
		//Visited = new ArrayList<>();
		
	}
	
	public void Ready(Player p, boolean v){
		//玩家提交准备信息
		if(Started){
			//无效
			return;
		}else{
			PlayerInfo pi = GetPlayerInfo(p);
			if(pi == null)
				return;
			//System.out.println("Room43");
			pi.SetReady(v);
			RefalshReady();
			Check();
		}
	}
	private void RefalshReady(){
		//向客户端更新准备状态
		boolean a = A.GetReady();
		boolean b = B.GetReady();
		A.RefalshReady(a,b);
		B.RefalshReady(b,a);
	}
	
	private PlayerInfo GetPlayerInfo(Player p){ //返回PlayerInfo
		if(A != null && A.GetPlayer() == p)
			return A;
		if(B != null && B.GetPlayer() == p)
			return B;
		return null;
	}
	
	private void Check(){
		//如果都准备则开始游戏
		if(A.GetReady() == true && B.GetReady() == true){
			//游戏开始
			this.Started = true;
			
			//生成游戏引擎
			Count++;
			gameCore = new GameCore();
			
			//分配角色执棋颜色
			if(Count%2 == 0){
				A.SetColor(1);
				B.SetColor(2);
			}else{
				A.SetColor(2);
				B.SetColor(1);
			}
			
			this.RefalshStart();
		}
	}
	
	private void RefalshStart(){
		//向客户端更新游戏开始 并告知双方执棋颜色
		A.RefalshColor(A.GetColor());
		B.RefalshColor(B.GetColor());
	}
	public void DownPiece(Player p,int x,int y){ //落棋动作
		PlayerInfo pi = this.GetPlayerInfo(p);
		if(pi == null)
			return; //无效棋手
		if(!this.Started){
			//无效动作
			return;
		}
		//获取当前执棋颜色
		int color = gameCore.GetCurrentPlayer();
		//System.out.println("current:"+color+"Player:"+pi.GetColor());
		if(pi.GetColor() == color){
			//许可落子
			//System.out.println("100x:"+x+"y:"+y);
			if(gameCore.Action(x, y)){
				//落子成功获得落子信息并更新
				//System.out.println("x:"+x+"y:"+y);
				int[] re = gameCore.GetStep(); //获取刚刚落子信息
				//System.out.println(re[1]+"-"+re[2]+"-"+re[0]);
				A.RefalshStep(re);
				B.RefalshStep(re);
				//检测是否有胜出玩家
				if(gameCore.Check()){
					//System.out.println("toom117");
					A.RefalshWinner(gameCore.GetWinner());
					B.RefalshWinner(gameCore.GetWinner());
					this.InitNewGame();
				}
			}
			return;
		}else{
			return;//不许可
		}
	}
	private void InitNewGame(){
		this.Started = false;
		A.SetReady(false);
		B.SetReady(false);
	}
	/*
	public boolean Enter(Player player){ //玩家进入房间消息
		if(IsStarted())
			return false; //游戏已经开始
		
		//是否是在场玩家
		PlayerInfo pi = this.GetPlayerInfo(player);
		if(pi != null)
			return true;
		
		//检测是否有空余位置
		if(A != null && B != null){
			return false; //对战人数已满
			//加入观战队列
		}
		PlayerInfo NewPlayer = new PlayerInfo(player); //加入新人 设置信息
		if(A == null)
			A = NewPlayer;
		else if(B == null)
			B = NewPlayer;
		return true;
	}
	*/
	/*
	public void Exit(Player player){ //棋手离场消息
		PlayerInfo pi = this.GetPlayerInfo(player);
		if(pi != null){
			//检测游戏是否进行，如果进行则结束游戏判另方获胜
			
			pi = null;
		}
	}
	
	private void SetPieceColor(){ //设置棋手颜色
		
	}
	
	public boolean Ready(Player player,boolean v){ //准备消息
		PlayerInfo pi = GetPlayerInfo(player);
		if(pi == null)
			return false;
		pi.SetReady(v);
		
		//检测是否是2人均准备，若均准备则开始游戏
		
		return true;
	}
	
	private void Notify(){	//广播消息
		
	}
	
	private PlayerInfo GetPlayerInfo(Player p){ //返回PlayerInfo
		if(A != null && A.GetPlayer() == p)
			return A;
		if(B != null && B.GetPlayer() == p)
			return B;
		return null;
	}
	
	private void Start(){
		//双方均准备则开始游戏
		this.SetPieceColor(); //为玩家分配棋子颜色
		gameCore = new GameCore();
	}
	
	private boolean IsStarted(){
		try{
			return gameCore.IsStarted();
		}catch(Exception e){
			return false;
		}
	}
	*/
}

class PlayerInfo{
	private boolean Ready = false;
	private Player player;
	private int Color;
	private Room room;
	public PlayerInfo(Player player,Room room){
		this.room = room;
		this.player = player;
		Color = 1;
		this.player.SetRoom(room);
	}
	public void SetReady(boolean v){
		Ready = v;
	}
	public boolean GetReady(){
		return Ready;
	}
	public Player GetPlayer(){
		return player;
	}
	public int GetColor(){
		return Color;
	}
	public void SetColor(int v){
		//System.out.println("214 Color"+v);
		Color = v;
	}
	public void RefalshReady(boolean m ,boolean y){
		player.RefalshReady(m,y);
	}
	public void RefalshColor(int v){
		player.RefalshColor(v);
	}
	public void RefalshStep(int[] r){
		player.RefalshStep(r);
	}
	public void RefalshWinner(int r){
		player.RefalshWinner(r);
	}
}