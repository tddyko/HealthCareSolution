package com.greencross.greencare.database.util;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.greencross.greencare.database.DBHelper;
import com.greencross.greencare.util.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by mrsohn on 2017. 4. 19..
 */

public class DBBackupManager {

    private static final String TAG = DBBackupManager.class.getSimpleName();

    public void importDB(Context context) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = "//data//" + context.getPackageName()+ File.separator
                        + "//databases//" + DBHelper.DB_NAME;

                String backupDBPath = DBHelper.DB_NAME; // From SD directory.
                File backupDB = new File(data, currentDBPath);
                File currentDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                Logger.i(TAG, "backupDB path="+backupDB.getParent());
                Logger.i(TAG, "currentDB path="+currentDB.getParent());

                Toast.makeText(context, "Import Successful!",
                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(context, "sdcard is not used", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Import Failed!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void exportDB(Context context) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + context.getPackageName()
                        + "//databases//"+ File.separator + DBHelper.DB_NAME;
                String backupDBPath = DBHelper.DB_NAME; // From SD directory.
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                Logger.i(TAG, "backupDB path="+backupDB.getParent());
                Logger.i(TAG, "currentDB path="+currentDB.getParent());

                Toast.makeText(context, "Backup Successful!",
                        Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Backup Failed!", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}