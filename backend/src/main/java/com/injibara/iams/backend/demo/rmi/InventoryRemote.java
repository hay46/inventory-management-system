package com.injibara.iams.backend.demo.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InventoryRemote extends Remote {
    String getSystemName() throws RemoteException;
    int computeStockValue(int qty, double unitPrice) throws RemoteException;
}
