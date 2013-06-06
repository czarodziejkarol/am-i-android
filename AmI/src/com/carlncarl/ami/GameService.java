package com.carlncarl.ami;

import com.carlncarl.ami.game.Game;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class GameService extends Service {
	private GameActivity activity;
	private Game game;
	
	private final IBinder binder = new GameBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	public void setActivity(GameActivity activity) {
		this.activity = activity;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		Toast.makeText(activity, "Game setted in service", Toast.LENGTH_SHORT).show();
		this.game = game;
	}

	public class GameBinder extends Binder{
		
		GameService getService(){
			return GameService.this;
		}
	}
}
