package com.carlncarl.ami;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.carlncarl.ami.game.Player;

@SuppressLint("ValidFragment")
public class CreateUserDialogFragment extends DialogFragment {

    LoadActivity activity;
    EditText editText;
    ImageView imageView;
    String fileName = Player.DEFAULT_PHOTO;

    public CreateUserDialogFragment(){
    	super();
    }
    
    public CreateUserDialogFragment(LoadActivity activity){
        this.activity = activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_player, container);
        editText = (EditText) view.findViewById(R.id.editTextUsername);
        Button button = (Button) view.findViewById(R.id.buttonCreatePlayer);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.clearFocus();
                editText.refreshDrawableState();
                createPlayer();
            }
        });
        imageView = (ImageView) view.findViewById(R.id.imageViewPlayerIcon);
        ImageButton buttonImage = (ImageButton) view.findViewById(R.id.buttonCamera);
        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
            }
        });
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                if(resultCode == Activity.RESULT_OK){
                    //data.get

                    Bitmap photo = (Bitmap) data.getExtras().get("data");

                    fileName = new Date().getTime()+".png";
                    FileOutputStream fos = null;
                    try {
                        fos = activity.openFileOutput(fileName, Context.MODE_PRIVATE);
                        photo.compress(Bitmap.CompressFormat.PNG,10,fos);
                        fos.flush();
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    File filePath = activity.getFileStreamPath(fileName);
                    imageView.setImageDrawable(Drawable.createFromPath(filePath.toString()));
                }
        }
    }

    protected void createPlayer(){

        Log.d("UserName: ", editText.getText().toString());
        activity.createPlayer(editText.getText().toString(), fileName);
        dismiss();
    }



}
