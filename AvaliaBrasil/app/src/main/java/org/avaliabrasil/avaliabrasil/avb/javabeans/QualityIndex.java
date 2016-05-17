package org.avaliabrasil.avaliabrasil.avb.javabeans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Created by Developer on 02/05/2016.
 */
public class QualityIndex {
    @SerializedName("month")
    @Expose
    private String month;

    @SerializedName("value")
    @Expose
    private Double value;

    public QualityIndex() {
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Double getValue() {
        return
                new BigDecimal(value)
                        .setScale(2, BigDecimal.ROUND_HALF_UP)
                        .doubleValue();
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
