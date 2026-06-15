package mew.module.modules;

import mew.module.Module;
import mew.property.properties.PercentProperty;

public class NoHurtCam extends Module {
    public final PercentProperty multiplier = new PercentProperty("multiplier", 0);

    public NoHurtCam() {
        super("NoHurtCam", false, true);
    }

    @Override
    public String getCategory() { return "Render"; }
}
