package radon.jujutsu_kaisen.entity.sorcerer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.ai.goal.BetterFloatGoal;
import radon.jujutsu_kaisen.entity.ai.goal.LookAtTargetGoal;
import radon.jujutsu_kaisen.entity.ai.goal.SorcererGoal;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.item.armor.InventoryCurseItem;
import radon.jujutsu_kaisen.menu.BountyMenu;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TojiFushiguroEntity extends SorcererEntity {
    private static final int PLAYFUL_CLOUD = 0;
    private static final int INVERTED_SPEAR_OF_HEAVEN = 1;
    private static final int SPLIT_SOUL_KATANA = 2;

    private static final int TELEPORT_RADIUS = 32;

    @Nullable
    private UUID bountyUUID;
    @Nullable
    private ServerPlayer cachedBounty;

    @Nullable
    private UUID issuerUUID;
    @Nullable
    private ServerPlayer cachedIssuer;

    @Nullable
    private Player currentCustomer;

    public TojiFushiguroEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        Arrays.fill(this.armorDropChances, 0.0F);
        Arrays.fill(this.handDropChances, 0.0F);
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);

        this.spawnAtLocation(JJKItems.INVENTORY_CURSE.get());
    }

    public void setCurrentCustomer(@Nullable Player pPlayer) {
        this.currentCustomer = pPlayer;
    }

    @Nullable
    public Player getCurrentCustomer() {
        return this.currentCustomer;
    }

    private boolean isTrading() {
        return this.currentCustomer != null;
    }

    public void stopTrading() {
        this.setCurrentCustomer(null);
    }

    public void setBounty(ServerPlayer issuer, ServerPlayer bounty) {
        this.issuerUUID = issuer.getUUID();
        this.cachedIssuer = issuer;

        this.bountyUUID = bounty.getUUID();
        this.cachedBounty = bounty;

        issuer.sendSystemMessage(Component.translatable(String.format("chat.%s.bounty_set", JujutsuKaisen.MOD_ID), bounty.getName()));
    }

    private void clearBounty() {
        this.issuerUUID = null;
        this.cachedIssuer = null;

        this.bountyUUID = null;
        this.cachedBounty = null;
    }

    @Nullable
    private ServerPlayer getIssuer() {
        if (this.cachedIssuer != null && !this.cachedIssuer.isRemoved()) {
            return this.cachedIssuer;
        } else if (this.issuerUUID != null && this.level() instanceof ServerLevel) {
            this.cachedIssuer = (ServerPlayer) ((ServerLevel) this.level()).getEntity(this.issuerUUID);
            return this.cachedIssuer;
        } else {
            return null;
        }
    }

    @Nullable
    private ServerPlayer getBounty() {
        if (this.cachedBounty != null && !this.cachedBounty.isRemoved()) {
            return this.cachedBounty;
        } else if (this.bountyUUID != null && this.level() instanceof ServerLevel) {
            this.cachedBounty = (ServerPlayer) ((ServerLevel) this.level()).getEntity(this.bountyUUID);
            return this.cachedBounty;
        } else {
            return null;
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        if (this.bountyUUID != null) {
            pCompound.putUUID("bounty", this.bountyUUID);
        }
        if (this.issuerUUID != null) {
            pCompound.putUUID("issuer", this.issuerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.contains("bounty")) {
            this.bountyUUID = pCompound.getUUID("bounty");
        }
        if (pCompound.contains("issuer")) {
            this.issuerUUID = pCompound.getUUID("issuer");
        }
    }

    @Override
    protected @NotNull InteractionResult mobInteract(Player pPlayer, @NotNull InteractionHand pHand) {
        if (!pPlayer.isSecondaryUseActive() && pHand == InteractionHand.MAIN_HAND) {
            this.setCurrentCustomer(pPlayer);
            pPlayer.openMenu(new SimpleMenuProvider((pContainerId, pPlayerInventory, ignored) ->
                    new BountyMenu(pContainerId, pPlayerInventory, ContainerLevelAccess.create(pPlayer.level(), this.blockPosition()), this), Component.empty()));
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public float getExperience() {
        return SorcererGrade.SPECIAL_GRADE.getRequiredExperience();
    }

    @Override
    public @Nullable CursedTechnique getTechnique() {
        return null;
    }

    @Override
    public @NotNull List<Trait> getTraits() {
        return List.of(Trait.HEAVENLY_RESTRICTION);
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.SORCERER;
    }

    @Override
    public @Nullable Ability getDomain() {
        return null;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        ItemStack inventory = new ItemStack(JJKItems.INVENTORY_CURSE.get());
        InventoryCurseItem.addItem(inventory, PLAYFUL_CLOUD, new ItemStack(JJKItems.PLAYFUL_CLOUD.get()));
        InventoryCurseItem.addItem(inventory, INVERTED_SPEAR_OF_HEAVEN, new ItemStack(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get()));
        InventoryCurseItem.addItem(inventory, SPLIT_SOUL_KATANA, new ItemStack(JJKItems.SPLIT_SOUL_KATANA.get()));
        this.setItemSlot(EquipmentSlot.CHEST, inventory);
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    protected boolean targetsCurses() {
        return false;
    }

    private int getSlot(ItemStack stack) {
        if (stack.is(JJKItems.PLAYFUL_CLOUD.get())) {
            return PLAYFUL_CLOUD;
        } else if (stack.is(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get())) {
            return INVERTED_SPEAR_OF_HEAVEN;
        } else if (stack.is(JJKItems.SPLIT_SOUL_KATANA.get())) {
            return SPLIT_SOUL_KATANA;
        }
        return -1;
    }

    private void pickWeapon(LivingEntity target) {
        AtomicInteger result = new AtomicInteger(PLAYFUL_CLOUD);

        if (JJKAbilities.hasToggled(target, JJKAbilities.SOUL_REINFORCEMENT.get()) || target.getArmorCoverPercentage() > 0 || target.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
            result.set(SPLIT_SOUL_KATANA);
        }

        target.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            for (Ability toggled : cap.getToggled()) {
                if (toggled.isTechnique()) {
                    result.set(INVERTED_SPEAR_OF_HEAVEN);
                    break;
                }
            }
        });

        ItemStack inventory = this.getItemBySlot(EquipmentSlot.CHEST);

        if (this.getSlot(this.getMainHandItem()) != result.get()) {
            if (!this.getMainHandItem().isEmpty()) {
                int slot = this.getSlot(this.getMainHandItem());

                if (slot != -1) {
                    InventoryCurseItem.addItem(inventory, slot, this.getMainHandItem());
                }
            }
            ItemStack main = InventoryCurseItem.getItem(inventory, result.get());
            InventoryCurseItem.removeItem(inventory, result.get());
            this.setItemInHand(InteractionHand.MAIN_HAND, main);
        }
    }

    @Override
    public boolean isNoAi() {
        return this.isTrading() || super.isNoAi();
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        LivingEntity target = this.getTarget();

        if (target != null) {
            this.pickWeapon(target);
        }

        ServerPlayer bounty = this.getBounty();

        if (bounty != null) {
            if (this.getTarget() == null) this.setTarget(bounty);

            if (this.distanceTo(bounty) >= this.getAttributeValue(Attributes.FOLLOW_RANGE)) {
                double d0 = bounty.getX() + ((HelperMethods.RANDOM.nextDouble() - HelperMethods.RANDOM.nextDouble()) + 0.1D) * TELEPORT_RADIUS + 0.5D;
                double d1 = bounty.getY() + HelperMethods.RANDOM.nextInt(3) - 1;
                double d2 = bounty.getZ() + ((HelperMethods.RANDOM.nextDouble() - HelperMethods.RANDOM.nextDouble()) + 0.1D) * TELEPORT_RADIUS + 0.5D;

                if (this.level().noCollision(this.getType().getAABB(d0, d1, d2))) {
                    this.setPos(d0, d1, d2);
                }
            }
        }
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity pEntity) {
        boolean result = super.doHurtTarget(pEntity);

        if (result && pEntity == this.getBounty()) {
            LivingEntity living = (LivingEntity) pEntity;

            if (living.isDeadOrDying()) {
                ServerPlayer issuer = this.getIssuer();

                if (issuer != null) {
                    issuer.sendSystemMessage(Component.translatable(String.format("chat.%s.bounty_success", JujutsuKaisen.MOD_ID), living.getName()));
                }
                this.clearBounty();
            }
        }
        return result;
    }

    @Override
    public void die(@NotNull DamageSource pDamageSource) {
        super.die(pDamageSource);

        this.stopTrading();

        ServerPlayer issuer = this.getIssuer();
        ServerPlayer bounty = this.getBounty();

        if (issuer != null && bounty != null) {
            issuer.sendSystemMessage(Component.translatable(String.format("chat.%s.bounty_fail", JujutsuKaisen.MOD_ID), bounty.getName()));
        }
    }
}
