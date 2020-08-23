package com.example.guff_gaf;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView user_single_name,user_single_status;

    CircleImageView user_single_profile;
    RadioButton online_state;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        user_single_name = itemView.findViewById(R.id.user_single_name);
        user_single_status=itemView.findViewById(R.id.user_single_status);
        user_single_profile= itemView.findViewById(R.id.user_single_profile);
        online_state = itemView.findViewById(R.id.online_status);


    }


}
