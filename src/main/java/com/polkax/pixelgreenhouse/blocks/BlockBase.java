package com.polkax.pixelgreenhouse.blocks;

import com.polkax.pixelgreenhouse.PixelGreenhouse;
import com.polkax.pixelgreenhouse.init.ModBlocks;
import com.polkax.pixelgreenhouse.init.ModItems;
import com.polkax.pixelgreenhouse.util.IHasModel;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class BlockBase extends Block implements IHasModel {
    
    public BlockBase(String name, Material material) {
        super(material);
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(CreativeTabs.DECORATIONS);
        
        ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }
    
    @Override
    public void registerModels() {
        PixelGreenhouse.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
    }
}
