package com.polkax.pixelgreenhouse.gui;

import com.polkax.pixelgreenhouse.container.ContainerGreenhouse;
import com.polkax.pixelgreenhouse.tileentity.TileEntityGreenhouse;
import com.polkax.pixelgreenhouse.util.Reference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
    
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        
        if (ID == Reference.GUI_GREENHOUSE && tileEntity instanceof TileEntityGreenhouse) {
            return new ContainerGreenhouse(player.inventory, (TileEntityGreenhouse) tileEntity);
        }
        
        return null;
    }
    
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        
        if (ID == Reference.GUI_GREENHOUSE && tileEntity instanceof TileEntityGreenhouse) {
            return new GuiGreenhouse(player.inventory, (TileEntityGreenhouse) tileEntity);
        }
        
        return null;
    }
}
