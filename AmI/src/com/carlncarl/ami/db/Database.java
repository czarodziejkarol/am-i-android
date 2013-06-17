package com.carlncarl.ami.db;


public class Database {

	public static abstract class Player  {
		public static final String TABLE_NAME = "players";
		public static final String COLUMN_NAME_NAME = "player_name";
		public static final String COLUMN_NAME_ME = "me";
		public static final String COLUMN_NAME_IMAGE = "image";
		public static final String COLUMN_NAME_UUID = "uuid";
		// rozne opcje moga dojscs
		public static final String COLUMN_NAME_AUTO_ADD = "auto_add";
	}

	public static abstract class Game {
		public static final String TABLE_NAME = "games";
		public static final String COLUMN_NAME_DATE = "game_start ";
		public static final String COLUMN_NAME_FINISHED = "finished";
		public static final String COLUMN_NAME_OWNER = "owner";
	}

	public static abstract class Character {
		public static final String TABLE_NAME = "characters";
		public static final String COLUMN_NAME_NAME = "character_name";
	}

	public static abstract class GamePlayers {
		public static final String TABLE_NAME = "games_players";
		public static final String COLUMN_NAME_PLAYER_UUID = "player_uuid";
		public static final String COLUMN_NAME_CHARACTER_NAME = "character_name";
		public static final String COLUMN_NAME_GAME_START = "game_start";
		public static final String COLUMN_NAME_POSITION = "position";
	}

	public static abstract class Question {
		public static final String TABLE_NAME = "questions";
		public static final String COLUMN_NAME_QUESTION = "question";
		public static final String COLUMN_NAME_MY_QUESTION = "my_question";
	}

	public static abstract class GameAction  {
		public static final String TABLE_NAME = "games_actions";
		public static final String COLUMN_NAME_TYPE = "type";
		public static final String COLUMN_NAME_PLAYER_UUID = "player_uuid";
		public static final String COLUMN_NAME_GAME_START = "game_start";
		public static final String COLUMN_NAME_VALUE = "value";
		
		public static final String COLUMN_NAME_PARENT_ACTION_NUMBER = "number";
		public static final String COLUMN_NAME_ACTION_NUMBER = "parent_number";
	}

	private Database() {
	}
}