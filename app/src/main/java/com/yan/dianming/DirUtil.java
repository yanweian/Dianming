package com.yan.dianming;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yan on 2016/10/11.
 */
public class DirUtil {
    //根目录
    private static final File root=Environment.getExternalStorageDirectory();
    //遍历子文件的方法，返回一个文件list集合
    public static List<File> getsubfiles(File parentfile){
        File[] files=parentfile.listFiles();
        List<File> fileList=null;
        if(files!=null){
            fileList=new ArrayList<>();
            for (File file:files) {
                fileList.add(file);
            }
        }
        return fileList;
    }
    //返回上一个文件列表
    public static List<File> getlastfiles(String parentpath){
        String rootAbsolutePath = root.getAbsolutePath();
        List<File> fileList=null;
        if(parentpath.equals(rootAbsolutePath)){
            Log.i("lastfile","ok");
            //说明此为最上一层，无法向上
        }else{
            Log.i("lastfile","no");
            //创建此文件夹
            File parentfile = new File(parentpath);
            File pparentfile=parentfile.getParentFile();
            fileList=getsubfiles(pparentfile);
        }
        return fileList;
    }
}
