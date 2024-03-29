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

public class PlayerAdapter extends ArrayAdapter<Player> {
	private final Context context;
	private final LinkedList<Player> values;

	public PlayerAdapter(Context context, LinkedList<Player> values) {
		super(context, R.layout.player_view, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		int viewId ;
		if(position==0){
			 viewId = R.layout.player_view;
		} else {
			 viewId = R.layout.player_view_mini;
		}
		
		View rowView = inflater.inflate(viewId , parent, false);
		TextView textViewPlayerName = (TextView) rowView.findViewById(R.id.player_view_name);
		//TextView textViewPlayerStatus = (TextView) rowView.findViewById(R.id.textViewPlayerStatus);
		ImageView imageViewPlayerImage = (ImageView) rowView.findViewById(R.id.player_view_image);
		
		textViewPlayerName.setText(values.get(position).getName());
		//textViewPlayerStatus.setText(values.get(position).getStatus());
		
		if(values.get(position).getImage().equals(Player.DEFAULT_PHOTO)){
			imageViewPlayerImage.setImageResource(R.drawable.default_icon);
		} else {
			 File filePath =context.getFileStreamPath(values.get(position).getImage());
			 imageViewPlayerImage.setImageDrawable(Drawable.createFromPath(filePath.toString()));
		}
		
		
		
		return rowView;
	}
}
