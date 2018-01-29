package cn.liuxinyumo.GameEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Room管理器
 * */
class RoomConsole {
	private Map<String,Player> WaitKeys = null;
	
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
		if(WaitKeys.containsValue(p)){
			//移除
			for(Map.Entry entry:WaitKeys.entrySet()){
			    if(p.equals(entry.getValue()))
			    	WaitKeys.remove(entry.getKey());
			}
		}
		
		//查找是否有key的预定房间，若没有则创建
		Player wp = WaitKeys.get(key);
		if(wp == null){
			//则创建一个预定房间
			WaitKeys.put(key, p);
			//System.out.println("创建了一个预定房间");
		}else{
			//与之匹配并共建房间
			Room NewRoom = new Room(wp,p);
			Rooms.add(NewRoom);
			p.EnterRoom(NewRoom);
			wp.EnterRoom(NewRoom);
			WaitKeys.remove(key);
			//System.out.println("两人均进入房间");
		}
	}
}
