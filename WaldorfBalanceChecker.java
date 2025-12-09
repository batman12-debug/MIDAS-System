package com.jpmc.midascore;

import com.jpmc.midascore.component.DatabaseConduit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WaldorfBalanceChecker {
    @Autowired
    private DatabaseConduit databaseConduit;

    public void checkWaldorfBalance() {
        databaseConduit.findUserByName("waldorf").ifPresentOrElse(
            user -> System.out.println("Waldorf's balance: " + user.getBalance() + " (rounded down: " + (int)Math.floor(user.getBalance()) + ")"),
            () -> System.out.println("Waldorf not found")
        );
    }
}

