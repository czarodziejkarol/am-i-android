package com.carlncarl.ami.game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;

import com.carlncarl.ami.GameService;

public class ServerCommunication implements Runnable {

	private Game game;
	Socket socket = new Socket();
	GameService gameService;
	private LinkedList<Communicat> toServerComms = new LinkedList<Communicat>();

	public ServerCommunication(GameService gameService, WifiP2pInfo info) {
		this.gameService = gameService;
		this.game = gameService.getGame();
		this.game.getMe().setDeviceMAC(gameService.myDevice.deviceAddress);
	//	new SendAsyncTask().execute(info);
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
			if (startCommunication(info)) {
				sendCommunicatsThread();
				receiveCommunicatsThread();

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

				DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
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
							
							
							dataOutputStream .writeByte(0);
							dataOutputStream.writeUTF(com
									.toString());
							dataOutputStream.flush();
							
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

	public static void copyFile(InputStream inputStream, OutputStream out) {
		byte buf[] = new byte[1024];
		int len;
		try {
			while ((len = inputStream.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
			// inputStream.close();
		} catch (IOException e) {

		}
	}

	public static void readFileFromSocker(InputStream inputStream,
			OutputStream out) {

		int len;
		try {
			len = inputStream.available();
			byte buf[] = new byte[len];
			inputStream.read(buf);
			out.write(buf, 0, len);
			//
			// while ((len = inputStream.read(buf)) != -1) {
			// out.write(buf, 0, len);
			// }
			// inputStream.close();
		} catch (IOException e) {

		}
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
						//TODO NA NOWE WYSY£ANIE
						
						Communicat com = toServerComms.removeFirst();
						DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
						dataOutputStream .writeByte(0);
						dataOutputStream.writeUTF(com
								.toString());
						dataOutputStream.flush();
					}
				}

			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void receiveCommunicatsThread() {

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				String lastPlayerPhotoUUID = null;
				Communicat com;
				try {
					DataInputStream dIn = new DataInputStream(socket
							.getInputStream());
					while (!socket.isClosed()) {
						byte messageType;

						messageType = dIn.readByte();

						switch (messageType) {
						case 0:
							com = new Communicat();
							String comStr = dIn.readUTF();

							if (com.parse(comStr)) {
								lastPlayerPhotoUUID = com.getPlayerUUID();
								game.addIn(com);
							}
							break;
						case 1:
							Player p = game
									.getPlayerByUUID(lastPlayerPhotoUUID);

							String fileName = p.getImage();
							FileOutputStream fos = null;
							try {
								fos = gameService.openFileOutput(fileName,
										Context.MODE_PRIVATE);
								readFileFromSocker(dIn, fos);
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
		});
		t.start();
	}

	private void sendCommunicatsThread() {
		Thread tSend = new Thread(new Runnable() {

			@Override
			public void run() {

				OutputStream outputStream = null;
				try {
					outputStream = socket.getOutputStream();

					while (true) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						synchronized (toServerComms) {

							while (toServerComms.size() > 0) {
								// TODO NA NOWE WYSY£ANIE

								Communicat com = toServerComms.removeFirst();
								DataOutputStream dataOutputStream = new DataOutputStream(
										outputStream);
								dataOutputStream.writeByte(0);
								dataOutputStream.writeUTF(com.toString());
								dataOutputStream.flush();
							}
						}

					}

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// TODO Auto-generated method stub

			}
		});
		tSend.start();
	}

	private boolean startCommunication(WifiP2pInfo info) {

		try {
			socket.bind(null);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		// try {
		while (!socket.isConnected()) {
			try {
				socket.connect((new InetSocketAddress(info.groupOwnerAddress,
						Game.GAME_PORT)));

			} catch (IOException e) {

				socket = new Socket();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
				return false;
			}
		}

		// } catch (ConnectException e) {
		// // Toast.makeText(ServerCommunication.this.game.activity,
		// // "BLAD POLACZENIA", Toast.LENGTH_SHORT).show();
		// // socket.connect((new InetSocketAddress(
		// // info.groupOwnerAddress, Game.GAME_PORT)));
		// }

		OutputStream outputStream = null;

		try {
			outputStream = socket.getOutputStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
		try {
			dataOutputStream.writeByte(0);// 0 czyli zwyk³y komunikat
			// identyfikacja
			Communicat com = new Communicat();
			com.setType(Communicat.TYPE_DEVICE_ID);
			com.setPlayerUUID(game.getMe().getUuid());
			com.setVal(game.getMe().getDeviceMAC());
			com.setPlayerName(game.getMe().getName());

			dataOutputStream.writeUTF(com.toString());
			dataOutputStream.flush();

			if (!game.getMe().getImage().equals(Player.DEFAULT_PHOTO)) {
				// nazwa photo
				dataOutputStream.writeByte(0);
				com = new Communicat();
				com.setType(Communicat.TYPE_PHOTO);
				com.setPlayerUUID(game.getMe().getUuid());
				com.setVal(game.getMe().getImage());
				dataOutputStream.writeUTF(com.toString());
				dataOutputStream.flush();

				dataOutputStream.writeByte(1);// bajt 1 dla obrazka!

				File fp = gameService
						.getFileStreamPath(game.getMe().getImage());
				InputStream fileIS = new FileInputStream(fp);
				copyFile(fileIS, dataOutputStream);
				fileIS.close();
				dataOutputStream.flush();

			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return true;
	}


}
