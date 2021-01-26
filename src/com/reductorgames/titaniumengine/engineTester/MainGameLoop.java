package com.reductorgames.titaniumengine.engineTester;

import com.reductorgames.titaniumengine.entities.Camera;
import com.reductorgames.titaniumengine.entities.Entity;
import com.reductorgames.titaniumengine.entities.Light;
import com.reductorgames.titaniumengine.entities.Player;
import com.reductorgames.titaniumengine.guis.GuiRenderer;
import com.reductorgames.titaniumengine.guis.GuiTexture;
import com.reductorgames.titaniumengine.models.TexturedModel;
import com.reductorgames.titaniumengine.toolbox.MousePicker;
import com.reductorgames.titaniumengine.water.WaterFrameBuffers;
import com.reductorgames.titaniumengine.water.WaterRenderer;
import com.reductorgames.titaniumengine.water.WaterShader;
import com.reductorgames.titaniumengine.water.WaterTile;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import com.reductorgames.titaniumengine.renderEngine.*;
import com.reductorgames.titaniumengine.models.RawModel;
import com.reductorgames.titaniumengine.terrains.Terrain;
import com.reductorgames.titaniumengine.textures.ModelTexture;
import com.reductorgames.titaniumengine.textures.TerrainTexture;
import com.reductorgames.titaniumengine.textures.TerrainTexturePack;
import org.lwjgl.util.vector.Vector4f;

import javax.jws.WebParam;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {

    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", "C:\\TitaniumEngine\\lib\\natives");

        DisplayManager.createDisplay();
        Loader loader = new Loader();

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        TexturedModel tree2 = new TexturedModel(OBJLoader.loadObjModel("LowPolyTreeModel", loader),
                new ModelTexture(loader.loadTexture("LowPolyTreeTexture")));
        TexturedModel tree1 = new TexturedModel(OBJLoader.loadObjModel("treeModel", loader),
                new ModelTexture(loader.loadTexture("treeTexture")));
        TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp", loader),
                new ModelTexture(loader.loadTexture("lamp")));

        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
        fernTextureAtlas.setNumberOfRows(2);

        TexturedModel fern  = new TexturedModel(OBJLoader.loadObjModel("fern", loader), fernTextureAtlas);
        fern.getTexture().setHasTransaperncy(true);

        Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmapWada");
        List<Terrain> terrains = new ArrayList<Terrain>();
        terrains.add(terrain);

        List<Entity> entities = new ArrayList<Entity>();
        Random random = new Random(676452);
        for (int i = 0; i < 400; i++) {
            if (i % 2 == 0) {
                float x = random.nextFloat() * 800 - 400;
                float z = random.nextFloat() * -600;
                float y = terrain.getHeightOfTerrain(x, z);

                entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360,
                        0, 0.9f));
            }

            if (i % 5 == 0) {
                float x = random.nextFloat() * 800 - 400;
                float z = random.nextFloat() * -600;
                float y = terrain.getHeightOfTerrain(x, z);
                entities.add(new Entity(tree1, new Vector3f(x, y, z), 0, 0, 0,
                        random.nextFloat() * 1 + 4));
                entities.add(new Entity(tree2, new Vector3f(x, y, z), 0, 0, 0,
                        random.nextFloat() * 1 + 6));
            }


            entities.add(new Entity(lamp, new Vector3f(155, 0, -575), 0, 0, 0, 1));

            MasterRenderer renderer = new   MasterRenderer(loader);

            RawModel personModel = OBJLoader.loadObjModel("person", loader);
            TexturedModel player1 = new TexturedModel(personModel, new ModelTexture(loader.loadTexture("playerTexture")));

            Player player = new Player(player1, new Vector3f(100, 5, -560), 0, 180, 0, 0.6f);
            Camera camera = new Camera(player);

            GuiRenderer guiRenderer = new GuiRenderer(loader);

            Light sun = new Light(new Vector3f(0, 20000, 20000), new Vector3f(1.3f, 1.3f, 1.3f));
            List<Light> lights = new ArrayList<Light>();
            lights.add(sun);

            WaterFrameBuffers buffers = new WaterFrameBuffers();
            WaterShader waterShader = new WaterShader();
            WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
            List<WaterTile> waters = new ArrayList<WaterTile>();
            WaterTile water = new WaterTile(140, -645, -5);
            waters.add(water);

            /**List<GuiTexture> guiTextures = new ArrayList<GuiTexture>();
            GuiTexture refraction = new GuiTexture(buffers.getRefractionTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
            GuiTexture reflection = new GuiTexture(buffers.getReflectionTexture(), new Vector2f(-0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
            guiTextures.add(refraction);
            guiTextures.add(reflection);**/

            while (!Display.isCloseRequested()) {
                player.move(terrain);
                camera.move();

                GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

                buffers.bindReflectionFrameBuffer();
                float distance = 2 * (camera.getPosition().y - water.getHeight());
                camera.getPosition().y -= distance;
                camera.invertPitch();
                renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, 1, 0, -water.getHeight() + 1f));
                camera.getPosition().y += distance;
                camera.invertPitch();

                buffers.bindRefractionFrameBuffer();
                renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, -1, 0, water.getHeight()));

                GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
                renderer.processEntity(player);
                buffers.unbindCurrentFrameBuffer();
                renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, -1, 0, 15));
                waterRenderer.render(waters, camera, sun);
                //guiRenderer.render(guiTextures);

                renderer.wireFrameMode(false);
                DisplayManager.updateDisplay();
            }

            waterShader.cleanUp();
            guiRenderer.cleanUp();
            renderer.cleanUp();
            loader.cleanUp();
            DisplayManager.closeDisplay();
        }
    }
}