package radon.jujutsu_kaisen.entity.ai.goal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.ability.curse_manipulation.util.CurseManipulationUtil;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.curse_manipulation.AbsorbedCurse;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.mimicry.IMimicryData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import radon.jujutsu_kaisen.item.CursedSpiritOrbItem;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;
import java.util.List;

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
