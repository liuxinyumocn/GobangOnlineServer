package cn.liuxinyumo.GameEngine;

import java.io.OutputStream;

class Output {
	private OutputStream out;
	public Output(OutputStream out){
		this.out = out;
	}
	
	public void Sent(String data) throws Exception{
			this.out.write(this.PackData(data));
	}
	
	private byte[] PackData(String message){
		byte[] contentBytes = null;
		byte[] temp = message.getBytes();
		//System.out.println("发送的内容字节数："+temp.length);
		if(temp.length < 126){
			contentBytes = new byte[temp.length + 2];
			contentBytes[0] = (byte)0x81;
			contentBytes[1] = (byte)temp.length;
			System.arraycopy(temp, 0, contentBytes, 2, temp.length);
		}else if(temp.length < 0xFFFF){
			contentBytes = new byte[temp.length + 4];
			contentBytes[0] = (byte)0x81;
			contentBytes[1] = 126;
			contentBytes[2] = (byte)(temp.length>>8);
			contentBytes[3] = (byte)(temp.length&0xFF);
			System.arraycopy(temp, 0, contentBytes, 4, temp.length);
		}else{
			contentBytes = new byte[temp.length + 10];
            contentBytes[0] = (byte)0x81;
            contentBytes[1] = 127;
            contentBytes[2] = 0;
            contentBytes[3] = 0;
            contentBytes[4] = 0;
            contentBytes[5] = 0;
            contentBytes[6] = (byte)(temp.length >>24);
            contentBytes[7] = (byte)(temp.length >>16);
            contentBytes[8] = (byte)(temp.length >>8);
            contentBytes[9] = (byte)(temp.length & 0xFF);
		}
		return contentBytes;
	}
}
