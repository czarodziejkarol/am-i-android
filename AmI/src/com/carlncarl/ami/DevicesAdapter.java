package com.carlncarl.ami;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.carlncarl.ami.game.Player;

public class DevicesAdapter extends ArrayAdapter<Player> {
	private final Context context;
	private final ArrayList<Player> values;

	public DevicesAdapter(Context context, ArrayList<Player> values) {
		super(context, R.layout.player_joined, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.player_joined, parent, false);
		TextView textViewPlayerName = (TextView) rowView.findViewById(R.id.textViewPlayerName);
		TextView textViewPlayerStatus = (TextView) rowView.findViewById(R.id.textViewPlayerStatus);
		ImageView imageViewPlayerImage = (ImageView) rowView.findViewById(R.id.imageViewPlayerImage);
		
		textViewPlayerName.setText(values.get(position).getName());
		textViewPlayerStatus.setText(values.get(position).getStatus());
		imageViewPlayerImage.setImageResource(R.drawable.default_icon);
		
		return rowView;
	}
}
