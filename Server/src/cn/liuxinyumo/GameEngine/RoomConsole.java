package cn.liuxinyumo.GameEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Room管理器
 * */
class RoomConsole {
	private Map<String,PlayerRoomInfo> WaitKeys = null;
	
	private List<Room> Rooms = null;
	
	public RoomConsole(){
		WaitKeys = new HashMap<>();
		Rooms = new ArrayList<>();
	}

	public boolean EnterRoom(Player p,String ID){//有用户请求进入一个房间
		
		return false;
	}
	
	public void ApplyRoom(Player p,String key){
		//查找Player是否已经创建过预定单
		//移除
		for(Map.Entry entry:WaitKeys.entrySet()){
		    if(((PlayerRoomInfo)entry.getValue()).player.equals(p))
		    	WaitKeys.remove(entry.getKey());
		}
		
		//查找是否有key的预定房间，若没有则创建
		PlayerRoomInfo wp = WaitKeys.get(key);
		if(wp != null){
			//检测该预订单是否超时
			long now = System.currentTimeMillis();
			if(now - wp.time > 1000*60*5){
				//超时移除
				WaitKeys.remove(key);
				wp = null;
			}
		}
		if(wp == null){
			//则创建一个预定房间
			WaitKeys.put(key, new PlayerRoomInfo(p));
			//System.out.println("创建了一个预定房间");
		}else{
			//与之匹配并共建房间
			Room NewRoom = new Room(wp.player,p,this);
			Rooms.add(NewRoom);
			p.EnterRoom(NewRoom);
			wp.player.EnterRoom(NewRoom);
			WaitKeys.remove(key);
			//System.out.println("两人均进入房间");
		}
	}
	public void DelRoom(Room r){
		Rooms.remove(r);
	}
}

class PlayerRoomInfo{
	public Player player;
	public long time;
	public PlayerRoomInfo(Player p){
		player = p;
		time = System.currentTimeMillis();
	}
}
