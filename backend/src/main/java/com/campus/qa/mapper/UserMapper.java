package com.campus.qa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.qa.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
