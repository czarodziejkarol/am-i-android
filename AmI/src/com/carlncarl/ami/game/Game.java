package com.carlncarl.ami.game;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.carlncarl.ami.GameActivity;
import com.carlncarl.ami.GameService;
import com.carlncarl.ami.db.Database;
import com.carlncarl.ami.db.MySQLiteHelper;

/**
 * Created by Karol on 20.05.13.
 */
public class Game implements Serializable {

	private static final long serialVersionUID = -3870083643336541693L;

	public static final int MESSAGE_QUESTION = 0;
	public static final int MESSAGE_ANSWER = 1;
	public static final int MESSAGE_PLAYER_DATA = 2;
	public static final int MESSAGE_TYPE = 3;

	public static final int ANSWER_YES = 1;
	public static final int ANSWER_NO = 0;
	public static final int ANSWER_DONT_KNOW = 3;

	public static int GAME_PORT = 12287;

	private LinkedList<Communicat> inCommunicats = new LinkedList<Communicat>();
	private LinkedList<Communicat> outCommunicats = new LinkedList<Communicat>();

	private String name;
	private Date start;
	private String password;
	private Boolean finished = false;
	private Player owner;
	private Player me;

	private ArrayList<Player> playersSet;
	private ArrayList<String> myQuestions = new ArrayList<String>();
	private LinkedList<Action> actions = new LinkedList<Action>();
	private LinkedList<Action> myActions = new LinkedList<Action>();

	public static LinkedList<Player> players = new LinkedList<Player>();

	// playerzy testowi
	public static LinkedList<Player> players_test = new LinkedList<Player>();

	private ServerSocket serverSocket = null;
	private ServerCommunication serCommunication = null;

	public WifiP2pInfo info;

	GameActivity activity;
	private int winNumber = 1;
	private boolean server;
	private boolean threadWorking = true;

	private boolean saveQuestions = true;

	private GameService gameService;

	public static final int GAME_STATUS_WRITE_QUESTION = 3;
	public static final int GAME_STATUS_WAIT_FOR_QUESTION = 4;
	public static final int GAME_STATUS_TYPE_ANSWER = 5;
	public static final int GAME_STATUS_WAIT_FOR_ANSWER = 6;
	public static final int GAME_STATUS_WAIT_NEXT_ROUND = 8;

	private int gameStatus;

	public Game(GameActivity activity) {

		this.activity = activity;
		playersSet = new ArrayList<Player>();
		finished = false;

	}

