package com.Lchasi.train.member.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class MemberSendCodeReq {

    @NotBlank(message = "【手机号】不能为空")//校验框架，且需设置开关@Valid
    @Pattern(regexp = "1\\d{10}$",message = "手机号码格式错误")//正则表达式
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "MemberSendCodeReq{" +
                "mobile='" + mobile + '\'' +
                '}';
    }
}
