package com.yan.dianming;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Yan on 2016/11/2.
 */
public class MyAdapterLittle extends BaseAdapter{
    private List<Studentdianming> mStudents;
    private LayoutInflater mInflater;

    public MyAdapterLittle(Context context, List<Studentdianming> list) {
        this.mStudents = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mStudents.size();
    }

    @Override
    public Object getItem(int position) {
        return mStudents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if(position<mStudents.size()){
            Studentdianming mItembean = mStudents.get(position);
            if (convertView == null) {
                //创建一个新视图
                convertView = mInflater.inflate(R.layout.littlelistview, null);
                mViewHolder = new ViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            mViewHolder.mTextView_littleweek.setText(mItembean.getWeek());
            mViewHolder.mTextView_littleclassno.setText(mItembean.getClassno());
            mViewHolder.mTextView_littlestatus.setText(mItembean.getStatus());
            if(mItembean.getStatus().equals("旷课")){
                mViewHolder.mTextView_littlestatus.setTextColor(Color.parseColor("red"));
            }else if(mItembean.getStatus().equals("全勤")){
                mViewHolder.mTextView_littlestatus.setTextColor(Color.parseColor("green"));
            }else{
                mViewHolder.mTextView_littlestatus.setTextColor(Color.parseColor("#c4c111"));
            }
        }
        return convertView;
    }

    class ViewHolder {
        public TextView mTextView_littleweek;
        public TextView mTextView_littleclassno;
        public TextView mTextView_littlestatus;

        public ViewHolder(View view) {
            mTextView_littleweek = (TextView) view.findViewById(R.id.littleweek);
            mTextView_littleclassno = (TextView) view.findViewById(R.id.littleclassno);
            mTextView_littlestatus = (TextView) view.findViewById(R.id.littlestatus);
        }
    }
}
