package com.carlncarl.ami;

import com.carlncarl.ami.game.Action;
//chyba to nie bêdzie potrzebnt
public class News {
	
	public static final int ANSWER = 0;
	public static final int GUESS = 1;
	
	
	private int type;
	private Action content;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Action getContent() {
		return content;
	}
	public void setContent(Action content) {
		this.content = content;
	}
}
