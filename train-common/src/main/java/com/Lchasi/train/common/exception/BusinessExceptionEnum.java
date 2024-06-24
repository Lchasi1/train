package com.Lchasi.train.common.exception;

//枚举类
public enum BusinessExceptionEnum {
    MEMBER_MOBILE_EXIST("手机号已注册"),
    MEMBER_MOBILE_NOT_EXIST("手机号不存在"),
    MEMBER_MOBILE_CODE_NOT_ERROR("短信验证码错误");

    private String desc;

    BusinessExceptionEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "desc='" + desc + '\'' +
                '}';
    }
}
