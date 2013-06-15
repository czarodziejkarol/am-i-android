package com.carlncarl.ami.game;

public class Communicat {

	public static final String KOM_DELIMETER = "comend";
	public static final String INSIDE_DELIMETER = "_";
	
	//typy komunikatów
	public static final int TYPE_DEVICE_ID = 1;
	public static final int TYPE_PHOTO = 2;
	public static final int TYPE_PLAYER = 0;
	public static final int TYPE_GAME_STATUS = 3;
	public static final int TYPE_TYPE_CHARACTER = 4;
	public static final int TYPE_DRAWED_CHARACTER = 5;
	
	
	public static final String CHOOSE_CHARACTER = "CHOOSECHAR";
	public static final String STATUS_START = "START";
	public static final int TYPE_TURN = 6;
	public static final int TYPE_QUESTION_ASKED = 7;
	public static final int TYPE_QUESTION = 8;
	public static final int TYPE_ANSWER_SERVER = 9;
	public static final int TYPE_ANSWER = 10;

	private int type;
	private String playerUUID;
	private String playerName;
	private String val;
	private String image;
	//private
	
	
	public String toString(){
		String com = null;
		switch (type) {
		case TYPE_PLAYER:
			com = TYPE_PLAYER + 
			INSIDE_DELIMETER + val + 
			INSIDE_DELIMETER + playerName + 
			INSIDE_DELIMETER + playerUUID + 
			INSIDE_DELIMETER + image + 
			INSIDE_DELIMETER + KOM_DELIMETER;
			break;
		case TYPE_DEVICE_ID:
			com = TYPE_DEVICE_ID + 
			INSIDE_DELIMETER + val + 
			INSIDE_DELIMETER + playerName + 
			INSIDE_DELIMETER + playerUUID + 
			INSIDE_DELIMETER + KOM_DELIMETER;
			break;
		case TYPE_PHOTO:
			com = TYPE_PHOTO + 
			INSIDE_DELIMETER + val + 
			INSIDE_DELIMETER + KOM_DELIMETER;
			break;
		default:
			com = type +
			INSIDE_DELIMETER + val + 
			INSIDE_DELIMETER + playerUUID + 
			INSIDE_DELIMETER + KOM_DELIMETER;
			break;
		}
		return com;
	}
	
	public boolean parse(String com){
		com = com.replace(KOM_DELIMETER, "");
		String[] splittedCom = com.split(INSIDE_DELIMETER);
		if(splittedCom.length == 0){
			return false;
		}
		type = Integer.parseInt(splittedCom[0]);

		switch (type) {
		case TYPE_PLAYER:
			this.val = new String(splittedCom[1]);
			this.playerName = new String(splittedCom[2]);
			this.playerUUID = new String(splittedCom[3]);
			this.image = new String(splittedCom[4]);
			break;
		case TYPE_DEVICE_ID:
			this.val = new String(splittedCom[1]);
			this.playerName = new String(splittedCom[2]);
			this.playerUUID = new String(splittedCom[3]);
			break;
		default:
			this.val = new String(splittedCom[1]);
			this.playerUUID = new String(splittedCom[2]);
			break;
		}
		
		
		return true;
	}

	public String getPlayerUUID() {
		return playerUUID;
	}

	public void setPlayerUUID(String playerUUID) {
		this.playerUUID = playerUUID;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String value) {
		this.val = value;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}


}
