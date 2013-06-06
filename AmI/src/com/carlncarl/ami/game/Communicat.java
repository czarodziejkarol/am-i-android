package com.carlncarl.ami.game;

public class Communicat {

	public static final String KOM_DELIMETER = "comend";
	public static final String INSIDE_DELIMETER = "com_end";
	
	//typy komunikatów
	public static final int DEVICE_ID = 1;
	
	private int type;
	private String playerUUID;
	private String playerName;
	private String value;
	//private
	
	
	public String toString(){
		String com = null;
		switch (type) {
		case DEVICE_ID:
			com = DEVICE_ID + 
			INSIDE_DELIMETER + value + 
			INSIDE_DELIMETER + playerName + 
			INSIDE_DELIMETER + KOM_DELIMETER;
			break;

		default:
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
		int type = Integer.parseInt(splittedCom[0]);
		
		switch (type) {
		case DEVICE_ID:
			this.value = splittedCom[1];
			this.playerName = splittedCom[2];
			break;

		default:
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
}
