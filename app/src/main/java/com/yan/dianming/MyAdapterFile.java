package com.yan.dianming;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by Yan on 2016/10/11.
 */
public class MyAdapterFile extends BaseAdapter {
    private List<File> mFiles;
    private LayoutInflater mInflater;

    public MyAdapterFile(List<File> files, Context context) {
        mFiles = files;
        mInflater=LayoutInflater.from(context);
    }

    @Override

    public int getCount() {
        return mFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return mFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Viewhodler viewhodler=null;
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.itemlayout, null);
            viewhodler=new Viewhodler(convertView);
            convertView.setTag(viewhodler);
        }else{
            viewhodler= (Viewhodler) convertView.getTag();
        }
        File file=mFiles.get(position);
        viewhodler.mTextView.setText(file.getName());
        if (file.isDirectory()){
            //放入文件夹图片
            viewhodler.mImageView.setBackgroundResource(R.drawable.dir);
        }else{
            //放入文件图片
            viewhodler.mImageView.setBackgroundResource(R.drawable.file);
        }
        return convertView;
    }
    class Viewhodler{
        public TextView mTextView;
        public ImageView mImageView;

        public Viewhodler(View convertview) {
            mTextView = (TextView) convertview.findViewById(R.id.filename_text);
            mImageView = (ImageView) convertview.findViewById(R.id.image);
        }
    }
}
