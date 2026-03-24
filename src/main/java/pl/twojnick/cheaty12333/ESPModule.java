package pl.twojnick.cheaty12333;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

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

        // Używamy specjalnego layera, który lepiej działa w 1.21+
        VertexConsumer buffer = mc.getBufferBuilders()
                .getEntityVertexConsumers()
                .getBuffer(RenderLayer.getDebugQuads());   // <-- zmiana tutaj

        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue;
            if (!shouldRender(entity)) continue;

            Color color = getColor(entity);
            drawBox(matrices, buffer, entity, color);
        }

        matrices.pop();
    }

    private boolean shouldRender(Entity e) {
        if (e instanceof PlayerEntity) return players;
        if (e instanceof LivingEntity && !(e instanceof PlayerEntity)) return mobs;
        return e instanceof ItemEntity && items;
    }

    private Color getColor(Entity e) {
        if (e instanceof PlayerEntity) return Color.RED;
        if (e instanceof LivingEntity) return Color.YELLOW;
        return Color.CYAN;
    }

    private void drawBox(MatrixStack matrices, VertexConsumer buffer, Entity entity, Color color) {
        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = 0.4f;   // przezroczystość

        Matrix4f mat = matrices.peek().getPositionMatrix();

        float x1 = (float) box.minX, y1 = (float) box.minY, z1 = (float) box.minZ;
        float x2 = (float) box.maxX, y2 = (float) box.maxY, z2 = (float) box.maxZ;

        // Dolna podstawa
        line(buffer, mat, x1, y1, z1, x2, y1, z1, r, g, b, a);
        line(buffer, mat, x2, y1, z1, x2, y1, z2, r, g, b, a);
        line(buffer, mat, x2, y1, z2, x1, y1, z2, r, g, b, a);
        line(buffer, mat, x1, y1, z2, x1, y1, z1, r, g, b, a);

        // Górna podstawa
        line(buffer, mat, x1, y2, z1, x2, y2, z1, r, g, b, a);
        line(buffer, mat, x2, y2, z1, x2, y2, z2, r, g, b, a);
        line(buffer, mat, x2, y2, z2, x1, y2, z2, r, g, b, a);
        line(buffer, mat, x1, y2, z2, x1, y2, z1, r, g, b, a);

        // Pionowe krawędzie
        line(buffer, mat, x1, y1, z1, x1, y2, z1, r, g, b, a);
        line(buffer, mat, x2, y1, z1, x2, y2, z1, r, g, b, a);
        line(buffer, mat, x2, y1, z2, x2, y2, z2, r, g, b, a);
        line(buffer, mat, x1, y1, z2, x1, y2, z2, r, g, b, a);
    }

    private void line(VertexConsumer buffer, Matrix4f mat,
                      float x1, float y1, float z1,
                      float x2, float y2, float z2,
                      float r, float g, float b, float a) {
        buffer.vertex(mat, x1, y1, z1).color(r, g, b, a);
        buffer.vertex(mat, x2, y2, z2).color(r, g, b, a);
    }
}