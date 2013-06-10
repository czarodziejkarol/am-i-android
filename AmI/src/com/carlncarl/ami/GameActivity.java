package com.carlncarl.ami;

import java.io.File;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.carlncarl.ami.db.Database;
import com.carlncarl.ami.db.MySQLiteHelper;
import com.carlncarl.ami.game.Game;
import com.carlncarl.ami.game.Player;

/**
 * Created by Karol on 20.05.13.
 */
public class GameActivity extends Activity implements
		WifiP2pManager.ChannelListener,
		CreateGameListFragment.DeviceActionListener {

	public static final String PLAYER_KEY = "player";
	public static final String IS_SERVER = "server_in";

	static boolean active = false;

	private Player player;
	private EditText editGameName;
	private Button buttonStartGame;
	private AutoCompleteTextView characterText;

	private ListView listViewJoinedPlayers;

	private DevicesAdapter adapter;
	private PreparePlayerAdapter preAdapter;

	private View viewSearchGames;
	private View viewCreateGame;
	private View viewJoinedPlayers;
	private View viewPrepare;

	private GameService gService = null;
	private boolean serviceConnected = false;

	private ServiceConnection sConn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			gService = null;
			serviceConnected = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			gService = ((GameService.GameBinder) service).getService();
			gService.initialize(GameActivity.this, game);
			// gService.setActivity(GameActivity.this);
			// gService.setGame(game);
			//
			serviceConnected = true;
		}
	};

	@Override
	protected void onStart() {
		active = true;
		super.onStart();

		bindService(new Intent(this, GameService.class), sConn,
				Context.BIND_AUTO_CREATE);
		Intent intent = new Intent(this, GameService.class);

		startService(intent);
	}

	@Override
	protected void onStop() {
		active = false;
//		if (serviceConnected) {
//			unbindService(sConn);
//			stopService(new Intent(this, GameService.class));
//			serviceConnected = false;
//		}
		super.onStop();
	}

	// @Override
	// protected void onStop() {
	// super.onStop();
	// //if(gameServiceConnected){
	// // unbindService(sConnection);
	// // stopService(new Intent(this,GameService.class));
	// // gameServiceConnected = Boolean.FALSE;
	// // }
	// }
	Game game;
	private ListView listViewPrepare;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		game = new Game(this);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			this.player = (Player) extras.getSerializable(PLAYER_KEY);
			game.setServer(extras.getBoolean(GameActivity.IS_SERVER));
		}
		game.setMe(player);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		setContentView(R.layout.game);
		editGameName = (EditText) findViewById(R.id.editTextGameName);
		listViewJoinedPlayers = (ListView) findViewById(R.id.listViewJoinedPlayers);
		adapter = new DevicesAdapter(this, game.getPlayersSet());
		listViewJoinedPlayers.setAdapter(adapter);

		listViewPrepare = (ListView) findViewById(R.id.listViewPrepare);
		preAdapter = new PreparePlayerAdapter(this, Game.players);
		listViewPrepare.setAdapter(preAdapter);
		characterText = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewChooseCharacter);
		new LoadCharactersTask().execute(new Object[0]);
		buttonStartGame = (Button) findViewById(R.id.buttonStart);
		buttonStartGame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				createGame();
			}
		});

		Button buttonSearchGame = (Button) findViewById(R.id.buttonSearchGames);
		buttonSearchGame.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				joinGame();
			}
		});

		Button prepareButton= (Button) findViewById(R.id.button_prepare);
		prepareButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startPrepareGame();
			}
		});
		
		Button selectButton = (Button) findViewById(R.id.buttonSelectCharacter);
		selectButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				chooseCharacter();
				
			}
		});
		
		
		// TEST
		Button test = (Button) findViewById(R.id.button_test);
		test.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				gService.hideSth();

			}
		});

		getWindow().addFlags(Window.FEATURE_NO_TITLE);

		
		viewPrepare =  (View) findViewById(R.id.viewPrepare);
		viewPrepare.setVisibility(View.INVISIBLE);
		viewSearchGames = (View) findViewById(R.id.viewSearchGames);
		viewCreateGame = (View) findViewById(R.id.viewCreateGame);
		viewJoinedPlayers = (View) findViewById(R.id.viewJoinigPlayers);
		if (game.isServer()) {
			viewCreateGame.setVisibility(View.VISIBLE);
			viewSearchGames.setVisibility(View.INVISIBLE);
		} else {
			viewCreateGame.setVisibility(View.INVISIBLE);
			viewSearchGames.setVisibility(View.VISIBLE);
		}

	}

	protected void chooseCharacter() {
		if(characterText.getText()!= null && 
				!characterText.getText().toString().equals("")){
			gService.chooseCharacter(characterText.getText().toString());
		} else {
			Toast.makeText(this, "Choose character name!", Toast.LENGTH_SHORT).show();
		}
			
	}

	protected void startPrepareGame() {
		gService.sendPrePrepare();
	}

	protected void joinGame() {
		gService.joinGame();
	}

	protected void createGame() {
		gService.createGame(editGameName.getText().toString(), player);
	}
	
	public void prepareGame(){
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				viewJoinedPlayers.setVisibility(View.GONE);
				viewPrepare.setVisibility(View.VISIBLE);
				preAdapter.notifyDataSetChanged();
			}
		});
		
	}

	protected void hideViewCreateGame() {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				viewCreateGame.setVisibility(View.GONE);
				viewSearchGames.setVisibility(View.GONE);
				viewJoinedPlayers.setVisibility(View.VISIBLE);
				if(!GameActivity.this.player.getImage().equals(Player.DEFAULT_PHOTO)){
					ImageView imv = (ImageView) findViewById(R.id.imageViewMe);
					
					File fp =GameActivity.this.getFileStreamPath(GameActivity.this.player.getImage());
							
					imv.setImageDrawable(Drawable.createFromPath(fp.toString()));
				}
				
			}
		});

	}

	@Override
	public void onChannelDisconnected() {

	}

	@Override
	public void showDetails(WifiP2pDevice device) {

	}

	@Override
	public void cancelDisconnect() {

	}

	@Override
	public void connect(WifiP2pConfig config) {

	}

	@Override
	public void disconnect() {

	}

	private class LoadCharactersTask extends
			AsyncTask<Object, Integer, String[]> {

		@Override
		protected String[] doInBackground(Object... params) {
			MySQLiteHelper myHel = new MySQLiteHelper(getBaseContext());

			SQLiteDatabase db = myHel.getReadableDatabase();

			Cursor c = db.query(Database.Character.TABLE_NAME, null, null,
					null, null, null, null);

			String[] result = new String[c.getCount()];
			for (int i = 0; i < result.length; i++) {
				c.moveToNext();
				result[i] = c.getString(c
						.getColumnIndex(Database.Character.COLUMN_NAME_NAME));
			}

			return result;

		}

		@Override
		protected void onProgressUpdate(Integer... values) {

		}

		@Override
		protected void onPostExecute(String[] result) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					GameActivity.this, android.R.layout.simple_list_item_1,
					result);
			characterText.setAdapter(adapter);
			characterText.refreshDrawableState();
		}
	}

	public DevicesAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(DevicesAdapter adapter) {
		this.adapter = adapter;
	}

	public GameService getGService() {
		return gService;
	}

	public void setGService(GameService gService) {
		this.gService = gService;
	}

	public void hideTestButton() {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Button test = (Button) findViewById(R.id.button_test);
				test.setVisibility(View.GONE);
			}
		});

	}

	public void notifyAdapter() {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				adapter.notifyDataSetChanged();
				
			}
		});

	}

	public void setMyDevName(final String devName) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				TextView textMyDevName = (TextView) findViewById(R.id.textViewMyDevName);
				textMyDevName.setText(devName);
			}
		});
	}

	public void notifyPreAdapter() {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				preAdapter.notifyDataSetChanged();
			}
		});
	}

}