package com.carlncarl.ami;

import java.io.File;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.carlncarl.ami.game.Player;

public class LobbyAdapter extends ArrayAdapter<Player> {

	private final Context context;
	private final LinkedList<Player> values;

	public LobbyAdapter(Context context, LinkedList<Player> values) {
		super(context, R.layout.lobby_view, values);
		this.context = context;
		this.values = values;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.lobby_view, parent, false);
		TextView textViewLobbyPlayerName = (TextView) rowView
				.findViewById(R.id.lobbyPlayerName);
		TextView textViewLobbyPlayerHero = (TextView) rowView
				.findViewById(R.id.lobbyCharacterName);

		TextView textViewLobbyPlayerStatus = (TextView) rowView
				.findViewById(R.id.lobbyPlayerStatus);
		ImageView imageViewLobbyPlayerImage = (ImageView) rowView
				.findViewById(R.id.lobbyPlayerImg);

		textViewLobbyPlayerName.setText(values.get(position).getName());
		if (values.get(position).isMe()) {
			textViewLobbyPlayerHero.setVisibility(View.INVISIBLE);
		} else {
			textViewLobbyPlayerHero.setText(values.get(position)
					.getCharacter());
		}
		if(values.get(position).getWinPos() > 0)
		textViewLobbyPlayerStatus.setText("Win in " + values.size() + " round");
		else{
			textViewLobbyPlayerStatus.setVisibility(View.INVISIBLE);
		} 
			
		if(values.get(position).getImage().equals(Player.DEFAULT_PHOTO)){
			imageViewLobbyPlayerImage.setImageResource(R.drawable.default_icon);
		} else {
			 File filePath =context.getFileStreamPath(values.get(position).getImage());
			 imageViewLobbyPlayerImage.setImageDrawable(Drawable.createFromPath(filePath.toString()));
		}

		return rowView;
	}
}
