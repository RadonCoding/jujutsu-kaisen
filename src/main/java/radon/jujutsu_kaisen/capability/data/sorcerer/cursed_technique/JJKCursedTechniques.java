package radon.jujutsu_kaisen.capability.data.sorcerer.cursed_technique;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.cursed_technique.base.ICursedTechnique;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Supplier;

public class JJKCursedTechniques {
    public static DeferredRegister<ICursedTechnique> CURSED_TECHNIQUES = DeferredRegister.create(
            new ResourceLocation(JujutsuKaisen.MOD_ID, "cursed_technique"), JujutsuKaisen.MOD_ID);
    public static Supplier<IForgeRegistry<ICursedTechnique>> CURSED_TECHNIQUE_REGISTRY =
            CURSED_TECHNIQUES.makeRegistry(RegistryBuilder::new);

    public static RegistryObject<ICursedTechnique> SHOCKWAVE = CURSED_TECHNIQUES.register("shockwave", ShockwaveTechnique::new);
    public static RegistryObject<ICursedTechnique> SKY_STRIKE = CURSED_TECHNIQUES.register("sky_strike", SkyStrikeTechnique::new);
    public static RegistryObject<ICursedTechnique> CURSE_MANIPULATION = CURSED_TECHNIQUES.register("curse_manipulation", CurseManipulationTechnique::new);
    public static RegistryObject<ICursedTechnique> LIMITLESS = CURSED_TECHNIQUES.register("limitless", LimitlessTechnique::new);
    public static RegistryObject<ICursedTechnique> DISMANTLE_AND_CLEAVE = CURSED_TECHNIQUES.register("dismantle_and_cleave", DismantleAndCleaveTechnique::new);
    public static RegistryObject<ICursedTechnique> CURSED_SPEECH = CURSED_TECHNIQUES.register("cursed_speech", CursedSpeechTechnique::new);
    public static RegistryObject<ICursedTechnique> MIMICRY = CURSED_TECHNIQUES.register("mimicry", MimicryTechnique::new);
    public static RegistryObject<ICursedTechnique> DISASTER_FLAMES = CURSED_TECHNIQUES.register("disaster_flames", DisasterFlamesTechnique::new);
    public static RegistryObject<ICursedTechnique> DISASTER_TIDES = CURSED_TECHNIQUES.register("disaster_tides", DisasterTidesTechnique::new);
    public static RegistryObject<ICursedTechnique> DISASTER_PLANTS = CURSED_TECHNIQUES.register("disaster_plants", DisasterPlantsTechnique::new);
    public static RegistryObject<ICursedTechnique> IDLE_TRANSFIGURATION = CURSED_TECHNIQUES.register("idle_transfiguration", IdleTransfigurationTechnique::new);
    public static RegistryObject<ICursedTechnique> TEN_SHADOWS = CURSED_TECHNIQUES.register("ten_shadows", TenShadowsTechnique::new);
    public static RegistryObject<ICursedTechnique> BOOGIE_WOOGIE = CURSED_TECHNIQUES.register("boogie_woogie", BoogieWoogieTechnique::new);
    public static RegistryObject<ICursedTechnique> PROJECTION_SORCERY = CURSED_TECHNIQUES.register("projection_sorcery", ProjectionSorceryTechnique::new);

    public static ResourceLocation getKey(ICursedTechnique technique) {
        return CURSED_TECHNIQUE_REGISTRY.get().getKey(technique);
    }

    public static ICursedTechnique getValue(ResourceLocation key) {
        return CURSED_TECHNIQUE_REGISTRY.get().getValue(key);
    }

    public static Set<ICursedTechnique> getTechniques(LivingEntity owner) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return Set.of();
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.getTechniques();
    }

    @Nullable
    public static ICursedTechnique getTechnique(Ability ability) {
        for (RegistryObject<ICursedTechnique> entry : JJKCursedTechniques.CURSED_TECHNIQUES.getEntries()) {
            ICursedTechnique technique = entry.get();

            if (technique.getAbilities().contains(ability)) return technique;
        }
        return null;
    }
}
