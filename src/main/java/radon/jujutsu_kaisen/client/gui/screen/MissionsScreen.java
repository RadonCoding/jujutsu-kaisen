package radon.jujutsu_kaisen.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.gui.screen.widget.ScrollableSlider;
import radon.jujutsu_kaisen.client.gui.screen.widget.VerticalSlider;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.mission.entity.IMissionEntityData;
import radon.jujutsu_kaisen.data.mission.level.IMissionLevelData;
import radon.jujutsu_kaisen.data.mission.Mission;
import radon.jujutsu_kaisen.data.mission.MissionGrade;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.AcceptMissionC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.SearchForMissionsC2SPacket;

import java.util.*;

public class MissionsScreen extends Screen {
    private static final ResourceLocation WINDOW = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/missions/window.png");
    private static final ResourceLocation MISSION_GRADE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/missions/mission_grade.png");

    private static final int WINDOW_WIDTH = 78;
    private static final int WINDOW_HEIGHT = 200;
    private static final int WINDOW_INSIDE_X = 9;
    private static final int WINDOW_INSIDE_Y = 18;
    private static final int WINDOW_OFFSET_X = 20;
    private static final int WINDOW_INSIDE_WIDTH = 60;
    private static final int WINDOW_INSIDE_HEIGHT = 173;

    private static final int MISSION_CARDS_OFFSET_X = 20;

    private static final int MISSION_GRADE_SIZE = 52;

    private static final int ACCEPT_BUTTON_WIDTH = 128;
    private static final int ACCEPT_BUTTON_HEIGHT = 16;

    private final List<MissionCard> cards;

    private ScrollableSlider missionGradesSlider;
    private ScrollableSlider missionCardsSlider;
    private Button acceptButton;

    private MissionGrade grade;

    private boolean isScrolling;

    @Nullable
    private MissionCard selected;

    private boolean initialized;

    public MissionsScreen() {
        super(GameNarrator.NO_TITLE);

        this.cards = new ArrayList<>();
        this.grade = MissionGrade.D;
    }

    @Override
    public void tick() {
        super.tick();

        this.acceptButton.active = this.selected != null && !this.hasMission();
    }

    private boolean hasMission() {
        if (this.minecraft == null || this.minecraft.player == null) return false;

        IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IMissionEntityData data = cap.getMissionData();

        return data.getMission() != null;
    }

