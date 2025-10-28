package com.polkax.pixelgreenhouse.proxy;

import com.polkax.pixelgreenhouse.client.render.RenderGreenhouse;
import com.polkax.pixelgreenhouse.tileentity.TileEntityGreenhouse;
import com.polkax.pixelgreenhouse.init.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraft.world.ColorizerGrass;

public class ClientProxy extends CommonProxy {
    
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGreenhouse.class, new RenderGreenhouse());
    }
    
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        BlockColors bc = Minecraft.getMinecraft().getBlockColors();
        bc.registerBlockColorHandler((state, world, pos, tintIndex) -> {
            if (tintIndex == 0) {
                if (world != null && pos != null) {
                    return BiomeColorHelper.getGrassColorAtPos(world, pos);
                }
                return ColorizerGrass.getGrassColor(0.5D, 1.0D);
            }
            return -1;
        }, ModBlocks.GREENHOUSE);

        ItemColors ic = Minecraft.getMinecraft().getItemColors();
        ic.registerItemColorHandler((stack, tintIndex) -> {
            if (tintIndex == 0) {
                return ColorizerGrass.getGrassColor(0.5D, 1.0D);
            }
            return -1;
        }, Item.getItemFromBlock(ModBlocks.GREENHOUSE));
    }
    
    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        // Client-specific post-initialization logic
    }
    
    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
    }
}
