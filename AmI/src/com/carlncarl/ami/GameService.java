package com.carlncarl.ami;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.carlncarl.ami.game.Action;
import com.carlncarl.ami.game.Communicat;
import com.carlncarl.ami.game.Game;
import com.carlncarl.ami.game.Player;
import com.carlncarl.ami.game.ServerCommunication;

public class GameService extends Service implements PeerListListener,ConnectionInfoListener {
	public static final String EXTRA_GAME = "game_extra";
	private GameActivity gameActivity;
	private TabHostActivity playActivity;
	private Game game;

	private WifiManager wifiManager;
	public WifiP2pDevice myDevice;
	WifiP2pManager mManager;
	WifiP2pManager.Channel mChannel;
	BroadcastReceiver mReceiver;
	IntentFilter mIntentFilter;
	private boolean wiFiEnabled = false;

	private final IBinder binder = new GameBinder();

	@Override
	public IBinder onBind(Intent intent) {
		
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		

	}
	
	
	public void initialize(GameActivity gameActivity, Game game2){
		this.gameActivity = gameActivity;
		this.game = game2;
		game.setService(this);
		game.loadQuestions();
		
		Toast.makeText(this, "CREATED", Toast.LENGTH_SHORT).show();

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
		registerReceiver(mReceiver, mIntentFilter);
		
	}
	

	public void setActivity(GameActivity activity) {
		this.gameActivity = activity;
	}

	public void setWiFiEnabled(boolean enabled) {
		this.wiFiEnabled = enabled;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
//		Toast.makeText(activity, "Game setted in service", Toast.LENGTH_SHORT)
//				.show();
		this.game = game;
	}

	public class GameBinder extends Binder {

		GameService getService() {
			return GameService.this;
		}
	}

	public void hideSth() {
		new DoAsync().execute("");
	}

	private class DoAsync extends AsyncTask<String, Integer, String> {

		@SuppressLint("ShowToast")
		@Override
		protected String doInBackground(String... params) {
			int i = 0;
			while (i < 20) {
				try {
					publishProgress(i);
					Thread.sleep(100);
					i++;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return "bronek";
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			Toast.makeText(gameActivity, "POWIADOMIENIE " + values[0],
					Toast.LENGTH_SHORT).show();
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
			gameActivity.hideTestButton();
			super.onPostExecute(result);
		}
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		Collection<WifiP2pDevice> peerList = peers.getDeviceList();
		for (WifiP2pDevice device : peerList) {
			boolean isIn = false;
			for (Player player : game.getPlayersSet()) {
				if (player.getDevice()!=null&&player.getDevice().deviceAddress
						.equals(player.getDevice().deviceAddress)) {
					isIn = true;
					break;
				}
			}
			if (!isIn) {
				Player p = new Player(device);
				game.addSetPlayer(p);
			}
		}
		ArrayList<Player> toDel = new ArrayList<Player>();
		for (Player player : game.getPlayersSet()) {
			// boolean isIn = false;
			if (player.getDevice()!=null && player.getDevice().status == WifiP2pDevice.UNAVAILABLE) {
				toDel.add(player);
			}
			// for (WifiP2pDevice wifiP2pDevice : peerList) {
			// if(isIn)
			// }
		}
		game.removeAllPlayers(toDel);
		gameActivity.getAdapter().notifyDataSetChanged();
	}

	@Override
	public void onRebind(Intent intent) {
		
		super.onRebind(intent);
	}

	
	@Override
	public boolean onUnbind(Intent intent) {
//		unregisterReceiver(mReceiver);
		super.onDestroy();
		return super.onUnbind(intent);
	}
	
	@Override
	public void onDestroy() {
//		unregisterReceiver(mReceiver);
		super.onDestroy();

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		return super.onStartCommand(intent, flags, startId);
	}

	public void createGame(String gameName, Player player) {
		game.setName(gameName);
		game.setOwner(player);
		game.setStart(new Date());
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
		game.startServer();
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

	public void joinGame() {
		
//		game.getMe().setDeviceMAC(
//				wifiManager.getConnectionInfo().getMacAddress());
//		
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

	protected void verifyClient(Player player) {
		// open server socket

	}

	public void connect(final Player player) {
		
		if (player.getStatus().equals(Player.STATUS_PEER)) {
			WifiP2pConfig config = new WifiP2pConfig();
			config.deviceAddress = player.getDevice().deviceAddress;
			mManager.connect(mChannel, config, new ActionListener() {

				@Override
				public void onSuccess() {
				
					player.setStatus(Player.STATUS_CONNECTING);
					gameActivity.notifyAdapter();
					verifyClient(player);
				}

				@Override
				public void onFailure(int reason) {
					player.setStatus(Player.STATUS_NOT_CONNECTED);
					gameActivity.notifyAdapter();
				}
			});
		}
	}

	private void searchPeers() {
		mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

			@Override
			public void onSuccess() {
				Toast.makeText(GameService.this,
						getResources().getString(R.string.look_for_peers),
						Toast.LENGTH_SHORT).show();
				gameActivity.hideViewCreateGame();
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

	public boolean getWiFiEnabled() {
		return wiFiEnabled;
	}


	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {

		if (game.isServer()) {
			
		} else {
			game.info = info;

			game.setSerCommunication(new ServerCommunication(this, info));
			// TODO wys³anie komunikatu device_ID
//			Thread t = new Thread(game.getSerCommunication());
//			t.start();

		}
	}

	public void setMyDevice(WifiP2pDevice parcelableExtra) {
		this.myDevice = parcelableExtra;
		if(gameActivity!=null){
			gameActivity.setMyDevName(myDevice.deviceName);
		}
	}

	public void sendPrePrepare() {
		mManager.stopPeerDiscovery(mChannel, null);
		
		
		game.sendStartData();
	}

	public void chooseCharacter() {
		gameActivity.prepareGame();
		
		
	}

	public void chooseCharacter(String characterName) {
		Communicat com = new Communicat();
		com.setType(Communicat.TYPE_TYPE_CHARACTER);
		com.setVal(characterName);
		com.setPlayerUUID(game.getMe().getUuid());
		
		game.sendToServerCommunicat(com);
		
		
	}

	public void startGame() {
		Intent dialogIntent = new Intent(getBaseContext(), TabHostActivity.class);
		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//dialogIntent.putExtra(EXTRA_GAME, game);
		getApplication().startActivity(dialogIntent);
	}

	public TabHostActivity getPlayActivity() {
		return playActivity;
	}

	public void setPlayActivity(TabHostActivity playActivity) {
		this.playActivity = playActivity;
	}

	public void startTurn(Player player) {
		playActivity.startNewTurn(player);
	}

	public void receveQuestion(Action action) {
		playActivity.receiveAction(action);
	}

	public void receiveAnswer(Action action) {
		playActivity.receiveAction(action);
	}

	public void closeWiFi() {
		mManager.cancelConnect(mChannel, null);
	}
	
	@Override
	public boolean stopService(Intent name) {
		unregisterReceiver(mReceiver);
		this.game.finishGame();
		mManager.stopPeerDiscovery(mChannel, null);
		mManager.clearLocalServices(mChannel, null);
		mManager.clearServiceRequests(mChannel, null);

		mManager.removeGroup(mChannel, null);
		
		//wifiManager.disconnect();
		
		Toast.makeText(this, "STOP SERVICE", Toast.LENGTH_LONG).show();
		return super.stopService(name);
	}

}
