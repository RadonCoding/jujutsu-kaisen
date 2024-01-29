package radon.jujutsu_kaisen.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.effect.JJKEffects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract void remove(Entity.RemovalReason pReason);

    @Shadow public abstract boolean hasEffect(MobEffect pEffect);

    @Inject(method = "isFallFlying", at = @At("HEAD"), cancellable = true)
    public void isFallFlying(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.level().isClientSide) {
            ClientVisualHandler.ClientData data = ClientVisualHandler.get((LivingEntity) (Object) this);

            if (data == null) return;

            for (Ability ability : data.toggled) {
                if (!(ability instanceof ITransformation transformation)) continue;
                if (!transformation.getItem().canElytraFly(transformation.getItem().getDefaultInstance(), entity))
                    continue;
                cir.setReturnValue(true);
            }
        } else {
            if (!entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

            ISorcererData cap = entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            for (Ability ability : cap.getToggled()) {
                if (!(ability instanceof ITransformation transformation)) continue;
                if (!transformation.getItem().canElytraFly(transformation.getItem().getDefaultInstance(), entity))
                    continue;
                cir.setReturnValue(true);
            }
        }
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFriction(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F"))
    public float travel(BlockState instance, LevelReader levelReader, BlockPos blockPos, Entity entity) {
        if (!(entity instanceof LivingEntity living) || !JJKAbilities.hasToggled(living, JJKAbilities.DISMANTLE_SKATING.get()) || !instance.getFluidState().isEmpty()) return instance.getFriction(levelReader, blockPos, entity);
        return 1.0989F - 0.02F;
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;travel(Lnet/minecraft/world/phys/Vec3;)V"))
    public void travel(LivingEntity instance, Vec3 f4) {
        if (this.hasEffect(JJKEffects.STUN.get())) {
            instance.travel(Vec3.ZERO);
            return;
        }
        instance.travel(f4);
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    public void jumpFromGround(CallbackInfo ci) {
        if (this.hasEffect(JJKEffects.STUN.get())) ci.cancel();
    }

    @Inject(method = "jumpInLiquid", at = @At("HEAD"), cancellable = true)
    public void jumpInLiquid(CallbackInfo ci) {
        if (this.hasEffect(JJKEffects.STUN.get())) ci.cancel();
    }
}
