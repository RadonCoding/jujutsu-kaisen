package radon.jujutsu_kaisen;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.block.JJKBlockEntities;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.client.layer.overlay.JJKOverlays;
import radon.jujutsu_kaisen.client.particle.JJKParticles;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.world.gen.processor.JJKProcessors;

@Mod(JujutsuKaisen.MOD_ID)
public class JujutsuKaisen {
    public static final String MOD_ID = "jujutsu_kaisen";

    public JujutsuKaisen() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        JJKAbilities.ABILITIES.register(bus);
        JJKOverlays.OVERLAYS.register(bus);

        JJKEntities.ENTITIES.register(bus);
        JJKParticles.PARTICLES.register(bus);
        JJKBlocks.BLOCKS.register(bus);
        JJKBlockEntities.BLOCK_ENTITIES.register(bus);
        JJKEffects.EFFECTS.register(bus);
        JJKItems.ITEMS.register(bus);
        JJKSounds.SOUNDS.register(bus);
        JJKProcessors.PROCESSORS.register(bus);

        bus.addListener(JujutsuKaisen::onCommonSetup);
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        PacketHandler.register();
    }
}
