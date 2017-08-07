package com.example.mac_mini_serttel.maps;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac-mini-serttel on 07/08/17.
 */

public class PermissionUtils {

    //solicita as permissões

    public static boolean validate(Activity activity, int requestCode, String... permissions){
        List<String> list = new ArrayList<String>();
        for (String permission : permissions){
            //Valida permissão
            boolean ok = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
            if (!ok) {
                list.add(permission);
            }
        }if (list.isEmpty()){
            //tudo ok, retorna true
            return true;
        }

        //Lista de permissões que falta acesso
        String[] newPermissions = new String[list.size()];
        list.toArray(newPermissions);

        //solicita permissão
        ActivityCompat.requestPermissions(activity, newPermissions, 1);

        return false;
    }


}
