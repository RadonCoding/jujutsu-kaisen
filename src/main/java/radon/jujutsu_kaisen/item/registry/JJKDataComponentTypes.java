package radon.jujutsu_kaisen.item.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.cursed_technique.ICursedTechnique;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.curse_manipulation.AbsorbedCurse;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.imbuement.Imbuement;
import radon.jujutsu_kaisen.item.armor.InventoryCurseItems;

import java.util.UUID;

public class JJKDataComponentTypes {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, JujutsuKaisen.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SorcererGrade>> SORCERER_GRADE = DATA_COMPONENT_TYPES.register("sorcerer_grade", () ->
            new DataComponentType.Builder<SorcererGrade>()
                    .persistent(StringRepresentable.fromEnum(SorcererGrade::values))
                    .networkSynchronized(NeoForgeStreamCodecs.enumCodec(SorcererGrade.class))
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ICursedTechnique>> CURSED_TECHNIQUE = DATA_COMPONENT_TYPES.register("cursed_technique", () ->
            new DataComponentType.Builder<ICursedTechnique>()
                    .persistent(JJKCursedTechniques.CURSED_TECHNIQUE_REGISTRY.byNameCodec())
                    .networkSynchronized(ByteBufCodecs.registry(JJKCursedTechniques.CURSED_TECHNIQUE_KEY))
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AbsorbedCurse>> ABSORBED_CURSE = DATA_COMPONENT_TYPES.register("absorbed_curse", () ->
            new DataComponentType.Builder<AbsorbedCurse>()
                    .persistent(AbsorbedCurse.CODEC)
                    .networkSynchronized(AbsorbedCurse.STREAM_CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> ENTITY_UUID = DATA_COMPONENT_TYPES.register("entity_uuid", () ->
            new DataComponentType.Builder<UUID>()
                    .persistent(UUIDUtil.CODEC)
                    .networkSynchronized(UUIDUtil.STREAM_CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> CURSED_ENERGY = DATA_COMPONENT_TYPES.register("cursed_energy", () ->
            new DataComponentType.Builder<Float>()
                    .persistent(Codec.FLOAT)
                    .networkSynchronized(ByteBufCodecs.FLOAT)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_FULL_SOUL = DATA_COMPONENT_TYPES.register("is_full_soul", () ->
            new DataComponentType.Builder<Boolean>()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Imbuement>> IMBUEMENTS = DATA_COMPONENT_TYPES.register("imbuements", () ->
            new DataComponentType.Builder<Imbuement>()
                    .persistent(Imbuement.CODEC)
                    .networkSynchronized(Imbuement.STREAM_CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<InventoryCurseItems>> INVENTORY_CURSE_ITEMS = DATA_COMPONENT_TYPES.register("inventory_curse_items", () ->
            new DataComponentType.Builder<InventoryCurseItems>()
                    .persistent(InventoryCurseItems.CODEC)
                    .networkSynchronized(InventoryCurseItems.STREAM_CODEC)
                    .build());
}
