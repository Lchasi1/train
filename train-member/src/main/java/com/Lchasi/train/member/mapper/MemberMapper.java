package com.Lchasi.train.member.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MemberMapper {

    @Select("select count(1) from member")
    int count();
}
