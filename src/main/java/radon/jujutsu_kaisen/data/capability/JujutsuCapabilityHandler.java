package radon.jujutsu_kaisen.data.capability;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.entity.ISorcerer;

@EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class JujutsuCapabilityHandler {
    public static final EntityCapability<IJujutsuCapability, Void> INSTANCE = EntityCapability.create(new ResourceLocation(JujutsuKaisen.MOD_ID, "jujutsu"), IJujutsuCapability.class, void.class);

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
            event.registerEntity(INSTANCE, type, (entity, ctx) -> {
                if (!(entity instanceof ISorcerer) && !(entity instanceof Player)) return null;

                return new JujutsuCapability((LivingEntity) entity);
            });
        }
    }
}