    public void refresh() {
        if (this.minecraft == null || this.minecraft.level == null) return;

        this.cards.clear();

        IMissionLevelData data = this.minecraft.level.getData(JJKAttachmentTypes.MISSION_LEVEL);

        Set<Mission> missions = data.getMissions();

        for (Mission mission : missions) {
            MissionCard card = new MissionCard(this.minecraft, mission);

            this.cards.add(card);

            if (this.selected == null) continue;

            if (this.selected.getMission().equals(mission)) this.selected = card;
        }

        List<MissionCard> subset = new ArrayList<>(this.cards);
        subset.removeIf(card -> card.getMission().getGrade() != this.grade);

        int windowOffsetX = WINDOW_OFFSET_X;

        int missionCardsOffsetX = windowOffsetX + WINDOW_WIDTH + MISSION_CARDS_OFFSET_X;

        this.missionCardsSlider.setMaxValue(Math.max(0, (subset.size() * (MissionCard.WINDOW_WIDTH + MissionCard.OUTER_PADDING) - MissionCard.OUTER_PADDING) -
                (this.width - missionCardsOffsetX - MISSION_CARDS_OFFSET_X)));
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (pButton != InputConstants.MOUSE_BUTTON_LEFT) {
            this.isScrolling = false;
        } else {
            if (!this.isScrolling) {
                this.isScrolling = true;
            } else {
                int offsetX = WINDOW_OFFSET_X + WINDOW_WIDTH + MISSION_CARDS_OFFSET_X;
                int offsetY = (this.height - MissionCard.WINDOW_HEIGHT) / 2;

                double x = pMouseX - offsetX - MissionCard.WINDOW_INSIDE_X;
                double y = pMouseY - offsetY - MissionCard.WINDOW_INSIDE_Y;

                if (y > 0.0D && y < WINDOW_INSIDE_HEIGHT) {
                    List<MissionCard> missions = new ArrayList<>(this.cards);
                    missions.removeIf(card -> card.getMission().getGrade() != this.grade);

                    for (int i = 0; i < missions.size(); i++) {
                        int insideX = -this.missionCardsSlider.getValueInt() + (i * (MissionCard.WINDOW_WIDTH + MissionCard.OUTER_PADDING));

                        double relativeX = x - insideX;

                        if (relativeX > 0.0D && relativeX < MissionCard.WINDOW_INSIDE_WIDTH) {
                            missions.get(i).scroll(pDragY);
                            break;
                        }
                    }
                }
            }
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.minecraft == null) return super.mouseClicked(pMouseX, pMouseY, pButton);

        int windowOffsetX = WINDOW_OFFSET_X;
        int windowOffsetY = (this.height - WINDOW_HEIGHT) / 2;

        double windowRelativeX = pMouseX - windowOffsetX - WINDOW_INSIDE_X;
        double windowRelativeY = pMouseY - windowOffsetY - WINDOW_INSIDE_Y;

        if (windowRelativeX > 0.0D && windowRelativeX < MISSION_GRADE_SIZE && windowRelativeY > 0.0D && windowRelativeY < WINDOW_INSIDE_HEIGHT) {
            for (int i = 0; i < MissionGrade.values().length; i++) {
                double offset = (i * MISSION_GRADE_SIZE) - this.missionGradesSlider.getValue();

                double relative = windowRelativeY - offset;

                if (relative > 0.0D && relative < MISSION_GRADE_SIZE) {
                    this.grade = MissionGrade.values()[i];
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }
        }

        if (!this.hasMission()) {
            int missionCardsOffsetX = WINDOW_OFFSET_X + WINDOW_WIDTH + MISSION_CARDS_OFFSET_X;
            int missionCardsOffsetY = (this.height - MissionCard.WINDOW_HEIGHT) / 2;

            double missionCardsRelativeX = pMouseX - missionCardsOffsetX - MissionCard.WINDOW_INSIDE_X;
            double missionCardsRelativeY = pMouseY - missionCardsOffsetY - MissionCard.WINDOW_INSIDE_Y;

            if (missionCardsRelativeY > 0.0D && missionCardsRelativeY < MissionCard.WINDOW_INSIDE_HEIGHT) {
                List<MissionCard> subset = new ArrayList<>(this.cards);
                subset.removeIf(card -> card.getMission().getGrade() != this.grade);

                for (int i = 0; i < subset.size(); i++) {
                    int insideX = -this.missionCardsSlider.getValueInt() + (i * (MissionCard.WINDOW_WIDTH + MissionCard.OUTER_PADDING));

                    double relativeX = missionCardsRelativeX - insideX;

                    if (relativeX > 0.0D && relativeX < MissionCard.WINDOW_INSIDE_WIDTH) {
                        this.selected = this.selected == subset.get(i) ? null : subset.get(i);
                        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    protected void init() {
        super.init();

        if (!this.initialized) {
            PacketHandler.sendToServer(new SearchForMissionsC2SPacket());
            this.initialized = true;
        }

        if (this.minecraft == null || this.minecraft.level == null) return;

        this.cards.clear();

        IMissionLevelData levelData = this.minecraft.level.getData(JJKAttachmentTypes.MISSION_LEVEL);

        Set<Mission> missions = levelData.getMissions();

        for (Mission mission : missions) {
            this.cards.add(new MissionCard(this.minecraft, mission));
        }

        int windowOffsetX = WINDOW_OFFSET_X;
        int windowOffsetY = (this.height - WINDOW_HEIGHT) / 2;

        int missionCardsOffsetX = windowOffsetX + WINDOW_WIDTH + MISSION_CARDS_OFFSET_X;
        int missionCardsOffsetY = (this.height - MissionCard.WINDOW_HEIGHT) / 2;

        List<MissionCard> subset = new ArrayList<>(this.cards);
        subset.removeIf(card -> card.getMission().getGrade() != this.grade);

        this.missionGradesSlider = new VerticalSlider(windowOffsetX + WINDOW_INSIDE_X + WINDOW_INSIDE_WIDTH - 8, windowOffsetY + WINDOW_INSIDE_Y,
                8, WINDOW_INSIDE_HEIGHT, Component.empty(), Component.empty(), 0.0D,
                (MissionGrade.values().length * MISSION_GRADE_SIZE) - WINDOW_INSIDE_HEIGHT,
                0, 0.1D, 0, false);
        this.addRenderableWidget(this.missionGradesSlider);

        this.missionCardsSlider = new ScrollableSlider(missionCardsOffsetX, missionCardsOffsetY + MissionCard.WINDOW_HEIGHT + MissionCard.OUTER_PADDING,
                this.width - missionCardsOffsetX - MISSION_CARDS_OFFSET_X, 8, Component.empty(), Component.empty(), 0.0D,
                Math.max(0, (subset.size() * (MissionCard.WINDOW_WIDTH + MissionCard.OUTER_PADDING) - MissionCard.OUTER_PADDING) -
                        (this.width - missionCardsOffsetX - MISSION_CARDS_OFFSET_X)),
                0, 0.1D, 0, false);
        this.addRenderableWidget(this.missionCardsSlider);

        this.acceptButton = Button.builder(Component.translatable(String.format("gui.%s.missions.accept", JujutsuKaisen.MOD_ID)), pButton -> {
            if (this.minecraft.level == null || this.minecraft.player == null) return;

            if (this.selected == null) return;

            Mission mission = this.selected.getMission();

            PacketHandler.sendToServer(new AcceptMissionC2SPacket(mission.getPos()));

            IJujutsuCapability cap = this.minecraft.player.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IMissionEntityData entityData = cap.getMissionData();

            entityData.setMission(mission);

            this.onClose();
        }).pos(missionCardsOffsetX + (this.width - missionCardsOffsetX - MISSION_CARDS_OFFSET_X - ACCEPT_BUTTON_WIDTH) / 2,
                        missionCardsOffsetY + MissionCard.WINDOW_HEIGHT + MissionCard.OUTER_PADDING + 10)
                .size(ACCEPT_BUTTON_WIDTH, ACCEPT_BUTTON_HEIGHT).build();
        this.addRenderableWidget(this.acceptButton);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        // If current accepted mission is not null render a text containing when the mission expires

        int windowOffsetX = WINDOW_OFFSET_X;
        int windowOffsetY = (this.height - WINDOW_HEIGHT) / 2;

        int missionCardOffsetX = WINDOW_OFFSET_X + WINDOW_WIDTH + MISSION_CARDS_OFFSET_X;
        int missionCardOffsetY = (this.height - MissionCard.WINDOW_HEIGHT) / 2;

        this.renderBackground(pGuiGraphics);
        this.renderWindow(pGuiGraphics, windowOffsetX, windowOffsetY);
        this.renderMissionGrades(pGuiGraphics, windowOffsetX, windowOffsetY);
        this.renderMissionCards(pGuiGraphics, missionCardOffsetX, missionCardOffsetY);
        this.renderMissionCardTooltips(pGuiGraphics, pMouseX, pMouseY, missionCardOffsetX, missionCardOffsetY);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    private void renderBackground(GuiGraphics graphics) {
        this.renderTransparentBackground(graphics);
    }

    private void renderWindow(GuiGraphics graphics, int offsetX, int offsetY) {
        RenderSystem.enableBlend();
        graphics.blit(WINDOW, offsetX, offsetY, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    private void renderMissionGrades(GuiGraphics graphics, int offsetX, int offsetY) {
        int x = offsetX + WINDOW_INSIDE_X;
        int y = offsetY + WINDOW_INSIDE_Y;

        graphics.enableScissor(x, y, x + WINDOW_INSIDE_WIDTH, y + WINDOW_INSIDE_HEIGHT);
        graphics.pose().pushPose();
        graphics.pose().translate(0.0D, -this.missionGradesSlider.getValue(), 0.0D);

        float scale = 2.0F;

        for (int i = 0; i < MissionGrade.values().length; i++) {
            int centerX = x + MISSION_GRADE_SIZE / 2;
            int centerY = y + MISSION_GRADE_SIZE / 2 - Math.round(this.font.lineHeight + 4 * scale) / 2 + i * MISSION_GRADE_SIZE;

            graphics.pose().pushPose();
            graphics.pose().scale(scale, scale, scale);
            graphics.drawCenteredString(this.font, MissionGrade.values()[i].name(), Math.round(centerX * (1.0F / scale)),
                    Math.round(centerY * (1.0F / scale)), MissionGrade.values()[i].getColor());
            graphics.pose().popPose();

            graphics.blit(MISSION_GRADE, x, y + i * MISSION_GRADE_SIZE, this.grade.ordinal() == i ? MISSION_GRADE_SIZE : 0, 0,
                    MISSION_GRADE_SIZE, MISSION_GRADE_SIZE);
        }
        graphics.pose().popPose();
        graphics.disableScissor();
    }

    private void renderMissionCards(GuiGraphics graphics, int offsetX, int offsetY) {
        if (this.minecraft == null || this.minecraft.player == null) return;

        graphics.enableScissor(offsetX, 0, this.width - MISSION_CARDS_OFFSET_X, this.height);

        List<MissionCard> subset = new ArrayList<>(this.cards);
        subset.removeIf(card -> card.getMission().getGrade() != this.grade);

        for (int i = 0; i < subset.size(); i++) {
            int insideX = offsetX - this.missionCardsSlider.getValueInt() + (i * (MissionCard.WINDOW_WIDTH + MissionCard.OUTER_PADDING)) + MissionCard.WINDOW_INSIDE_X;
            int insideY = offsetY + MissionCard.WINDOW_INSIDE_Y;

            int windowX = offsetX - this.missionCardsSlider.getValueInt() + i * (MissionCard.WINDOW_WIDTH + MissionCard.OUTER_PADDING);
            int windowY = offsetY;

            subset.get(i).drawInside(graphics, insideX, insideY);
            subset.get(i).drawWindow(graphics, windowX, windowY, this.selected == subset.get(i));
        }
        graphics.disableScissor();
    }

    private void renderMissionCardTooltips(GuiGraphics graphics, int mouseX, int mouseY, int offsetX, int offsetY) {
        double x = mouseX - offsetX;
        double y = mouseY - offsetY;

        if (y > 0.0D && y < MissionCard.WINDOW_INSIDE_Y) {
            List<MissionCard> subset = new ArrayList<>(this.cards);
            subset.removeIf(card -> card.getMission().getGrade() != this.grade);

            for (int i = 0; i < subset.size(); i++) {
                int insideX = -this.missionCardsSlider.getValueInt() + (i * (MissionCard.WINDOW_WIDTH + MissionCard.OUTER_PADDING));

                double relativeX = x - insideX;

                if (relativeX > 0.0D && relativeX < MissionCard.WINDOW_WIDTH) {
                    subset.get(i).drawTooltip(graphics, mouseX, mouseY);
                    break;
                }
            }
        }
    }
}
