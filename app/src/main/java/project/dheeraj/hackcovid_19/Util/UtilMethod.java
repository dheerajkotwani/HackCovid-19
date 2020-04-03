package project.dheeraj.hackcovid_19.Util;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.net.InetAddress;


public class UtilMethod {

    public static ProgressDialog Dialog;

    public static void showToast(Context context, String message) {
        //
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean checkLocationPermission(final Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != context.getPackageManager().PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    99);

            return false;
        }
        else {
            return true;
        }
    }


    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

    public static void showLoader(Context context, String title, String message){
        Dialog = new ProgressDialog(context);
        Dialog.setMessage(message);
        Dialog.setTitle(title);
        Dialog.show();
    }

    public static void dismissLoader(){
        Dialog.dismiss();
    }

    public static boolean isInternetConnected(Context context)
    {
        ConnectivityManager connectivityManager =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

//    public static void showNoInternetDialog(Context context){
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        View view = LayoutInflater.from(context).inflate(R.layout.dialog_no_internet, null, false);
//        builder.setView(view);
//
//        final AlertDialog dialog = builder.create();
//        dialog.show();
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//        view.findViewById(R.id.dialog_button_try_again).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//
//    }

}

