package com.greencross.greencare.main;

import android.graphics.Color;

import com.greencross.greencare.R;

/**
 * Created by Lincoln on 18/05/16.
 */
public class MainCardData {
    private CardE cardE;
    private int value;
    private int maxValue;
    private int cardColor;
    private int cardImage;
    private boolean isVisible;

    public MainCardData(CardE cardE) {
        setCardE(cardE);
        settingValue();
    }

    private void settingValue() {
        if (cardE == CardE.WORKING) {
            setValue(0);
            setMaxValue(100);
            setCardColor(Color.parseColor("#EF5D51"));
            setCardImage(R.drawable.icon_main_sports);
        } else if (cardE == CardE.SUGAR) {
            setValue(100);
            setMaxValue(100);
            setCardColor(Color.parseColor("#F9C4DF"));
            setCardImage(R.drawable.icon_main_sugar);
        } else if (cardE == CardE.PRESURE) {
            setValue(100);
            setMaxValue(100);
            setCardColor(Color.parseColor("#D6C1EF"));
            setCardImage(R.drawable.icon_main_pressure);
        } else if (cardE == CardE.WEIGHT) {
            setValue(0);
            setMaxValue(100);
            setCardColor(Color.parseColor("#E9D0B5"));
            setCardImage(R.drawable.icon_main_weight);
        } else if (cardE == CardE.WATER) {
            setValue(0);
            setMaxValue(100);
            setCardColor(Color.parseColor("#43ADEE"));
            setCardImage(R.drawable.icon_main_water);
        } else if (cardE == CardE.FOOD) {
            setValue(0);
            setMaxValue(100);
            setCardColor(Color.parseColor("#7BC060"));
            setCardImage(R.drawable.icon_main_food);
        } else if (cardE == CardE.FAT) {
            setValue(0);
            setMaxValue(100);
            setCardColor(Color.parseColor("#9ED8DA"));
            setCardImage(R.drawable.icon_main_fat);
        }
    }

    public enum CardE {
        WORKING("운동")   // (mContext.getString(R.string.text_work))
        , SUGAR("혈당")   // SUGAR(mContext.getString(R.string.text_sugar))
        , PRESURE("혈압") // PRESURE(mContext.getString(R.string.text_pressure))
        , WEIGHT("체중")  // WEIGHT(mContext.getString(R.string.text_weight))
        , WATER("물")    // WATER(mContext.getString(R.string.text_water))
        , FOOD("식사")    // FOOD(mContext.getString(R.string.text_food))
        , FAT("체지방률")    // FOOD(mContext.getString(R.string.text_food))
        , ADD("추가");    // ADD(mContext.getString(R.string.text_add));

        private String mCardName;
        CardE(String cardName) {
                mCardName = cardName;
        }

        public String getCardName() {
            return mCardName;
        }

    }

    public CardE getCardE() {
        return cardE;
    }

    public void setCardE(CardE cardE) {
        this.cardE = cardE;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getCardColor() {
        return cardColor;
    }

    public void setCardColor(int cardColor) {
        this.cardColor = cardColor;
    }

    public int getCardImage() {
        return cardImage;
    }

    public void setCardImage(int cardImage) {
        this.cardImage = cardImage;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
