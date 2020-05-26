package democat.aggrotp.config;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

public class Configuration {
    public static Map<ResourceLocation, MobEntry> mobTeleports = new HashMap<>();
    public static int defaultRadius;

    public static class MobEntry {
        public final ResourceLocation resloc;
        public final boolean tpAllPlayers;
        public final int delay;
        public final float offset;
        public final int radius;

        public MobEntry(ResourceLocation resloc, boolean tpAllPlayers, int delay, float offset) {
            this(resloc, tpAllPlayers, delay, offset, defaultRadius);
        }

        public MobEntry(ResourceLocation resloc, boolean tpAllPlayers, int delay, float offset, int radius) {
            this.resloc = resloc;
            this.tpAllPlayers = tpAllPlayers;
            this.delay = delay;
            this.offset = offset;
            this.radius = radius;
        }
    }
}