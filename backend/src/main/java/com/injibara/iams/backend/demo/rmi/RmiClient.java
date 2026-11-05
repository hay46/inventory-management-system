package com.injibara.iams.backend.demo.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            InventoryRemote remote = (InventoryRemote) registry.lookup("InventoryRemote");
            System.out.println("System: " + remote.getSystemName());
            System.out.println("Stock value: " + remote.computeStockValue(5, 100.0));
        } catch (Exception e) {
            System.err.println("RMI client error: " + e.getMessage());
        }
    }
}
