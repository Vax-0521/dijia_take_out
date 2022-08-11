package com.xyy.dijia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyy.dijia.entity.AddressBook;
import com.xyy.dijia.mapper.AddressBookMapper;
import com.xyy.dijia.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
