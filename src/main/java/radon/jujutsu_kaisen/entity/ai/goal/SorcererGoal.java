package radon.jujutsu_kaisen.entity.ai.goal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.curse_manipulation.CurseManipulationDataHandler;
import radon.jujutsu_kaisen.capability.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.item.CursedSpiritOrbItem;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.CurseManipulationUtil;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.ArrayList;
import java.util.List;

public class SorcererGoal extends Goal {
    private static final int CHANGE_COPIED_TECHNIQUE_INTERVAL = 10 * 20;

    private final PathfinderMob mob;
    //private long lastCanUseCheck;

    public SorcererGoal(PathfinderMob mob) {
        this.mob = mob;
    }

    @Override
    public void tick() {
        List<Ability> abilities = JJKAbilities.getAbilities(this.mob);

        ISorcererData sorcererCap = this.mob.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        ICurseManipulationData curseManipulationCap = this.mob.getCapability(CurseManipulationDataHandler.INSTANCE).resolve().orElseThrow();

        if (sorcererCap.hasToggled(JJKAbilities.RIKA.get())) {
            if (sorcererCap.getCurrentCopied() == null || this.mob.tickCount % CHANGE_COPIED_TECHNIQUE_INTERVAL == 0) {
                List<ICursedTechnique> copied = new ArrayList<>(sorcererCap.getCopied());

                if (!copied.isEmpty()) {
                    sorcererCap.setCurrentCopied(copied.get(HelperMethods.RANDOM.nextInt(copied.size())));
                }
            }
        }

        if (JJKAbilities.hasTechnique(this.mob, JJKCursedTechniques.CURSE_MANIPULATION.get())) {
            LivingEntity target = this.mob.getTarget();

            if (target != null && HelperMethods.RANDOM.nextInt(5) == 0) {
                List<AbsorbedCurse> curses = curseManipulationCap.getCurses();

                if (target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                    ISorcererData targetCap = target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                    AbsorbedCurse closest = null;

                    for (AbsorbedCurse curse : curses) {
                        float diff = Math.abs(CurseManipulationUtil.getCurseExperience(curse) - targetCap.getExperience());

                        if (closest == null || diff < Math.abs(CurseManipulationUtil.getCurseExperience(closest) - targetCap.getExperience())) {
                            closest = curse;
                        }
                    }

                    if (closest != null) {
                        CurseManipulationUtil.summonCurse(this.mob, closest, true);
                    }
                } else if (!curses.isEmpty()) {
                    CurseManipulationUtil.summonCurse(this.mob, HelperMethods.RANDOM.nextInt(curses.size()), true);
                }
            }

            ItemStack stack = this.mob.getItemInHand(InteractionHand.MAIN_HAND);

            if (stack.is(JJKItems.CURSED_SPIRIT_ORB.get())) {
                this.mob.playSound(this.mob.getEatingSound(stack), 1.0F, 1.0F + (HelperMethods.RANDOM.nextFloat() - HelperMethods.RANDOM.nextFloat()) * 0.4F);
                curseManipulationCap.addCurse(CursedSpiritOrbItem.getAbsorbed(stack));
                this.mob.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
        }

        for (Ability ability : abilities) {
            boolean success = ability.shouldTrigger(this.mob, this.mob.getTarget());

            if (ability.getActivationType(this.mob) == Ability.ActivationType.TOGGLED) {
                if (success) {
                    if (!JJKAbilities.hasToggled(this.mob, ability)) {
                        AbilityHandler.trigger(this.mob, ability);
                    }
                } else if (JJKAbilities.hasToggled(this.mob, ability)) {
                    AbilityHandler.untrigger(this.mob, ability);
                }
            } else if (ability.getActivationType(this.mob) == Ability.ActivationType.CHANNELED) {
                if (success) {
                    if (!JJKAbilities.isChanneling(this.mob, ability)) {
                        AbilityHandler.trigger(this.mob, ability);
                    }
                } else if (JJKAbilities.isChanneling(this.mob, ability)) {
                    AbilityHandler.untrigger(this.mob, ability);
                }
            } else if (success) {
                AbilityHandler.trigger(this.mob, ability);
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

        /*long i = this.mob.level().getGameTime();

        if (i - this.lastCanUseCheck < 20L) {
            return false;
        } else {
            this.lastCanUseCheck = i;
            return true;
        }*/
    }
}
