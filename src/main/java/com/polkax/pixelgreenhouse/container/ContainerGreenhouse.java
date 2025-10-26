package com.polkax.pixelgreenhouse.container;

import com.pixelmonmod.pixelmon.items.ItemApricorn;
import com.polkax.pixelgreenhouse.tileentity.TileEntityGreenhouse;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerGreenhouse extends Container {
    
    private final TileEntityGreenhouse tileEntity;
    private int burnTime;
    private int currentItemBurnTime;
    private int growthProgress;
    private int growthTime;
    
    public ContainerGreenhouse(InventoryPlayer playerInventory, TileEntityGreenhouse tileEntity) {
        this.tileEntity = tileEntity;
        
        // Инициализируем локальные переменные из TileEntity
        this.burnTime = tileEntity.getField(0);
        this.currentItemBurnTime = tileEntity.getField(1);
        this.growthProgress = tileEntity.getField(2);
        this.growthTime = tileEntity.getField(3);
        
        // Input slots (left side) - 6 slots for apricorns (3x2 grid)
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlotToContainer(new SlotApricorn(tileEntity, j + i * 3, 8 + j * 18, 17 + i * 18));
            }
        }
        
        // Fuel slot (center)
        this.addSlotToContainer(new SlotFuel(tileEntity, 6, 80, 53));
        
        // Output slots (right side) - 6 slots for duplicated apricorns (3x2 grid)
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlotToContainer(new SlotOutput(tileEntity, 7 + j + i * 3, 116 + j * 18, 17 + i * 18));
            }
        }
        
        // Player inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        
        // Player hotbar
        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
    
    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.tileEntity);
    }
    
    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        
        for (int i = 0; i < this.listeners.size(); ++i) {
            IContainerListener listener = this.listeners.get(i);
            
            if (this.burnTime != this.tileEntity.getField(0)) {
                listener.sendWindowProperty(this, 0, this.tileEntity.getField(0));
            }
            
            if (this.currentItemBurnTime != this.tileEntity.getField(1)) {
                listener.sendWindowProperty(this, 1, this.tileEntity.getField(1));
            }
            
            if (this.growthProgress != this.tileEntity.getField(2)) {
                listener.sendWindowProperty(this, 2, this.tileEntity.getField(2));
            }
            
            if (this.growthTime != this.tileEntity.getField(3)) {
                listener.sendWindowProperty(this, 3, this.tileEntity.getField(3));
            }
        }
        
        this.burnTime = this.tileEntity.getField(0);
        this.currentItemBurnTime = this.tileEntity.getField(1);
        this.growthProgress = this.tileEntity.getField(2);
        this.growthTime = this.tileEntity.getField(3);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        this.tileEntity.setField(id, data);
        // Также обновляем локальные переменные контейнера
        switch (id) {
            case 0:
                this.burnTime = data;
                break;
            case 1:
                this.currentItemBurnTime = data;
                break;
            case 2:
                this.growthProgress = data;
                break;
            case 3:
                this.growthTime = data;
                break;
        }
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.tileEntity.isUsableByPlayer(playerIn);
    }
    
    // Публичные геттеры для GUI
    public int getBurnTime() {
        return this.burnTime;
    }
    
    public int getCurrentItemBurnTime() {
        return this.currentItemBurnTime;
    }
    
    public int getGrowthProgress() {
        return this.growthProgress;
    }
    
    public int getGrowthTime() {
        return this.growthTime;
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            
            // From output slots
            if (index >= 7 && index < 13) {
                if (!this.mergeItemStack(itemstack1, 13, 49, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            }
            // From player inventory
            else if (index >= 13) {
                // If it's an apricorn, try to put in input slots
                if (itemstack1.getItem() instanceof ItemApricorn) {
                    if (!this.mergeItemStack(itemstack1, 0, 6, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // If it's fuel, try to put in fuel slot
                else if (TileEntityFurnace.isItemFuel(itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 6, 7, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // Move between player inventory and hotbar
                else if (index >= 13 && index < 40) {
                    if (!this.mergeItemStack(itemstack1, 40, 49, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 40 && index < 49) {
                    if (!this.mergeItemStack(itemstack1, 13, 40, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            // From input or fuel slots
            else if (!this.mergeItemStack(itemstack1, 13, 49, false)) {
                return ItemStack.EMPTY;
            }
            
            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            
            slot.onTake(playerIn, itemstack1);
        }
        
        return itemstack;
    }
    
    // Custom slot for apricorns only
    private static class SlotApricorn extends Slot {
        public SlotApricorn(TileEntityGreenhouse tileEntity, int index, int xPosition, int yPosition) {
            super(tileEntity, index, xPosition, yPosition);
        }
        
        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack.getItem() instanceof ItemApricorn;
        }
    }
    
    // Custom slot for fuel only
    private static class SlotFuel extends Slot {
        public SlotFuel(TileEntityGreenhouse tileEntity, int index, int xPosition, int yPosition) {
            super(tileEntity, index, xPosition, yPosition);
        }
        
        @Override
        public boolean isItemValid(ItemStack stack) {
            return TileEntityFurnace.isItemFuel(stack);
        }
    }
    
    // Custom slot for output only (no placing items)
    private static class SlotOutput extends Slot {
        public SlotOutput(TileEntityGreenhouse tileEntity, int index, int xPosition, int yPosition) {
            super(tileEntity, index, xPosition, yPosition);
        }
        
        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }
    }
}
