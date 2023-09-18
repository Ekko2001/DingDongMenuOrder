package com.yakusa.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yakusa.reggie.entity.OrderDetail;
import com.yakusa.reggie.mapper.OrderDetailMapper;
import com.yakusa.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>implements OrderDetailService {
}