	public void startServer() {
		if (this.server) {
			try {
				if (serverSocket == null) {
					serverSocket = new ServerSocket(GAME_PORT);
					new ServerSendMessageTask().execute("");

				}
				Log.d("AML", "WYSTARTOWANO SERWER");
				// new Thread(new Runnable() {
				//
				// @Override
				// public void run() {
				// try {
				// while (threadWorking) {
				// Thread.sleep(2000);
				// String status = "";
				// if (serverSocket != null) {
				// if (serverSocket.isBound()) {
				// status += "bound ";
				// }
				// if (serverSocket.isClosed()) {
				// status += "closed";
				// }
				// } else {
				// status = "NULL";
				// }
				//
				// Log.d("STATUS SERWERA", status);
				// }
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// }
				// }).start();
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							while (threadWorking) {
								Log.d("AML", "THREAD  POLACZENIE START");
								Socket socket = serverSocket.accept();
								Log.d("AML", "ZAAKCEPTOWANO POLACZENIE");
								Thread t = new Thread(new PlayerCommunication(
										Game.this, Game.this.gameService,
										socket));
								t.start();
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getFinished() {
		return finished;
	}

	public void setFinished(Boolean finished) {
		this.finished = finished;
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public ArrayList<Player> getPlayersSet() {
		return playersSet;
	}

	public void setPlayersSet(ArrayList<Player> players) {
		this.playersSet = players;
	}

	public void addSetPlayer(Player p) {
		this.playersSet.add(p);

	}

	public void removeAllPlayers(ArrayList<Player> toDel) {
		this.playersSet.removeAll(toDel);

	}

	public LinkedList<Communicat> getInCommunicats() {
		return inCommunicats;
	}

	public void setInCommunicats(LinkedList<Communicat> inCommunicats) {
		this.inCommunicats = inCommunicats;
	}

	public LinkedList<Communicat> getOutCommunicats() {
		return outCommunicats;
	}

	public void setOutCommunicats(LinkedList<Communicat> outCommunicats) {
		this.outCommunicats = outCommunicats;
	}

	public synchronized Player addIn(Communicat com) {
		synchronized (inCommunicats) {
			//
			inCommunicats.add(com);
		}
		Log.d("COMMUNICAT:", com.toString());
		switch (com.getType()) {
		case Communicat.TYPE_DEVICE_ID:
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			for (Player player : playersSet) {
				if (com.getVal().equalsIgnoreCase(
						(String) player.getDevice().deviceAddress.toString())) {
					player.setName(com.getPlayerName());
					player.setUuid(com.getPlayerUUID());
					player.setStatus(Player.STATUS_ACCEPTED);
					activity.notifyAdapter();
					return player;
				}
			}
			break;
		case Communicat.TYPE_PLAYER:
			typePlayer(com);
			break;
		case Communicat.TYPE_PHOTO:
			setPlayerPhoto(com);
			break;

		case Communicat.TYPE_GAME_STATUS:
			checkGameStatus(com);
			break;

		case Communicat.TYPE_TYPE_CHARACTER:
			checkTypeCharacter(com);
			break;

		case Communicat.TYPE_DRAWED_CHARACTER:
			drawedCharacter(com);
			break;
		case Communicat.TYPE_TURN:
			startNewTurn(com);
			break;

		case Communicat.TYPE_QUESTION_ASKED:
			sendQuestion(com);
			break;
		case Communicat.TYPE_QUESTION:
			receiveQuestion(com);
			break;
		case Communicat.TYPE_ANSWER_SERVER:
			sendAnswer(com);
			break;
		case Communicat.TYPE_ANSWER:
			receiveAnswer(com);
			break;
		case Communicat.TYPE_AM_I:
			receiveAmIType(com);
			break;
		case Communicat.TYPE_PLAYER_GUESS:
			receivePlayerGuess(com);
			break;
		default:
			break;
		}
		return null;
	}

	private void receivePlayerGuess(Communicat com) {
		Player p = getPlayerByUUID(com.getPlayerUUID(), Game.players);
		if (com.getVal2() != null && !com.getVal2().equals("0")) {
			p.setWinPos(Integer.parseInt(com.getVal2()));
		}
		Action action = new Action();
		action.setType(Action.ACTION_GUESS);
		action.setNumber(com.getNumber());
		action.setPlayer(p);
		action.setValue(com.getVal());
		if (action.getPlayer().getUuid().equals(me.getUuid())) {
			myActions.add(action);
		}
		actions.add(action);

		gameService.playerGuess(action);

	}

	private void receiveAmIType(Communicat com) {
		Player p = getPlayerByUUID(com.getPlayerUUID(), Game.players);
		Communicat sCom = new Communicat();
		sCom.setVal(com.getVal());
		sCom.setType(Communicat.TYPE_PLAYER_GUESS);

		if (p.getCharacter().equalsIgnoreCase(com.getVal())) {
			p.setWinPos(winNumber++);
			// ustawiæ ¿e dobry
			sCom.setVal2(p.getWinPos() + "");
		}

		sCom.setNumber(number++);
		sCom.setPlayerUUID(com.getPlayerUUID());
		sendCommunicat(sCom);

		endTurn();
	}

	private void setPlayerPhoto(Communicat com) {
		Player p = this.getPlayerByUUID(com.getPlayerUUID(), playersSet);
		p.setImage(com.getVal());
	}

	private void receiveAnswer(Communicat com) {
		// przes³anie do activity otrzymania odpowiedzi

		Player p = getPlayerByUUID(com.getPlayerUUID(), Game.players);

		Action action = new Action();
		action.setNumber(com.getNumber());
		action.setPlayer(p);
		action.setValue(com.getVal());
		lastQuestion.addAnswer(action);
		gameService.receiveAnswer(action);
		answers++;
		if (answers == Game.players.size() - 1) {
			endTurn();
		}
	}

	int answers = 0;

	private void sendAnswer(Communicat com) {
		gameStatus = GAME_STATUS_WAIT_NEXT_ROUND;
		Communicat sCom = new Communicat();
		sCom.setNumber(number++);
		sCom.setType(Communicat.TYPE_ANSWER);
		sCom.setVal(com.getVal());
		sCom.setPlayerUUID(com.getPlayerUUID());
		sendCommunicat(sCom);

	}

	private Action lastQuestion;

	private void receiveQuestion(Communicat com) {
		answers = 0;
		String question = com.getVal();
		Player p = getPlayerByUUID(com.getPlayerUUID(), Game.players);
		Action action = new Action();
		lastQuestion = action;
		action.setType(Action.ACTION_QUESTION);
		action.setNumber(com.getNumber());
		action.setValue(question);
		p.addAction(action);
		actions.add(action);
		if (action.getPlayer().getUuid().equals(me.getUuid())) {
			myActions.add(action);
		}

		if (this.saveQuestions) {
			myQuestions.add(question);
			gameService.getPlayActivity().notifyQuestionsAdapter();
			// zapis do db
		}

		if (p.getUuid().equals(this.me.getUuid())) {
			gameStatus = GAME_STATUS_WAIT_FOR_ANSWER;
		} else {
			gameStatus = GAME_STATUS_TYPE_ANSWER;
		}
		gameService.receveQuestion(action);
	}

	private int number = 0;

	private void sendQuestion(Communicat com) {
		Communicat sCom = new Communicat();
		sCom.setType(Communicat.TYPE_QUESTION);
		sCom.setVal(com.getVal());
		sCom.setNumber(number++);
		gameStatus = GAME_STATUS_WAIT_FOR_ANSWER;
		sCom.setPlayerUUID(com.getPlayerUUID());

		sendCommunicat(sCom);
	}

	private void startNewTurn(Communicat com) {
		// szukanie playera z uuid
		for (Player player : Game.players) {
			if (player.getUuid().equals(com.getPlayerUUID())) {
				if (player.getUuid().equals(this.getMe().getUuid())) {
					gameStatus = GAME_STATUS_WRITE_QUESTION;
				} else {
					gameStatus = GAME_STATUS_WAIT_FOR_QUESTION;
				}
				gameService.startTurn(player);
				break;
			}
		}

	}

	public Player getPlayerByUUID(String uuid, List<Player> players) {
		for (Player player : players) {
			if (player.getUuid().equals(uuid)) {
				return player;
			}
		}
		return null;
	}

	private void typePlayer(Communicat com) {
		Player p = new Player(com);
		p.setMe(p.getUuid().equals(me.getUuid()));

		players.add(p);
	}

	private void checkGameStatus(Communicat com) {
		if (com.getVal().equals(Communicat.CHOOSE_CHARACTER)) {
			gameService.chooseCharacter();

		} else if (com.getVal().equals(Communicat.STATUS_START)) {
			gameService.startGame();
		}
	}

	private void drawedCharacter(Communicat com) {
		for (Player player : players) {
			if (player.getUuid().equals(com.getPlayerUUID())) {
				player.setCharacter(com.getVal());
				break;
			}
		}
	}

	private void checkTypeCharacter(Communicat com) {
		for (Player player : players) {
			if (player.getUuid().equals(com.getPlayerUUID())) {
				player.setTypedCharacter(com.getVal());
				break;
			}
		}
		boolean allSet = true;
		for (Player player : players) {
			if (player.getTypedCharacter() == null) {
				allSet = false;
				break;
			}
		}

		if (allSet) {
			// losowanie
			drawCharacters();
		}
	}

	private void drawCharacters() {
		ArrayList<String> chars = new ArrayList<String>();
		for (Player player : players) {
			chars.add(player.getTypedCharacter());
		}
		for (int i = 0; i < players.size() - 1; i++) {
			players.get(i).setCharacter(chars.get(i + 1));
		}
		players.get(players.size() - 1).setCharacter(chars.get(0));
		for (Player player : players) {
			Communicat com = new Communicat();
			com.setType(Communicat.TYPE_DRAWED_CHARACTER);
			com.setVal(player.getCharacter());
			com.setPlayerUUID(player.getUuid());
			sendCommunicat(com);

		}

		Communicat com = new Communicat();
		com.setType(Communicat.TYPE_GAME_STATUS);
		com.setVal(Communicat.STATUS_START);
		sendCommunicat(com);

	}

	public Player getMe() {
		return me;
	}

	public void setMe(Player me) {
		this.me = me;
	}

	public boolean isServer() {
		return server;
	}

	public void setServer(boolean server) {
		this.server = server;
	}

	public ServerCommunication getSerCommunication() {
		return serCommunication;
	}

	public void setSerCommunication(ServerCommunication serCommunication) {
		this.serCommunication = serCommunication;
	}

	public void sendStartData() {
		me.setStatus(Player.STATUS_ACCEPTED);
		playersSet.add(0, me);

		for (Player playerOut : playersSet) {
			if (playerOut.getStatus().equals(Player.STATUS_ACCEPTED)) {
				Communicat com = new Communicat();
				com.setPlayerName(playerOut.getName());
				com.setPlayerUUID(playerOut.getUuid());
				com.setType(Communicat.TYPE_PLAYER);
				com.setImage(playerOut.getImage());
				com.setVal(playerOut.getDeviceMAC());

				synchronized (outCommunicats) {
					outCommunicats.add(com);
				}
			}
		}

		Communicat com = new Communicat();
		com.setType(Communicat.TYPE_GAME_STATUS);
		com.setVal(Communicat.CHOOSE_CHARACTER);
		synchronized (outCommunicats) {
			outCommunicats.add(com);
		}

	}

	public void startTurn() {
		if (isServer()) {
			Communicat com = new Communicat();
			com.setType(Communicat.TYPE_TURN);
			com.setPlayerUUID(Game.players.getFirst().getUuid());
			sendCommunicat(com);
		}
	}

	public void endTurn() {

		// przejœcie do nastêpnego gracza
		Game.players.addLast(Game.players.removeFirst());
		startTurn();
	}

	public void sendCommunicat(Communicat com) {
		synchronized (this.outCommunicats) {

			outCommunicats.addLast(com);
		}
	}

	private class ServerSendMessageTask extends
			AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {

			while (!Game.this.finished) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				while (outCommunicats.size() > 0) {
					Communicat com = outCommunicats.removeFirst();
					for (Player player : Game.this.playersSet) {
						if (player.getStatus().equals(Player.STATUS_ACCEPTED))
							if (player.getUuid().equals(Game.this.me.getUuid())) {
								Game.this.addIn(com);
							} else {
								try {

									DataOutputStream dataOutputStream = new DataOutputStream(
											player.getCommun().socket
													.getOutputStream());
									if (com.getType() == Communicat.TYPE_PHOTO) {
										if (!com.getPlayerUUID().equals(
												player.getUuid())) {

											dataOutputStream.writeByte(0);
											dataOutputStream.writeUTF(com
													.toString());
											dataOutputStream.flush();

											dataOutputStream.writeByte(1);// bajt
																			// 1
																			// dla
																			// obrazka!

											File fp = gameService
													.getFileStreamPath(com
															.getVal());
											InputStream fileIS = new FileInputStream(
													fp);
											ServerCommunication.copyFile(
													fileIS, dataOutputStream);
											fileIS.close();
											dataOutputStream.flush();

										}

									} else {
										dataOutputStream.writeByte(0);
										dataOutputStream.writeUTF(com
												.toString());
										dataOutputStream.flush();
									}

								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
					}
				}

			}

			return null;
		}

	}

	public void setService(GameService gameService) {
		this.gameService = gameService;
	}

	public void sendToServerCommunicat(Communicat com) {
		if (this.server) {
			addIn(com);
		} else {
			serCommunication.sendToServerComm(com);
		}
	}

	public ArrayList<String> getMyQuestions() {
		return myQuestions;
	}

	public LinkedList<Action> getActions() {
		return actions;
	}

	public void setActions(LinkedList<Action> actions) {
		this.actions = actions;
	}

	public Action getLastAction() {
		return lastQuestion;
	}

	public int getGameStatus() {
		return gameStatus;
	}

	public void setGameStatus(int gameStatus) {
		this.gameStatus = gameStatus;
	}

	public void finishGame() {
		this.finished = true;

		if (server) {
			threadWorking = false;
			for (Player player : this.playersSet) {
				try {
					if (player.getCommun() != null
							&& player.getCommun().socket != null
							&& !player.getCommun().socket.isClosed())

						if (player.getCommun() != null
								&& player.getCommun().socket.getInputStream() != null) {
							player.getCommun().socket.getInputStream().close();
						}
					if (player.getCommun() != null
							&& player.getCommun().socket.getOutputStream() != null) {
						player.getCommun().socket.getOutputStream().close();
					}

					{
						if (player.getCommun() != null
								&& player.getCommun().socket != null) {
							player.getCommun().socket.close();
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			try {
				if (serverSocket != null)
					serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				if (serCommunication != null && serCommunication.socket != null
						&& !serCommunication.socket.isClosed()) {
					serCommunication.socket.getOutputStream().close();
					serCommunication.socket.getInputStream().close();
					serCommunication.socket.close();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public boolean isSaveQuestions() {
		return saveQuestions;
	}

	public void setSaveQuestions(boolean saveQuestions) {
		this.saveQuestions = saveQuestions;
	}

	public void loadQuestions() {
		new LoadingQuestionsTask().execute("");
	}

	private class LoadingQuestionsTask extends
			AsyncTask<Object, Integer, ArrayList<String>> {

		@Override
		protected ArrayList<String> doInBackground(Object... params) {
			MySQLiteHelper myHel = new MySQLiteHelper(
					Game.this.gameService.getBaseContext());

			SQLiteDatabase db = myHel.getReadableDatabase();
			try {
				get(1000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String selection = "" + Database.Question.COLUMN_NAME_MY_QUESTION
					+ " = 1";
			Cursor c = db.query(Database.Question.TABLE_NAME, null, selection,
					null, null, null, null);

			ArrayList<String> questions = new ArrayList<String>();
			for (int i = 0; i < c.getCount(); i++) {
				c.moveToNext();
				questions
						.add(c.getString(c
								.getColumnIndex(Database.Question.COLUMN_NAME_QUESTION)));
			}

			db.close();
			return questions;

		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			if (result != null) {
				Game.this.myQuestions = result;
			}
		}
	}

	public Player getPlayerByUUID(String lastPlayerPhotoUUID) {
		// TODO Auto-generated method stub
		return getPlayerByUUID(lastPlayerPhotoUUID, this.playersSet);
	}

	public LinkedList<Action> getMyActions() {
		return myActions;
	}

	public void setMyActions(LinkedList<Action> myActions) {
		this.myActions = myActions;
	}

	public void sendAskedQuestion(String question) {
		Communicat com = new Communicat();
		com.setType(Communicat.TYPE_QUESTION_ASKED);
		com.setVal(question);
		com.setPlayerUUID(me.getUuid());
		sendToServerCommunicat(com);
	}

	public void sendAnswerToServer(int answer) {
		Communicat com = new Communicat();
		com.setType(Communicat.TYPE_ANSWER_SERVER);
		com.setVal(Integer.toString(answer));
		com.setPlayerUUID(me.getUuid());
		sendToServerCommunicat(com);
	}

	public void sendTypeMyCharacter(String character) {
		Communicat com = new Communicat();
		com.setType(Communicat.TYPE_AM_I);
		com.setVal(character);
		com.setPlayerUUID(me.getUuid());
		sendToServerCommunicat(com);
	}
}
