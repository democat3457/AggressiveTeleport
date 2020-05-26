package democat.aggrotp.handlers;

import democat.aggrotp.AggroTP;
import democat.aggrotp.config.ConfigHandler;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MainHandler {
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent event) {
        if (event.getModID().equals(AggroTP.MODID)) {
            ConfigHandler.config.save();
            ConfigHandler.initConfigs();
        }
    }
}