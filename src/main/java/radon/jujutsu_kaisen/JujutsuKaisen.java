package radon.jujutsu_kaisen;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import radon.jujutsu_kaisen.ability.JujutsuAbilities;
import radon.jujutsu_kaisen.block.JujutsuBlocks;
import radon.jujutsu_kaisen.client.particle.JujutsuParticles;
import radon.jujutsu_kaisen.effect.JujutsuEffects;
import radon.jujutsu_kaisen.entity.JujutsuEntities;
import radon.jujutsu_kaisen.network.PacketHandler;

@Mod(JujutsuKaisen.MOD_ID)
public class JujutsuKaisen {
    public static final String MOD_ID = "jujutsu_kaisen";

    public JujutsuKaisen() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        JujutsuAbilities.ABILITIES.register(bus);
        JujutsuEntities.ENTITIES.register(bus);
        JujutsuParticles.PARTICLES.register(bus);
        JujutsuBlocks.BLOCKS.register(bus);
        JujutsuEffects.EFFECTS.register(bus);

        bus.addListener(JujutsuKaisen::onCommonSetup);
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        PacketHandler.register();
    }
}
