package cn.liuxinyumo.GameEngine;

import java.io.InputStream;

/*
 * 接收数据模型
 * */

class Input {
	private int hasRead;
	private InputStream in;
	private byte[] buff;
	public Input(InputStream in){
		hasRead = 0;
		this.in = in;
		buff = new byte[1024];
	}
	public boolean HasNext() throws Exception{
		if((hasRead = in.read(buff))>0){
			return true;
		}else{
			return false;
		}
	}
	public String GetNext(){
		int dataIndex = 6;
		int SecondByte = buff[1]&0x0FF;
		boolean HasMask = SecondByte >= 128 ? true : false;
		int MaskBegin = 2;//掩码起始位
		SecondByte -= HasMask ? 128 : 0;
		int dataLength = 0;
		//判断是几位长度位
		if(SecondByte == 126){
			MaskBegin = 4;
			dataIndex+=2;	//23位长度位
			int a = buff[3]&0x0FF;
			int b = buff[2]<<8;
			dataLength = a+b;
		}else if(SecondByte == 127){
			MaskBegin = 6;
			dataIndex+=8;	//2345位长度位
			int a = buff[5]&0x0FF;
			int b = buff[4]<<8;
			int c = buff[3]<<16;
			int d = buff[4]<<24;
			dataLength = a+b+c+d;
		}else{
			dataLength = SecondByte;
		}
		//解码
		for(int i=0;i<dataLength;i++){
			buff[i+dataIndex] = (byte)(buff[i%4+MaskBegin]^buff[i+dataIndex]);
		}
		String pushMsg;
		try{
			pushMsg = new String(buff,dataIndex,dataLength,"UTF-8");
		}catch(Exception e){
			return null;
		}
		//System.out.println("接收的内容："+pushMsg);
		return pushMsg;
	}
}
