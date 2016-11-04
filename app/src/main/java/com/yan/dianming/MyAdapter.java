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
 * Created by Yan on 2016/10/30.
 */
public class MyAdapter extends BaseAdapter {
    private List<Studentdianming> mStudents;
    private LayoutInflater mInflater;

    public MyAdapter(Context context, List<Studentdianming> list) {
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
                convertView = mInflater.inflate(R.layout.listviewlayout, null);
                mViewHolder = new ViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            mViewHolder.mTextView_stuno.setText(mItembean.getStu_no());
            mViewHolder.mTextView_stuname.setText(mItembean.getStu_name());
            mViewHolder.mTextView_status.setText(mItembean.getStatus());
            mViewHolder.mTextView_class.setText(mItembean.getStu_class());
            mViewHolder.mTextView_badcount.setText(mItembean.getBadcount()+"");
            if(mItembean.getBadcount()<1){
                mViewHolder.mTextView_badcount.setTextColor(Color.parseColor("green"));
            }else if(mItembean.getBadcount()<3){
                mViewHolder.mTextView_badcount.setTextColor(Color.parseColor("#c4c111"));
            }else{
                mViewHolder.mTextView_badcount.setTextColor(Color.parseColor("red"));
            }
            if(mItembean.getStatus().equals("旷课")){
                mViewHolder.mTextView_status.setTextColor(Color.parseColor("red"));
            }else if(mItembean.getStatus().equals("全勤")){
                mViewHolder.mTextView_status.setTextColor(Color.parseColor("green"));
            }else{
                mViewHolder.mTextView_status.setTextColor(Color.parseColor("#c4c111"));
            }
        }
        return convertView;
    }
    class ViewHolder {
        public TextView mTextView_stuno;
        public TextView mTextView_stuname;
        public TextView mTextView_class;
        public TextView mTextView_badcount;
        public TextView mTextView_status;

        public ViewHolder(View view) {
            mTextView_stuno = (TextView) view.findViewById(R.id.stu_no);
            mTextView_stuname = (TextView) view.findViewById(R.id.stu_name);
            mTextView_class = (TextView) view.findViewById(R.id.stu_class);
            mTextView_badcount= (TextView) view.findViewById(R.id.badcount);
            mTextView_status= (TextView) view.findViewById(R.id.stu_status);
        }
    }
}
