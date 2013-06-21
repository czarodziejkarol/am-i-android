package com.carlncarl.ami.game;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import android.content.Context;

import com.carlncarl.ami.GameService;

public class PlayerCommunication implements Runnable {

	Socket socket;
	Player player;
	Game game;
	GameService gameService;
	public PlayerCommunication(Game game, GameService gameService, Socket socket) {
		this.gameService = gameService;
		this.game = game;
		this.socket = socket;


		// TODO wys³anie wszystkich elementów do danego u¿ytkownika.

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		while (!socket.isClosed()) {

			String lastPlayerPhotoUUID = null;
			Communicat com;
			try {
				DataInputStream dIn = new DataInputStream(
						socket.getInputStream());
				while (!socket.isClosed()) {
					byte messageType;

					messageType = dIn.readByte();

					switch (messageType) {
					case 0:
						com = new Communicat();
						String comStr = dIn.readUTF();

						

						if (com.parse(comStr)) {
							lastPlayerPhotoUUID = com.getPlayerUUID();
							switch (com.getType()) {
							case Communicat.TYPE_DEVICE_ID:
								this.player = game.addIn(com);
								player.setCommun(this);
								break;
							default:
								game.addIn(com);
								break;
							}

							//game.addIn(com);

						}
						break;
					case 1:
						Player p = game.getPlayerByUUID(lastPlayerPhotoUUID);

						String fileName = p.getImage();
						FileOutputStream fos = null;
						try {
							fos = gameService.openFileOutput(fileName,
									Context.MODE_PRIVATE);
							ServerCommunication.readFileFromSocker(dIn, fos);
							fos.flush();
							fos.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					default:
						break;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		/*
		 * 
		 * while (scan.hasNext()) { Communicat com = new Communicat(); String
		 * comStr = scan.next();
		 * 
		 * 
		 * if (com.parse(comStr)) { switch (com.getType()) { case
		 * Communicat.TYPE_DEVICE_ID: this.player = game.addIn(com);
		 * player.setCommun(this); break; case Communicat.TYPE_PHOTO:
		 * //zczytywanie bajt po bajcie FileOutputStream fos = null; try { fos =
		 * game.activity.openFileOutput(com.getVal(), Context.MODE_PRIVATE); }
		 * catch (FileNotFoundException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } try { scan =
		 * ServerCommunication.copyFile(socket.getInputStream(), fos, scan); }
		 * catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } player.setImage(com.getVal());
		 * game.activity.notifyAdapter(); break; default: game.addIn(com);
		 * break; }
		 * 
		 * }
		 * 
		 * }
		 */

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
