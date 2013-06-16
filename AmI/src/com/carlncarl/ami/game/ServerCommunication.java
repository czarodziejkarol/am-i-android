package com.carlncarl.ami.game;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;

import com.carlncarl.ami.GameService;

public class ServerCommunication implements Runnable{

	private Game game;
	Socket socket = new Socket();
	Scanner scan;
	private LinkedList<Communicat> toServerComms = new LinkedList<Communicat>();

	public ServerCommunication(GameService gameService, WifiP2pInfo info) {
		this.game = gameService.getGame();
		this.game.getMe().setDeviceMAC(gameService.myDevice.deviceAddress);
		//new SendAsyncTask().execute(info);
		new ConnectAsyncTask().execute(info);
		
	}

	public void sendToServerComm(Communicat comm) {

			synchronized (toServerComms) {
				toServerComms.addLast(comm);
			}
		
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	private class ConnectAsyncTask extends
			AsyncTask<WifiP2pInfo, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(WifiP2pInfo... params) {
			WifiP2pInfo info = params[0];
			try {
				socket.bind(null);
				try {
					socket.connect((new InetSocketAddress(
							info.groupOwnerAddress, Game.GAME_PORT)));
				} catch (ConnectException e) {
					// Toast.makeText(ServerCommunication.this.game.activity,
					// "BLAD POLACZENIA", Toast.LENGTH_SHORT).show();
					socket.connect((new InetSocketAddress(
							info.groupOwnerAddress, Game.GAME_PORT)));
				}
				OutputStream outputStream = socket.getOutputStream();
				PrintWriter pw = new PrintWriter(outputStream);
				Communicat com = new Communicat();
				com.setType(Communicat.TYPE_DEVICE_ID);
				com.setPlayerUUID(game.getMe().getUuid());
				com.setVal(game.getMe().getDeviceMAC());
				com.setPlayerName(game.getMe().getName());
				pw.print(com.toString());
				pw.flush();

				Thread  t = new Thread(ServerCommunication.this);
				t.start();
				
				try {
					scan = new Scanner(socket.getInputStream())
							.useDelimiter(Communicat.KOM_DELIMETER);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				while (scan.hasNext()) {
					com = new Communicat();
					String comStr = scan.next();

					if (com.parse(comStr)) {
						game.addIn(com);
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}

	private class SendAsyncTask extends
			AsyncTask<WifiP2pInfo, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(WifiP2pInfo... params) {
			do {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} while (!socket.isConnected());

			OutputStream outputStream = null;
			try {
				outputStream = socket.getOutputStream();
				

				while (socket.isConnected()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					synchronized (toServerComms) {

						while (toServerComms.size() > 0) {
							Communicat com = toServerComms.removeFirst();
							PrintWriter pw = new PrintWriter(outputStream);
							pw.print(com.toString());
							pw.flush();
						}
					}

				}

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		}

	}

	public static Scanner copyFile(InputStream inputStream, OutputStream out,
			Scanner sc) {
		byte buf[] = new byte[1024];
		int len;
		try {
			while ((len = inputStream.read(buf)) != -1) {
				out.write(buf, 0, len);
			}

			if (sc != null) {
				sc = new Scanner(inputStream)
						.useDelimiter(Communicat.KOM_DELIMETER);
			}
		} catch (IOException e) {

			return null;
		}
		return sc;
	}

	@Override
	public void run() {
		OutputStream outputStream = null;
		try {
			outputStream = socket.getOutputStream();
			

			while (socket.isConnected()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				synchronized (toServerComms) {

					while (toServerComms.size() > 0) {
						Communicat com = toServerComms.removeFirst();
						PrintWriter pw = new PrintWriter(outputStream);
						pw.print(com.toString());
						pw.flush();
					}
				}

			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
