package com.example.yueyue.campusapp.adapter;

import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.yueyue.campusapp.models.ContactInfo;
import com.example.yueyue.campusapp.R;

import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.widget.AdapterHolder;
import org.kymjs.kjframe.widget.KJAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 办公电话适配器-->已经Adapter进行了二次封装
 * -->更多详情问他:https://github.com/kymjs/Contacts
 * Created by Administrator on 2015/10/27.
 */
public class PhoneAdapter extends KJAdapter<ContactInfo> implements SectionIndexer {

    private static final String TAG = PhoneAdapter.class.getSimpleName();

    private KJBitmap kjb = new KJBitmap();
    private List<ContactInfo> datas;

    public PhoneAdapter(AbsListView view, List<ContactInfo> mDatas) {
        super(view, mDatas, R.layout.item_list_contact);
        this.datas = mDatas;
        if (datas == null) {
            datas = new ArrayList<>(0);
        }
        Collections.sort(datas);
    }

    @Override
    public void convert(AdapterHolder helper, ContactInfo item, boolean isScrolling) {
    }


    @Override
    public void convert(AdapterHolder holder, ContactInfo item,
                        boolean isScrolling, int position) {

        holder.setText(R.id.contact_name, item.name);

        // TODO: 2017/5/28 待会改为我们自己的 -->按照原始的来搞
        ImageView headImg = holder.getView(R.id.contact_head);
        item.url="11111";
        if (isScrolling) {
            kjb.displayCacheOrDefult(headImg, item.url, R.drawable.default_head_rect);
        } else {
            kjb.displayWithLoadBitmap(headImg, item.url, R.drawable.default_head_rect);
        }


        TextView tvLetter = holder.getView(R.id.contact_catalog);
//        tvCatalog.getParent().requestDisallowInterceptTouchEvent(false);
//        tvCatalog.setClickable(false);
        TextView tvLine = holder.getView(R.id.contact_line);
//        tvLine.setClickable(false);

        if (datas.size() > 0) {
            //如果是第0个那么一定显示#号
            if (position == 0) {
                tvLetter.setVisibility(View.VISIBLE);
                tvLetter.setText("#");
                tvLine.setVisibility(View.VISIBLE);
            } else {
                //如果和上一个item的首字母不同，则认为是新分类的开始
                ContactInfo prevData = datas.get(position - 1);
                if (item.firstChar != prevData.firstChar) {
                    tvLetter.setVisibility(View.VISIBLE);
                    tvLetter.setText("" + item.firstChar);
                    tvLine.setVisibility(View.VISIBLE);
                } else {
                    tvLetter.setVisibility(View.GONE);
                    tvLine.setVisibility(View.GONE);
                }

            }

        }
    }


    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        ContactInfo item = datas.get(position);
        return item.firstChar;
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
            datas = new ArrayList<>(0);
        }
        Collections.sort(datas);
        this.datas = datas;
        notifyDataSetChanged();
    }

}
