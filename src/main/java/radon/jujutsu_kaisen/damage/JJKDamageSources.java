package radon.jujutsu_kaisen.damage;

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

import javax.annotation.Nullable;

public class JJKDamageSources {
    public static final ResourceKey<DamageType> JUJUTSU = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JujutsuKaisen.MOD_ID, "jujutsu"));
    public static final ResourceKey<DamageType> JUJUTSU_PROJECTILE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JujutsuKaisen.MOD_ID, "jujutsu_projectile"));

    public static DamageSource jujutsuAttack(LivingEntity mob) {
        RegistryAccess registry = mob.level.registryAccess();
        Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
        return new DamageSource(types.getHolderOrThrow(JUJUTSU), mob);
    }

    public static DamageSource indirectJujutsuAttack(Entity source, @Nullable LivingEntity indirect) {
        RegistryAccess registry = source.level.registryAccess();
        Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
        return new DamageSource(types.getHolderOrThrow(JUJUTSU_PROJECTILE), source, indirect);
    }
}
