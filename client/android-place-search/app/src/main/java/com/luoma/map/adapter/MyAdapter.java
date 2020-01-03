package com.luoma.map.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.map.R;

import java.util.ArrayList;
import java.util.List;


public class MyAdapter extends BaseAdapter {


        List<String> datas = new ArrayList<>();
        Context mContext;
        public MyAdapter(Context context) {
            this.mContext = context;
        }

        public void setDatas(List<String> datas) {
            this.datas = datas;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return datas==null?0:datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas==null?null:datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHodler hodler = null;
            if (convertView == null) {
                hodler = new ViewHodler();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item, null);
                hodler.mTextView = (TextView) convertView;
                convertView.setTag(hodler);
            } else {
                hodler = (ViewHodler) convertView.getTag();
            }

            hodler.mTextView.setText(datas.get(position));

            return convertView;
        }

        private static class ViewHodler{
            TextView mTextView;
        }
    }

