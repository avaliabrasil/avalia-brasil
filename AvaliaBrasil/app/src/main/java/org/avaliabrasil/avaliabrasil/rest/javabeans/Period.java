package org.avaliabrasil.avaliabrasil.rest.javabeans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Period {

    @SerializedName("open")
    @Expose
    private PeriodDescription isOpen;

    @SerializedName("close")
    @Expose
    private PeriodDescription isClose;

    public PeriodDescription getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(PeriodDescription isOpen) {
        this.isOpen = isOpen;
    }

    public PeriodDescription getIsClose() {
        return isClose;
    }

    public void setIsClose(PeriodDescription isClose) {
        this.isClose = isClose;
    }
}