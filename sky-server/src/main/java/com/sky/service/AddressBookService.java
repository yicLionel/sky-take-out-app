package com.sky.service;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.AddressBookDTO;
import com.sky.entity.AddressBook;
import com.sky.exception.BaseException;
import com.sky.mapper.AddressBookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;

    public List<AddressBook> list() {
        return addressBookMapper.list(BaseContext.getCurrentId());
    }

    public AddressBook getDefault() {
        return addressBookMapper.getDefault(BaseContext.getCurrentId());
    }

    @Transactional
    public AddressBook save(AddressBookDTO dto) {
        AddressBook addressBook = toEntity(dto);
        addressBook.userId = BaseContext.getCurrentId();
        if (addressBook.defaultStatus == null) {
            addressBook.defaultStatus = 0;
        }
        if (addressBook.defaultStatus == 1) {
            addressBookMapper.clearDefault(addressBook.userId);
        }
        addressBookMapper.insert(addressBook);
        return addressBook;
    }

    @Transactional
    public void update(AddressBookDTO dto) {
        AddressBook old = addressBookMapper.getByIdAndUserId(dto.id, BaseContext.getCurrentId());
        if (old == null) {
            throw new BaseException(MessageConstant.ADDRESS_NOT_FOUND);
        }
        AddressBook addressBook = toEntity(dto);
        addressBook.userId = BaseContext.getCurrentId();
        if (addressBook.defaultStatus == null) {
            addressBook.defaultStatus = old.defaultStatus;
        }
        if (addressBook.defaultStatus == 1) {
            addressBookMapper.clearDefault(addressBook.userId);
        }
        addressBookMapper.update(addressBook);
    }

    @Transactional
    public void setDefault(Long id) {
        AddressBook old = addressBookMapper.getByIdAndUserId(id, BaseContext.getCurrentId());
        if (old == null) {
            throw new BaseException(MessageConstant.ADDRESS_NOT_FOUND);
        }
        addressBookMapper.clearDefault(BaseContext.getCurrentId());
        addressBookMapper.setDefault(id, BaseContext.getCurrentId());
    }

    public void deleteById(Long id) {
        addressBookMapper.deleteById(id, BaseContext.getCurrentId());
    }

    private AddressBook toEntity(AddressBookDTO dto) {
        AddressBook addressBook = new AddressBook();
        addressBook.id = dto.id;
        addressBook.consignee = dto.consignee;
        addressBook.sex = dto.sex;
        addressBook.phone = dto.phone;
        addressBook.provinceName = dto.provinceName;
        addressBook.cityName = dto.cityName;
        addressBook.districtName = dto.districtName;
        addressBook.detail = dto.detail;
        addressBook.defaultStatus = dto.defaultStatus;
        return addressBook;
    }
}
