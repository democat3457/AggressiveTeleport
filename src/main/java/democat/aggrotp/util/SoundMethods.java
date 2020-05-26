package democat.aggrotp.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;

public class SoundMethods {
    public static void playSound(Entity entity, SoundEvent event, float volume, float pitch) {

        entity.world.playSound((EntityPlayer) null, entity.prevPosX, entity.prevPosY, entity.prevPosZ, event,
                entity.getSoundCategory(), volume, pitch);
        entity.playSound(event, volume, pitch);
    }

    public static void playSound(Entity entity, SoundEvent event) {
        playSound(entity, event, 1.0F, 1.0F);
    }
}