package com.projet.yueq.myapplication_v2;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.leancloud.chatkit.LCChatKitUser;

/**
 * Created by delll on 2017/4/4.
 * 成员列表 Adapter
 */
public class MembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * 所有 Adapter 成员的list
     */
    private List<MemberItem> memberList = new ArrayList<MemberItem>();

    /**
     * 在有序 memberList 中 MemberItem.sortContent 第一次出现时的字母与位置的 map
     */
    private Map<Character, Integer> indexMap = new HashMap<Character, Integer>();

    public MembersAdapter() {}

    /**
     * 设置成员列表，然后更新索引
     * 此处会对数据以 空格、数字、字母（汉字转化为拼音后的字母） 的顺序进行重新排列
     */
    public void setMemberList(List<LCChatKitUser> userList) {
        memberList.clear();
        if (userList.size()>=1) {
            for (LCChatKitUser user : userList) {
                MemberItem item = new MemberItem();
                item.lcChatKitUser = user;
                item.sortContent = PinyinHelper.convertToPinyinString(user.getUserName(), "", PinyinFormat.WITHOUT_TONE);
                memberList.add(item);
            }
        }
        Collections.sort(memberList, new SortChineseName());
        updateIndex();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactItemHolder(parent.getContext(), parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((ContactItemHolder) holder).bindData(memberList.get(position).lcChatKitUser);
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    /**
     * 获取索引 Map
     */
    public Map<Character, Integer> getIndexMap() {
        return indexMap;
    }

    /**
     * 更新索引 Map
     */
    public void updateIndex() {
        Character lastCharcter = '#';
        indexMap.clear();
        for (int i = 0; i < memberList.size(); i++) {
            Character curChar = Character.toLowerCase(memberList.get(i).sortContent.charAt(0));
            if (!lastCharcter.equals(curChar)) {
                indexMap.put(curChar, i);
            }
            lastCharcter = curChar;
        }
    }


    public static class MemberItem {
        public LCChatKitUser lcChatKitUser;
        public String sortContent;
    }

}