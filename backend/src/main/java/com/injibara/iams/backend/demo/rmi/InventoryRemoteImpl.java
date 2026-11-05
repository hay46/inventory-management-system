package com.injibara.iams.backend.demo.rmi;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class InventoryRemoteImpl extends UnicastRemoteObject implements InventoryRemote {
    protected InventoryRemoteImpl() throws RemoteException { super(); }

    @Override
    public String getSystemName() throws RemoteException {
        return "Inventory and Asset Management System (IAMS)";
    }

    @Override
    public int computeStockValue(int qty, double unitPrice) throws RemoteException {
        return (int) (qty * unitPrice);
    }
}
