package com.red.spring.examples.mapper.primary;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PrimaryMapper {
    @Select("SELECT 'Hello Primary!'")
    String selectHello();
}
