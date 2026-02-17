package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.client.Minecraft;
import net.minecraft.client.CameraType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import com.mojang.blaze3d.vertex.PoseStack;

@Mod(Bid.MODID)
public class Bid {
    public static final String MODID = "bid";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public Bid(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ITEMS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
        context.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) { }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("server starting");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onRenderPlayer(RenderPlayerEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            if (event.getEntity() != mc.player) return;
            if (mc.options.getCameraType() == CameraType.FIRST_PERSON) return;
            PoseStack pose = event.getPoseStack();
            pose.pushPose();
            double w = Config.BOX_WIDTH.get();
            double h = Config.BOX_HEIGHT.get();
            float scale = Config.SCALE.get().floatValue();
            float up = event.getEntity().getBbHeight();
            pose.translate(0.0, up, 0.0);
            float yaw = event.getEntity().getYHeadRot();
            float rad = yaw * ((float)Math.PI / 180F);
            double rx = Mth.cos(rad), rz = Mth.sin(rad);
            double fx = -Mth.sin(rad), fz = Mth.cos(rad);
            double ox = Config.OFFSET_X.get(), oy = Config.OFFSET_Y.get(), oz = Config.OFFSET_Z.get();
            pose.translate(rx * ox + fx * oz, oy, rz * ox + fz * oz);
            pose.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
            pose.scale(-scale, -scale, scale);
            pose.translate(0.0, 0.0, Config.Z_BIAS.get());
            MultiBufferSource buffers = event.getMultiBufferSource();
            VertexConsumer line = buffers.getBuffer(RenderType.lines());
            int bcol = Config.BORDER_COLOR.get();
            int br = (bcol >> 16) & 0xFF, bgc = (bcol >> 8) & 0xFF, bb = bcol & 0xFF, ba = (bcol >> 24) & 0xFF;
            drawRectBorder(pose, line, 0, 0, w, h, br, bgc, bb, ba);
            int light = LightTexture.FULL_BRIGHT;
            Font font = mc.font;
            int yText = 6;
            int textColor = Config.TEXT_COLOR.get();
            int bgColor = Config.BACKGROUND_ENABLED.get() ? Config.BACKGROUND_COLOR.get() : 0;
            Font.DisplayMode mode = Config.USE_SEE_THROUGH.get() ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL;
            boolean shadow = Config.SHADOW.get();
            if (Config.SHOW_HP.get()) {
                String hp = "HP: " + (int)event.getEntity().getHealth() + "/" + (int)event.getEntity().getMaxHealth();
                font.drawInBatch(hp, 6, yText, textColor, shadow, pose.last().pose(), buffers, mode, bgColor, light);
                yText += 12;
            }
            if (Config.SHOW_HELD.get()) {
                String item = "Held: " + event.getEntity().getMainHandItem().getHoverName().getString();
                font.drawInBatch(item, 6, yText, textColor, shadow, pose.last().pose(), buffers, mode, bgColor, light);
                yText += 12;
            }
            if (Config.SHOW_EFFECTS.get()) {
                int step = 14;
                var mtm = mc.getMobEffectTextures();
                java.util.ArrayList<net.minecraft.client.renderer.texture.TextureAtlasSprite> list = new java.util.ArrayList<>();
                for (MobEffectInstance eff : event.getEntity().getActiveEffects()) {
                    var sprite = mtm.get(eff.getEffect());
                    if (sprite != null) list.add(sprite);
                }
                int x = 6;
                int y = (int)h - 6 - 12;
                for (var sprite : list) {
                    if (y < yText + 12) break;
                    ResourceLocation atlas = sprite.atlasLocation();
                    drawSprite(pose, buffers.getBuffer(RenderType.entityTranslucent(atlas)), x, y, 12, 12, sprite);
                    x += step;
                    if (x + 12 > (int)w - 6) { x = 6; y -= step; }
                }
            }
            pose.popPose();
        }
        private static void drawRectBorder(PoseStack pose, VertexConsumer vc, double x, double y, double w, double h, int r, int g, int b, int a) {
            float rf = r / 255f, gf = g / 255f, bf = b / 255f, af = a / 255f;
            vertexLine(vc, pose, x, y, 0, x + w, y, 0, rf, gf, bf, af);
            vertexLine(vc, pose, x + w, y, 0, x + w, y + h, 0, rf, gf, bf, af);
            vertexLine(vc, pose, x + w, y + h, 0, x, y + h, 0, rf, gf, bf, af);
            vertexLine(vc, pose, x, y + h, 0, x, y, 0, rf, gf, bf, af);
        }
        private static void vertexLine(VertexConsumer vc, PoseStack pose, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float a) {
            vc.vertex(pose.last().pose(), (float)x1, (float)y1, (float)z1).color(r, g, b, a).normal(pose.last().normal(), 0, 0, 1).endVertex();
            vc.vertex(pose.last().pose(), (float)x2, (float)y2, (float)z2).color(r, g, b, a).normal(pose.last().normal(), 0, 0, 1).endVertex();
        }

        private static void drawSprite(PoseStack pose, VertexConsumer vc, double x, double y, double w, double h, net.minecraft.client.renderer.texture.TextureAtlasSprite sprite) {
            float u0 = sprite.getU0(), v0 = sprite.getV0(), u1 = sprite.getU1(), v1 = sprite.getV1();
            vc.vertex(pose.last().pose(), (float)x, (float)y, 0)
                .color(1f, 1f, 1f, 1f)
                .uv(u0, v0)
                .overlayCoords(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.last().normal(), 0, 0, 1)
                .endVertex();
            vc.vertex(pose.last().pose(), (float)(x + w), (float)y, 0)
                .color(1f, 1f, 1f, 1f)
                .uv(u1, v0)
                .overlayCoords(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.last().normal(), 0, 0, 1)
                .endVertex();
            vc.vertex(pose.last().pose(), (float)(x + w), (float)(y + h), 0)
                .color(1f, 1f, 1f, 1f)
                .uv(u1, v1)
                .overlayCoords(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.last().normal(), 0, 0, 1)
                .endVertex();
            vc.vertex(pose.last().pose(), (float)x, (float)(y + h), 0)
                .color(1f, 1f, 1f, 1f)
                .uv(u0, v1)
                .overlayCoords(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.last().normal(), 0, 0, 1)
                .endVertex();
        }
    }
}
