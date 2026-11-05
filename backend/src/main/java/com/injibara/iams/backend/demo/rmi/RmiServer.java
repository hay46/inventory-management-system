package com.injibara.iams.backend.demo.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiServer {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("InventoryRemote", new InventoryRemoteImpl());
            System.out.println("RMI server ready on port 1099");
        } catch (Exception e) {
            System.err.println("RMI server error: " + e.getMessage());
        }
    }
}
