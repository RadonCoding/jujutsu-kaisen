package radon.jujutsu_kaisen.entity.sorcerer;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.EntityUtil;

import java.util.List;
import java.util.Set;

public class MegumiFushiguroEntity extends SorcererEntity {
    public MegumiFushiguroEntity(EntityType<? extends PathfinderMob> pType, Level pLevel) {
        super(pType, pLevel);
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player pPlayer, @NotNull InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);

        if (stack.is(JJKItems.SUKUNA_FINGER.get())) {
            this.playSound(this.getEatingSound(stack), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);

            int count = stack.getCount();

            stack.shrink(count);
            EntityUtil.convertTo(this, new SukunaEntity(this, count, false), true, false);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return super.mobInteract(pPlayer, pHand);
        }
    }

    @Override
    public float getExperience() {
        return SorcererGrade.GRADE_1.getRequiredExperience();
    }

    @Override
    public @Nullable ICursedTechnique getTechnique() {
        return JJKCursedTechniques.TEN_SHADOWS.get();
    }

    @Override
    public Set<Ability> getUnlocked() {
        return Set.of(JJKAbilities.CHIMERA_SHADOW_GARDEN.get(), JJKAbilities.SIMPLE_DOMAIN.get());
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.SORCERER;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(JJKItems.JET_BLACK_SHADOW_SWORD.get()));

        IJujutsuCapability cap = this.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ITenShadowsData data = cap.getTenShadowsData();

        data.tame(JJKEntities.RABBIT_ESCAPE.get());
        data.tame(JJKEntities.TOAD.get());
        data.tame(JJKEntities.NUE.get());
        data.tame(JJKEntities.GREAT_SERPENT.get());
        data.tame(JJKEntities.MAX_ELEPHANT.get());
    }
}
