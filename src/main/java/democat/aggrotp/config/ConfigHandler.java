package democat.aggrotp.config;

import org.apache.logging.log4j.Level;

import democat.aggrotp.AggroTP;
import democat.aggrotp.config.Configuration.MobEntry;
import net.minecraft.util.ResourceLocation;

public class ConfigHandler {
    public static net.minecraftforge.common.config.Configuration config;

    private static final String CATEGORY_MOBTP = "mobteleports";

    public static void initConfigs() {
        if (config == null) {
            AggroTP.logger.log(Level.ERROR, "Config attempted to be loaded before it was initialized!");
            return;
        }

        config.load();
        
        Configuration.defaultRadius = config.getInt("Default Teleport Radius", CATEGORY_MOBTP, 25, 1, Integer.MAX_VALUE, "The default radius that mobs will teleport players to.");
        Configuration.defaultSoundResloc = new ResourceLocation(config.getString("Default Sound", CATEGORY_MOBTP, "minecraft:entity.enderman.teleport", "The default sound that will play when mobs teleport players."));

        for (String s : config.getStringList("Player-teleporting Mobs", CATEGORY_MOBTP,
                new String[] { "minecraft:enderman;false;10;5" },
                "Configure mob teleport on aggro in the form entity id;tp all players?;delay in seconds;permitted offset from delay;[optional]tp range;[optional]played sound")) {
            String[] parts = s.split(";");
            
            if (parts.length <= 0) continue;
            
            ResourceLocation resloc = new ResourceLocation(parts[0]);
            MobEntry entry;

            if (parts.length == 4)
                entry = new MobEntry(resloc, Boolean.parseBoolean(parts[1]), Integer.parseInt(parts[2]), Float.parseFloat(parts[3]));
            else if (parts.length == 5) {
                try {
                    entry = new MobEntry(resloc, Boolean.parseBoolean(parts[1]), Integer.parseInt(parts[2]), Float.parseFloat(parts[3]), Integer.parseInt(parts[4]));
                } catch (Exception e) {
                    entry = new MobEntry(resloc, Boolean.parseBoolean(parts[1]), Integer.parseInt(parts[2]), Float.parseFloat(parts[3]), new ResourceLocation(parts[4]));
                }
            } else if (parts.length == 6)
                entry = new MobEntry(resloc, Boolean.parseBoolean(parts[1]), Integer.parseInt(parts[2]), Float.parseFloat(parts[3]), Integer.parseInt(parts[4]), new ResourceLocation(parts[5]));
            else {
                AggroTP.logger.log(Level.WARN, "Invalid argument size for " + parts[0] + ", ignoring");
                continue;
            }

            if (!Configuration.mobTeleports.containsKey(resloc))
                Configuration.mobTeleports.put(resloc, entry);
            else
                AggroTP.logger.log(Level.WARN, "Duplicate configuration for " + parts[0] + ", ignoring");
        }

        config.save();
    }
}
