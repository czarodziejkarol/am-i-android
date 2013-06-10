package com.carlncarl.ami;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

/**
 * Created by Karol on 21.05.13.
 */
public class WiFiBroadcastReceiver extends BroadcastReceiver {

	private WifiP2pManager mManager;
	private WifiP2pManager.Channel mChannel;
	private GameService service;

	public WiFiBroadcastReceiver(WifiP2pManager manager,
			WifiP2pManager.Channel channel, GameService gameService) {
		super();
		this.mManager = manager;
		this.mChannel = channel;
		this.service = gameService;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
				service.setWiFiEnabled(true);
			} else {
				// Wi-Fi Direct is not enabled
				service.setWiFiEnabled(false);
			}
			// Log.d(WiFiDirectActivity.TAG, "P2P state changed - " + state);

		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
			// Call WifiP2pManager.requestPeers() to get a list of current peers

			if (mManager != null && service.getGame().isServer()) {
				mManager.requestPeers(mChannel, service);
			}
		} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION
				.equals(action)) {
			// Respond to new connection or disconnections
			if (mManager == null) {
				return;
			}

			NetworkInfo networkInfo = (NetworkInfo) intent
					.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

			if (networkInfo.isConnected()) {

				// we are connected with the other device, request connection
				// info to find group owner IP

				mManager.requestConnectionInfo(mChannel, service);
			}

		} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
				.equals(action)) {
			// Respond to this device's wifi state changing
			// (WifiP2pDevice) intent.getParcelableExtra(
			// WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
			if (intent.hasExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)) {
				
				service.setMyDevice((WifiP2pDevice) intent
						.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
				
				
				
//				Toast.makeText(context, "CHANGED " + service.myDevice.deviceAddress,
//						Toast.LENGTH_SHORT).show();
			}
		}
	}

}