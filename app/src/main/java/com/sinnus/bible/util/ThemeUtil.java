package com.sinnus.bible.util;

/**
 * Created by sinnus on 2015/8/31.
 */

import android.app.Activity;
import android.content.Context;

import com.sinnus.bible.R;
import com.sinnus.bible.fragment.MainFragment;

public class ThemeUtil {

    public static Integer[] themeColors = {R.color.green, R.color.blue, R.color.red, R.color.pink, R.color.black};
    private static int[] styles = {R.style.GreenTheme, R.style.BlueTheme, R.style.RedTheme, R.style.PinkTheme,
            R.style.BlackTheme};

    public static Integer[] backGroundColors = {
            R.color.main_background, R.color.black,R.color.window_background_dark
    };
    public static void setTheme(Activity activity, int theme) {
        if (activity == null)
            return;
        activity.setTheme(styles[theme]);
    }

    public static boolean isNightMode(Context context) {
        boolean value = PreferenceUtil.getInstance(context)
                .getBooleanParam(context.getString(R.string.night_mode_key), false);
        return value;
    }

    public static void setBackgroundColor(Activity activity,int position){
        activity.getWindow().getDecorView().findViewById(android.R.id.content).setBackgroundColor(activity.getResources().getColor(
                        getCurrentBackgroundColorResourceId(activity, position))
        );
    }

    public static int getCurrentTheme(Context context) {
        int value = PreferenceUtil.getInstance(context)
                .getIntParam(context.getString(R.string.change_theme_key), 0);
        return value;
    }

    public static int getDarkThemePosition() {
        return 4;
    }
    public static int getCurrentBackgroundColor(Context context){
        int value = PreferenceUtil.getInstance(context)
                .getIntParam(context.getString(R.string.change_background_color_key), 0);
        return value;
    }
    public static int getCurrentBackgroundColorResourceId(Context context,int position){
        return backGroundColors[position];
    }

}
