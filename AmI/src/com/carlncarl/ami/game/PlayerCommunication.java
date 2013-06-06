package com.carlncarl.ami.game;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import android.net.wifi.p2p.WifiP2pDevice;


public class PlayerCommunication implements Runnable {

	
	Socket socket;
	Player player;
	Scanner scan;
	private Game game;
	
	public PlayerCommunication(Game game,Socket socket){
		this.game = game;
		this.socket = socket;
		try {
			scan = new Scanner(socket.getInputStream()).useDelimiter(Communicat.KOM_DELIMETER);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//TODO wys³anie wszystkich elementów do danego u¿ytkownika. 
		
		
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(player == null || player.getDevice().status == WifiP2pDevice.UNAVAILABLE
				||player.getDevice().status == WifiP2pDevice.FAILED){
			
			if(scan.hasNext()){
				Communicat com = new Communicat();
				String comStr = scan.next();
				if(com.parse(comStr)){
					game.addIn(com);
					player.setCommun(this);
				}
			}
		}
	}
	
	public Player getPlayer(){
		return this.player;
	}
	
	public void setPlayer(Player player){
		this.player = player;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}
	

}
