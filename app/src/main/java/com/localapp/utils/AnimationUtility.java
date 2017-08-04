package com.localapp.utils;

import android.view.View;

/**
 * Created by Vijay Kumar on 03-08-2017.
 */

public class AnimationUtility {

    private static final float POSITIVE = -350F;
    private static final float NEGATIVE = 350F;
    private static final float NEUTRAL = 0F;

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    /**
     * hide view with animation
     * @param view Object of {@link View}
     * @param direction Direction of animation , it must be {@link Direction}
     */
    public static void hideViewWithAnimation(View view, Direction direction) {
        if(view != null) {
            switch (direction) {
                case UP: hideTranslationY(view, POSITIVE);break;
                case DOWN:hideTranslationY(view, NEGATIVE);break;
                case LEFT:hideTranslationX(view, NEGATIVE);break;
                case RIGHT:hideTranslationX(view, POSITIVE);break;
            }
        }
    }

    /**
     * show view with animation
     * @param view Object of {@link View}
     * @param direction Direction of animation , it must be {@link Direction}
     */
    public static void showViewWithAnimation(View view, Direction direction){
        if(view != null) {
            switch (direction) {
                case UP: showTranslationY(view);break;
                case DOWN:showTranslationY(view);break;
                case LEFT:showTranslationX(view);break;
                case RIGHT:showTranslationX(view);break;
            }
        }
    }



    private static void hideTranslationY(final View view, float translation) {
        view.animate().translationY(translation).setDuration(200L)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(View.INVISIBLE);
                    }
                }).start();
    }

    private static void hideTranslationX(final View view, float translation) {
        view.animate().translationX(translation).setDuration(200L)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(View.INVISIBLE);
                    }
                }).start();
    }

    private static void showTranslationY(View view) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
            view.animate().translationY(NEUTRAL).setDuration(600L).start();
        }
    }

    private static void showTranslationX(View view) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
            view.animate().translationX(NEUTRAL).setDuration(600L).start();
        }
    }
}
