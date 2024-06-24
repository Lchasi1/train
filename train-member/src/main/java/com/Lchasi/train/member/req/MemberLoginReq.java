package com.Lchasi.train.member.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginReq {

    @NotBlank(message = "【手机号】不能为空")//校验框架，且需设置开关@Valid
    @Pattern(regexp = "1\\d{10}$",message = "手机号码格式错误")//正则表达式
    private String mobile;
    @NotBlank(message = "验证码不能为空")
    private String code;


}
