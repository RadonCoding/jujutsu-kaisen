package radon.jujutsu_kaisen.entity.ai.goal;


import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.curse_manipulation.util.CurseManipulationUtil;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.chant.ServerChantHandler;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.chant.IChantData;
import radon.jujutsu_kaisen.data.curse_manipulation.AbsorbedCurse;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.mimicry.IMimicryData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import radon.jujutsu_kaisen.item.registry.JJKDataComponentTypes;
import radon.jujutsu_kaisen.item.registry.JJKItems;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.SorcererUtil;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class SorcererGoal extends Goal {
    private static final int CHANGE_TECHNIQUE_INTERVAL = 10 * 20;

    private final PathfinderMob mob;
    //private long lastCanUseCheck;

    @Nullable
    private Ability chanting;

    private final Queue<String> chants = new ArrayDeque<>();

    private long nextChantTime;

    public SorcererGoal(PathfinderMob mob) {
        this.mob = mob;
    }

    private void trigger(Ability ability) {
        IJujutsuCapability cap = this.mob.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData sorcererData = cap.getSorcererData();
        IChantData chantData = cap.getChantData();

        if (this.chanting == null && ability.isChantable() && HelperMethods.RANDOM.nextInt(Math.max(1, (int) (50 * sorcererData.getMaximumOutput()))) == 0) {
            List<String> chants = new ArrayList<>(chantData.getFirstChants(ability));

            if (!chants.isEmpty()) {
                for (int i = 0; i < HelperMethods.RANDOM.nextInt(chants.size()); i++) {
                    this.chants.add(chants.get(i));
                }

                this.chanting = ability;
                this.nextChantTime = 0;
                return;
            }
        }
        AbilityHandler.trigger(this.mob, ability);
    }

    @Override
    public void tick() {
        List<Ability> abilities = JJKAbilities.getAbilities(this.mob);

        IJujutsuCapability ownerCap = this.mob.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (ownerCap == null) return;

        ISorcererData ownerSorcererData = ownerCap.getSorcererData();
        IAbilityData ownerAbilityData = ownerCap.getAbilityData();
        ICurseManipulationData ownerCurseManipulationData = ownerCap.getCurseManipulationData();
        IMimicryData ownerMimicryData = ownerCap.getMimicryData();
        ISkillData ownerSkillData = ownerCap.getSkillData();

        int points = ownerSorcererData.getSkillPoints();

        if (points > 0) {
            List<Skill> skills = new ArrayList<>();

            for (Skill skill : Skill.values()) {
                if (!skill.isValid(this.mob)) continue;

                skills.add(skill);
            }

            int distributed = points / skills.size();

            if (distributed > 0) {
                for (Skill skill : skills) {
                    int current = ownerSkillData.getSkill(skill);

                    int max = SorcererUtil.getMaximumSkillLevel(ownerSorcererData.getExperience(), current, distributed);

                    int real = max - current;

                    if (real == 0) continue;

                    ownerSkillData.increaseSkill(skill, real);
                    ownerSorcererData.useSkillPoints(real);
                }
            }
        }

        if (ownerSorcererData.getCurrentAdditional() == null || this.mob.tickCount % CHANGE_TECHNIQUE_INTERVAL == 0) {
            List<CursedTechnique> additional = new ArrayList<>(ownerSorcererData.getAdditional());

            if (!additional.isEmpty()) {
                ownerSorcererData.setCurrentAdditional(additional.get(HelperMethods.RANDOM.nextInt(additional.size())));
            }
        }

        if (ownerAbilityData.hasToggled(JJKAbilities.RIKA.get())) {
            if (ownerMimicryData.getCurrentCopied() == null || this.mob.tickCount % CHANGE_TECHNIQUE_INTERVAL == 0) {
                List<CursedTechnique> copied = new ArrayList<>(ownerMimicryData.getCopied());

                if (!copied.isEmpty()) {
                    ownerMimicryData.setCurrentCopied(copied.get(HelperMethods.RANDOM.nextInt(copied.size())));
                }
            }
        }

        if (ownerSorcererData.hasActiveTechnique(JJKCursedTechniques.CURSE_MANIPULATION.get())) {
            LivingEntity target = this.mob.getTarget();

            if (target != null && HelperMethods.RANDOM.nextInt(5) == 0) {
                List<AbsorbedCurse> curses = ownerCurseManipulationData.getCurses();

                IJujutsuCapability targetCap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (targetCap != null) {
                    ISorcererData targetData = targetCap.getSorcererData();

                    AbsorbedCurse closest = null;

                    for (AbsorbedCurse curse : curses) {
                        float diff = Math.abs(CurseManipulationUtil.getCurseExperience(curse) - targetData.getExperience());

                        if (closest == null || diff < Math.abs(CurseManipulationUtil.getCurseExperience(closest) - targetData.getExperience())) {
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
                AbsorbedCurse absorbed = stack.get(JJKDataComponentTypes.ABSORBED_CURSE);

                if (absorbed != null) {
                    this.mob.playSound(this.mob.getEatingSound(stack), 1.0F, 1.0F + (HelperMethods.RANDOM.nextFloat() - HelperMethods.RANDOM.nextFloat()) * 0.4F);
                    ownerCurseManipulationData.addCurse(absorbed);
                    this.mob.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                }
            }
        }

        if (ownerSorcererData.hasSummonOfClass(RikaEntity.class)) {
            RikaEntity rika = ownerSorcererData.getSummonByClass(RikaEntity.class);

            if (rika != null) {
                rika.changeTarget(this.mob.getTarget());
            }
        }

        if (this.nextChantTime == 0 && !this.chants.isEmpty()) {
            String chant = this.chants.peek();
            this.nextChantTime = this.mob.tickCount + chant.length();
        }

        if (this.chanting != null && this.mob.tickCount >= this.nextChantTime) {
            if (this.chants.isEmpty()) {
                this.trigger(this.chanting);
                this.chanting = null;
                this.chants.clear();
            } else {
                String chant = this.chants.poll();

                ServerChantHandler.onChant(this.mob, chant);

                for (ServerPlayer player : ((ServerLevel) this.mob.level()).players()) {
                    if (player.distanceTo(this.mob) > this.mob.getAttributeValue(Attributes.FOLLOW_RANGE))
                        continue;

                    player.sendSystemMessage(Component.literal(String.format("<%s> %s", this.mob.getName().getString(), chant)));
                }
            }
        }

        for (Ability ability : abilities) {
            boolean success = ability.shouldTrigger(this.mob, this.mob.getTarget());

            if (ability.getActivationType(this.mob) == Ability.ActivationType.TOGGLED) {
                if (success) {
                    if (!ownerAbilityData.hasToggled(ability)) {
                        this.trigger(ability);
                    }
                } else if (ownerAbilityData.hasToggled(ability)) {
                    AbilityHandler.untrigger(this.mob, ability);
                }
            } else if (ability.getActivationType(this.mob) == Ability.ActivationType.CHANNELED) {
                if (success) {
                    if (!ownerAbilityData.isChanneling(ability)) {
                        this.trigger(ability);
                    }
                } else if (ownerAbilityData.isChanneling(ability)) {
                    AbilityHandler.untrigger(this.mob, ability);
                }
            } else if (success) {
                this.trigger(ability);
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
