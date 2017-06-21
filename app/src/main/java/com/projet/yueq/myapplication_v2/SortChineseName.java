package com.projet.yueq.myapplication_v2;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by delll on 2017/5/11.
 */

public class SortChineseName implements Comparator<MembersAdapter.MemberItem> {
    /**
     * 简体中文的 Collator
     */
    Collator cmp = Collator.getInstance(Locale.SIMPLIFIED_CHINESE);
    @Override
    public int compare(MembersAdapter.MemberItem str1, MembersAdapter.MemberItem str2) {
        if (null == str1) {
            return -1;
        }
        if (null == str2) {
            return 1;
        }
        if (cmp.compare(str1.sortContent, str2.sortContent)>0){
            return 1;
        }else if (cmp.compare(str1.sortContent, str2.sortContent)<0){
            return -1;
        }
        return 0;
    }
}
