package radon.jujutsu_kaisen;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.binding_vow.JJKBindingVows;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.JJKBlockEntities;
import radon.jujutsu_kaisen.block.fluid.JJKFluidTypes;
import radon.jujutsu_kaisen.block.fluid.JJKFluids;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.client.particle.JJKParticles;
import radon.jujutsu_kaisen.client.render.item.armor.InventoryCurseRenderer;
import radon.jujutsu_kaisen.command.argument.JJKCommandArgumentTypes;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.JJKEntityDataSerializers;
import radon.jujutsu_kaisen.item.JJKCreativeTabs;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.menu.JJKMenus;
import radon.jujutsu_kaisen.pact.JJKPacts;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.world.gen.feature.JJKFeatures;
import radon.jujutsu_kaisen.world.gen.loot.JJKLootModifiers;
import radon.jujutsu_kaisen.world.gen.processor.JJKProcessors;
import radon.jujutsu_kaisen.world.gen.structure.JJKStructureTypes;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod(JujutsuKaisen.MOD_ID)
public class JujutsuKaisen {
    public static final String MOD_ID = "jujutsu_kaisen";

    public JujutsuKaisen(IEventBus bus) {
        ModLoadingContext ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.SERVER, ConfigHolder.SERVER_SPEC);
        ctx.registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);

        JJKAbilities.ABILITIES.register(bus);
        JJKCursedTechniques.CURSED_TECHNIQUES.register(bus);
        JJKBindingVows.BINDING_VOWS.register(bus);
        JJKPacts.PACTS.register(bus);

        JJKEntities.ENTITIES.register(bus);
        JJKEntityDataSerializers.ENTITY_DATA_SERIALIZERS.register(bus);

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
        JJKLootModifiers.LOOT_MODIFIERS.register(bus);
        JJKFeatures.FEATURES.register(bus);
        JJKStructureTypes.STRUCTURE_TYPES.register(bus);

        JJKCreativeTabs.CREATIVE_MODE_TABS.register(bus);

        JJKCommandArgumentTypes.COMMAND_ARGUMENT_TYPES.register(bus);

        JJKAttachmentTypes.ATTACHMENT_TYPES.register(bus);

        bus.addListener(JujutsuKaisen::onClientSetup);
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
         CuriosRendererRegistry.register(JJKItems.INVENTORY_CURSE.get(), InventoryCurseRenderer::new);
    }
}
