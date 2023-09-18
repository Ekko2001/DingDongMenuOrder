package com.yakusa.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yakusa.reggie.entity.User;
import com.yakusa.reggie.mapper.UserMapper;
import com.yakusa.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
