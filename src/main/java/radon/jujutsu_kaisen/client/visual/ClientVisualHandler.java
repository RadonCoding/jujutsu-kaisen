package radon.jujutsu_kaisen.client.visual;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.IArmPoseTransformer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.entity.sorcerer.HeianSukunaEntity;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.mixin.client.ILivingEntityRendererAccessor;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.RequestVisualDataC2SPacket;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.*;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientVisualHandler {
    private static final RenderType SIX_EYES = JJKRenderTypes.eyes(new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/overlay/six_eyes.png"));
    private static final RenderType INUMAKI = RenderType.entityCutoutNoCull(new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/overlay/inumaki.png"));

    private static final int MAX_MOUTH_FRAMES = 4;

    private static final Map<UUID, VisualData> synced = new HashMap<>();

    public static void receive(UUID identifier, VisualData data) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null) return;

        synced.put(identifier, data);
    }

    public static void onChant(UUID identifier) {
        VisualData data = get(identifier);

        if (data == null) return;

        data.mouth++;
    }

    @Nullable
    public static VisualData get(UUID identifier) {
        return synced.get(identifier);
    }

    @Nullable
    public static VisualData get(Entity entity) {
        if (!entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return null;

        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null) return null;

        if (synced.containsKey(entity.getUUID())) {
            return synced.get(entity.getUUID());
        } else if (entity == mc.player) {
            if (mc.player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                ISorcererData cap = mc.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                Set<CursedTechnique> techniques = new HashSet<>();

                if (cap.getTechnique() != null) techniques.add(cap.getTechnique());
                if (cap.getCurrentCopied() != null) techniques.add(cap.getCurrentCopied());
                if (cap.getCurrentAbsorbed() != null) techniques.add(cap.getCurrentAbsorbed());
                if (cap.getAdditional() != null) techniques.add(cap.getAdditional());

                return new VisualData(cap.getToggled(), cap.getTraits(), techniques, cap.getType(), cap.getCursedEnergyColor());
            }
        }
        return null;
    }

    private static <T extends LivingEntity> void renderCuriosInventory(T entity, HumanoidModel<T> model, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        LazyOptional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(entity);

        if (optional.isPresent()) {
            ICuriosItemHandler inventory = optional.resolve().orElseThrow();

            Optional<SlotResult> rightHand = inventory.findCurio("right_hand", 0);
            Optional<SlotResult> leftHand = inventory.findCurio("left_hand", 0);

            ItemStack right = rightHand.isPresent() ? rightHand.get().stack() : ItemStack.EMPTY;
            ItemStack left = leftHand.isPresent() ? leftHand.get().stack() : ItemStack.EMPTY;

            if (!right.isEmpty() || !left.isEmpty()) {
                poseStack.pushPose();

                if (model.young) {
                    poseStack.translate(0.0F, 0.75F, 0.0F);
                    poseStack.scale(0.5F, 0.5F, 0.5F);
                }

                if (!right.isEmpty()) {
                    poseStack.pushPose();
                    model.translateToHand(HumanoidArm.RIGHT, poseStack);
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                    poseStack.translate((float) 1 / 16.0F, 0.125F, -0.625F);
                    Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(entity, right, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, poseStack, buffer, packedLight);
                    poseStack.popPose();
                }
                if (!left.isEmpty()) {
                    poseStack.pushPose();
                    model.translateToHand(HumanoidArm.LEFT, poseStack);
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                    poseStack.translate((float) -1 / 16.0F, 0.125F, -0.625F);
                    Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(entity, left, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, true, poseStack, buffer, packedLight);
                    poseStack.popPose();
                }
                poseStack.popPose();
            }
        }
    }

    public static boolean shouldRenderExtraArms(VisualData data) {
        for (Ability ability : data.toggled) {
            if (!(ability instanceof ITransformation transformation)) continue;
            if (transformation.getBodyPart() != ITransformation.Part.BODY || !transformation.isReplacement()) continue;
            return false;
        }
        return data.traits.contains(Trait.PERFECT_BODY);
    }

    public static <T extends LivingEntity> void renderOverlay(T entity, ResourceLocation texture, EntityModel<T> model, PoseStack poseStack, MultiBufferSource buffer, float partialTicks, int packedLight) {
        VisualData data = get(entity);

        if (data == null) return;

        if (data.traits.contains(Trait.SIX_EYES)) {
            VertexConsumer consumer = buffer.getBuffer(SIX_EYES);
            model.renderToBuffer(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                    1.0F, 1.0F, 1.0F, 1.0F);
        }
        if (shouldRenderExtraArms(data)) {
            VertexConsumer overlay = buffer.getBuffer(RenderType.entityCutoutNoCull(new ResourceLocation(JujutsuKaisen.MOD_ID,
                    String.format("textures/overlay/mouth_%d.png", data.mouth + 1))));
            model.renderToBuffer(poseStack, overlay, packedLight, OverlayTexture.NO_OVERLAY,
                    1.0F, 1.0F, 1.0F, 1.0F);

            if (model instanceof PlayerModel<T> humanoid) {
                VertexConsumer skin = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));

                poseStack.pushPose();
                poseStack.translate(0.0F, 0.2F, 0.0F);

                humanoid.rightArmPose = HumanoidModel.ArmPose.EMPTY;
                humanoid.leftArmPose = HumanoidModel.ArmPose.EMPTY;

                boolean shouldSit = entity.isPassenger() && (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());

                float f = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
                float f1 = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
                float f2 = f1 - f;

                if (shouldSit && entity.getVehicle() instanceof LivingEntity living) {
                    f = Mth.rotLerp(partialTicks, living.yBodyRotO, living.yBodyRot);
                    f2 = f1 - f;
                    float f3 = Mth.wrapDegrees(f2);

                    if (f3 < -85.0F) {
                        f3 = -85.0F;
                    }

                    if (f3 >= 85.0F) {
                        f3 = 85.0F;
                    }

                    f = f1 - f3;

                    if (f3 * f3 > 2500.0F) {
                        f += f3 * 0.2F;
                    }
                    f2 = f1 - f;
                }

                float f6 = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

                if (LivingEntityRenderer.isEntityUpsideDown(entity)) {
                    f6 *= -1.0F;
                    f2 *= -1.0F;
                }

                if (!(Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity) instanceof LivingEntityRenderer<?, ?> renderer))
                    return;

                float f7 = ((ILivingEntityRendererAccessor<?, ?>) renderer).invokeGetBob(entity, partialTicks);
                float f8 = 0.0F;
                float f5 = 0.0F;

                if (!shouldSit && entity.isAlive()) {
                    f8 = entity.walkAnimation.speed(partialTicks);
                    f5 = entity.walkAnimation.position(partialTicks);

                    if (entity.isBaby()) {
                        f5 *= 3.0F;
                    }
                    if (f8 > 1.0F) {
                        f8 = 1.0F;
                    }
                }
                humanoid.setupAnim(entity, f5, f8, f7, f2, f6);

                if (model.attackTime <= 0) {
                    humanoid.rightArm.xRot -= humanoid.rightArm.xRot * 0.5F - ((float) Math.PI * 0.1F);
                }
                humanoid.rightArm.zRot += humanoid.rightArm.zRot * 0.5F - ((float) Math.PI * 0.125F);
                humanoid.rightSleeve.copyFrom(humanoid.rightArm);
                humanoid.rightArm.render(poseStack, skin, packedLight, OverlayTexture.NO_OVERLAY);

                if (model.attackTime <= 0) {
                    humanoid.leftArm.xRot -= humanoid.leftArm.xRot * 0.5F - ((float) Math.PI * 0.1F);
                }
                humanoid.leftArm.zRot -= humanoid.leftArm.zRot * 0.5F - ((float) Math.PI * 0.025F);
                humanoid.leftSleeve.copyFrom(humanoid.leftArm);
                humanoid.leftArm.render(poseStack, skin, packedLight, OverlayTexture.NO_OVERLAY);

                renderCuriosInventory(entity, humanoid, poseStack, buffer, packedLight);

                poseStack.popPose();
            }
        }
        if (data.techniques.contains(CursedTechnique.CURSED_SPEECH)) {
            VertexConsumer consumer = buffer.getBuffer(INUMAKI);
            model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                    1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        synced.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.getConnection() == null) return;

        Entity entity = event.getEntity();

        if (entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            PacketHandler.sendToServer(new RequestVisualDataC2SPacket(entity.getUUID()));
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        VisualData data = get(entity);

        if (data == null) return;

        BlueFistsVisual.tick(data, entity);
        IdleTransfigurationVisual.tick(data, entity);

        if (entity.level().getGameTime() % 5 == 0) {
            if (data.mouth > 0) {
                if (++data.mouth >= MAX_MOUTH_FRAMES) {
                    data.mouth = 0;
                }
            }
        }
    }

    /*@SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null) return;

        VisualData data = get(player);

        if (data == null) return;

        BlueFistsVisual.tick(data, player);
    }*/

    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        VisualData data = get(event.getEntity());

        if (data == null) return;

        if (!(event.getRenderer().getModel() instanceof PlayerModel<?> player)) return;

        for (Ability ability : data.toggled) {
            if (!(ability instanceof ITransformation transformation)) continue;

            if (transformation.isReplacement()) {
                switch (transformation.getBodyPart()) {
                    case HEAD -> {
                        player.head.visible = false;
                        player.hat.visible = false;
                    }
                    case BODY -> player.setAllVisible(false);
                    case RIGHT_ARM -> {
                        player.rightArm.visible = false;
                        player.rightSleeve.visible = false;
                    }
                    case LEFT_ARM -> {
                        player.leftArm.visible = false;
                        player.rightSleeve.visible = false;
                    }
                    case LEGS -> {
                        player.rightLeg.visible = false;
                        player.rightPants.visible = false;
                        player.leftLeg.visible = false;
                        player.leftPants.visible = false;
                    }
                }
            }

            HumanoidModel.ArmPose pose = IClientItemExtensions.of(transformation.getItem()).getArmPose(event.getEntity(), InteractionHand.MAIN_HAND, transformation.getItem().getDefaultInstance());

            if (pose != null) {
                if (transformation.getBodyPart() == ITransformation.Part.RIGHT_ARM) {
                    player.rightArmPose = pose;
                } else if (transformation.getBodyPart() == ITransformation.Part.LEFT_ARM) {
                    player.leftArmPose = pose;
                }
            }
        }
    }

    public static class VisualData {
        public final Set<Ability> toggled;
        public final Set<Trait> traits;
        public final Set<CursedTechnique> techniques;
        public final JujutsuType type;
        public final int cursedEnergyColor;

        public int mouth;

        public VisualData(CompoundTag nbt) {
            this.toggled = new HashSet<>();
            this.traits = new HashSet<>();
            this.techniques = new HashSet<>();

            for (Tag key : nbt.getList("toggled", Tag.TAG_STRING)) {
                this.toggled.add(JJKAbilities.getValue(new ResourceLocation(key.getAsString())));
            }

            for (Tag key : nbt.getList("traits", Tag.TAG_INT)) {
                if (key instanceof IntTag tag) {
                    this.traits.add(Trait.values()[tag.getAsInt()]);
                }
            }

            for (Tag key : nbt.getList("techniques", Tag.TAG_INT)) {
                if (key instanceof IntTag tag) {
                    this.techniques.add(CursedTechnique.values()[tag.getAsInt()]);
                }
            }

            this.type = JujutsuType.values()[nbt.getInt("type")];
            this.cursedEnergyColor = nbt.getInt("cursed_energy_color");
        }

        public VisualData(Set<Ability> toggled, Set<Trait> traits, Set<CursedTechnique> techniques, JujutsuType type, int cursedEnergyColor) {
            this.toggled = toggled;
            this.traits = traits;
            this.techniques = techniques;
            this.type = type;
            this.cursedEnergyColor = cursedEnergyColor;
        }

        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();

            ListTag toggledTag = new ListTag();

            for (Ability ability : this.toggled) {
                toggledTag.add(StringTag.valueOf(JJKAbilities.getKey(ability).toString()));
            }
            nbt.put("toggled", toggledTag);

            ListTag traitsTag = new ListTag();

            for (Trait trait : this.traits) {
                traitsTag.add(IntTag.valueOf(trait.ordinal()));
            }
            nbt.put("traits", traitsTag);

            ListTag techniquesTag = new ListTag();

            for (CursedTechnique technique : this.techniques) {
                techniquesTag.add(IntTag.valueOf(technique.ordinal()));
            }
            nbt.put("techniques", techniquesTag);

            nbt.putInt("type", this.type.ordinal());
            nbt.putInt("cursed_energy_color", this.cursedEnergyColor);

            return nbt;
        }
    }
}
