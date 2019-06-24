package com.example.unblockneteasemusic;

import android.content.Context;
import android.widget.Toast;

public class ToastText {
    ToastText(Context context, String string) {
        //MIUI会在提示前加包名，所以用下面方法
        Toast toast = Toast.makeText(context, null, Toast.LENGTH_LONG);
        toast.setText(string);
        toast.show();
    }
}
