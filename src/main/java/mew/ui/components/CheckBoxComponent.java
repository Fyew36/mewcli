package mew.ui.components;

import mew.Mew;
import mew.enums.ChatColors;
import mew.module.modules.HUD;
import mew.property.properties.BooleanProperty;
import mew.ui.ClickGui;
import mew.ui.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CheckBoxComponent implements Component {
    private final BooleanProperty property;
    private final ModuleComponent module;
    private int offsetY;
    private int x;
    private int y;

    public CheckBoxComponent(BooleanProperty property, ModuleComponent parentModule, int offsetY) {
        this.property = property;
        this.module = parentModule;
        this.x = parentModule.category.getX() + parentModule.category.getWidth();
        this.y = parentModule.category.getY() + parentModule.offsetY;
        this.offsetY = offsetY;
    }

    public void draw(AtomicInteger offset) {
        if (ClickGui.isModern()) {
            int cx = this.module.category.getX() + 4;
            int cy = this.module.category.getY() + this.offsetY + 2;
            int toggleSize = 8;

            int bgColor = this.property.getValue()
                    ? ((HUD) Mew.moduleManager.modules.get(HUD.class)).getColor(System.currentTimeMillis(), offset.get()).getRGB()
                    : new Color(60, 60, 70).getRGB();

            Gui.drawRect(cx, cy, cx + toggleSize, cy + toggleSize, bgColor);

            if (this.property.getValue()) {
                Gui.drawRect(cx + 2, cy + 2, cx + toggleSize - 2, cy + toggleSize - 2, new Color(255, 255, 255, 200).getRGB());
            }

            GL11.glPushMatrix();
            GL11.glScaled(0.5D, 0.5D, 0.5D);
            Minecraft.getMinecraft().fontRendererObj.drawString(
                    this.property.getName().replace("-", " "),
                    (float) ((this.module.category.getX() + 14) * 2),
                    (float) ((this.module.category.getY() + this.offsetY + 3) * 2),
                    -1, false
            );
            GL11.glPopMatrix();
        } else {
            GL11.glPushMatrix();
            GL11.glScaled(0.5D, 0.5D, 0.5D);
            Minecraft.getMinecraft().fontRendererObj.drawString(
                    this.property.getName().replace("-", " ") + ": " + ChatColors.formatColor(this.property.formatValue()),
                    (float) ((this.module.category.getX() + 4) * 2),
                    (float) ((this.module.category.getY() + this.offsetY + 5) * 2),
                    -1, false
            );
            GL11.glPopMatrix();
        }
    }

    public void setComponentStartAt(int newOffsetY) {
        this.offsetY = newOffsetY;
    }

    @Override
    public int getHeight() {
        return 12;
    }

    public void update(int mousePosX, int mousePosY) {
        this.y = this.module.category.getY() + this.offsetY;
        this.x = this.module.category.getX();
    }

    public void mouseDown(int x, int y, int button) {
        if (this.isHovered(x, y) && button == 0 && this.module.panelExpand) {
            this.property.setValue(!this.property.getValue());
        }
    }

    @Override
    public void mouseReleased(int x, int y, int button) {

    }

    @Override
    public void keyTyped(char chatTyped, int keyCode) {

    }

    public boolean isHovered(int x, int y) {
        return x > this.x && x < this.x + this.module.category.getWidth() && y > this.y && y < this.y + 11;
    }

    @Override
    public boolean isVisible() {
        return property.isVisible();
    }
}
