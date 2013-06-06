package com.carlncarl.ami.game;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;

import com.carlncarl.ami.GameActivity;

/**
 * Created by Karol on 20.05.13.
 */
public class Game implements Serializable, ConnectionInfoListener {

	private static final long serialVersionUID = -3870083643336541693L;
	
	public static final int MESSAGE_QUESTION = 0;
	public static final int MESSAGE_ANSWER = 1;
	public static final int MESSAGE_PLAYER_DATA = 2;
	public static final int MESSAGE_TYPE = 3;
	
	public static int GAME_PORT = 8898;
	
	private LinkedList<Communicat> inCommunicats = new LinkedList<Communicat>();
	private LinkedList<Communicat> outCommunicats = new LinkedList<Communicat>();
	
	private String name;
    private Date start;
    private String password;
    private Boolean finished;
    private Player owner;
    private Player me;
    private ArrayList<Player> players;
    
    private ServerSocket serverSocket = null;
    private ServerCommunication serCommunication = null;
    

	WifiP2pInfo info ;
	
	GameActivity activity;
    
    public Game(GameActivity activity){
    	this.activity = activity;
    	players = new ArrayList<Player>();
    	finished = false;
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

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

	public void addPlayer(Player p) {
		this.players.add(p);
		
	}

	public void removeAllPlayers(ArrayList<Player> toDel) {
		this.players.removeAll(toDel);
		
	}
	
	public void sendMessage(int type, String content){
		for (Player player : players) {
			
		}
	}


	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		
		if(activity.getServer()){
    		try {
    			if(serverSocket==null)
				serverSocket = new ServerSocket(GAME_PORT);
    			new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							Socket socket = serverSocket.accept();
							Thread t = new Thread(new PlayerCommunication(Game.this, socket));
							t.start();
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
    	} else {
    		this.info = info;
    		
    		serCommunication = new ServerCommunication(this, info);
    		//TODO wys³anie komunikatu device_ID
    		Thread t = new Thread(serCommunication);
    		t.start();
    		
    		
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

	public synchronized void addIn(Communicat com) {
		inCommunicats.add(com);
		switch (com.getType()) {
		case Communicat.DEVICE_ID:
			for (Player player : players) {
				if(player.getDevice().deviceAddress.equals(com.getValue())){
					player.setName(com.getPlayerName());
					activity.getAdapter().notify();
					break;
				}
			}
			break;

		default:
			break;
		}
	}

	public Player getMe() {
		return me;
	}

	public void setMe(Player me) {
		this.me = me;
	}
}
