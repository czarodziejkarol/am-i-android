package com.carlncarl.ami.game;

public class Question {

	private String question;
	private boolean my;
	
	public Question(){
		this.my = false;
	}
	
	public Question(String question, boolean my){
		this.question = question;
		this.my = my;
	}
	
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public boolean isMy() {
		return my;
	}
	public void setMy(boolean my) {
		this.my = my;
	}
	
	@Override
	public boolean equals(Object o) {
		Question q1 = (Question) o;
		return this.question.equals(q1.getQuestion());
	}
}
