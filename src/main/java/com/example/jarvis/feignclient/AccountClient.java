package com.example.jarvis.feignclient;

import com.example.jarvis.entity.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

//@FeignClient(url = "http://localhost:9899",value = "AccountClient")
@FeignClient(name = "ACCOUNT-SERVICE")
public interface AccountClient {

    @GetMapping("/account/list/{id}")
    public List<Account> getAccountsByUserId(@PathVariable Long id);


    @PostMapping("/create")
    public Account create(@RequestBody Account account);
}
