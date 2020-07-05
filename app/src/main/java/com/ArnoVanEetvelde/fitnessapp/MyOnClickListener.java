package com.ArnoVanEetvelde.fitnessapp;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

public class MyOnClickListener implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private Context mContext;

    public MyOnClickListener(RecyclerView mRecyclerView, Context mContext){
        this.mContext = mContext;
        this.mRecyclerView = mRecyclerView;
    }

    @Override
    public void onClick(View view) {
        int itemPosition = mRecyclerView.getChildLayoutPosition(view);
        Toast.makeText(mContext, Integer.toString(itemPosition), Toast.LENGTH_LONG).show();
    }
}
