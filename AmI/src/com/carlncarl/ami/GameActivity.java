package com.carlncarl.ami;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
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
import android.widget.ListView;
import android.widget.Toast;

import com.carlncarl.ami.db.Database;
import com.carlncarl.ami.db.MySQLiteHelper;
import com.carlncarl.ami.game.Game;
import com.carlncarl.ami.game.Player;

/**
 * Created by Karol on 20.05.13.
 */
public class GameActivity extends Activity implements PeerListListener,
		WifiP2pManager.ChannelListener,
		CreateGameListFragment.DeviceActionListener {

	public static final String PLAYER_KEY = "player";
	public static final String IS_SERVER = "server_in";
	// private GameService gameService;
	// private Boolean gameServiceConnected = Boolean.FALSE;
	//
	// private ServiceConnection sConnection = new ServiceConnection() {
	// @Override
	// public void onServiceConnected(ComponentName componentName, IBinder
	// iBinder) {
	// gameService = ((GameService.GameBinder) iBinder).getService();
	// gameServiceConnected = Boolean.TRUE;
	// }
	//
	// @Override
	// public void onServiceDisconnected(ComponentName componentName) {
	// gameService = null;
	// gameServiceConnected = Boolean.FALSE;
	// }
	// };
	
	private Player player;
	private EditText editGameName;
	private EditText editGamePassword;
	private Button buttonStartGame;
	private AutoCompleteTextView characterText;
	private int step = 0;
	private Boolean server = false;
	private WifiManager wifiManager;

	WifiP2pManager mManager;
	WifiP2pManager.Channel mChannel;
	BroadcastReceiver mReceiver;

	IntentFilter mIntentFilter;
	private boolean wiFiEnabled = false;
	private ListView listViewJoinedPlayers;

	private DevicesAdapter adapter;

	private View viewSearchGames;
	private View viewCreateGame;
	private View viewJoinedPlayers;

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
			
			gService.setActivity(GameActivity.this);
			gService.setGame(game);
			serviceConnected = true;
		}
	};

	@Override
	protected void onStart() {
		super.onStart();

		bindService(new Intent(this, GameService.class), sConn,
				Context.BIND_AUTO_CREATE);
		startService(new Intent(this, GameService.class));
	}

	@Override
	protected void onStop() {
		if (serviceConnected) {
			unbindService(sConn);
			stopService(new Intent(this, GameService.class));
			serviceConnected = false;
		}
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

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		game = new Game(this);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			this.player = (Player) extras.getSerializable(PLAYER_KEY);
			this.server = extras.getBoolean(GameActivity.IS_SERVER);
		}
		game.setMe(player);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		setContentView(R.layout.game);
		editGameName = (EditText) findViewById(R.id.editTextGameName);
		listViewJoinedPlayers = (ListView) findViewById(R.id.listViewJoinedPlayers);
		adapter = new DevicesAdapter(this, game.getPlayers());
		listViewJoinedPlayers.setAdapter(adapter);

		editGamePassword = (EditText) findViewById(R.id.editTextGamePassword);
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

		
		
		getWindow().addFlags(Window.FEATURE_NO_TITLE);

		viewSearchGames = (View) findViewById(R.id.viewSearchGames);
		viewCreateGame = (View) findViewById(R.id.viewCreateGame);
		viewJoinedPlayers = (View) findViewById(R.id.viewJoinigPlayers);
		if (this.server) {
			viewCreateGame.setVisibility(View.VISIBLE);
			viewSearchGames.setVisibility(View.INVISIBLE);
		} else {
			viewCreateGame.setVisibility(View.INVISIBLE);
			viewSearchGames.setVisibility(View.VISIBLE);
		}

		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		mIntentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		mIntentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mManager.initialize(this, getMainLooper(), null);
		mReceiver = new WiFiBroadcastReceiver(mManager, mChannel, this);

	}

	/* register the broadcast receiver with the intent values to be matched */
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mReceiver, mIntentFilter);
	}

	/* unregister the broadcast receiver */
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}

	private void joinGame() {
		gService.getGame().getMe().setDeviceMAC(
				wifiManager.getConnectionInfo().getMacAddress());
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
			do {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (!wifiManager.isWifiEnabled());
		}

		searchPeers();
	}

	private void createGame() {
		gService.getGame().setName(editGameName.getText().toString());
		gService.getGame().setOwner(this.player);
		gService.getGame().setStart(new Date());
		gService.getGame().setPassword(editGamePassword.getText().toString());
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
			do {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (!wifiManager.isWifiEnabled());
		}
		searchPeers();

		// gameService.createGame(game);
		/*
		 * WifiManager manager = (WifiManager)
		 * getSystemService(Context.WIFI_SERVICE); if(manager.isWifiEnabled()){
		 * manager.setWifiEnabled(false); } WifiApManager apManager = new
		 * WifiApManager(this); WifiConfiguration wc = new WifiConfiguration();
		 * wc.SSID = editGameName.getText().toString(); wc.preSharedKey =
		 * "\""+editGamePassword.getText().toString()+"\"";
		 * wc.wepKeys[0]="\""+editGamePassword.getText().toString()+"\"";
		 * wc.hiddenSSID = false; wc.status = WifiConfiguration.Status.ENABLED;
		 * wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		 * wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		 * wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		 * wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		 * wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		 * wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		 * wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		 * apManager.setWifiApEnabled(wc, true); //manager.
		 */
	}


	private void searchPeers() {
		mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

			@Override
			public void onSuccess() {
				Toast.makeText(GameActivity.this,
						getResources().getString(R.string.look_for_peers),
						Toast.LENGTH_SHORT).show();
				hideViewCreateGame();
			}

			@Override
			public void onFailure(int reasonCode) {
				// Toast.makeText(GameActivity.this, "Discovery Failed : " +
				// reasonCode,
				// Toast.LENGTH_SHORT).show();
				searchPeers();
			}
		});
	}

	protected void hideViewCreateGame() {
		viewCreateGame.setVisibility(View.GONE);
		viewSearchGames.setVisibility(View.GONE);
		viewJoinedPlayers.setVisibility(View.VISIBLE);
	}

	@Override
	public void onChannelDisconnected() {

	}

	public void setWiFiEnabled(boolean enabled) {
		this.wiFiEnabled = enabled;
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

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		Collection<WifiP2pDevice> peerList = peers.getDeviceList();
		Game game = gService.getGame();
		for (WifiP2pDevice device : peerList) {
			boolean isIn = false;
			for (Player player : game.getPlayers()) {
				if (player.getDevice().deviceAddress
						.equals(player.getDevice().deviceAddress)) {
					isIn = true;
					break;
				}
			}
			if (!isIn) {
				Player p = new Player(device);
				game.addPlayer(p);
			}
		}
		ArrayList<Player> toDel = new ArrayList<Player>();
		for (Player player : game.getPlayers()) {
			// boolean isIn = false;
			if (player.getDevice().status == WifiP2pDevice.UNAVAILABLE) {
				toDel.add(player);
			}
			// for (WifiP2pDevice wifiP2pDevice : peerList) {
			// if(isIn)
			// }
		}
		game.removeAllPlayers(toDel);
		adapter.notifyDataSetChanged();
		connect();
	}

	public void connect() {
		if (server) {
			Game game = gService.getGame();
			for (final Player player : game.getPlayers()) {
				if (player.getStatus().equals(Player.STATUS_PEER)) {
					WifiP2pConfig config = new WifiP2pConfig();
					config.deviceAddress = player.getDevice().deviceAddress;

					mManager.connect(mChannel, config, new ActionListener() {

						@Override
						public void onSuccess() {
							player.setStatus(Player.STATUS_CONNECTED);
							// mManager.
							// mManager.
							verifyClient(player);
						}

						@Override
						public void onFailure(int reason) {
							player.setStatus(Player.STATUS_NOT_CONNECTED);
						}
					});
				}
			}
		}
	}

	protected void verifyClient(Player player) {
		// open server socket

	}

	public Boolean getServer() {
		return server;
	}

	public void setServer(Boolean server) {
		this.server = server;
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

}