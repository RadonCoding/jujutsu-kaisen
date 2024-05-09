package radon.jujutsu_kaisen.ability.idle_transfiguration;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Transformation;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.item.registry.JJKItems;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.UUID;

public class ArmBlade extends Transformation {
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("2b553d20-2aab-46c3-9199-4c6500948aaa");

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData data = cap.getAbilityData();

        if (data.hasToggled(this)) {
            return HelperMethods.RANDOM.nextInt(20) != 0;
        }
        return HelperMethods.RANDOM.nextInt(5) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.2F;
    }

    @Override
    public boolean isReplacement() {
        return false;
    }

    @Override
    public Item getItem() {
        return JJKItems.ARM_BLADE.get();
    }

    @Override
    public Part getBodyPart() {
        return Part.RIGHT_ARM;
    }

    @Override
    public void onRightClick(LivingEntity owner) {

    }

    @Override
    public void applyModifiers(LivingEntity owner) {
        EntityUtil.applyModifier(owner, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID, "Attack damage", 4.0D, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public void removeModifiers(LivingEntity owner) {
        EntityUtil.removeModifier(owner, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID);
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public float getSlimTranslation() {
        return 0.0625F;
    }
}