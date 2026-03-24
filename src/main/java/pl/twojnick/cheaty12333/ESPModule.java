package pl.twojnick.mycheats.module;

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

    public boolean enabled = true;
    public boolean players = true;
    public boolean mobs = true;
    public boolean items = true;
    public boolean tracers = true;   // linie od środka ekranu

    private final MinecraftClient mc = MinecraftClient.getInstance();

    public void render(WorldRenderContext context) {
        if (!enabled || mc.world == null || mc.player == null) return;

        MatrixStack matrices = context.matrixStack();
        Vec3d cameraPos = context.camera().getPos();

        matrices.push();

        // Przesuń rendering względem kamery
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue;           // pomiń siebie
            if (!shouldRender(entity)) continue;

            Color color = getColor(entity);

            drawBox(matrices, entity, color);
            if (tracers) drawTracer(matrices, entity, color);
        }

        matrices.pop();
    }

    private boolean shouldRender(Entity e) {
        if (e instanceof PlayerEntity && players) return true;
        if (e instanceof LivingEntity && !(e instanceof PlayerEntity) && mobs) return true;
        if (e instanceof ItemEntity && items) return true;
        return false;
    }

    private Color getColor(Entity e) {
        if (e instanceof PlayerEntity) return Color.RED;
        if (e instanceof LivingEntity) return Color.YELLOW;
        if (e instanceof ItemEntity)   return Color.CYAN;
        return Color.WHITE;
    }

    // Rysowanie boxa 3D wokół entity
    private void drawBox(MatrixStack matrices, Entity entity, Color color) {
        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = 0.4f;  // przezroczystość

        VertexConsumerProvider.Immediate vertices = mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer buffer = vertices.getBuffer(RenderLayer.getLines());

        Matrix4f posMat = matrices.peek().getPositionMatrix();

        // Rysujemy 12 krawędzi boxa (proste linie)
        WorldRenderer.drawBox(matrices, buffer, box, r, g, b, a);
    }

    // Tracers – linia od środka ekranu do entity
    private void drawTracer(MatrixStack matrices, Entity entity, Color color) {
        Vec3d entityPos = entity.getPos().add(0, entity.getHeight() / 2, 0); // środek wysokości

        double x = entityPos.x - mc.player.getX();
        double y = entityPos.y - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double z = entityPos.z - mc.player.getZ();

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;

        MatrixStack matrix = RenderSystem.getModelViewStack();
        matrix.push();
        matrix.translate(0, 0, -1000); // bardzo daleko żeby było na HUDzie

        VertexConsumerProvider.Immediate vertices = mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer buffer = vertices.getBuffer(RenderLayer.getLines());

        // Środek ekranu
        float midX = mc.getWindow().getScaledWidth() / 2f;
        float midY = mc.getWindow().getScaledHeight() / 2f - 10; // trochę wyżej

        // To jest uproszczone – pełny tracer wymaga projekcji 3D → 2D, ale na początek prosty sposób z WorldRenderer

        // Lepsza wersja (prosta linia w world space + render w AFTER_ENTITIES)
        // Dla prawdziwych tracerów z ekranu lepiej użyć innego eventu, ale to działa jako start

        matrices.push();
        // możesz użyć Tessellator + BufferBuilder dla custom linii
        // na razie zostawiam tylko box – tracers dodam w kolejnej wersji jeśli chcesz
        matrices.pop();
    }

    // Toggle (możesz podpiąć pod KeyBinding)
    public void toggle() {
        enabled = !enabled;
        System.out.println("ESP: " + (enabled ? "ON" : "OFF"));
    }
}