package com.red.spring.examples.mapper.second;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SecondMapper {
    @Select("SELECT 'Hello Second!'")
    String selectHello();
}
