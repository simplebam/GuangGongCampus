package com.example.yueyue.campusapp.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.yueyue.campusapp.models.ContactInfo;
import com.example.yueyue.campusapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * 办公电话适配器-->没有封装过的adapter
 * Created by yueyue on 2017/5/28.
 */

public class ContactAdapter extends BaseAdapter implements SectionIndexer {

    private List<ContactInfo> datas;

    public ContactAdapter(List<ContactInfo> datas) {
        this.datas = datas;
        if (datas == null) {
            datas = new ArrayList<>();
        }
        Collections.sort(datas);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public ContactInfo getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            //R.layout.item_list_contact
            holder = new ViewHolder();
            convertView = View.inflate(parent.getContext(), R.layout.item_list_contact, null);
            holder.tvHead = (TextView) convertView.findViewById(R.id.contact_head);
            holder.tvCatalog = (TextView) convertView.findViewById(R.id.contact_catalog);
            holder.tvLine = (TextView) convertView.findViewById(R.id.contact_line);
            holder.tvName = (TextView) convertView.findViewById(R.id.contact_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        holder.tvName.setText(getItem(position).name);
        //       holder.tvHead.setBackgroundResource(R.drawable.default_head_rect);

        Random random = new Random();
        int circleShape = 0;
        int count = random.nextInt(10);
        int color = 0;

        if (count >= 8) {
            circleShape = R.drawable.circle_deep_orange;
            color = parent.getContext().getResources().getColor(R.color.deep_orange_500);
        } else if (count >= 6 && count < 8) {
            circleShape = R.drawable.circle_teal;
            color = parent.getContext().getResources().getColor(R.color.indigo_500);
        } else if (count >= 4 && count < 6) {
            circleShape = R.drawable.circle_cyan;
            color = parent.getContext().getResources().getColor(R.color.cyan_500);
        } else if (count >= 2 && count < 4) {
            circleShape = R.drawable.circle_blue;
            color = parent.getContext().getResources().getColor(R.color.blue_500);
        } else {
            circleShape = R.drawable.circle_amber;
            color = parent.getContext().getResources().getColor(R.color.amber_500);
        }


 //       holder.tvHead.setBackgroundResource(R.drawable.default_head_rect);
        holder.tvHead.setBackgroundResource(circleShape);
//        holder.tvHead.settext;
        holder.tvHead.setText(getItem(position).name.substring(0, 1));


        //如果是第0个那么一定显示#号
        if (position == 0) {
            holder.tvCatalog.setVisibility(View.VISIBLE);
            holder.tvCatalog.setText("#");
            holder.tvLine.setVisibility(View.VISIBLE);
        } else {

            //如果和上一个item的首字母不同，则认为是新分类的开始
            ContactInfo prevData = datas.get(position - 1);
            if (getItem(position).firstChar != prevData.firstChar) {
                holder.tvCatalog.setVisibility(View.VISIBLE);
                holder.tvCatalog.setText("" + getItem(position).firstChar);
                holder.tvLine.setVisibility(View.VISIBLE);
            } else {
                holder.tvCatalog.setVisibility(View.GONE);
                holder.tvLine.setVisibility(View.GONE);
            }
        }


        return convertView;
    }

    static class ViewHolder {
        //显示名字
        public TextView tvName;
        //首字母
        public TextView tvHead;
        //显示A-Z
        public TextView tvCatalog;
        //显示划线
        public TextView tvLine;

    }


    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        ContactInfo item = datas.get(position);
        return item.firstChar;
    }


    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            char firstChar = datas.get(i).firstChar;
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    public void refresh(ArrayList<ContactInfo> datas) {
        if (datas == null) {
            datas = new ArrayList<ContactInfo>(0);
        }
        Collections.sort(datas);
        this.datas = datas;
        notifyDataSetChanged();

    }
}
