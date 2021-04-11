package com.openclassrooms.realestatemanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Utils {

    public static final Float EUR_DOLLAR_RATIO = 0.812f;

    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     *
     * @param dollars amount to be converted
     * @return the corresponding euro amount
     */
    public static int convertDollarToEuro(int dollars) {
        return Math.round(dollars * EUR_DOLLAR_RATIO);
    }

    /**
     * Conversion d'un prix d'un bien immobilier (Euros vers Dollars)
     *
     * @param euros amount to be converted
     * @return the corresponding US dollar amount
     */
    public static int convertEuroToDollar(int euros) {
        return Math.round(euros / EUR_DOLLAR_RATIO);
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     *
     * @return the current Date formatted as a String dd/MM/yyyy
     */
    public static String getTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        return dateFormat.format(new Date());
    }

    /**
     * Vérification de la connexion réseau
     *
     * @param context from which the method is called
     * @return a Boolean stating the status of the internet availability
     */
    public static Boolean isInternetAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            NetworkInfo netInfo = null;
            if (cm != null) netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        } else {
            NetworkCapabilities nc = null;
            if (cm != null) nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
            return nc != null && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }
    }
}

