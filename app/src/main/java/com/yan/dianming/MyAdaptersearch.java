package com.yan.dianming;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Yan on 2016/11/2.
 */
public class MyAdaptersearch extends BaseAdapter{
    private List<Student> mStudents;
    private LayoutInflater mInflater;

    public MyAdaptersearch(Context context, List<Student> list) {
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
            Student mItembean = mStudents.get(position);
            if (convertView == null) {
                //创建一个新视图
                convertView = mInflater.inflate(R.layout.goodlistview, null);
                mViewHolder = new ViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            mViewHolder.mRelativeLayout.setTag(mItembean.getStu_no());
            mViewHolder.mRelativeLayout.setVisibility(View.GONE);
            mViewHolder.mTextView_stuno.setText(mItembean.getStu_no());
            mViewHolder.mTextView_stuname.setText(mItembean.getStu_name());
            mViewHolder.mTextView_class.setText(mItembean.getStu_class());
            mViewHolder.mTextView_badcount.setText(mItembean.getBad()+"");
            if(mItembean.getBad()<1){
                mViewHolder.mTextView_badcount.setTextColor(Color.parseColor("green"));
            }else if(mItembean.getBad()<3){
                mViewHolder.mTextView_badcount.setTextColor(Color.parseColor("#c4c111"));
            }else{
                mViewHolder.mTextView_badcount.setTextColor(Color.parseColor("red"));
            }
        }
        return convertView;
    }

    class ViewHolder {
        public TextView mTextView_stuno;
        public TextView mTextView_stuname;
        public TextView mTextView_class;
        public TextView mTextView_badcount;
        public RelativeLayout mRelativeLayout;
        public ListView mListView_little;

        public ViewHolder(View view) {
            mTextView_stuno = (TextView) view.findViewById(R.id.goodstu_no);
            mTextView_stuname = (TextView) view.findViewById(R.id.goodstu_name);
            mTextView_class = (TextView) view.findViewById(R.id.goodstu_class);
            mTextView_badcount= (TextView) view.findViewById(R.id.goodbadcount);
            mRelativeLayout= (RelativeLayout) view.findViewById(R.id.detaillayout);
            mListView_little= (ListView) view.findViewById(R.id.goodlistview);
        }
    }
}
