package democat.aggrotp;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.Logger;

import democat.aggrotp.config.ConfigHandler;
import democat.aggrotp.handlers.*;

@Mod(modid = AggroTP.MODID, name = AggroTP.NAME, version = AggroTP.VERSION)
public class AggroTP {
    public static final String MODID = "aggrotp";
    public static final String NAME = "Entity Testing";
    public static final String VERSION = "0.4.1";

    public static Logger logger;

    public AggroTP() {
        MinecraftForge.EVENT_BUS.register(new EntityEventHandler());
        MinecraftForge.EVENT_BUS.register(new MainHandler());
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        ConfigHandler.config = new net.minecraftforge.common.config.Configuration(
                event.getSuggestedConfigurationFile());
        ConfigHandler.initConfigs();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // some example code
        // logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
        logger.info("Hi, welcome!");
    }
}
