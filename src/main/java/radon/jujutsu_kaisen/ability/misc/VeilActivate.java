package radon.jujutsu_kaisen.ability.misc;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.IAttack;
import radon.jujutsu_kaisen.ability.IChanneled;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.ICharged;
import radon.jujutsu_kaisen.ability.IDomainAttack;
import radon.jujutsu_kaisen.ability.IDurationable;
import radon.jujutsu_kaisen.ability.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.IToggled;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;
import radon.jujutsu_kaisen.entity.VeilEntity;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.List;

public class VeilActivate extends Ability {
    public static final double RANGE = 16.0D;
    private static final int RADIUS = 64;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel level)) return;

        for (ServerPlayer player : level.players()) {
            if (owner instanceof Mob && player.distanceTo(owner) > owner.getAttributeValue(Attributes.FOLLOW_RANGE)) continue;

            player.sendSystemMessage(Component.translatable(String.format("chat.%s.veil", JujutsuKaisen.MOD_ID), owner.getName().getString()));
        }

        if (RotationUtil.getLookAtHit(owner, RANGE) instanceof BlockHitResult blockHit) {
            BlockPos pos = blockHit.getBlockPos();

            if (owner.level().getBlockEntity(pos) instanceof VeilRodBlockEntity be) {
                if (be.getOwnerUUID() != null && be.getOwnerUUID().equals(owner.getUUID())) {
                    VeilEntity veil = new VeilEntity(owner, pos.getCenter(), be.getRadius(), be.getModifiers(), pos);
                    owner.level().addFreshEntity(veil);
                    return;
                }
            }
        }

        Vec3 start = owner.getEyePosition();
        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        Vec3 end = start.add(look.scale(RANGE));
        HitResult result = RotationUtil.getHitResult(owner, start, end);

        Vec3 pos = result.getType() == HitResult.Type.MISS ? end : result.getLocation();

        VeilEntity veil = new VeilEntity(owner, pos, RADIUS, List.of());
        owner.level().addFreshEntity(veil);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.DOMAIN;
    }
}
