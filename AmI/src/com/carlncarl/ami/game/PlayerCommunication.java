package com.carlncarl.ami.game;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import android.content.Context;

public class PlayerCommunication implements Runnable {

	Socket socket;
	Player player;
	Scanner scan;
	private Game game;

	public PlayerCommunication(Game game, Socket socket) {
		this.game = game;
		this.socket = socket;
		try {
			scan = new Scanner(socket.getInputStream())
					.useDelimiter(Communicat.KOM_DELIMETER);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO wys³anie wszystkich elementów do danego u¿ytkownika.

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		while (scan.hasNext()) {
			Communicat com = new Communicat();
			String comStr = scan.next();
			
			
			if (com.parse(comStr)) {
				switch (com.getType()) {
				case Communicat.TYPE_DEVICE_ID:
					this.player = game.addIn(com);
					player.setCommun(this);
					break;
				case Communicat.TYPE_PHOTO:
					//zczytywanie bajt po bajcie
					FileOutputStream fos = null;
					try {
						fos = game.activity.openFileOutput(com.getVal(), Context.MODE_PRIVATE);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						scan = ServerCommunication.copyFile(socket.getInputStream(), fos, scan);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					player.setImage(com.getVal());
					game.activity.notifyAdapter();
					break;
				default:
					game.addIn(com);
					break;
				}
				
			}

		}

	}

	public Player getPlayer() {
		return this.player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

}
