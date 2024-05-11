package radon.jujutsu_kaisen.item.cursed_tool;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.client.render.item.SplitSoulKatanaRenderer;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.item.CursedToolItem;
import radon.jujutsu_kaisen.item.registry.JJKItems;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class SplitSoulKatanaItem extends CursedToolItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SplitSoulKatanaItem(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);
    }

    @Override
    public float doHurtEffects(ItemStack stack, DamageSource source, LivingEntity attacker, LivingEntity victim, float amount) {
        if (source.is(JJKDamageSources.SPLIT_SOUL_KATANA)) return amount;

        IJujutsuCapability attackerCap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (attackerCap == null) return amount;

        ISorcererData attackerData = attackerCap.getSorcererData();

        if (!attackerData.hasTrait(Trait.HEAVENLY_RESTRICTION_BODY) && attackerData.getFingers() == 0 &&
                !attackerData.hasTechnique(JJKCursedTechniques.IDLE_TRANSFIGURATION.get())) return amount;

        float soul = JJKItems.SPLIT_SOUL_KATANA.get().getDamage(JJKItems.SPLIT_SOUL_KATANA.get().getDefaultInstance());

        IJujutsuCapability victimCap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (victimCap == null) return amount;

        ISkillData victimData = victimCap.getSkillData();

        float armor = victimData.getSkill(Skill.SOUL) * 0.5F;

        float toughness = armor * 0.1F;

        float f = 2.0F + toughness / 4.0F;
        float f1 = Mth.clamp(armor - soul / f, armor * 0.2F, 23.75F);
        soul *= 1.0F - f1 / 25.0F;

        return victim.hurt(JJKDamageSources.splitSoulKatanaAttack(attacker), soul) ? 0.0F : amount;
    }

    @Override
    public SorcererGrade getGrade() {
        return SorcererGrade.SPECIAL_GRADE;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private SplitSoulKatanaRenderer renderer;

            @Override
            public @NotNull SplitSoulKatanaRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new SplitSoulKatanaRenderer();
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
