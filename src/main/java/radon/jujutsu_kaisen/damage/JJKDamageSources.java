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
import radon.jujutsu_kaisen.ability.Ability;

import javax.annotation.Nullable;

public class JJKDamageSources {
    public static final ResourceKey<DamageType> JUJUTSU = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JujutsuKaisen.MOD_ID, "jujutsu"));
    public static final ResourceKey<DamageType> JUJUTSU_BYPASS = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JujutsuKaisen.MOD_ID, "jujutsu_bypass"));
    public static final ResourceKey<DamageType> SOUL = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JujutsuKaisen.MOD_ID, "soul"));

    public static DamageSource soulAttack(LivingEntity source) {
        RegistryAccess registry = source.level.registryAccess();
        Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
        return new DamageSource(types.getHolderOrThrow(SOUL), source);
    }

    public static JujutsuDamageSource jujutsuAttack(LivingEntity source, @Nullable Ability ability) {
        RegistryAccess registry = source.level.registryAccess();
        Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
        return new JujutsuDamageSource(types.getHolderOrThrow(JUJUTSU), source, ability);
    }

    public static JujutsuDamageSource jujutsuBypassAttack(LivingEntity source, @Nullable Ability ability) {
        RegistryAccess registry = source.level.registryAccess();
        Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
        return new JujutsuDamageSource(types.getHolderOrThrow(JUJUTSU_BYPASS), source, ability);
    }

    public static JujutsuDamageSource indirectJujutsuAttack(Entity source, @Nullable LivingEntity indirect, @Nullable Ability ability) {
        RegistryAccess registry = source.level.registryAccess();
        Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
        return new JujutsuDamageSource(types.getHolderOrThrow(JUJUTSU), source, indirect, ability);
    }

    public static class JujutsuDamageSource extends DamageSource {
        private final @Nullable Ability ability;

        public JujutsuDamageSource(Holder<DamageType> pType, @Nullable Ability ability) {
            super(pType);

            this.ability = ability;
        }

        public JujutsuDamageSource(Holder.Reference<DamageType> holderOrThrow, Entity source, LivingEntity indirect, @Nullable Ability ability) {
            super(holderOrThrow, source, indirect);

            this.ability = ability;
        }

        public JujutsuDamageSource(Holder.Reference<DamageType> holderOrThrow, LivingEntity mob, @Nullable Ability ability) {
            super(holderOrThrow, mob);

            this.ability = ability;
        }

        public @Nullable Ability getAbility() {
            return this.ability;
        }
    }
}
