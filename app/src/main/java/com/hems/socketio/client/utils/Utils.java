package com.hems.socketio.client.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.SyncStateContract;
import android.text.Html;
import android.text.Spanned;
import android.text.format.Time;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.hems.socketio.client.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by intel on 03-Mar-17.
 */

public class Utils extends BaseUtils {
    public static Toast toast;

    public final static boolean isValidEmail(CharSequence target) {
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        if (target == null) {
            return false;
        } else {
            boolean result = android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
            String s = target.toString().split("@")[0];
            if (s.charAt(s.length() - 1) == '.')
                result = false;

            if (result) {
                Matcher matcher;
                Pattern pattern = Pattern.compile(emailPattern);
                matcher = pattern.matcher(target);
                return matcher.matches();
            }

            return result;
        }
    }

    public final static boolean isValidPassword(CharSequence target) {
        String emailPattern = ".{4,}";
        if (target == null) {
            return false;
        } else {
            Pattern pattern;
            Matcher matcher;
            pattern = Pattern.compile(emailPattern);
            matcher = pattern.matcher(target);

            boolean result = matcher.matches();

            return result;
        }
    }

    public static void showToast(Context context, int message) {
        if (toast == null)
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);

        toast.setText(message);
        toast.show();
    }

    public static void showToast(Context context, String message) {
        if (toast == null)
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);

        toast.setText(message);
        toast.show();
    }

    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

    public static Locale getCurrentLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return context.getResources().getConfiguration().locale;
        }
    }

    public static boolean isArabic(Context context) {
        return ((getCurrentLocale(context).getLanguage().equalsIgnoreCase("ar")));
    }


    public static void keyboard(Context context, View view, boolean show) {
        InputMethodManager keyboard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!show)
            keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
        else
            keyboard.showSoftInput(view, 0);
    }

    public static String secondsToString(int improperSeconds) {

        //Seconds must be fewer than are in a day

        Time secConverter = new Time();

        secConverter.hour = 0;
        secConverter.minute = 0;
        secConverter.second = 0;

        secConverter.second = improperSeconds;
        secConverter.normalize(true);

        String hours = String.valueOf(secConverter.hour);
        String minutes = String.valueOf(secConverter.minute);
        String seconds = String.valueOf(secConverter.second);

        if (seconds.length() < 2) {
            seconds = "0" + seconds;
        }
        if (minutes.length() < 2) {
            minutes = "0" + minutes;
        }
        if (hours.length() < 2) {
            hours = "0" + hours;
        }

        return hours + ":" + minutes + ":" + seconds;
    }

    /**
     * get app current version name example : 0.0
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String readableFileSize(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getAllowedFileMimeTypes(Context context) {
        InputStreamReader is = null;
        List<String> allowList = new ArrayList<>();
        try {
            is = new InputStreamReader(context.getAssets()
                    .open(Constants.ALLOWED_MIME_TYPE_FILE_NAME));
            BufferedReader reader = new BufferedReader(is);
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                allowList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allowList;
    }

}
