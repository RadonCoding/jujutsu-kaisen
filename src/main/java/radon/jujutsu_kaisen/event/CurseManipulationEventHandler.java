package radon.jujutsu_kaisen.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.curse_manipulation.util.CurseManipulationUtil;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.curse_manipulation.AbsorbedCurse;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.entity.curse.CursedSpirit;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.*;

@EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class CurseManipulationEventHandler {
    private static final Map<CursedSpirit, Integer> curses = new HashMap<>();

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        Iterator<Map.Entry<CursedSpirit, Integer>> iter = curses.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<CursedSpirit, Integer> entry = iter.next();

            CursedSpirit entity = entry.getKey();

            if (entity.level().dimension() != level.dimension()) continue;

            int ticks = entry.getValue();

            if (--ticks == 0) {
                level.addFreshEntity(entity);
                iter.remove();
                continue;
            }

            entry.setValue(ticks);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();

        if (victim.level().isClientSide) return;

        IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData sorcererData = cap.getSorcererData();

        if (!sorcererData.hasTechnique(JJKCursedTechniques.CURSE_MANIPULATION.get())) return;

        ICurseManipulationData curseManipulationData = cap.getCurseManipulationData();

        int ticks = 0;

        for (AbsorbedCurse curse : curseManipulationData.getCurses()) {
            CursedSpirit entity = CurseManipulationUtil.createCurse(victim, curse);

            if (entity == null) continue;

            entity.setPos(victim.getX(), victim.getY(), victim.getZ());

            Vec3 movement = new Vec3(
                    (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.0D,
                    HelperMethods.RANDOM.nextDouble() * 2.0D,
                    (HelperMethods.RANDOM.nextDouble() - 0.5D) * 2.0D
            );
            entity.setDeltaMovement(movement);

            curses.put(entity, ++ticks);
        }
    }
}
