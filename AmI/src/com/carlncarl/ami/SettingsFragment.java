package com.carlncarl.ami;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

/**
 * @author mwho
 * 
 */
public class SettingsFragment extends Fragment {

	private GameService gService;
	private TabHostActivity parent;
	private SettingsTabInterface callback;
	private CheckBox checkBoxSaveQuestions;
	private CheckBox checkBoxSounds;

	public interface SettingsTabInterface {
		public void changeSettingsQuestions(boolean saveQuestions);

		public void changeSettingsSound(boolean sounds);

		public void leaveGame();

	}

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
		return (LinearLayout) inflater.inflate(R.layout.gameplay_tab4,
				container, false);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parent = (TabHostActivity) getActivity();
		gService = parent.getGService();

		Button buttonLeaveGame = (Button) getView().findViewById(
				R.id.buttonLeaveGane);
		buttonLeaveGame.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				leaveGame();
			}

		});

		checkBoxSaveQuestions = (CheckBox) getView().findViewById(
				R.id.checkBoxSaveQuestions);
		checkBoxSaveQuestions
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						changeSaveSettings(isChecked);
					}
				});
		checkBoxSounds = (CheckBox) getView().findViewById(R.id.checkBoxSounds);
		checkBoxSounds
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						changeSoundSettings(isChecked);
					}
				});
		// /

		setGService(parent.getGService());
	}

	protected void changeSoundSettings(boolean isChecked) {
		callback.changeSettingsSound(isChecked);
	}

	protected void changeSaveSettings(boolean isChecked) {
		callback.changeSettingsQuestions(isChecked);
	}

	protected void leaveGame() {
		callback.leaveGame();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		callback = (SettingsTabInterface) activity;
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
			checkBoxSaveQuestions.setChecked(gService.getGame().isSaveQuestions());
			checkBoxSounds.setChecked(gService.getGame().isSounds());
		}
	}
}