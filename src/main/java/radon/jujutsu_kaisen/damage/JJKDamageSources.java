package radon.jujutsu_kaisen.damage;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.*;

import javax.annotation.Nullable;

public class JJKDamageSources {
    public static final ResourceKey<DamageType> JUJUTSU = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JujutsuKaisen.MOD_ID, "jujutsu"));
    public static final ResourceKey<DamageType> WORLD_SLASH = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JujutsuKaisen.MOD_ID, "world_slash"));
    public static final ResourceKey<DamageType> SOUL = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JujutsuKaisen.MOD_ID, "soul"));
    public static final ResourceKey<DamageType> SELF = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JujutsuKaisen.MOD_ID, "self"));
    public static final ResourceKey<DamageType> SPLIT_SOUL_KATANA = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JujutsuKaisen.MOD_ID, "split_soul_katana"));

    public static JujutsuDamageSource jujutsuAttack(LivingEntity source, @Nullable Ability ability) {
        RegistryAccess registry = source.registryAccess();
        Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
        return new JujutsuDamageSource(types.getHolderOrThrow(JUJUTSU), source, ability);
    }

    public static JujutsuDamageSource indirectJujutsuAttack(Entity source, @Nullable LivingEntity indirect, @Nullable Ability ability) {
        RegistryAccess registry = source.registryAccess();
        Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
        return new JujutsuDamageSource(types.getHolderOrThrow(JUJUTSU), source, indirect, ability);
    }

    public static DamageSource worldSlash(Entity source, @Nullable LivingEntity indirect) {
        RegistryAccess registry = source.registryAccess();
        Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
        return new DamageSource(types.getHolderOrThrow(WORLD_SLASH), source, indirect);
    }

    public static DamageSource soulAttack(LivingEntity source) {
        RegistryAccess registry = source.registryAccess();
        Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
        return new DamageSource(types.getHolderOrThrow(SOUL), source);
    }

    public static DamageSource self(LivingEntity source) {
        RegistryAccess registry = source.registryAccess();
        Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
        return new DamageSource(types.getHolderOrThrow(SELF), source);
    }

    public static DamageSource splitSoulKatanaAttack(LivingEntity source) {
        RegistryAccess registry = source.registryAccess();
        Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
        return new DamageSource(types.getHolderOrThrow(SPLIT_SOUL_KATANA), source);
    }

    public static class JujutsuDamageSource extends DamageSource {
        private final @Nullable Ability ability;

        public JujutsuDamageSource(Holder.Reference<DamageType> holderOrThrow, Entity source, @Nullable LivingEntity indirect, @Nullable Ability ability) {
            super(holderOrThrow, source, indirect);

            this.ability = ability;
        }

        public JujutsuDamageSource(Holder.Reference<DamageType> holderOrThrow, @Nullable LivingEntity mob, @Nullable Ability ability) {
            super(holderOrThrow, mob);

            this.ability = ability;
        }

        @Nullable
        public Ability getAbility() {
            return this.ability;
        }
    }
}
