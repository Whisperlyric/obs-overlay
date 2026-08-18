package me.zziger.obsoverlay.mixin;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.cursor.CursorType;
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
        if (overlayGuiGraphicsExtractor == null) return;

        GuiGraphicsExtractorAccessor accessor = (GuiGraphicsExtractorAccessor) overlayGuiGraphicsExtractor;

        if (accessor.getPendingCursor() != null && accessor.getPendingCursor() != CursorType.DEFAULT) {
            // Can't call applyCursor() here: the mixin is class-level, so calling it on
            // the overlay extractor would re-enter this handler -> infinite recursion.
            // applyCursor() just forwards to window.selectCursor() anyway.
            window.selectCursor(accessor.getPendingCursor());
        }
    }
}