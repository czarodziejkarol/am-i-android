package com.carlncarl.ami.game;

import java.util.LinkedList;

public class Action {
    
	public static final int ACTION_QUESTION = 0;
	
	
	
	private int type;
	private Player player;
	private String character;
	private String value;
	private int number;
	private Action parentAction;
	private LinkedList<Action> childs;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public String getCharacter() {
		return character;
	}
	public void setCharacter(String character) {
		this.character = character;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public LinkedList<Action> getChilds() {
		return childs;
	}
	public void setChilds(LinkedList<Action> childs) {
		this.childs = childs;
	}
	public Action getParentAction() {
		return parentAction;
	}
	public void setParentAction(Action parentAction) {
		this.parentAction = parentAction;
	}
	public void addAnswer(Action action){
		action.setParentAction(this);
		this.childs.add(action);
	}
}
