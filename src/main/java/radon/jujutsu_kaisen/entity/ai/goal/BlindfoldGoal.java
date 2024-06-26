package radon.jujutsu_kaisen.entity.ai.goal;


import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.item.registry.JJKItems;

public class BlindfoldGoal extends Goal {
    private final PathfinderMob mob;

    private boolean wasWearing;

    public BlindfoldGoal(PathfinderMob mob) {
        this.mob = mob;
    }

    @Override
    public void tick() {
        if (this.mob.getItemBySlot(EquipmentSlot.HEAD).is(JJKItems.BLINDFOLD.get())) {
            this.wasWearing = true;
        }

        LivingEntity target = this.mob.getTarget();

        boolean wear = false;

        if (target == null) {
            wear = true;
        } else {
            IJujutsuCapability cap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap != null) {
                ISorcererData data = cap.getSorcererData();

                if (data.getExperience() >= SorcererGrade.SPECIAL_GRADE.getRequiredExperience()) {
                    wear = false;
                }
            }
        }

        if (this.mob.getItemBySlot(EquipmentSlot.HEAD).is(JJKItems.BLINDFOLD.get())) {
            if (!wear) {
                this.mob.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
            }
        } else if (this.wasWearing) {
            if (wear) {
                this.mob.setItemSlot(EquipmentSlot.HEAD, new ItemStack(JJKItems.BLINDFOLD.get()));
            }
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        return true;
    }
}
