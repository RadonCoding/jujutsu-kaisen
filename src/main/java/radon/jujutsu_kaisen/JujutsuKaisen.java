package radon.jujutsu_kaisen;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.binding_vow.JJKBindingVows;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.JJKBlockEntities;
import radon.jujutsu_kaisen.block.fluid.JJKFluidTypes;
import radon.jujutsu_kaisen.block.fluid.JJKFluids;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.client.particle.registry.JJKParticles;
import radon.jujutsu_kaisen.command.argument.JJKCommandArgumentTypes;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;
import radon.jujutsu_kaisen.effect.registry.JJKEffects;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.registry.JJKEntityDataSerializers;
import radon.jujutsu_kaisen.item.armor.registry.JJKArmorMaterials;
import radon.jujutsu_kaisen.item.registry.JJKDataComponentTypes;
import radon.jujutsu_kaisen.tab.JJKCreativeTabs;
import radon.jujutsu_kaisen.item.registry.JJKItems;
import radon.jujutsu_kaisen.menu.JJKMenus;
import radon.jujutsu_kaisen.pact.JJKPacts;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.world.gen.feature.JJKFeatures;
import radon.jujutsu_kaisen.world.gen.processor.JJKProcessors;
import radon.jujutsu_kaisen.world.gen.structure.JJKStructureTypes;

@Mod(JujutsuKaisen.MOD_ID)
public class JujutsuKaisen {
    public static final String MOD_ID = "jujutsu_kaisen";

    public JujutsuKaisen(IEventBus bus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, ConfigHolder.SERVER_SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);

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
        JJKArmorMaterials.ARMOR_MATERIALS.register(bus);
        JJKDataComponentTypes.DATA_COMPONENT_TYPES.register(bus);

        JJKSounds.SOUNDS.register(bus);

        JJKProcessors.PROCESSORS.register(bus);
        JJKFeatures.FEATURES.register(bus);
        JJKStructureTypes.STRUCTURE_TYPES.register(bus);

        JJKCreativeTabs.CREATIVE_MODE_TABS.register(bus);

        JJKCommandArgumentTypes.COMMAND_ARGUMENT_TYPES.register(bus);

        JJKAttachmentTypes.ATTACHMENT_TYPES.register(bus);
    }
}
