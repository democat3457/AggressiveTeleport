package democat.aggrotp.handlers;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;

import democat.aggrotp.AggroTP;
import democat.aggrotp.config.Configuration;
import democat.aggrotp.config.Configuration.MobEntry;
import democat.aggrotp.util.MathMethods;
import democat.aggrotp.util.SoundMethods;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityEventHandler {
    private Map<EntityLiving, Entry<MobEntry, Integer>> tpTimer = new HashMap<>();

    /*
    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {

        Entity trueSource = event.getSource().getTrueSource();
        Entity immediateSource = event.getSource().getImmediateSource();
        Entity target = event.getEntity();

        if (trueSource == null || target == null || 
                target instanceof FakePlayer || trueSource instanceof FakePlayer || immediateSource instanceof FakePlayer || 
                target.world.isRemote || 
                !((trueSource instanceof EntityPlayer && target instanceof EntityLiving) || (trueSource instanceof EntityLiving && target instanceof EntityPlayer))) {
                return;
        }
        
        debug("Target " + target.getName() + " has true source " + trueSource.getName() + " and imm source " + immediateSource.getName());

        EntityEntry ee = EntityRegistry.getEntry(target.getClass());
        if (trueSource instanceof EntityLiving)
            ee = EntityRegistry.getEntry(trueSource.getClass());

        if (ee == null)
            return;
        debug("Entity registry entry found: " + ee.getRegistryName());

        if (ee != null && Configuration.mobTeleports.containsKey(ee.getRegistryName())) {
            if (target instanceof EntityLivingBase) {
                EntityLivingBase livingTarget = (EntityLivingBase) target;
                debug("Target " + livingTarget.getName() + " has revenge target " + ((livingTarget.getRevengeTarget() != null) ? livingTarget.getRevengeTarget().getName() : "NULL"));
                if (!tpTimer.containsKey(livingTarget) && livingTarget.getAttackTarget() instanceof EntityPlayer) {
                    MobEntry mobentry = Configuration.mobTeleports.get(ee.getRegistryName());
                    int delay = (int) (MathMethods.randomRangeSec(mobentry.delay - mobentry.offset, mobentry.delay + mobentry.offset));
                    debug("Entry for " + livingTarget.getName() + " does not exist in tpTimer. Placing entry with timer " + delay);
                    tpTimer.put(livingTarget, new SimpleEntry<>(mobentry, delay));
                } else if (livingTarget.getRevengeTarget() instanceof EntityPlayer && tpTimer.get(livingTarget).getValue() == -1) {
                    MobEntry mobentry = Configuration.mobTeleports.get(ee.getRegistryName());
                    int delay = (int) (MathMethods.randomRangeSec(mobentry.delay - mobentry.offset, mobentry.delay + mobentry.offset));
                    debug("Entry for " + livingTarget.getName() + " exists in tpTimer. Replacing entry with timer " + delay);
                    tpTimer.replace(livingTarget, new SimpleEntry<>(mobentry, delay));
                }
            }
        }
    }
    */

    @SubscribeEvent
    public void setAttackTargetEvent(LivingSetAttackTargetEvent event) {
        EntityLivingBase mob = event.getEntityLiving();
        EntityLivingBase target = event.getTarget();

        if (mob == null || target == null || mob instanceof FakePlayer || target instanceof FakePlayer || target.world.isRemote)
            return;
        
        debug("Living entity " + mob.getName() + " has acquired attack target " + target.getName());

        if (!(mob instanceof EntityLiving) || !(target instanceof EntityPlayer))
            return;
        
        debug("Passed first check");

        EntityLiving livingMob = (EntityLiving) mob;
        EntityPlayer targetPlayer = (EntityPlayer) target;

        EntityEntry ee = EntityRegistry.getEntry(livingMob.getClass());

        if (ee == null)
            return;
        
        debug("Entity registry entry found: " + ee.getRegistryName());

        if (ee != null && Configuration.mobTeleports.containsKey(ee.getRegistryName())) {
            AggroTP.logger.log(Level.DEBUG,
                    "Target " + livingMob.getName() + " has attack target "
                            + ((livingMob.getAttackTarget() != null) ? livingMob.getAttackTarget().getName()
                                    : "NULL"));
            if (!tpTimer.containsKey(livingMob) && livingMob.getAttackTarget() instanceof EntityPlayer) {
                MobEntry mobentry = Configuration.mobTeleports.get(ee.getRegistryName());
                int delay = (int) (MathMethods.randomRangeSec(mobentry.delay - mobentry.offset,
                        mobentry.delay + mobentry.offset));
                debug("Entry for " + livingMob.getName()
                        + " does not exist in tpTimer. Placing entry with timer " + delay);
                tpTimer.put(livingMob, new SimpleEntry<>(mobentry, delay));
            } else if (livingMob.getAttackTarget() instanceof EntityPlayer
                    && tpTimer.get(livingMob).getValue() == -1) {
                MobEntry mobentry = Configuration.mobTeleports.get(ee.getRegistryName());
                int delay = (int) (MathMethods.randomRangeSec(mobentry.delay - mobentry.offset,
                        mobentry.delay + mobentry.offset));
                debug("Entry for " + livingMob.getName()
                        + " exists in tpTimer. Replacing entry with timer " + delay);
                tpTimer.replace(livingMob, new SimpleEntry<>(mobentry, delay));
            }
        }
    }

    @SubscribeEvent
    public void serverTickEvent(TickEvent.ServerTickEvent event) {

        if (event.phase != Phase.START)
            return;

        // debug("Server TICK EVENT (checks passed)");

        tpTimer.forEach((entity, entry) -> {
            if (entity.isDead && entry.getValue() != -1) {
                entry.setValue(-1);
                debug("Entity is dead. Setting timer to -1: " + entity.getName());
            } else if (entry.getValue() == 0) {
                MobEntry mobentry = entry.getKey();
                debug("Timer is 0 for entity " + entity.getName() + ". Preparing to tp.");
                boolean success = teleport(entity, mobentry);
                debug("TP Success: " + success);
                entry.setValue((int) (MathMethods.randomRangeSec(mobentry.delay - mobentry.offset, mobentry.delay + mobentry.offset)));
                debug("Setting timer of entity " + entity.getName() + " to " + entry.getValue());
            } else if (entry.getValue() > 0) {
                if (entity.getAttackTarget() instanceof EntityPlayer) {
                    entry.setValue(entry.getValue() - 1);
                    debug("Decrementing... " + entity.getName() + " has timer value " + entry.getValue());
                } else {
                    entry.setValue(-1);
                    debug("Entity " + entity.getName() + " no longer has player attack target. Setting timer to -1.");
                }
            }
        });
    }

    public boolean teleport(EntityLiving target, MobEntry entry) {
        if (!(target.getAttackTarget() instanceof EntityPlayer))
            return false;
        debug("TP method: passed first check.");
        EntityPlayer attacker = (EntityPlayer) target.getAttackTarget();

        if (attacker == null || attacker instanceof FakePlayer || attacker.isDead)
            return false;
        debug("TP method: passed second check.");

        // MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        // debug("TP method: passed third check.");
        
        List<EntityPlayer> entityTeleportList = new ArrayList<>();
        if (entry.tpAllPlayers)
            entityTeleportList.addAll(target.world.playerEntities);
        else
            entityTeleportList.add(attacker);

        debug("Entity teleport list: " + entityTeleportList.toString());

        boolean success = true;
        
        for (EntityPlayer player : entityTeleportList) {
            double x = target.posX + (Math.random() - 0.5D) * entry.radius * 2;
            double temp = Math.random() - 0.5D;
            double y = target.posY + (temp < 0 ? 0 : temp) * entry.radius * 2;
            double z = target.posZ + (Math.random() - 0.5D) * entry.radius * 2;

            debug("Obtained x, y, z " + x + ", " + y + ", " + z + " for player " + player.getName());

            EnderTeleportEvent event = new EnderTeleportEvent(player, x, y, z, 0);

            if (MinecraftForge.EVENT_BUS.post(event) || event == null || player == null)
                continue;

            debug("EVENT BUS Post check passed for " + player.getName());

            boolean flag = player.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ());

            if (flag) {
                SoundMethods.playSound(player, SoundEvents.ENTITY_ENDERMEN_TELEPORT);
                debug("Played tp sound");
            } else {
                debug("Attempting second teleport directly");
                event = new EnderTeleportEvent(player, target.posX, target.posY, target.posZ, 0);
                flag = player.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ());

                if (flag) {
                    SoundEvent sound = new SoundEvent(entry.soundResloc);
                    SoundMethods.playSound(player, sound);
                    debug("Played tp sound");
                } else {
                    success = false;
                }
            }
        }

        return success;
    }

    @SuppressWarnings("unused")
    private void info(String message) {
        AggroTP.logger.log(Level.INFO, message);
    }

    @SuppressWarnings("unused")
    private void debug(String message) {
        if (Configuration.debug)
            AggroTP.logger.log(Level.DEBUG, message);
    }

    @SuppressWarnings("unused")
    private void warn(String message) {
        AggroTP.logger.log(Level.WARN, message);
    }

    @SuppressWarnings("unused")
    private void error(String message) {
        AggroTP.logger.log(Level.ERROR, message);
    }
}
