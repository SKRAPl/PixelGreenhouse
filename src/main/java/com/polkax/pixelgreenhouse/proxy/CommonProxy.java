package com.polkax.pixelgreenhouse.proxy;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    
    public void preInit(FMLPreInitializationEvent event) {
        // Common pre-initialization logic
    }
    
    public void init(FMLInitializationEvent event) {
        // Common initialization logic
    }
    
    public void postInit(FMLPostInitializationEvent event) {
        // Common post-initialization logic
    }
    
    public void registerItemRenderer(Item item, int meta, String id) {
        // Server doesn't need to register item renderers
    }
}
