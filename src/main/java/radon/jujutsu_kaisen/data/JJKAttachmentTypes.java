package radon.jujutsu_kaisen.data;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.curse_manipulation.CurseManipulationData;
import radon.jujutsu_kaisen.data.curse_manipulation.CurseManipulationDataProvider;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.projection_sorcery.IProjectionSorceryData;
import radon.jujutsu_kaisen.data.projection_sorcery.ProjectionSorcereryDataProvider;
import radon.jujutsu_kaisen.data.projection_sorcery.ProjectionSorceryData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.SorcererData;
import radon.jujutsu_kaisen.data.sorcerer.SorcererDataProvider;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.data.ten_shadows.TenShadowsData;
import radon.jujutsu_kaisen.data.ten_shadows.TenShadowsDataProvider;

import java.util.Objects;
import java.util.function.Supplier;

public class JJKAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, JujutsuKaisen.MOD_ID);

    public static final Supplier<AttachmentType<ISorcererData>> SORCERER = ATTACHMENT_TYPES.register("sorcerer",
            AttachmentType.<ISorcererData>builder(holder -> new SorcererData((LivingEntity) holder)).serialize(new SorcererDataProvider.Serializer()).copyOnDeath()::build);
    public static final Supplier<AttachmentType<ITenShadowsData>> TEN_SHADOWS = ATTACHMENT_TYPES.register("ten_shadows",
            AttachmentType.<ITenShadowsData>builder(holder -> new TenShadowsData((LivingEntity) holder)).serialize(new TenShadowsDataProvider.Serializer()).copyOnDeath()::build);
    public static final Supplier<AttachmentType<IProjectionSorceryData>> PROJECTION_SORCERY = ATTACHMENT_TYPES.register("projection_sorcery",
            AttachmentType.<IProjectionSorceryData>builder(holder -> new ProjectionSorceryData((LivingEntity) holder)).serialize(new ProjectionSorcereryDataProvider.Serializer()).copyOnDeath()::build);
    public static final Supplier<AttachmentType<ICurseManipulationData>> CURSE_MANIPULATION = ATTACHMENT_TYPES.register("curse_manipulation",
            AttachmentType.<ICurseManipulationData>builder(holder -> new CurseManipulationData((LivingEntity) holder)).serialize(new CurseManipulationDataProvider.Serializer()).copyOnDeath()::build);
}
