package cn.liuxinyumo.GameEngine;

import java.util.ArrayList;
import java.util.List;

/*
 * GameCore 仅负责游戏逻辑
 * */

class GameCore {
	private int[][] Data;
	private int CurrentPlayer;
	private int Winner;
	private boolean Started = true;
	
	private int[] JustNowStep;
	
	private List<Step> Steps; //步骤记录 用于悔棋
	
	public GameCore(){
		//初始化棋盘
		JustNowStep = new int[3];
		CurrentPlayer = 1;//执黑先手
		Winner = 0;
		
		Data = new int[15][15];
		for(int i = 0;i<15;i++){
			for(int n = 0;n<15;n++){
				Data[i][n] = 0;
			}
		}
		
		Steps = new ArrayList<>();
	}
	public int GetCurrentPlayer(){
		return CurrentPlayer;
	}
	
	public int[] GetStep(){
		return JustNowStep;
	}
	
	public boolean IsStarted(){
		return Started;
	}
	
	public boolean Action(int x,int y){
		if(Winner != 0){
			return false;
		}
		if(x<1||x>15||y<1||y>15)
			return false;
		if(this.Data[x-1][y-1] != 0){
			return false;//已经有落子
		}
		this.Data[x-1][y-1] = this.CurrentPlayer;
		this.AddStep(x,y);
		JustNowStep[0] = this.CurrentPlayer;
		JustNowStep[1] = x;
		JustNowStep[2] = y;
		this.CurrentPlayer = this.CurrentPlayer == 1 ? 2 :1;
		return true;
	}
	
	public boolean Back(){
		if(Winner != 0)
			return false;
		if(this.Steps.size() == 0)
			return true;
		Step s = Steps.get(Steps.size()-1);
		Steps.remove(s);
		this.Data[s.x][s.y] = 0;
		this.CurrentPlayer = this.CurrentPlayer == 1? 2:1;
		return true;
	}
	
	public boolean Check(){
		for(int y = 0;y<15;y++){
			//System.out.println("55");
			for(int x = 0;x<15;x++){
				//System.out.println("6");
				if(this.Data[x][y] == 0)
					continue;
				
				int count = 1;
				//右上扫描
				for(int i=1;i<5;i++){
					if(x+i > 14 || y-i<0)
						break;
					if(this.Data[x+i][y-i] == this.Data[x][y]){
						count++;
					}else{
						break;
					}
				}
				if(count >= 5){
					this.Winner = this.Data[x][y];
					return true;
				}
				//右扫描
				count = 1;
				for(int i=1;i<5;i++){
					if(x+i > 14)
						break;
					if(this.Data[x+i][y] == this.Data[x][y]){
						count++;
					}else{
						break;
					}
				}
				if(count >= 5){
					this.Winner = this.Data[x][y];
					return true;
				}
				//右下扫描
				count = 1;
				for(int i=1;i<5;i++){
					if(x+i > 14 || y+i > 14)
						break;
					if(this.Data[x+i][y+i] == this.Data[x][y]){
						count++;
					}else{
						break;
					}
				}
				if(count >= 5){
					this.Winner = this.Data[x][y];
					return true;
				}
				//下扫描
				count = 1;
				for(int i=1;i<5;i++){
					if(y+i > 14)
						break;
					if(this.Data[x][y+i] == this.Data[x][y]){
						count++;
					}else{
						break;
					}
				}
				if(count >= 5){
					this.Winner = this.Data[x][y];
					return true;
				}
			}
		}
		return false;
	}
	
	private void AddStep(int x,int y){
		Steps.add(new Step(x,y,this.CurrentPlayer));
	}
	
	public int GetWinner(){
		return this.Winner;
	}
}

class Step {
	public int x;
	public int y;
	public int CurrentPlayer;
	public Step(int x,int y,int p){
		this.x = x;
		this.y = y;
		this.CurrentPlayer = p;
	}
}