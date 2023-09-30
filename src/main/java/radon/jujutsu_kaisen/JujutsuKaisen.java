package radon.jujutsu_kaisen;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.JJKBlockEntities;
import radon.jujutsu_kaisen.block.fluid.JJKFluidTypes;
import radon.jujutsu_kaisen.block.fluid.JJKFluids;
import radon.jujutsu_kaisen.client.gui.screen.AltarScreen;
import radon.jujutsu_kaisen.client.gui.screen.BountyScreen;
import radon.jujutsu_kaisen.client.gui.screen.VeilRodScreen;
import radon.jujutsu_kaisen.client.particle.JJKParticles;
import radon.jujutsu_kaisen.config.Config;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.item.JJKCreativeTabs;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.menu.JJKMenus;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.world.gen.biome.modifier.JJKBiomeModifiers;
import radon.jujutsu_kaisen.world.gen.loot.JJKLootModifiers;
import radon.jujutsu_kaisen.world.gen.processor.JJKProcessors;

@Mod(JujutsuKaisen.MOD_ID)
public class JujutsuKaisen {
    public static final String MOD_ID = "jujutsu_kaisen";

    public JujutsuKaisen() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        JJKAbilities.ABILITIES.register(bus);

        JJKEntities.ENTITIES.register(bus);

        JJKParticles.PARTICLES.register(bus);

        JJKMenus.MENUS.register(bus);

        JJKBlocks.BLOCKS.register(bus);
        JJKFluids.FLUIDS.register(bus);
        JJKFluidTypes.FLUID_TYPES.register(bus);

        JJKBlockEntities.BLOCK_ENTITIES.register(bus);

        JJKEffects.EFFECTS.register(bus);

        JJKItems.ITEMS.register(bus);

        JJKSounds.SOUNDS.register(bus);

        JJKProcessors.PROCESSORS.register(bus);
        JJKBiomeModifiers.BIOME_MODIFIERS.register(bus);
        JJKLootModifiers.LOOT_MODIFIERS.register(bus);

        JJKCreativeTabs.CREATIVE_MODE_TABS.register(bus);

        bus.addListener(JujutsuKaisen::onCommonSetup);
        bus.addListener(JujutsuKaisen::onClientSetup);
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        PacketHandler.register();
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(JJKMenus.ALTAR.get(), AltarScreen::new);
        MenuScreens.register(JJKMenus.VEIL_ROD.get(), VeilRodScreen::new);
        MenuScreens.register(JJKMenus.BOUNTY.get(), BountyScreen::new);

        ItemBlockRenderTypes.setRenderLayer(JJKFluids.FAKE_WATER_SOURCE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(JJKBlocks.SHINING_SEA_OF_FLOWERS_FLOOR.get(), RenderType.cutout());
    }
}
