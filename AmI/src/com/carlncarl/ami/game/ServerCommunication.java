package com.carlncarl.ami.game;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;


public class ServerCommunication implements Runnable {

	private Game game;
	Socket socket = new Socket();
	
	public ServerCommunication(Game game, WifiP2pInfo info){
		this.game = game;
		
	    
		new ConnectAsyncTask().execute(info);
		
	}
	
	@Override
	public void run() {
		// TODO odbieranie wiadomoœci z serwera
		while(true){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}
	
	private class ConnectAsyncTask extends AsyncTask<WifiP2pInfo, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(WifiP2pInfo... params) {
			WifiP2pInfo info = params[0];
			try {
				socket.bind(null);
				socket.connect((new InetSocketAddress(info.groupOwnerAddress, Game.GAME_PORT)), 500);
				
				OutputStream outputStream = socket.getOutputStream();
				PrintWriter pw = new PrintWriter(outputStream);
				Communicat com = new Communicat();
				com.setType(Communicat.DEVICE_ID);
				com.setPlayerUUID(game.getMe().getUuid());
				com.setValue(game.getMe().getDeviceMAC());
				com.setPlayerName(game.getMe().getName());
				pw.print(com.toString());
				pw.flush();
				outputStream.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
	}

}
