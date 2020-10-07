package me.techchrism.funkyperipherals.util;

public class InventoryTransferResult
{
    private final int amount;
    private final String message;
    
    public InventoryTransferResult(int amount, String message)
    {
        this.amount = amount;
        this.message = message;
    }
    
    public int getAmount()
    {
        return amount;
    }
    
    public String getMessage()
    {
        return message;
    }
}
