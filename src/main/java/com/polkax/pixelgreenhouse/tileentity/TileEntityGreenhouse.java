package com.polkax.pixelgreenhouse.tileentity;

import com.pixelmonmod.pixelmon.items.ItemApricorn;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class TileEntityGreenhouse extends TileEntity implements ITickable, IInventory, net.minecraft.inventory.ISidedInventory {
    
    // Слоты, доступные снизу (для воронки)
    private static final int[] SLOTS_BOTTOM = new int[] {7, 8, 9, 10, 11, 12}; // Только выходные слоты
    private static final int[] SLOTS_TOP = new int[] {0, 1, 2, 3, 4, 5}; // Входные слоты
    private static final int[] SLOTS_SIDES = new int[] {6}; // Слот для топлива
    
    private NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(13, ItemStack.EMPTY);
    private String customName;
    
    private int burnTime;
    private int currentItemBurnTime;
    private int growthProgress;
    private static final int GROWTH_TIME = 1200; // 1 minute (1200 ticks = 60 seconds)
    private int syncTicker = 0;
    private static final int SYNC_INTERVAL = 5; // каждые 5 тиков (~0.25s)
    
    @Override
    public void update() {
        if (!world.isRemote) {
            boolean wasBurning = this.isBurning();
            boolean dirty = false;
            
            // Consume fuel
            if (this.isBurning()) {
                --this.burnTime;
            }
            
            // Check if we need fuel and have items to process
            if (!this.isBurning() && this.canGrow()) {
                ItemStack fuelStack = this.inventory.get(6);
                if (!fuelStack.isEmpty()) {
                    this.currentItemBurnTime = this.burnTime = TileEntityFurnace.getItemBurnTime(fuelStack);
                    
                    if (this.isBurning()) {
                        dirty = true;
                        
                        if (!fuelStack.isEmpty()) {
                            fuelStack.shrink(1);
                            
                            if (fuelStack.isEmpty()) {
                                this.inventory.set(6, fuelStack.getItem().getContainerItem(fuelStack));
                            }
                        }
                    }
                }
            }
            
            // Process growth
            if (this.isBurning() && this.canGrow()) {
                ++this.growthProgress;
                dirty = true; // Отмечаем изменение для синхронизации с клиентом
                
                if (this.growthProgress >= GROWTH_TIME) {
                    this.growthProgress = 0;
                    this.growApricorns();
                    sendUpdate();
                }
            } else if (!this.canGrow()) {
                // Сбрасываем прогресс только если нет предметов для роста
                if (this.growthProgress != 0) {
                    this.growthProgress = 0;
                    dirty = true;
                    sendUpdate();
                }
            }
            // Если просто нет топлива, но есть предметы - прогресс сохраняется
            
            if (wasBurning != this.isBurning()) {
                dirty = true;
            }
            
            // Периодическая синхронизация для TESR
            if (this.isBurning() && this.canGrow()) {
                syncTicker++;
                if (syncTicker >= SYNC_INTERVAL) {
                    sendUpdate();
                    syncTicker = 0;
                }
            } else {
                syncTicker = 0;
            }
            
            if (dirty) {
                this.markDirty();
            }
        }
    }
    
    private boolean canGrow() {
        // Check if there are apricorns in input slots
        for (int i = 0; i < 6; i++) {
            ItemStack stack = this.inventory.get(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemApricorn) {
                // Check if there's space in output slots
                for (int j = 7; j < 13; j++) {
                    ItemStack outputStack = this.inventory.get(j);
                    if (outputStack.isEmpty() || (outputStack.getItem() == stack.getItem() && outputStack.getCount() < outputStack.getMaxStackSize())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private void growApricorns() {
        // Generate random amount once for all slots in this growth cycle
        int amount = 1 + world.rand.nextInt(3); // Рандомное количество от 1 до 3
        
        // Process each input slot
        for (int i = 0; i < 6; i++) {
            ItemStack inputStack = this.inventory.get(i);
            if (!inputStack.isEmpty() && inputStack.getItem() instanceof ItemApricorn) {
                // Try to add to output slots
                for (int j = 7; j < 13; j++) {
                    ItemStack outputStack = this.inventory.get(j);
                    if (outputStack.isEmpty()) {
                        this.inventory.set(j, new ItemStack(inputStack.getItem(), amount));
                        break;
                    } else if (outputStack.getItem() == inputStack.getItem() && outputStack.getCount() < outputStack.getMaxStackSize()) {
                        outputStack.grow(Math.min(amount, outputStack.getMaxStackSize() - outputStack.getCount()));
                        break;
                    }
                }
            }
        }
    }
    
    public boolean isBurning() {
        return this.burnTime > 0;
    }
    
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) == this && 
               player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
    }
    
    public int getGrowthProgress() {
        return this.growthProgress;
    }
    
    public int getGrowthTime() {
        return GROWTH_TIME;
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("BurnTime", this.burnTime);
        compound.setInteger("CurrentItemBurnTime", this.currentItemBurnTime);
        compound.setInteger("GrowthProgress", this.growthProgress);
        ItemStackHelper.saveAllItems(compound, this.inventory);
        
        if (this.hasCustomName()) {
            compound.setString("CustomName", this.customName);
        }
        
        return compound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.burnTime = compound.getInteger("BurnTime");
        this.currentItemBurnTime = compound.getInteger("CurrentItemBurnTime");
        this.growthProgress = compound.getInteger("GrowthProgress");
        this.inventory = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.inventory);
        
        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }
    }
    
    // IInventory implementation
    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.greenhouse";
    }
    
    @Override
    public boolean hasCustomName() {
        return this.customName != null && !this.customName.isEmpty();
    }
    
    public void setCustomName(String customName) {
        this.customName = customName;
    }
    
    @Override
    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }
    
    @Override
    public int getSizeInventory() {
        return this.inventory.size();
    }
    
    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public ItemStack getStackInSlot(int index) {
        return this.inventory.get(index);
    }
    
    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(this.inventory, index, count);
    }
    
    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.inventory, index);
    }
    
    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack itemstack = this.inventory.get(index);
        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
        this.inventory.set(index, stack);
        
        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        
        if (index < 6 && !flag) {
            this.growthProgress = 0;
            this.markDirty();
            if (!world.isRemote) {
                sendUpdate();
            }
        }
    }
    
    @Override
    public int getInventoryStackLimit() {
        return 64;
    }
    
    @Override
    public void openInventory(EntityPlayer player) {
    }
    
    @Override
    public void closeInventory(EntityPlayer player) {
    }
    
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index >= 7 && index < 13) {
            return false; // Output slots
        } else if (index == 6) {
            return TileEntityFurnace.isItemFuel(stack); // Fuel slot
        } else {
            return stack.getItem() instanceof ItemApricorn; // Input slots
        }
    }
    
    @Override
    public int getField(int id) {
        switch (id) {
            case 0:
                return this.burnTime;
            case 1:
                return this.currentItemBurnTime;
            case 2:
                return this.growthProgress;
            case 3:
                return GROWTH_TIME;
            default:
                return 0;
        }
    }
    
    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0:
                this.burnTime = value;
                break;
            case 1:
                this.currentItemBurnTime = value;
                break;
            case 2:
                this.growthProgress = value;
                break;
            case 3:
                // GROWTH_TIME is constant, no need to set
                break;
        }
    }
    
    @Override
    public int getFieldCount() {
        return 4;
    }
    
    @Override
    public void clear() {
        this.inventory.clear();
    }

    // ISidedInventory implementation
    @Override
    public int[] getSlotsForFace(net.minecraft.util.EnumFacing side) {
        if (side == net.minecraft.util.EnumFacing.DOWN) {
            return SLOTS_BOTTOM; // Только выходные слоты доступны снизу
        } else if (side == net.minecraft.util.EnumFacing.UP) {
            return SLOTS_TOP; // Входные слоты доступны сверху
        } else {
            return SLOTS_SIDES; // Слот для топлива доступен с боков
        }
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, net.minecraft.util.EnumFacing direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, net.minecraft.util.EnumFacing direction) {
        if (direction == net.minecraft.util.EnumFacing.DOWN) {
            return index >= 7 && index < 13; // Только из выходных слотов можно извлекать снизу
        }
        return false;
    }

    private void sendUpdate() {
        if (world == null) return;
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        markDirty();
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new SPacketUpdateTileEntity(this.pos, 1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }
}
