package me.zziger.obsoverlay.mixin;

import me.zziger.obsoverlay.OBSOverlay;
import me.zziger.obsoverlay.OBSOverlayConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
    private void onExtractBackground(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        Screen screen = (Screen) (Object) this;
        if (OBSOverlayConfig.isScreenOverlayed(screen)) {
            ci.cancel();
        }
    }


    @ModifyVariable(method = "extractRenderState", at = @At("HEAD"), argsOnly = true, index = 1)
    private GuiGraphicsExtractor onExtractRenderState(GuiGraphicsExtractor value) {
        Screen screen = (Screen) (Object) this;
        if (OBSOverlayConfig.isScreenOverlayed(screen)) {
            return OBSOverlay.getAPI().getOverlayGuiGraphicsExtractor();
        }
        return value;
    }
}