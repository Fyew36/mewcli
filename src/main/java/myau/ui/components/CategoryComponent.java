package myau.ui.components;

import myau.Myau;
import myau.module.Module;
import myau.module.modules.HUD;
import myau.ui.ClickGui;
import myau.ui.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CategoryComponent {
    private final int MAX_HEIGHT = 300;

    public ArrayList<Component> modulesInCategory = new ArrayList<>();
    public String categoryName;
    private boolean categoryOpened;
    private int width;
    private int y;
    private int x;
    private final int bh;
    public boolean dragging;
    public int xx;
    public int yy;
    public boolean pin = false;
    private double marginY, marginX;
    private int scroll = 0;
    private double animScroll = 0;
    private int height = 0;
    private double animOpen = 0.0;
    private float hoverFade = 0.0F;

    public CategoryComponent(String category, List<Module> modules) {
        this.categoryName = category;
        this.width = 92;
        this.x = 5;
        this.y = 5;
        this.bh = 13;
        this.xx = 0;
        this.categoryOpened = false;
        this.dragging = false;
        int tY = this.bh + 3;
        this.marginX = 80;
        this.marginY = 4.5;
        for (Module mod : modules) {
            ModuleComponent b = new ModuleComponent(mod, this, tY);
            this.modulesInCategory.add(b);
            tY += 16;
        }
    }

    public ArrayList<Component> getModules() {
        return this.modulesInCategory;
    }

    public void setX(int n) {
        this.x = n;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void mousePressed(boolean d) {
        this.dragging = d;
    }

    public boolean isPin() {
        return this.pin;
    }

    public void setPin(boolean on) {
        this.pin = on;
    }

    public boolean isOpened() {
        return this.categoryOpened;
    }

    public void setOpened(boolean on) {
        this.categoryOpened = on;
    }

    private void drawRoundedRect(int x, int y, int w, int h, int color) {
        Gui.drawRect(x + 2, y, x + w - 2, y + h, color);
        Gui.drawRect(x, y + 2, x + w, y + h - 2, color);
        Gui.drawRect(x + 1, y + 1, x + w - 1, y + h - 1, color);
    }

    private void drawGradientRect(int x, int y, int w, int h, int color1, int color2) {
        Gui.drawRect(x, y, x + w, y + h, color1);
        Gui.drawRect(x, y + h / 2, x + w, y + h, color2);
    }

    public void render(FontRenderer renderer) {
        this.width = 92;
        update();
        height = 0;
        for (Component moduleRenderManager : this.modulesInCategory) {
            height += moduleRenderManager.getHeight();
        }
        int maxScroll = Math.max(0, height - MAX_HEIGHT);
        if (scroll > maxScroll) scroll = maxScroll;
        if (animScroll > maxScroll) animScroll = maxScroll;
        animScroll += (scroll - animScroll) * 0.2;

        if (ClickGui.isModern()) {
            boolean hovered = insideAreaHover();
            hoverFade += (hovered ? 0.08F : -0.08F);
            hoverFade = Math.max(0.0F, Math.min(1.0F, hoverFade));

            animOpen += (categoryOpened ? 0.15 : -0.15);
            animOpen = Math.max(0.0, Math.min(1.0, animOpen));
            int displayHeight = (int) (Math.min(height, MAX_HEIGHT) * animOpen);

            int headerColor = new Color(15, 15, 20, 230).getRGB();
            int bodyColor = new Color(10, 10, 15, 180).getRGB();

            if (displayHeight > 0) {
                drawRoundedRect(this.x, this.y + this.bh + 3, this.width, displayHeight, bodyColor);
            }

            drawRoundedRect(this.x, this.y, this.width, this.bh + 3, headerColor);

            if (hoverFade > 0.01F) {
                int glowAlpha = (int) (40 * hoverFade);
                int glowColor = ((HUD) Myau.moduleManager.modules.get(HUD.class)).getColor(System.currentTimeMillis(), 0).getRGB();
                Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.bh + 3, glowAlpha << 24 | (glowColor & 0x00FFFFFF));
            }

            renderer.drawString(this.categoryName, (float) (this.x + 4), (float) (this.y + 4), -1, false);
            String toggleIcon = categoryOpened ? "-" : "+";
            renderer.drawString(toggleIcon, (float) (this.x + marginX), (float) ((double) this.y + marginY), new Color(180, 180, 180).getRGB(), false);

            if (displayHeight > 0 && !this.modulesInCategory.isEmpty()) {
                int renderHeight = 0;
                ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
                double scale = sr.getScaleFactor();
                int bottom = this.y + this.bh + MAX_HEIGHT + 3;
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                GL11.glScissor((int) (this.x * scale), (int) ((sr.getScaledHeight() - bottom) * scale), (int) (this.width * scale), (int) (MAX_HEIGHT * scale));
                for (Component c2 : this.modulesInCategory) {
                    int compHeight = c2.getHeight();
                    if (renderHeight + compHeight > animScroll &&
                            renderHeight < animScroll + MAX_HEIGHT) {
                        int drawY = (int) (renderHeight - animScroll);
                        c2.setComponentStartAt(this.bh + 3 + drawY);
                        c2.draw(new AtomicInteger(0));
                    }
                    renderHeight += compHeight;
                }
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                if (height > MAX_HEIGHT) {
                    float scrollY = (float) this.y + this.bh + 3 + (float) (animScroll * MAX_HEIGHT / height);
                    Gui.drawRect(this.x + this.width - 2, (int) scrollY, this.x + this.width, (int) (scrollY + ((float) MAX_HEIGHT * MAX_HEIGHT / height)), new Color(255, 255, 255, 60).getRGB());
                }
            }
        } else {
            if (!this.modulesInCategory.isEmpty() && this.categoryOpened) {
                int displayHeight = Math.min(height, MAX_HEIGHT);
                Gui.drawRect(this.x - 1, this.y, this.x + this.width + 1, this.y + this.bh + displayHeight + 4, new Color(0, 0, 0, 100).getRGB());
            }
            Gui.drawRect((this.x - 2), this.y, (this.x + this.width + 2), (this.y + this.bh + 3), new Color(0, 0, 0, 200).getRGB());
            renderer.drawString(this.categoryName, (float) (this.x + 2), (float) (this.y + 4), -1, false);
            renderer.drawString(this.categoryOpened ? "-" : "+", (float) (this.x + marginX), (float) ((double) this.y + marginY), Color.white.getRGB(), false);
            if (this.categoryOpened && !this.modulesInCategory.isEmpty()) {
                int renderHeight = 0;
                ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
                double scale = sr.getScaleFactor();
                int bottom = this.y + this.bh + MAX_HEIGHT + 3;
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                GL11.glScissor((int) (this.x * scale), (int) ((sr.getScaledHeight() - bottom) * scale), (int) (this.width * scale), (int) (MAX_HEIGHT * scale));
                for (Component c2 : this.modulesInCategory) {
                    int compHeight = c2.getHeight();
                    if (renderHeight + compHeight > animScroll &&
                            renderHeight < animScroll + MAX_HEIGHT) {
                        int drawY = (int) (renderHeight - animScroll);
                        c2.setComponentStartAt(this.bh + 3 + drawY);
                        c2.draw(new AtomicInteger(0));
                    }
                    renderHeight += compHeight;
                }
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                if (height > MAX_HEIGHT) {
                    float scrollY = (float) this.y + this.bh + 3 + (float) (animScroll * MAX_HEIGHT / height);
                    Gui.drawRect(this.x + this.width - 2, (int) scrollY, this.x + this.width, (int) (scrollY + ((float) MAX_HEIGHT * MAX_HEIGHT / height)), new Color(255, 255, 255, 60).getRGB());
                }
            }
        }
    }

    private boolean insideAreaHover() {
        int mx = org.lwjgl.input.Mouse.getX() * new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth() / Minecraft.getMinecraft().displayWidth;
        int my = new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() - org.lwjgl.input.Mouse.getY() * new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;
        return mx >= this.x && mx <= this.x + this.width && my >= this.y && my <= this.y + this.bh;
    }

    public void update() {
        int offset = this.bh + 3;
        for (Component component : this.modulesInCategory) {
            component.setComponentStartAt(offset);
            offset += component.getHeight();
        }
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public void handleDrag(int x, int y) {
        if (this.dragging) {
            this.setX(x - this.xx);
            this.setY(y - this.yy);
        }
    }

    public boolean isHovered(int x, int y) {
        return x >= this.x + 92 - 13 && x <= this.x + this.width && (float) y >= (float) this.y + 2.0F && y <= this.y + this.bh + 1;
    }

    public boolean mousePressed(int x, int y) {
        return x >= this.x + 77 && x <= this.x + this.width - 6 && (float) y >= (float) this.y + 2.0F && y <= this.y + this.bh + 1;
    }

    public boolean insideArea(int x, int y) {
        return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.bh;
    }

    public String getName() {
        return categoryName;
    }

    public void setLocation(int parseInt, int parseInt1) {
        this.x = parseInt;
        this.y = parseInt1;
    }

    public void onScroll(int mouseX, int mouseY, int scrollAmount) {
        if (!categoryOpened || height <= MAX_HEIGHT) return;

        int areaTop = this.y + this.bh;
        int areaBottom = this.y + this.bh + MAX_HEIGHT;

        if (mouseX >= this.x && mouseX <= this.x + width && mouseY >= areaTop && mouseY <= areaBottom) {
            scroll -= scrollAmount * 12;
            scroll = Math.max(0, Math.min(scroll, height - MAX_HEIGHT));
        }
    }
}
