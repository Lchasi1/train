package com.Lchasi.train.common.exception;

//枚举类
public enum BusinessExceptionEnum {
    MEMBER_MOBILE_EXIST("手机号已注册"),
    MEMBER_MOBILE_NOT_EXIST("手机号不存在"),
    MEMBER_MOBILE_CODE_NOT_ERROR("短信验证码错误"),
    BUSINESS_STATION_NAME_UNIQUE_ERROR("车站已存在"),
    BUSINESS_TRAIN_CODE_UNIQUE_ERROR("车次编号已存在"),
    BUSINESS_TRAIN_STATION_INDEX_UNIQUE_ERROR("同车次战序已存在"),
    BUSINESS_TRAIN_STATION_NAME_UNIQUE_ERROR("同车次站名已存在"),
    BUSINESS_TRAIN_CARRIAGE_INDEX_UNIQUE_ERROR("同车次箱号已存在"),
    CONFIRM_ORDER_EXCEPTION("服务器忙请稍后重试"),
    CONFIRM_ORDER_LOCK_FAIL("当前抢票人数多，请稍后重试"),
    CONFIRM_ORDER_TICKET_COUNT_ERROR("余票不足"),
    CONFIRM_ORDER_SK_TOKEN_FAIL("当前抢票人数过多，请5s后重试"),
    CONFIRM_ORDER_Flow_EXCEPTION("当前抢票人数多，请稍后重试");


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
