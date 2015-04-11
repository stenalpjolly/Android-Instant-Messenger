package com.spj.messenger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Stenal P Jolly on 15-Mar-15.
 */
public class ScreenSlidePageFragment extends Fragment {

    private Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        mContext = getActivity().getBaseContext();
        Integer position = bundle.getInt("index");
        if (position == 0) {
            RelativeLayout relativeLayout = new RelativeLayout(mContext);
            relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            relativeLayout.setGravity(Gravity.CENTER);
            relativeLayout.setBackgroundColor(Color.parseColor("#003a6c"));
            ImageView imgWelcome = new ImageView(mContext);
            imgWelcome.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imgWelcome.setImageResource(R.drawable.welcomemsg);
            relativeLayout.addView(imgWelcome);
            return relativeLayout;
        }else {
            final ViewGroup rootView = (ViewGroup) inflater.inflate(
                    R.layout.fragment_screen_slide_page, container, false);
            Button btnGetStart = (Button) rootView.findViewById(R.id.btnMsgGetStarted);
            btnGetStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText txtPhone = (EditText) rootView.findViewById(R.id.txtMsgPhoneNumber);
                    if(txtPhone.getText().toString().equals("")){
                        Toast.makeText(mContext,"Enter Phone Number",Toast.LENGTH_LONG).show();
                        return;
                    }
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("hybrid_messenger",0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("phoneNumber", txtPhone.getText().toString());
                    editor.commit();
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
            return rootView;
        }
    }
}
