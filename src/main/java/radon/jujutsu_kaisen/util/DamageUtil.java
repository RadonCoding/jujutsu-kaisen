package radon.jujutsu_kaisen.util;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.curse.KuchisakeOnnaEntity;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.entity.projectile.WorldSlashProjectile;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.item.JJKItems;

import java.util.Optional;
import java.util.UUID;

public class DamageUtil {
    public static boolean isBlockable(LivingEntity target, Projectile projectile) {
        if (projectile instanceof WorldSlashProjectile) return false;
        if (projectile.getOwner() == target) return false;

        if (projectile instanceof ThrownChainProjectile chain) {
            if (chain.getStack().is(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get())) return false;
        }

        for (KuchisakeOnnaEntity curse : target.level().getEntitiesOfClass(KuchisakeOnnaEntity.class, AABB.ofSize(target.position(),
                KuchisakeOnnaEntity.RANGE, KuchisakeOnnaEntity.RANGE, KuchisakeOnnaEntity.RANGE))) {
            Optional<UUID> identifier = curse.getCurrent();
            if (identifier.isEmpty()) continue;
            if (identifier.get() == target.getUUID() && projectile.getOwner() == curse) return false;
        }

        if (projectile instanceof JujutsuProjectile jujutsu) {
            return !jujutsu.isDomain();
        }
        return true;
    }

    public static boolean isBlockable(LivingEntity target, DamageSource source) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || source.is(DamageTypes.STARVE) || source.is(JJKDamageSources.SOUL)) return false;

        if (source.getDirectEntity() instanceof Projectile projectile && !isBlockable(target, projectile)) return false;

        if (source.getDirectEntity() instanceof DomainExpansionEntity) return false;

        return source.getEntity() != target;
    }

    public static boolean isMelee(DamageSource source) {
        return !source.isIndirect() && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK) || source.is(JJKDamageSources.SPLIT_SOUL_KATANA)) ||
                source instanceof JJKDamageSources.JujutsuDamageSource cap && cap.getAbility() != null && cap.getAbility().isMelee();
    }

}
