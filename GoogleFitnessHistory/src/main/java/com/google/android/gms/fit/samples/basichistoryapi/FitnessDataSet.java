package com.google.android.gms.fit.samples.basichistoryapi;

/**
 * Created by mrsohn on 2017. 2. 28..
 */

public class FitnessDataSet {

    public enum Period {
        PERIOD_DAY
        , PERIOD_WEEK
        , PERIOD_MONTH
    }

    public enum Type {
        TYPE_CALORY
        , TYPE_STEP
    }

    public enum Currency {
        PENNY(1), NICKLE(5), DIME(10), QUARTER(25);
        private int value;

        private Currency(int value) {
            this.value = value;
        }
    };
}
