package me.zziger.obsoverlay.mixin;

import me.zziger.obsoverlay.OBSOverlayConfig;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VideoSettingsScreen.class)
public abstract class VideoSettingsScreenMixin extends OptionsSubScreen {

    public VideoSettingsScreenMixin(Screen parent, Options gameOptions, Component title) {
        super(parent, gameOptions, title);
    }

    @Inject(method = "addOptions", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        if (this.list != null) {
            this.list.addSmall(Button.builder(Component.translatable("obs_overlay.config.title"), (button) -> {
                //#if MC >= 260200
                //$$ // Minecraft.setScreen moved to Minecraft.gui.setScreen in 26.2
                //$$ this.minecraft.gui.setScreen(OBSOverlayConfig.getScreenSupplier((VideoSettingsScreen) (Object) this).get());
                //#else
                this.minecraft.setScreen(OBSOverlayConfig.getScreenSupplier((VideoSettingsScreen) (Object) this).get());
                //#endif
            }).build(), null);
        }
    }
}
