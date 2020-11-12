package com.inscription.library.root;

import android.annotation.SuppressLint;
import android.util.Log;
import java.io.DataOutputStream;

public class runRootCommander {
    /* Command Root */
    @SuppressLint("LongLogTag")
    public static void runRootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command+"\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            Log.d("shell command execution result:", process.exitValue()+"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
