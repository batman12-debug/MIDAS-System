package com.jpmc.midascore.controller;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.foundation.Balance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {
    private final DatabaseConduit databaseConduit;

    public BalanceController(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
    }

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam("userId") Long userId) {
        return databaseConduit.findUserById(userId)
            .map(user -> new Balance(user.getBalance()))
            .orElse(new Balance(0.0f));
    }
}

