package com.yan.dianming;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yan on 2016/10/30.
 */
public class FileioUtil {
    public static Map<String, Student> getStudents(File file) {
        Map<String, Student> map = new HashMap<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file)));
            String s = "";
            while ((s = bufferedReader.readLine()) != null) {
                String[] strings = s.split(" ");
                if (strings.length != 3) {
                    map=null;
                    return null;
                }
                Student student = new Student(strings[0], strings[1], strings[2], 0,0);
                map.put(strings[0], student);
                Log.i("strings", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            return map;
        }
    }
}
