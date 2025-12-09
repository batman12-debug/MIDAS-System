package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);
    private final DatabaseConduit databaseConduit;

    public TransactionListener(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeTransaction(Transaction transaction) {
        logger.info("Received transaction: {}", transaction);

        // Validate and process transaction
        boolean success = databaseConduit.processTransaction(transaction);

        if (success) {
            logger.info("Transaction processed successfully: senderId={}, recipientId={}, amount={}", 
                transaction.getSenderId(), transaction.getRecipientId(), transaction.getAmount());
        } else {
            logger.warn("Transaction discarded (validation failed): senderId={}, recipientId={}, amount={}", 
                transaction.getSenderId(), transaction.getRecipientId(), transaction.getAmount());
        }
    }
    
    // Helper method to get waldorf's balance for debugging
    public void logWaldorfBalance() {
        databaseConduit.findUserByName("waldorf").ifPresentOrElse(
            user -> logger.info("Waldorf's balance: {}", user.getBalance()),
            () -> logger.warn("Waldorf not found")
        );
    }
}

