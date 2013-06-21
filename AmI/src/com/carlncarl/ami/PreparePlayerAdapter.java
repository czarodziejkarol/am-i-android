package com.carlncarl.ami;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carlncarl.ami.game.Player;

public class PreparePlayerAdapter extends ArrayAdapter<Player> {
	private final Context context;
	private final LinkedList<Player> values;

	public PreparePlayerAdapter(Context context, LinkedList<Player> players) {
		super(context, R.layout.player_joined, players);
		this.context = context;
		this.values = players;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.player_joined, parent, false);
		TextView textViewPlayerName = (TextView) rowView
				.findViewById(R.id.textViewPlayerName);
		TextView textViewPlayerStatus = (TextView) rowView
				.findViewById(R.id.textViewPlayerStatus);
		ImageView imageViewPlayerImage = (ImageView) rowView
				.findViewById(R.id.imageViewPlayerImage);

		Button buttonConnect = (Button) rowView
				.findViewById(R.id.button_connect);
		buttonConnect.setVisibility(View.GONE);

		final Player p = values.get(position);

		buttonConnect.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				((GameActivity) context).getGService().connect(p);
			}
		});

		ProgressBar barConnecting = (ProgressBar) rowView
				.findViewById(R.id.progress_bar_p_conn);

		barConnecting.setVisibility(View.GONE);

		textViewPlayerName.setText(p.getName());
		textViewPlayerStatus.setText(p.getStatus());
		if (p.getImage().equals(Player.DEFAULT_PHOTO)) {
			imageViewPlayerImage.setImageResource(R.drawable.default_icon);
		} else {
			File fp = context.getFileStreamPath(p.getImage());
			
			imageViewPlayerImage.setImageDrawable(Drawable.createFromPath(fp.toString()));
		}

		return rowView;
	}

}
