package mew.module.modules;

import mew.module.Module;

public class NoHitDelay extends Module {
    public NoHitDelay() {
        super("NoHitDelay", true, true);
    }

    @Override
    public String getCategory() { return "Combat"; }
}
