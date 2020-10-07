package me.techchrism.funkyperipherals.util;

import java.util.Optional;

import net.minecraft.inventory.Inventory;

public class InventoryUtil
{
    public static InventoryTransferResult transfer(Inventory from, int fromSlot, Inventory to, int toSlot, Optional<Integer> limitOption)
    {
        if(fromSlot < 0 || fromSlot >= from.size())
        {
            return new InventoryTransferResult(0, "Invalid \"from\" slot");
        }
        if(toSlot < 0 || toSlot >= to.size())
        {
            return new InventoryTransferResult(0, "Invalid \"to\" slot");
        }

        if(!to.getStack(toSlot).isEmpty() && !to.getStack(toSlot).isItemEqual(from.getStack(fromSlot)))
        {
            return new InventoryTransferResult(0, "Slot already occupied");
        }
    
        int limit = limitOption.orElse(from.getStack(fromSlot).getCount());
    
        // Either use the limit as the amount, the "from" stack size, or
        // the max amount until the "to" stack is maxed out, whichever is smallest
        int transferAmount = Math.min(Math.min(
                limit,
                from.getStack(fromSlot).getMaxCount() - to.getStack(toSlot).getCount()),
                from.getStack(fromSlot).getCount());
    
        if(transferAmount < 1)
        {
            return new InventoryTransferResult(0, "Transferred no items");
        }
    
        if(to.getStack(toSlot).isItemEqual(from.getStack(fromSlot)))
        {
            to.getStack(toSlot).increment(transferAmount);
        }
        else
        {
            to.setStack(toSlot, from.getStack(fromSlot).copy());
            to.getStack(toSlot).setCount(transferAmount);
        }
        from.getStack(fromSlot).decrement(transferAmount);
    
        return new InventoryTransferResult(transferAmount, "Transferred items");
    }
}
