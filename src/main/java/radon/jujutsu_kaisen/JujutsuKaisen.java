package radon.jujutsu_kaisen;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import radon.jujutsu_kaisen.ability.JujutsuAbilities;
import radon.jujutsu_kaisen.client.JujutsuParticles;
import radon.jujutsu_kaisen.entity.JujutsuEntities;
import radon.jujutsu_kaisen.network.PacketHandler;

@Mod(JujutsuKaisen.MODID)
public class JujutsuKaisen {
    public static final String MODID = "jujutsu_kaisen";

    public JujutsuKaisen() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        JujutsuAbilities.ABILITIES.register(bus);
        JujutsuEntities.ENTITIES.register(bus);
        JujutsuParticles.PARTICLES.register(bus);

        bus.addListener(JujutsuKaisen::onCommonSetup);
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        PacketHandler.register();
    }
}
