package pl.twojnick.cheaty12333;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import java.awt.Color;

public class ESPModule {

    public boolean enabled = false;

    public boolean players = true;
    public boolean mobs = true;
    public boolean items = true;

    private final MinecraftClient mc = MinecraftClient.getInstance();

    public void toggle() {
        enabled = !enabled;
        System.out.println("[Cheaty12333] ESP: " + (enabled ? "ON" : "OFF"));
    }

    public void render(WorldRenderContext context) {
        if (!enabled || mc.world == null || mc.player == null) return;

        MatrixStack matrices = context.matrixStack();
        Vec3d camPos = context.camera().getPos();

        matrices.push();
        matrices.translate(-camPos.x, -camPos.y, -camPos.z);

        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue;
            if (!shouldRender(entity)) continue;

            Color color = getColor(entity);
            drawBox(matrices, entity, color);
        }

        matrices.pop();
    }

    private boolean shouldRender(Entity e) {
        if (e instanceof PlayerEntity) return players;
        if (e instanceof LivingEntity) return mobs;
        if (e instanceof ItemEntity)   return items;
        return false;
    }

    private Color getColor(Entity e) {
        if (e instanceof PlayerEntity) return Color.RED;
        if (e instanceof LivingEntity) return Color.YELLOW;
        return Color.CYAN;
    }

    private void drawBox(MatrixStack matrices, Entity entity, Color color) {
        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = 0.5f;

        VertexConsumer buffer = MinecraftClient.getInstance().getBufferBuilders()
                .getEntityVertexConsumers().getBuffer(RenderLayer.getLines());

        WorldRenderer.drawBox(matrices, buffer, box, r, g, b, a);
    }
}