package cn.edu.hbpu.reggie.service.impl;

import cn.edu.hbpu.reggie.entity.AddressBook;
import cn.edu.hbpu.reggie.mapper.AddressBookMapper;
import cn.edu.hbpu.reggie.service.AddressBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
