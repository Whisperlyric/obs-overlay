package me.zziger.obsoverlay.mixin;

import com.mojang.blaze3d.platform.Window;
import me.zziger.obsoverlay.OBSOverlay;
import me.zziger.obsoverlay.mixin.accessor.GuiGraphicsExtractorAccessor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsExtractorMixin {

    @Inject(method = "applyCursor", at = @At("RETURN"))
    private void onApplyCursor(Window window, CallbackInfo ci) {
        if (!OBSOverlay.getIsInitialized()) return;

        GuiGraphicsExtractor overlayGuiGraphicsExtractor = OBSOverlay.getAPI().getOverlayGuiGraphicsExtractor();
        GuiGraphicsExtractorAccessor accessor = (GuiGraphicsExtractorAccessor) overlayGuiGraphicsExtractor;

        if (accessor.getPendingCursor() != null && accessor.getPendingCursor() != com.mojang.blaze3d.platform.cursor.CursorType.DEFAULT) {
            overlayGuiGraphicsExtractor.applyCursor(window);
        }
    }
}