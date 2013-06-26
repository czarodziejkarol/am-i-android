package com.carlncarl.ami;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.carlncarl.ami.game.Game;

/**
 * @author mwho
 * 
 */
public class HistoryFragment extends Fragment {

	private GameService gService;
	private TabHostActivity parent;
	private HistoryAdapter historyAdapter;
	private ListView historyList;
	private GridLayout viewIAm;
	private HistoryTabInterface callback;
	private EditText editTextIAm;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			// We have different layouts, and in one of them this
			// fragment's containing frame doesn't exist. The fragment
			// may still be created from its saved state, but there is
			// no reason to try to create its view hierarchy because it
			// won't be displayed. Note this is not needed -- we could
			// just run the code below, where we would create and return
			// the view hierarchy; it would just never be used.
			return null;
		}
		return (LinearLayout) inflater.inflate(R.layout.gameplay_tab2,
				container, false);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parent = (TabHostActivity) getActivity();
		gService = parent.getGService();

		Button buttonIAm = (Button) getView().findViewById(R.id.buttonIAm);
		buttonIAm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				guessMyCharacter();
			}

		});

		viewIAm = (GridLayout) getView().findViewById(R.id.viewIAm);
		editTextIAm = (EditText) getView().findViewById(R.id.editTextIAm);

		// /

		setGService(parent.getGService());
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		callback = (HistoryTabInterface) activity;
	}

	protected void guessMyCharacter() {
		callback.onTypeMyCharacter(editTextIAm.getText().toString());
		viewIAm.setVisibility(View.GONE);
		editTextIAm.setText("");
		InputMethodManager mgr = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(editTextIAm.getWindowToken(), 0);
	}

	public void setGService(GameService gs) {
		gService = gs;
		setState();

	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		setState();
	}

	private void setState() {
		if (gService != null) {
			int state = gService.getGame().getGameStatus();

			switch (state) {
			case Game.GAME_STATUS_WRITE_QUESTION:
				viewIAm.setVisibility(View.VISIBLE);
				break;
			default:
				viewIAm.setVisibility(View.GONE);
				break;
			}
			historyList = (ListView) getView().findViewById(R.id.historyList);
			historyAdapter = new HistoryAdapter(gService, gService.getGame()
					.getMyActions());
			historyList.setAdapter(historyAdapter);

		}
	}

	public interface HistoryTabInterface {
		public void onTypeMyCharacter(String character);
	}

}