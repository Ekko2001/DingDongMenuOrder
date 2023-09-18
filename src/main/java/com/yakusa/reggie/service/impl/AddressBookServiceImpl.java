package com.yakusa.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yakusa.reggie.entity.AddressBook;
import com.yakusa.reggie.mapper.AddressBookMapper;
import com.yakusa.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
