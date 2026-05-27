package com.sky.controller.user;

import com.sky.dto.AddressBookDTO;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @GetMapping
    public Result<List<AddressBook>> listRoot() {
        return Result.success(addressBookService.list());
    }

    @GetMapping("/list")
    public Result<List<AddressBook>> list() {
        return Result.success(addressBookService.list());
    }

    @GetMapping("/default")
    public Result<AddressBook> getDefault() {
        return Result.success(addressBookService.getDefault());
    }

    @PostMapping
    public Result<AddressBook> save(@RequestBody AddressBookDTO addressBookDTO) {
        return Result.success(addressBookService.save(addressBookDTO));
    }

    @PutMapping
    public Result<String> update(@RequestBody AddressBookDTO addressBookDTO) {
        addressBookService.update(addressBookDTO);
        return Result.success();
    }

    @PutMapping("/default")
    public Result<String> setDefault(@RequestParam Long id) {
        addressBookService.setDefault(id);
        return Result.success();
    }

    @DeleteMapping
    public Result<String> delete(@RequestParam Long id) {
        addressBookService.deleteById(id);
        return Result.success();
    }
}
