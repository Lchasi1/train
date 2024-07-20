package com.Lchasi.train.member.req;

public class PassengerQueryReq {//新增和保存使用同一个接口

//    @NotNull(message = "【会员ID】不能为空")//long类型不能用notBlank校验
    private Long memberId;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    @Override
    public String toString() {
        return "PassengerQueryReq{" +
                "memberId=" + memberId +
                '}';
    }
}