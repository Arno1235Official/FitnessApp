package com.ArnoVanEetvelde.fitnessapp;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerOnSwipeListener implements View.OnTouchListener {

    private RecyclerView mRecyclerView;
    private Context mContext;
    private WorkoutAdapter.WorkoutHolder mWorkoutHolder;
    private CustomLinearLayoutManager customLinearLayoutManager;
    private float startX, startY;
    private boolean first = true;
    private int width, currentScreen = 0, maxMovement, ratioTreshold = 2, confirmTreshold, animationVelocity = 2, clickTreshold = 24;

    public RecyclerOnSwipeListener(RecyclerView mRecyclerView, Context mContext, WorkoutAdapter.WorkoutHolder mWorkoutHolder, CustomLinearLayoutManager customLinearLayoutManager, int width){
        this.mContext = mContext;
        this.mRecyclerView = mRecyclerView;
        this.mWorkoutHolder = mWorkoutHolder;
        this.customLinearLayoutManager = customLinearLayoutManager;
        this.maxMovement = (width/2) - 256;
        this.confirmTreshold = maxMovement/2;
        this.width = width;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            first = true;
            startX = motionEvent.getRawX();
            startY = motionEvent.getRawY();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE){

            if (first && Math.abs(startX - motionEvent.getRawX()) > ratioTreshold * Math.abs(startY - motionEvent.getRawY())){
                customLinearLayoutManager.setScrollEnabled(false);
                first = false;
            }
            if ((motionEvent.getRawX() - startX < 0 && motionEvent.getRawX() - startX + currentScreen*maxMovement > -maxMovement) ||
                motionEvent.getRawX() - startX > 0 && motionEvent.getRawX() - startX + currentScreen*maxMovement < maxMovement) {
                mWorkoutHolder.moveCard(motionEvent.getRawX() - startX + currentScreen*maxMovement);
            }
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP){

            if (currentScreen == 0) {
                if (motionEvent.getRawX() - startX > confirmTreshold) {
                    currentScreen = 1;
                    if (motionEvent.getRawX() - startX > maxMovement) {
                        cardAnimation(maxMovement, maxMovement);
                    } else {
                        cardAnimation(motionEvent.getRawX() - startX, maxMovement);
                    }
                } else if (startX - motionEvent.getRawX() > confirmTreshold) {
                    currentScreen = -1;
                    if (startX - motionEvent.getRawX() > maxMovement) {
                        cardAnimation(-maxMovement, -maxMovement);
                    } else {
                        cardAnimation(motionEvent.getRawX() - startX, -maxMovement);
                    }
                } else {
                    currentScreen = 0;
                    cardAnimation(motionEvent.getRawX() - startX, 0);
                }
            } else if (currentScreen == -1){
                if (startX > width/2 + 96 && Math.abs(startX - motionEvent.getRawX()) < clickTreshold){
                    Toast.makeText(mContext, "remove", Toast.LENGTH_SHORT).show();
                } else if (motionEvent.getRawX() - startX > confirmTreshold + maxMovement) {
                    currentScreen = 1;
                    if (motionEvent.getRawX() - startX > maxMovement) {
                        cardAnimation(maxMovement, maxMovement);
                    } else {
                        cardAnimation(motionEvent.getRawX() - startX, maxMovement);
                    }
                } else if (motionEvent.getRawX() - startX > confirmTreshold) {
                    currentScreen = 0;
                    cardAnimation(motionEvent.getRawX() - startX - maxMovement, 0);
                } else {
                    currentScreen = -1;
                    if (startX - motionEvent.getRawX() > maxMovement) {
                        cardAnimation(-maxMovement, -maxMovement);
                    } else {
                        cardAnimation(motionEvent.getRawX() - startX - maxMovement, -maxMovement);
                    }
                }
            } else if (currentScreen == 1){
                if (startX < width/2 - 96 && Math.abs(startX - motionEvent.getRawX()) < clickTreshold){
                    Toast.makeText(mContext, "edit", Toast.LENGTH_SHORT).show();
                } else if (motionEvent.getRawX() - startX < -confirmTreshold - maxMovement){
                    currentScreen = -1;
                    if (startX - motionEvent.getRawX() > maxMovement) {
                        cardAnimation(-maxMovement, -maxMovement);
                    } else {
                        cardAnimation(motionEvent.getRawX() - startX, -maxMovement);
                    }
                } else if (motionEvent.getRawX() - startX < -confirmTreshold){
                    currentScreen = 0;
                    cardAnimation(motionEvent.getRawX() - startX + maxMovement, 0);
                } else {
                    currentScreen = 1;
                    if (motionEvent.getRawX() - startX > maxMovement) {
                        cardAnimation(maxMovement, maxMovement);
                    } else {
                        cardAnimation(motionEvent.getRawX() - startX + maxMovement, maxMovement);
                    }
                }
            }

        }

        int itemPosition = mRecyclerView.getChildLayoutPosition(view);
        //Toast.makeText(mContext, Integer.toString(itemPosition) + " - " + motionEvent.getRawY(), Toast.LENGTH_SHORT).show();

        return true;
    }

    public void cardAnimation(float start, float end){
        ValueAnimator anim = ValueAnimator.ofFloat(start, end);
        anim.setDuration((int) Math.abs((end - start) / animationVelocity));
        anim.start();

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                mWorkoutHolder.moveCard(value);
            }
        });
        anim.addListener(new ValueAnimator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                customLinearLayoutManager.setScrollEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                customLinearLayoutManager.setScrollEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }
}