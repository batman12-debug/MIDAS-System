package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final IncentiveQuerier incentiveQuerier;

    public DatabaseConduit(UserRepository userRepository, TransactionRepository transactionRepository, IncentiveQuerier incentiveQuerier) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.incentiveQuerier = incentiveQuerier;
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    public Optional<UserRecord> findUserById(long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public boolean processTransaction(Transaction transaction) {
        long senderId = transaction.getSenderId();
        long recipientId = transaction.getRecipientId();
        float amount = transaction.getAmount();

        // Find sender and recipient
        Optional<UserRecord> senderOpt = userRepository.findById(senderId);
        Optional<UserRecord> recipientOpt = userRepository.findById(recipientId);

        // Validate: both users must exist
        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            return false;
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        // Validate: sender has sufficient balance
        if (sender.getBalance() < amount) {
            return false;
        }

        // Query incentive API after validation
        Incentive incentive = incentiveQuerier.queryIncentive(transaction);
        float incentiveAmount = incentive != null ? incentive.getAmount() : 0.0f;

        // Update balances
        // Deduct transaction amount from sender
        sender.setBalance(sender.getBalance() - amount);
        // Add transaction amount + incentive to recipient (incentive not deducted from sender)
        recipient.setBalance(recipient.getBalance() + amount + incentiveAmount);

        // Save updated users
        userRepository.save(sender);
        userRepository.save(recipient);

        // Create and save transaction record with incentive
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, amount, incentiveAmount);
        transactionRepository.save(transactionRecord);

        return true;
    }

    public Optional<UserRecord> findUserByName(String name) {
        return userRepository.findByName(name);
    }
}
