package com.carlncarl.ami.game;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.carlncarl.ami.GameActivity;
import com.carlncarl.ami.GameService;

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
	
	
	public static int GAME_PORT = 8888;

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
	
	
	public static LinkedList<Player> players = new LinkedList<Player>();
	
	//playerzy testowi
	public static LinkedList<Player> players_test = new LinkedList<Player>();
	
	
	

	private ServerSocket serverSocket = null;
	private ServerCommunication serCommunication = null;

	public WifiP2pInfo info;

	GameActivity activity;

	private boolean server;

	private GameService gameService;

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
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							while (true) {
								Log.d("AML", "THREAD  POLACZENIE START");
								Socket socket = serverSocket.accept();
								Log.d("AML", "ZAAKCEPTOWANO POLACZENIE");
								Thread t = new Thread(new PlayerCommunication(
										Game.this, socket));
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

	public void sendMessage(int type, String content) {
		for (Player player : playersSet) {

		}
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

		inCommunicats.add(com);
		switch (com.getType()) {
		case Communicat.TYPE_DEVICE_ID:
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
		default:
			break;
		}
		return null;
	}

	private void receiveAnswer(Communicat com) {
		//przes³anie do activity otrzymania odpowiedzi
		
	}

	int answers = 0;
	
	private void sendAnswer(Communicat com) {
		Communicat sCom = new Communicat();
		sCom.setType(Communicat.TYPE_ANSWER);
		sCom.setVal(com.getVal());
		sCom.setPlayerUUID(com.getPlayerUUID());
		sendCommunicat(sCom);
		answers++;
		if(answers==Game.players.size()-1){
			endTurn();
		}
		
		
	}

	private void receiveQuestion(Communicat com) {
		answers = 0;
		String question = com.getVal();
		Player p = getPlayerByUUID(com.getPlayerUUID());
		gameService.receveQuestion(p,question);
	}

	private void sendQuestion(Communicat com) {
		Communicat sCom = new Communicat();
		sCom.setType(Communicat.TYPE_QUESTION);
		sCom.setVal(com.getVal());
		sCom.setPlayerUUID(com.getPlayerUUID());
		sendCommunicat(sCom);
	}

	private void startNewTurn(Communicat com) {
		//szukanie playera z uuid
		for (Player player : Game.players) {
			if(player.getUuid().equals(com.getPlayerUUID())){
				gameService.startTurn(player);
				break;
			}
		}
		
	}
	
	public Player getPlayerByUUID(String uuid){
		for (Player player : Game.players) {
			if(player.getUuid().equals(uuid)){
				return player;
			}
		}
		return null;
	}

	private void typePlayer(Communicat com) {
		Player p = new Player(com);
		players.add(p);
	}

	private void checkGameStatus(Communicat com) {
		if (com.getVal().equals(Communicat.CHOOSE_CHARACTER)) {
			gameService.chooseCharacter();

		} else if(com.getVal().equals(Communicat.STATUS_START)){
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
			players.get(i).setCharacter(chars.get(i));
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
	
	public void startTurn(){
		Communicat com = new Communicat();
		com.setType(Communicat.TYPE_TURN);
		com.setPlayerUUID(Game.players.getFirst().getUuid());
		sendCommunicat(com);
	}
	
	public void askQuestion(Communicat com){
		
	}
	
	public void endTurn(){
		
		
		//przejœcie do nastêpnego gracza
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
									PrintWriter pw = new PrintWriter(
											player.getCommun().socket
													.getOutputStream());
									pw.print(com.toString());
									pw.flush();

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

	public void sendAskedQuestion(String question) {
		Communicat com = new Communicat();
		com.setType(Communicat.TYPE_QUESTION_ASKED);
		com.setVal(question);
		com.setPlayerUUID(me.getUuid());
		sendToServerCommunicat(com);
	}

	public void setAnswerToServer(int answer) {
		Communicat com = new Communicat();
		com.setType(Communicat.TYPE_ANSWER_SERVER);
		com.setVal(Integer.toString(answer));
		com.setPlayerUUID(me.getUuid());
		sendToServerCommunicat(com);
	}

}
