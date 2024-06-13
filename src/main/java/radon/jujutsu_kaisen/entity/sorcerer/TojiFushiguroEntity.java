package radon.jujutsu_kaisen.entity.sorcerer;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.item.registry.JJKDataComponentTypes;
import radon.jujutsu_kaisen.item.registry.JJKItems;
import radon.jujutsu_kaisen.menu.BountyMenu;
import radon.jujutsu_kaisen.util.CuriosUtil;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

    public TojiFushiguroEntity(EntityType<? extends PathfinderMob> pType, Level pLevel) {
        super(pType, pLevel);

        Arrays.fill(this.armorDropChances, 0.0F);
        Arrays.fill(this.handDropChances, 0.0F);
    }

    @Override
    protected boolean isCustom() {
        return false;
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
        ItemStack stack = pPlayer.getItemInHand(pHand);

        if (!pPlayer.isSecondaryUseActive() && stack.isEmpty()) {
            this.setCurrentCustomer(pPlayer);
            pPlayer.openMenu(new SimpleMenuProvider((pContainerId, pPlayerInventory, ignored) ->
                    new BountyMenu(pContainerId, pPlayerInventory, ContainerLevelAccess.create(pPlayer.level(), this.blockPosition()), this), Component.empty()));
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public float getExperience() {
        return SorcererGrade.SPECIAL_GRADE.getRequiredExperience() * 2.0F;
    }

    @Override
    @Nullable
    public CursedTechnique getTechnique() {
        return null;
    }

    @Override
    public @NotNull List<Trait> getTraits() {
        return List.of(Trait.HEAVENLY_RESTRICTION_BODY);
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.SORCERER;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        ItemStack chest = new ItemStack(JJKItems.INVENTORY_CURSE.get());

        List<ItemStack> inventory = chest.get(JJKDataComponentTypes.HIDDEN_INVENTORY);

        if (inventory == null) return;

        inventory.add(PLAYFUL_CLOUD, new ItemStack(JJKItems.PLAYFUL_CLOUD.get()));
        inventory.add(INVERTED_SPEAR_OF_HEAVEN, new ItemStack(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get()));
        inventory.add(SPLIT_SOUL_KATANA, new ItemStack(JJKItems.SPLIT_SOUL_KATANA.get()));

        CuriosUtil.setItemInSlot(this, "bodyDL", chest);
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

    private void pickWeapon(@Nullable LivingEntity target) {
        ItemStack chest = CuriosUtil.findSlot(this, "bodyDL");

        List<ItemStack> inventory = chest.get(JJKDataComponentTypes.HIDDEN_INVENTORY);

        if (inventory == null) return;

        if (target == null) {
            if (!this.getMainHandItem().isEmpty()) {
                int slot = this.getSlot(this.getMainHandItem());

                if (slot != -1) {
                    inventory.add(slot, this.getMainHandItem());
                }
            }
            this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            return;
        }

        int result = PLAYFUL_CLOUD;

        IJujutsuCapability cap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap != null) {
            result = SPLIT_SOUL_KATANA;

            IAbilityData data = cap.getAbilityData();

            for (Ability toggled : data.getToggled()) {
                if (toggled.isTechnique()) {
                    result = INVERTED_SPEAR_OF_HEAVEN;
                    break;
                }
            }
        }

        if (this.getSlot(this.getMainHandItem()) != result) {
            if (!this.getMainHandItem().isEmpty()) {
                int slot = this.getSlot(this.getMainHandItem());

                if (slot != -1) {
                    inventory.add(slot, this.getMainHandItem());
                }
            }

            ItemStack main = inventory.get(result);
            inventory.remove(result);
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
        this.pickWeapon(target);

        ServerPlayer bounty = this.getBounty();

        if (bounty != null) {
            if (this.getTarget() == null) this.setTarget(bounty);

            if (this.distanceTo(bounty) >= this.getAttributeValue(Attributes.FOLLOW_RANGE)) {
                double d0 = bounty.getX() + ((HelperMethods.RANDOM.nextDouble() - HelperMethods.RANDOM.nextDouble()) + 0.1D) * TELEPORT_RADIUS + 0.5D;
                double d1 = bounty.getY() + HelperMethods.RANDOM.nextInt(3) - 1;
                double d2 = bounty.getZ() + ((HelperMethods.RANDOM.nextDouble() - HelperMethods.RANDOM.nextDouble()) + 0.1D) * TELEPORT_RADIUS + 0.5D;

                if (this.level().noCollision(this.getType().getSpawnAABB(d0, d1, d2))) {
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
