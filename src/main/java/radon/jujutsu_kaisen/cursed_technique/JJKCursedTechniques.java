package radon.jujutsu_kaisen.cursed_technique;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;

import javax.annotation.Nullable;

public class JJKCursedTechniques {
    public static ResourceKey<Registry<ICursedTechnique>> CURSED_TECHNIQUE_KEY = ResourceKey.createRegistryKey(new ResourceLocation(JujutsuKaisen.MOD_ID, "cursed_technique"));
    public static Registry<ICursedTechnique> CURSED_TECHNIQUE_REGISTRY = new RegistryBuilder<>(CURSED_TECHNIQUE_KEY).create();
    public static DeferredRegister<ICursedTechnique> CURSED_TECHNIQUES = DeferredRegister.create(CURSED_TECHNIQUE_REGISTRY, JujutsuKaisen.MOD_ID);

    public static DeferredHolder<ICursedTechnique, ICursedTechnique> SHOCKWAVE = CURSED_TECHNIQUES.register("shockwave", ShockwaveTechnique::new);
    public static DeferredHolder<ICursedTechnique, ICursedTechnique> SKY_STRIKE = CURSED_TECHNIQUES.register("sky_strike", SkyStrikeTechnique::new);
    public static DeferredHolder<ICursedTechnique, ICursedTechnique> SCISSORS = CURSED_TECHNIQUES.register("scissors", ScissorsTechnique::new);
    public static DeferredHolder<ICursedTechnique, ICursedTechnique> CURSE_MANIPULATION = CURSED_TECHNIQUES.register("curse_manipulation", CurseManipulationTechnique::new);
    public static DeferredHolder<ICursedTechnique, ICursedTechnique> LIMITLESS = CURSED_TECHNIQUES.register("limitless", LimitlessTechnique::new);
    public static DeferredHolder<ICursedTechnique, ICursedTechnique> SHRINE = CURSED_TECHNIQUES.register("shrine", Shrine::new);
    public static DeferredHolder<ICursedTechnique, ICursedTechnique> CURSED_SPEECH = CURSED_TECHNIQUES.register("cursed_speech", CursedSpeechTechnique::new);
    public static DeferredHolder<ICursedTechnique, ICursedTechnique> MIMICRY = CURSED_TECHNIQUES.register("mimicry", MimicryTechnique::new);
    public static DeferredHolder<ICursedTechnique, ICursedTechnique> DISASTER_FLAMES = CURSED_TECHNIQUES.register("disaster_flames", DisasterFlamesTechnique::new);
    public static DeferredHolder<ICursedTechnique, ICursedTechnique> DISASTER_TIDES = CURSED_TECHNIQUES.register("disaster_tides", DisasterTidesTechnique::new);
    public static DeferredHolder<ICursedTechnique, ICursedTechnique> DISASTER_PLANTS = CURSED_TECHNIQUES.register("disaster_plants", DisasterPlantsTechnique::new);
    public static DeferredHolder<ICursedTechnique, ICursedTechnique> IDLE_TRANSFIGURATION = CURSED_TECHNIQUES.register("idle_transfiguration", IdleTransfigurationTechnique::new);
    public static DeferredHolder<ICursedTechnique, ICursedTechnique> TEN_SHADOWS = CURSED_TECHNIQUES.register("ten_shadows", TenShadowsTechnique::new);
    public static DeferredHolder<ICursedTechnique, ICursedTechnique> BOOGIE_WOOGIE = CURSED_TECHNIQUES.register("boogie_woogie", BoogieWoogieTechnique::new);
    public static DeferredHolder<ICursedTechnique, ICursedTechnique> PROJECTION_SORCERY = CURSED_TECHNIQUES.register("projection_sorcery", ProjectionSorceryTechnique::new);

    public static ResourceLocation getKey(ICursedTechnique technique) {
        return CURSED_TECHNIQUE_REGISTRY.getKey(technique);
    }

    public static ICursedTechnique getValue(ResourceLocation key) {
        return CURSED_TECHNIQUE_REGISTRY.get(key);
    }

    @Nullable
    public static ICursedTechnique getTechnique(Ability ability) {
        for (DeferredHolder<ICursedTechnique, ? extends ICursedTechnique> entry : JJKCursedTechniques.CURSED_TECHNIQUES.getEntries()) {
            ICursedTechnique technique = entry.get();

            if (technique.getAbilities().contains(ability) || technique.getDomain() == ability) return technique;
        }
        return null;
    }
}
