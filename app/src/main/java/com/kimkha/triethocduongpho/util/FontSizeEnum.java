package com.kimkha.triethocduongpho.util;

import com.kimkha.triethocduongpho.R;

/**
 * @author kimkha
 * @since 7/24/15
 */
public enum FontSizeEnum {
    SMALL(0, R.style.NormalTitleText, R.style.SubTitleText, R.style.BigTitleText, R.style.BigSubTitleText,
            R.style.ContentText, R.style.ContentHead, R.style.ContentSubHead),
    MEDIUM(1, R.style.NormalTitleText_Medium, R.style.SubTitleText_Medium, R.style.BigTitleText_Medium, R.style.BigSubTitleText_Medium,
            R.style.ContentText_Medium, R.style.ContentHead_Medium, R.style.ContentSubHead_Medium),
    LARGE(2, R.style.NormalTitleText_Large, R.style.SubTitleText_Large, R.style.BigTitleText_Large, R.style.BigSubTitleText_Large,
            R.style.ContentText_Large, R.style.ContentHead_Large, R.style.ContentSubHead_Large);

    private final int idx;
    private final int normalTitle;
    private final int normalSubTitle;
    private final int bigTitle;
    private final int bigSubTitle;
    private final int contentHead;
    private final int contentSubHead;
    private final int contentText;

    FontSizeEnum(int idx, int normalTitle, int normalSubTitle, int bigTitle, int bigSubTitle,
                 int contentText, int contentHead, int contentSubHead) {
        this.idx = idx;
        this.normalTitle = normalTitle;
        this.normalSubTitle = normalSubTitle;
        this.bigTitle = bigTitle;
        this.bigSubTitle = bigSubTitle;
        this.contentHead = contentHead;
        this.contentSubHead = contentSubHead;
        this.contentText = contentText;
    }

    public static FontSizeEnum parse(int idx) {
        if (idx == LARGE.idx) {
            return LARGE;
        }
        if (idx == MEDIUM.idx) {
            return MEDIUM;
        }
        return SMALL;
    }

    public int getNormalTitle() {
        return normalTitle;
    }

    public int getNormalSubTitle() {
        return normalSubTitle;
    }

    public int getBigTitle() {
        return bigTitle;
    }

    public int getBigSubTitle() {
        return bigSubTitle;
    }

    public int getContentHead() {
        return contentHead;
    }

    public int getContentSubHead() {
        return contentSubHead;
    }

    public int getContentText() {
        return contentText;
    }
}
