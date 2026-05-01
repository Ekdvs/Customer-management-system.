package com.Customer.management.system.Customer.management.system.config;


public class DuplicateNicException extends RuntimeException {

    public DuplicateNicException(String nic) {
        super("A customer with NIC '" + nic + "' already exists.");
    }
}
