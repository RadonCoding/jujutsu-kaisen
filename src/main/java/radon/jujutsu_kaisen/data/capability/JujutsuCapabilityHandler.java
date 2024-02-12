package radon.jujutsu_kaisen.data.capability;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.entity.base.ISorcerer;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class JujutsuCapabilityHandler {
    public static final EntityCapability<IJujutsuCapability, Void> INSTANCE = EntityCapability.create(new ResourceLocation(JujutsuKaisen.MOD_ID, "jujutsu"), IJujutsuCapability.class, void.class);

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
            if (!type.getBaseClass().isInstance(Player.class) && !type.getBaseClass().isInstance(ISorcerer.class)) continue;

            event.registerEntity(INSTANCE, type, (entity, ctx) -> new JujutsuCapability((LivingEntity) entity));
        }
    }
}
