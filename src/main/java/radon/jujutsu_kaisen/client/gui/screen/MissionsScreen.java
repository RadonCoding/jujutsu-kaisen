package radon.jujutsu_kaisen.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.gui.screen.widget.ScrollableSlider;
import radon.jujutsu_kaisen.client.gui.screen.widget.VerticalSlider;

import java.util.*;
import java.util.stream.Collectors;

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

    private final Map<MissionGrade, List<MissionCard>> missions;

    private ExtendedSlider missionGradesSlider;
    private ExtendedSlider missionCardsSlider;

    private MissionGrade grade;

    private boolean isScrolling;

    public MissionsScreen() {
        super(GameNarrator.NO_TITLE);

        this.missions = new LinkedHashMap<>();
        this.grade = MissionGrade.D;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (pButton != 0) {
            this.isScrolling = false;
            return false;
        } else {
            if (!this.isScrolling) {
                this.isScrolling = true;
            } else {
                int offsetX = WINDOW_OFFSET_X + WINDOW_WIDTH + MISSION_CARDS_OFFSET_X;
                int offsetY = (this.height - MissionCard.WINDOW_HEIGHT) / 2;

                double x = pMouseX - offsetX - MissionCard.WINDOW_INSIDE_X;
                double y = pMouseY - offsetY - MissionCard.WINDOW_INSIDE_Y;

                if (y > 0.0D && y < WINDOW_INSIDE_HEIGHT) {
                    List<MissionCard> missions = this.missions.get(this.grade);

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
            return true;
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.minecraft == null) return super.mouseClicked(pMouseX, pMouseY, pButton);

        int windowOffsetX = WINDOW_OFFSET_X;
        int windowOffsetY = (this.height - WINDOW_HEIGHT) / 2;

        double x = pMouseX - windowOffsetX - WINDOW_INSIDE_X;
        double y = pMouseY - windowOffsetY - WINDOW_INSIDE_Y;

        if (x > 0.0D && x < MISSION_GRADE_SIZE && y > 0.0D && y < WINDOW_INSIDE_HEIGHT) {
            for (int i = 0; i < MissionGrade.values().length; i++) {
                double offset = (i * MISSION_GRADE_SIZE) - this.missionGradesSlider.getValue();

                double relative = y - offset;

                if (relative > 0.0D && relative < MISSION_GRADE_SIZE) {
                    this.grade = MissionGrade.values()[i];
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private void setGrade(MissionGrade grade) {
        this.grade = grade;

        this.rebuildWidgets();
    }

    @Override
    protected void init() {
        super.init();

        this.missions.clear();

        List<Mission> d = new ArrayList<>();
        d.add(new Mission(Component.literal("D-tier mission title"), Component.literal("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")));
        d.add(new Mission(Component.literal("D-tier mission title"), Component.literal("D-tier mission description")));
        d.add(new Mission(Component.literal("D-tier mission title"), Component.literal("D-tier mission description")));
        d.add(new Mission(Component.literal("D-tier mission title"), Component.literal("D-tier mission description")));
        d.add(new Mission(Component.literal("D-tier mission title"), Component.literal("D-tier mission description")));
        d.add(new Mission(Component.literal("D-tier mission title"), Component.literal("D-tier mission description")));
        d.add(new Mission(Component.literal("D-tier mission title"), Component.literal("D-tier mission description")));
        d.add(new Mission(Component.literal("D-tier mission title"), Component.literal("D-tier mission description")));
        d.add(new Mission(Component.literal("D-tier mission title"), Component.literal("D-tier mission description")));
        d.add(new Mission(Component.literal("D-tier mission title"), Component.literal("D-tier mission description")));

        List<Mission> c = new ArrayList<>();
        c.add(new Mission(Component.literal("C-tier mission title"), Component.literal("C-tier mission description")));

        List<Mission> b = new ArrayList<>();
        b.add(new Mission(Component.literal("B-tier mission title"), Component.literal("B-tier mission description")));

        List<Mission> a = new ArrayList<>();
        a.add(new Mission(Component.literal("A-tier mission title"), Component.literal("A-tier mission description")));

        List<Mission> s = new ArrayList<>();
        s.add(new Mission(Component.literal("S-tier mission title"), Component.literal("S-tier mission description")));

        this.missions.put(MissionGrade.D, d.stream().map(mission -> new MissionCard(this.minecraft, mission)).collect(Collectors.toList()));
        this.missions.put(MissionGrade.C, c.stream().map(mission -> new MissionCard(this.minecraft, mission)).collect(Collectors.toList()));
        this.missions.put(MissionGrade.B, b.stream().map(mission -> new MissionCard(this.minecraft, mission)).collect(Collectors.toList()));
        this.missions.put(MissionGrade.A, a.stream().map(mission -> new MissionCard(this.minecraft, mission)).collect(Collectors.toList()));
        this.missions.put(MissionGrade.S, s.stream().map(mission -> new MissionCard(this.minecraft, mission)).collect(Collectors.toList()));

        int windowOffsetX = WINDOW_OFFSET_X;
        int windowOffsetY = (this.height - WINDOW_HEIGHT) / 2;

        int missionCardOffsetX = windowOffsetX + WINDOW_WIDTH + MISSION_CARDS_OFFSET_X;
        int missionCardOffsetY = (this.height - MissionCard.WINDOW_HEIGHT) / 2;

        this.missionGradesSlider = new VerticalSlider(windowOffsetX + WINDOW_INSIDE_X + WINDOW_INSIDE_WIDTH - 8, windowOffsetY + WINDOW_INSIDE_Y,
                8, WINDOW_INSIDE_HEIGHT, Component.empty(), Component.empty(), 0.0D,
                (MissionGrade.values().length * MISSION_GRADE_SIZE) - WINDOW_INSIDE_HEIGHT,
                0, 0.1D, 0, false);
        this.addRenderableWidget(this.missionGradesSlider);

        this.missionCardsSlider = new ScrollableSlider(missionCardOffsetX, missionCardOffsetY + MissionCard.WINDOW_HEIGHT + MissionCard.OUTER_PADDING,
                this.width - missionCardOffsetX - MISSION_CARDS_OFFSET_X, 8, Component.empty(), Component.empty(), 0.0D,
                Math.max(0, (this.missions.get(this.grade).size() * (MissionCard.WINDOW_WIDTH + MissionCard.OUTER_PADDING) - MissionCard.OUTER_PADDING) - (this.width - missionCardOffsetX - MISSION_CARDS_OFFSET_X)),
                0, 0.1D, 0, false);
        this.addRenderableWidget(this.missionCardsSlider);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
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
        graphics.enableScissor(offsetX, 0, this.width - MISSION_CARDS_OFFSET_X, this.height);

        List<MissionCard> missions = this.missions.get(this.grade);

        for (int i = 0; i < missions.size(); i++) {
            int insideX = offsetX - this.missionCardsSlider.getValueInt() + (i * (MissionCard.WINDOW_WIDTH + MissionCard.OUTER_PADDING)) + MissionCard.WINDOW_INSIDE_X;
            int insideY = offsetY + MissionCard.WINDOW_INSIDE_Y;

            int windowX = offsetX - this.missionCardsSlider.getValueInt() + i * (MissionCard.WINDOW_WIDTH + MissionCard.OUTER_PADDING);
            int windowY = offsetY;

            missions.get(i).drawInside(graphics, insideX, insideY);
            missions.get(i).drawWindow(graphics, windowX, windowY);
        }
        graphics.disableScissor();
    }

    private void renderMissionCardTooltips(GuiGraphics graphics, int mouseX, int mouseY, int offsetX, int offsetY) {
        double x = mouseX - offsetX;
        double y = mouseY - offsetY;

        if (y > 0.0D && y < MissionCard.WINDOW_INSIDE_Y) {
            List<MissionCard> missions = this.missions.get(this.grade);

            for (int i = 0; i < missions.size(); i++) {
                int insideX = -this.missionCardsSlider.getValueInt() + (i * (MissionCard.WINDOW_WIDTH + MissionCard.OUTER_PADDING));

                double relativeX = x - insideX;

                if (relativeX > 0.0D && relativeX < MissionCard.WINDOW_WIDTH) {
                    missions.get(i).drawTooltip(graphics, mouseX, mouseY);
                    break;
                }
            }
        }
    }

    public record Mission(Component title, Component description) {}

    private enum MissionGrade {
        D(0x7FFFFF),
        C(0x7EFF80),
        B(0xFEFF7F),
        A(0xFFBF7F),
        S(0xFF7F7E);

        private final int color;

        MissionGrade(int color) {
            this.color = color;
        }

        public int getColor() {
            return this.color;
        }
    }
}
