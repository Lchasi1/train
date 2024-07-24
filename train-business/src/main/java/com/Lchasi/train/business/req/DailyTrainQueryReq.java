package com.Lchasi.train.business.req;

import com.Lchasi.train.common.req.PageReq;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class DailyTrainQueryReq extends PageReq {

//    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")//post请求这样写
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    private String code;

    public String getCode() {
        return code;
    }


    public void setCode(String code) {
        this.code = code;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    @Override
    public String toString() {
        return "DailyTrainQueryReq{" +
                "date=" + date +
                ", code='" + code + '\'' +
                '}';
    }
}
