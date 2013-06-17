package com.carlncarl.ami.db;

import com.carlncarl.ami.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "AmI.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TEXT_TYPE = " TEXT";
	private static final String INT_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_PLAYERS_TABLE = "CREATE TABLE " + Database.Player.TABLE_NAME + " ("
            + Database.Player.COLUMN_NAME_UUID + TEXT_TYPE + " PRIMARY KEY ,"
            + Database.Player.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP
            + Database.Player.COLUMN_NAME_ME + INT_TYPE + COMMA_SEP
            + Database.Player.COLUMN_NAME_AUTO_ADD + INT_TYPE + COMMA_SEP
            + Database.Player.COLUMN_NAME_IMAGE + TEXT_TYPE + " );";
    private static final String SQL_CREATE_CHARACTERS_TABLE = " CREATE TABLE " + Database.Character.TABLE_NAME + " ("
            + Database.Character.COLUMN_NAME_NAME + TEXT_TYPE + " PRIMARY KEY" + " );";
    private static final String SQL_CREATE_GAME_TABLE = " CREATE TABLE " + Database.Game.TABLE_NAME + " ("
            + Database.Game.COLUMN_NAME_DATE + " INTEGER PRIMARY KEY ,"
            + Database.Game.COLUMN_NAME_FINISHED + INT_TYPE + COMMA_SEP
            + Database.Game.COLUMN_NAME_OWNER + TEXT_TYPE + COMMA_SEP
            + " FOREIGN KEY (" + Database.Game.COLUMN_NAME_OWNER
            + ") REFERENCES " + Database.Player.TABLE_NAME + "("
            + Database.Player.COLUMN_NAME_UUID + ")" + " );";
    private static final String SQL_CREATE_GAME_PLAYERS_TABLE = " CREATE TABLE " + Database.GamePlayers.TABLE_NAME + " ("
            + Database.GamePlayers.COLUMN_NAME_PLAYER_UUID + TEXT_TYPE
            + COMMA_SEP + Database.GamePlayers.COLUMN_NAME_GAME_START
            + INT_TYPE + COMMA_SEP
            + Database.GamePlayers.COLUMN_NAME_CHARACTER_NAME + TEXT_TYPE
            + COMMA_SEP + Database.GamePlayers.COLUMN_NAME_POSITION + INT_TYPE
            + COMMA_SEP + " PRIMARY KEY ("
            + Database.GamePlayers.COLUMN_NAME_PLAYER_UUID + COMMA_SEP
            + Database.GamePlayers.COLUMN_NAME_GAME_START + COMMA_SEP
            + Database.GamePlayers.COLUMN_NAME_CHARACTER_NAME + ")" + COMMA_SEP
            + " FOREIGN KEY (" + Database.GamePlayers.COLUMN_NAME_PLAYER_UUID
            + ") REFERENCES " + Database.Player.TABLE_NAME + "("
            + Database.Player.COLUMN_NAME_UUID + ")" + COMMA_SEP
            + " FOREIGN KEY (" + Database.GamePlayers.COLUMN_NAME_GAME_START
            + ") REFERENCES " + Database.Game.TABLE_NAME + "("
            + Database.Game.COLUMN_NAME_DATE + ")"
            + " );";
    private static final String SQL_CREATE_QUESTIONS_TABLE = " CREATE TABLE " + Database.Question.TABLE_NAME + " ("
            + Database.Question.COLUMN_NAME_QUESTION + TEXT_TYPE
            + " PRIMARY KEY " + COMMA_SEP
            + Database.Question.COLUMN_NAME_MY_QUESTION + INT_TYPE + " ) ;";
	private static final String SQL_CREATE_GAME_ACTIONS_TABLE = " CREATE TABLE " 
            + Database.GameAction.TABLE_NAME + " ("
			+ Database.GameAction.COLUMN_NAME_PLAYER_UUID + TEXT_TYPE
			+ COMMA_SEP + Database.GameAction.COLUMN_NAME_GAME_START + INT_TYPE
			+ COMMA_SEP + Database.GameAction.COLUMN_NAME_ACTION_NUMBER
			+ INT_TYPE + COMMA_SEP + Database.GameAction.COLUMN_NAME_PARENT_ACTION_NUMBER
			+ INT_TYPE + COMMA_SEP + Database.GameAction.COLUMN_NAME_TYPE
			+ INT_TYPE + COMMA_SEP + Database.GameAction.COLUMN_NAME_VALUE
			+ TEXT_TYPE + COMMA_SEP + " PRIMARY KEY ("
			+ Database.GameAction.COLUMN_NAME_GAME_START + COMMA_SEP
			+ Database.GameAction.COLUMN_NAME_ACTION_NUMBER + ")" + COMMA_SEP
			+ " FOREIGN KEY (" + Database.GameAction.COLUMN_NAME_PLAYER_UUID
			+ ") REFERENCES " + Database.Player.TABLE_NAME + "("
			+ Database.Player.COLUMN_NAME_UUID + ")" + COMMA_SEP
			+ " FOREIGN KEY (" + Database.GameAction.COLUMN_NAME_GAME_START
			+ ") REFERENCES " + Database.Game.TABLE_NAME + "("
			+ Database.Game.COLUMN_NAME_DATE + ")" + " );" ;

    private Context context;


	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_PLAYERS_TABLE);
        db.execSQL(SQL_CREATE_CHARACTERS_TABLE);
        db.execSQL(SQL_CREATE_GAME_TABLE);
        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        db.execSQL(SQL_CREATE_GAME_PLAYERS_TABLE);
        db.execSQL(SQL_CREATE_GAME_ACTIONS_TABLE);

        String[] characters = context.getResources().getStringArray(R.array.characters_array);
        for(String character: characters){
            ContentValues values = new ContentValues();
            values.put(Database.Character.COLUMN_NAME_NAME,character);
            Log.d("Dodana: ",db.insert(Database.Character.TABLE_NAME , null , values)+"");
        }

        String[] questions = context.getResources().getStringArray(R.array.questions_array);
        for(String question: questions){
            ContentValues values = new ContentValues();
            values.put(Database.Question.COLUMN_NAME_QUESTION,question);
            values.put(Database.Question.COLUMN_NAME_MY_QUESTION, 1);
            db.insert(Database.Question.TABLE_NAME , null , values);
        }

    }

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	// private static class Database2 {
	// private static final String TEXT_TYPE = " TEXT";
	// private static final String INT_TYPE = " INTEGER";
	// private static final String COMMA_SEP = ",";
	//
	// public static abstract class Player implements BaseColumns {
	// public static final String TABLE_NAME = "players";
	// public static final String COLUMN_NAME_NAME = "name";
	// public static final String COLUMN_NAME_IMAGE = "image";
	// // różne opcje mogą dojść
	// public static final String COLUMN_NAME_AUTO_ADD = "auto_add";
	// }
	//
	// public static abstract class Game implements BaseColumns {
	// public static final String TABLE_NAME = "games";
	// public static final String COLUMN_NAME_DATE = "start_date";
	// public static final String COLUMN_NAME_FINISHED = "finished";
	// public static final String COLUMN_NAME_OWNER = "owner";
	// }
	//
	// public static abstract class Character implements BaseColumns {
	// public static final String TABLE_NAME = "characters";
	// public static final String COLUMN_NAME_NAME = "name";
	// }
	//
	// public static abstract class GamePlayers implements BaseColumns {
	// public static final String TABLE_NAME = "games_players";
	// public static final String COLUMN_NAME_PLAYER_ID = "player_id";
	// public static final String COLUMN_NAME_CHARACTER_ID = "character_id";
	// public static final String COLUMN_NAME_GAME_ID = "game_id";
	// public static final String COLUMN_NAME_POSITION = "position";
	// }
	//
	// public static abstract class Question implements BaseColumns {
	// public static final String TABLE_NAME = "questions";
	// public static final String COLUMN_NAME_QUESTION = "question";
	// public static final String COLUMN_NAME_MY_QUESTION = "my_question";
	// }
	//
	// public static abstract class GameAction implements BaseColumns {
	// public static final String TABLE_NAME = "games_actions";
	// public static final String COLUMN_NAME_TYPE = "type";
	// public static final String COLUMN_NAME_PLAYER_ID = "player_id";
	// public static final String COLUMN_NAME_GAME_ID = "game_id";
	// public static final String COLUMN_NAME_VALUE = "value";
	// public static final String COLUMN_NAME_QUESTION_ID = "question_id"; //
	// ?czy
	// // na
	// // pewno
	// // potrzebne
	// public static final String COLUMN_NAME_POSITION = "position";
	// }
	//
	// public static abstract class Answer implements BaseColumns {
	// public static final String TABLE_NAME = "games_actions";
	// public static final String COLUMN_NAME_PLAYER_ID = "player_id";
	// public static final String COLUMN_NAME_GAME_ACTION_ID = "game_action_id";
	// public static final String COLUMN_NAME_VALUE = "value";
	// }

	// private static final String SQL_CREATE_ENTRIES =
	//
	// "CREATE TABLE " + Database2.Player.TABLE_NAME + " ("
	// + Database2.Player._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
	// + Database2.Player.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP
	// + Database2.Player.COLUMN_NAME_AUTO_ADD + INT_TYPE + COMMA_SEP
	// + Database2.Player.COLUMN_NAME_IMAGE + TEXT_TYPE + " )" + ";"
	//
	// + "CREATE TABLE " + Database2.Game.TABLE_NAME + " ("
	// + Database2.Game._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
	// + Database2.Game.COLUMN_NAME_DATE + INT_TYPE + COMMA_SEP
	// + Database2.Game.COLUMN_NAME_FINISHED + INT_TYPE + COMMA_SEP
	// + Database2.Game.COLUMN_NAME_OWNER + INT_TYPE + COMMA_SEP
	// + " FOREIGN KEY (" + Database2.Game.COLUMN_NAME_OWNER
	// + ") REFERENCES " + Database2.Player.TABLE_NAME + "("
	// + Database2.Player._ID + ")" + " )" + ";"
	//
	// + "CREATE TABLE " + Database2.Character.TABLE_NAME + " ("
	// + Database2.Character._ID
	// + " INTEGER PRIMARY KEY AUTOINCREMENT,"
	// + Database2.Character.COLUMN_NAME_NAME + TEXT_TYPE + " ) ;"
	//
	// + "CREATE TABLE " + Database2.GamePlayers.TABLE_NAME + " ("
	// + Database2.GamePlayers._ID
	// + " INTEGER PRIMARY KEY AUTOINCREMENT,"
	// + Database2.GamePlayers.COLUMN_NAME_PLAYER_ID + INT_TYPE
	// + COMMA_SEP + Database2.GamePlayers.COLUMN_NAME_GAME_ID
	// + INT_TYPE + COMMA_SEP
	// + Database2.GamePlayers.COLUMN_NAME_CHARACTER_ID + INT_TYPE
	// + COMMA_SEP + Database2.GamePlayers.COLUMN_NAME_POSITION
	// + INT_TYPE + COMMA_SEP + " FOREIGN KEY ("
	// + Database2.GamePlayers.COLUMN_NAME_PLAYER_ID + ") REFERENCES "
	// + Database2.Player.TABLE_NAME + "(" + Database2.Player._ID
	// + ")" + COMMA_SEP + " FOREIGN KEY ("
	// + Database2.GamePlayers.COLUMN_NAME_GAME_ID + ") REFERENCES "
	// + Database2.Game.TABLE_NAME + "(" + Database2.Game._ID + ")"
	// + COMMA_SEP + " FOREIGN KEY ("
	// + Database2.GamePlayers.COLUMN_NAME_CHARACTER_ID
	// + ") REFERENCES " + Database2.Character.TABLE_NAME + "("
	// + Database2.Character._ID + ")" + " )" + ";"
	//
	// + "CREATE TABLE " + Database2.Question.TABLE_NAME + " ("
	// + Database2.Question._ID
	// + " INTEGER PRIMARY KEY AUTOINCREMENT,"
	// + Database2.Question.COLUMN_NAME_QUESTION + TEXT_TYPE
	// + COMMA_SEP + Database2.Question.COLUMN_NAME_MY_QUESTION
	// + INT_TYPE + " ) ;"
	//
	// + "CREATE TABLE " + Database2.GameAction.TABLE_NAME + " ("
	// + Database2.GameAction._ID
	// + " INTEGER PRIMARY KEY AUTOINCREMENT,"
	// + Database2.GameAction.COLUMN_NAME_PLAYER_ID + INT_TYPE
	// + COMMA_SEP + Database2.GameAction.COLUMN_NAME_GAME_ID
	// + INT_TYPE + COMMA_SEP
	// + Database2.GameAction.COLUMN_NAME_POSITION + INT_TYPE
	// + COMMA_SEP + Database2.GameAction.COLUMN_NAME_QUESTION_ID
	// + INT_TYPE + COMMA_SEP + Database2.GameAction.COLUMN_NAME_TYPE
	// + INT_TYPE + COMMA_SEP + Database2.GameAction.COLUMN_NAME_VALUE
	// + TEXT_TYPE + COMMA_SEP + " FOREIGN KEY ("
	// + Database2.GameAction.COLUMN_NAME_PLAYER_ID + ") REFERENCES "
	// + Database2.Player.TABLE_NAME + "(" + Database2.Player._ID
	// + ")" + COMMA_SEP + " FOREIGN KEY ("
	// + Database2.GameAction.COLUMN_NAME_GAME_ID + ") REFERENCES "
	// + Database2.Game.TABLE_NAME + "(" + Database2.Game._ID + ")"
	// + COMMA_SEP + " FOREIGN KEY ("
	// + Database2.GameAction.COLUMN_NAME_QUESTION_ID
	// + ") REFERENCES " + Database2.Question.TABLE_NAME + "("
	// + Database2.Question._ID + ")" + " )" + ";"
	//
	// + "CREATE TABLE " + Database2.Answer.TABLE_NAME + " ("
	// + Database2.Answer._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
	// + Database2.Answer.COLUMN_NAME_GAME_ACTION_ID + INT_TYPE
	// + COMMA_SEP + Database2.Answer.COLUMN_NAME_PLAYER_ID + INT_TYPE
	// + COMMA_SEP + Database2.Answer.COLUMN_NAME_VALUE + TEXT_TYPE
	// + COMMA_SEP + " FOREIGN KEY ("
	// + Database2.Answer.COLUMN_NAME_GAME_ACTION_ID + ") REFERENCES "
	// + Database2.GameAction.TABLE_NAME + "("
	// + Database2.GameAction._ID + ")" + COMMA_SEP + " FOREIGN KEY ("
	// + Database2.Answer.COLUMN_NAME_PLAYER_ID + ") REFERENCES "
	// + Database2.Player.TABLE_NAME + "(" + Database2.Player._ID
	// + ")" + " )" + ";" + ";";
	//
	// private Database2() {
	// }
	// }

}
