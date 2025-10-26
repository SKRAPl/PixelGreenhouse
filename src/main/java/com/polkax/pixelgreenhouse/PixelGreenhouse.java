package com.polkax.pixelgreenhouse;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import org.apache.logging.log4j.Logger;

import com.polkax.pixelgreenhouse.gui.GuiHandler;
import com.polkax.pixelgreenhouse.proxy.CommonProxy;
import com.polkax.pixelgreenhouse.util.Reference;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, dependencies = Reference.DEPENDENCIES)
public class PixelGreenhouse {
    
    @Instance
    public static PixelGreenhouse instance;
    
    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
    public static CommonProxy proxy;
    
    private static Logger logger;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        logger.info("PixelGreenhouse Pre-Initialization");
        
        // Register GUI Handler
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        
        proxy.preInit(event);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("PixelGreenhouse Initialization");
        proxy.init(event);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        logger.info("PixelGreenhouse Post-Initialization");
        proxy.postInit(event);
    }
    
    public static Logger getLogger() {
        return logger;
    }
}
