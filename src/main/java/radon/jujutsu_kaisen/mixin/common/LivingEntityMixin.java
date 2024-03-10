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
import radon.jujutsu_kaisen.ability.base.IFlight;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
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
            ClientVisualHandler.ClientData client = ClientVisualHandler.get((LivingEntity) (Object) this);

            if (client == null) return;

            for (Ability ability : client.toggled) {
                if (!(ability instanceof IFlight flight)) continue;
                if (!flight.canFly(entity)) continue;
                cir.setReturnValue(true);
            }

            if (!(client.channeled instanceof IFlight flight)) return;

            if (!flight.canFly(entity)) return;

            cir.setReturnValue(true);
        } else {
            IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            for (Ability ability : data.getToggled()) {
                if (!(ability instanceof IFlight flight)) continue;
                if (!flight.canFly(entity)) continue;
                cir.setReturnValue(true);
            }

            Ability channeled = data.getChanneled();

            if (!(channeled instanceof IFlight flight)) return;

            if (!flight.canFly(entity)) return;

            cir.setReturnValue(true);
        }
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFriction(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F"))
    public float travel(BlockState instance, LevelReader levelReader, BlockPos blockPos, Entity entity) {
        IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap != null) {
            IAbilityData data = cap.getAbilityData();

            if (data.hasToggled(JJKAbilities.DISMANTLE_SKATING.get()) && instance.getFluidState().isEmpty()) {
                return 1.0989F - 0.02F;
            }
        }
        return instance.getFriction(levelReader, blockPos, entity);
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
