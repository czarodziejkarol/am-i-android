package com.carlncarl.ami.game;

import java.io.Serializable;
import java.net.Socket;
import java.util.LinkedList;

import android.database.Cursor;
import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

import com.carlncarl.ami.db.Database;


public class Player implements Serializable{
	
	private static final long serialVersionUID = -5967579089793811451L;
	public static final String DEFAULT_PHOTO = "default";
    public static final String KEY = "player";
    
    public static final String STATUS_PEER = "found peer";
    public static final String STATUS_CONNECTING = "connecting";
    public static final String STATUS_ACCEPTED = "accepted";
	public static final String STATUS_NOT_CONNECTED = "not connected";
	public static final String IN_GAME = "choosing character";
    
    private WifiP2pDevice device;
    private String uuid;
	private String name;
	private boolean me;
	private boolean auto_add;
	private String image;
	private String status;
	private Socket socket;
	private PlayerCommunication commun;
	private String deviceMAC;
	private String typedCharacter;
	private String character;
	private LinkedList<Action> actions;
	private int winPos = 0;
	
	
	public Player(Cursor c){
		//device.
        c.moveToFirst();
		this.uuid = c.getString(c.getColumnIndex(Database.Player.COLUMN_NAME_UUID));
        this.name = c.getString(c.getColumnIndex(Database.Player.COLUMN_NAME_NAME));
        this.me = c.getInt(c.getColumnIndex(Database.Player.COLUMN_NAME_ME)) == 1;
        this.auto_add = c.getInt(c.getColumnIndex(Database.Player.COLUMN_NAME_AUTO_ADD)) == 1;
        this.image = c.getString(c.getColumnIndex(Database.Player.COLUMN_NAME_IMAGE));

	}

    public Player(String myUUID, String name,String image) {
        this.uuid = myUUID;
        this.name = name;
        this.image = image;
        this.auto_add = true;
        this.me = true;

    }



    public Player(WifiP2pDevice device) {
		this.status = STATUS_PEER;
		this.image = DEFAULT_PHOTO;
		this.name = device.deviceName;
		this.device = device;
	}

	public Player(Communicat com) {
		this.status = IN_GAME;
		this.image = com.getImage();
		this.name = com.getPlayerName();
		this.uuid = com.getPlayerUUID();
		this.deviceMAC = com.getVal();
		this.actions = new LinkedList<Action>();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isMe() {
		return me;
	}

	public void setMe(boolean me) {
		this.me = me;
	}

	public boolean isAuto_add() {
		return auto_add;
	}

	public void setAuto_add(boolean auto_add) {
		this.auto_add = auto_add;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public WifiP2pDevice getDevice() {
		return device;
	}

	public void setDevice(WifiP2pDevice device) {
		this.device = device;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public PlayerCommunication getCommun() {
		return commun;
	}

	public void setCommun(PlayerCommunication commun) {
		this.commun = commun;
	}

	public String getDeviceMAC() {
		return deviceMAC;
	}

	public void setDeviceMAC(String deviceMAC) {
		Log.d("MOJ COS", deviceMAC);
		this.deviceMAC = deviceMAC;
	}

	public String getTypedCharacter() {
		return typedCharacter;
	}

	public void setTypedCharacter(String typedCharacter) {
		this.typedCharacter = typedCharacter;
	}

	public String getCharacter() {
		return character;
	}

	public void setCharacter(String character) {
		this.character = character;
	}

	public LinkedList<Action> getActions() {
		return actions;
	}

	public void setActions(LinkedList<Action> actions) {
		this.actions = actions;
	}

	public void addAction(Action action) {
		action.setPlayer(this);
		actions.add(action);
	}

	public int getWinPos() {
		return winPos;
	}

	public void setWinPos(int winPos) {
		this.winPos = winPos;
	}


}
