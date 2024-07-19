package com.Lchasi.train.common.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

/**
 * 用来封装返回类，避免一些敏感字段，比如密码
 */
public class MemberLoginResp {
    private Long id;

    private String mobile;

    private String token;


}