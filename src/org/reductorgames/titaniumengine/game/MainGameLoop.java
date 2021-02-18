package org.reductorgames.titaniumengine.game;

import org.reductorgames.titaniumengine.audioEngine.AudioMaster;
import org.reductorgames.titaniumengine.audioEngine.Source;
import org.reductorgames.titaniumengine.entities.*;
import org.reductorgames.titaniumengine.fontMeshCreator.FontType;
import org.reductorgames.titaniumengine.fontMeshCreator.GUIText;
import org.reductorgames.titaniumengine.fontRendering.TextMaster;
import org.reductorgames.titaniumengine.guis.GuiRenderer;
import org.reductorgames.titaniumengine.guis.GuiTexture;
import org.reductorgames.titaniumengine.models.TexturedModel;
import org.reductorgames.titaniumengine.normalMappingObjConverter.NormalMappedObjLoader;
import org.reductorgames.titaniumengine.objConverter.OBJFileLoader;
import org.reductorgames.titaniumengine.postProcessing.PostProcessing;
import org.reductorgames.titaniumengine.renderEngine.*;
import org.reductorgames.titaniumengine.terrains.Terrain;
import org.reductorgames.titaniumengine.textures.*;
import org.reductorgames.titaniumengine.toolbox.MousePicker;
import org.reductorgames.titaniumengine.water.*;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.io.File;
import java.util.*;

import java.util.concurrent.atomic.AtomicInteger;
import net.steelswing.mclog.ServerConsole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.reductorgames.titaniumengine.postProcessing.Fbo.*;

public class MainGameLoop {

	private static final Logger log = LogManager.getLogger();

	public static void main(String[] args) throws Exception {
		System.setProperty("org.lwjgl.librarypath", new File("lib/natives").getAbsolutePath());

		ServerConsole console = new ServerConsole().addListener((ServerConsole cns, String cmd) -> {
			if (cmd.equalsIgnoreCase("stop")) {
				cns.setRunning(false);
			}
		});

		AtomicInteger counter = new AtomicInteger(0);

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		TextMaster.init(loader);
		AudioMaster.init();
		AudioMaster.setListenerData(0, 0, 0);
		TexturedModel stanfordBunny = new TexturedModel(OBJLoader.loadObjModel("person", loader), new ModelTexture(
				loader.loadTexture("playerTexture")));
		Player player = new Player(stanfordBunny, new Vector3f(75, 5, -75), 0, 100, 0, 0.6f);
		Camera camera = new Camera(player);
		MasterRenderer renderer = new MasterRenderer(loader, camera);
		FontType font = new FontType(loader.loadTexture("arial2"), new File("res/arial2.fnt"));
		GUIText text = new GUIText("Titanium Engine DEMO", 2f, font, new Vector2f(0f, 0f), 1f, true);
		text.setColour(1, 0, 0);
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture,
				gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		TexturedModel rocks = new TexturedModel(OBJFileLoader.loadOBJ("rocks", loader),
				new ModelTexture(loader.loadTexture("rocks")));
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		fernTextureAtlas.setNumberOfRows(2);
		TexturedModel fern = new TexturedModel(OBJFileLoader.loadOBJ("fern", loader),
				fernTextureAtlas);
		TexturedModel bobble = new TexturedModel(OBJFileLoader.loadOBJ("pine", loader),
				new ModelTexture(loader.loadTexture("pine")));
		bobble.getTexture().setHasTransparency(true);
		fern.getTexture().setHasTransparency(true);
		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");
		List<Terrain> terrains = new ArrayList<Terrain>();
		terrains.add(terrain);
		TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp", loader),
				new ModelTexture(loader.loadTexture("lamp")));
		lamp.getTexture().setUseFakeLighting(true);
		List<Entity> entities = new ArrayList<Entity>();
		List<Entity> normalMapEntities = new ArrayList<Entity>();
		TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader),
				new ModelTexture(loader.loadTexture("barrel")));
		barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
		barrelModel.getTexture().setShineDamper(10);
		barrelModel.getTexture().setReflectivity(0.5f);
		TexturedModel crateModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader),
				new ModelTexture(loader.loadTexture("crate")));
		crateModel.getTexture().setNormalMap(loader.loadTexture("crateNormal"));
		crateModel.getTexture().setShineDamper(10);
		crateModel.getTexture().setReflectivity(0.5f);
		TexturedModel boulderModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader),
				new ModelTexture(loader.loadTexture("boulder")));
		boulderModel.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
		boulderModel.getTexture().setShineDamper(10);
		boulderModel.getTexture().setReflectivity(0.5f);

		Entity entity = new Entity(barrelModel, new Vector3f(75, 10, -75), 0, 0, 0, 1f);
		barrelModel.getTexture().setShineDamper(10);
		barrelModel.getTexture().setReflectivity(0.5f);
		barrelModel.getTexture().setSpecualrMap(loader.loadTexture("barrelS"));

		Entity entity2 = new Entity(boulderModel, new Vector3f(85, 10, -75), 0, 0, 0, 1f);
		Entity entity3 = new Entity(crateModel, new Vector3f(65, 10, -75), 0, 0, 0, 0.04f);
		normalMapEntities.add(entity);
		normalMapEntities.add(entity2);
		normalMapEntities.add(entity3);

		TexturedModel cherryModel = new TexturedModel(OBJFileLoader.loadOBJ("cherry", loader),
				new ModelTexture(loader.loadTexture("cherry")));
		cherryModel.getTexture().setHasTransparency(true);
		cherryModel.getTexture().setShineDamper(10);
		cherryModel.getTexture().setReflectivity(0.5f);
		cherryModel.getTexture().setSpecualrMap(loader.loadTexture("cherryS"));
		entities.add(new Entity(cherryModel, 1, new Vector3f(60, 10, -25), 0, 0, 0, 10));

		Random random = new Random(5666778);
		for (int i = 0; i < 60; i++) {
			if (i % 3 == 0) {
				float x = random.nextFloat() * 150;
				float z = random.nextFloat() * -150;
				if ((x > 50 && x < 100) || (z < -50 && z > -100)) {
				} else {
					float y = terrain.getHeightOfTerrain(x, z);
					entities.add(new Entity(fern, 3, new Vector3f(x, y, z), 0,
							random.nextFloat() * 360, 0, 0.9f));
				}
			}
			if (i % 2 == 0) {
				float x = random.nextFloat() * 150;
				float z = random.nextFloat() * -150;
				if ((x > 50 && x < 100) || (z < -50 && z > -100)) {
				} else {
					float y = terrain.getHeightOfTerrain(x, z);
					entities.add(new Entity(bobble, 1, new Vector3f(x, y, z), 0,
							random.nextFloat() * 360, 0, random.nextFloat() * 0.6f + 0.8f));
				}
			}
		}
		entities.add(new Entity(rocks, new Vector3f(75, 4.6f, -75), 0, 0, 0, 75));
		List<Light> lights = new ArrayList<Light>();
		Light sun = new Light(new Vector3f(1000000, 1500000, -1000000), new Vector3f(1.3f, 1.3f, 1.3f));
		lights.add(sun);
		List<GuiTexture> guiTextures = new ArrayList<GuiTexture>();
		GuiTexture shadowMap = new GuiTexture(renderer.getShadowMapTexture(),
				new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);
		WaterFrameBuffers buffers = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		WaterTile water = new WaterTile(75, -75, 0);
		waters.add(water);
		entities.add(player);
		int buffer = AudioMaster.loadSound(new File("res/music.wav"));
		Source music = new Source();
		music.setLooping(true);
		music.play(buffer);
		music.setPosition(0, 0, 0);
		music.setVolume(0.05f);

		PostProcessing.init(loader);

		while (!Display.isCloseRequested()) {
			player.move(terrain);
			camera.move();
			picker.update();
			renderer.renderShadowMap(entities, sun);
			entity.increaseRotation(0, 1, 0);
			entity2.increaseRotation(0, 1, 0);
			entity3.increaseRotation(0, 1, 0);

			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			buffers.bindReflectionFrameBuffer();

			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, 1, 0, -water.getHeight() + 1));
			camera.getPosition().y += distance;
			camera.invertPitch();
			buffers.bindRefractionFrameBuffer();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, water.getHeight()));

			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			buffers.unbindCurrentFrameBuffer();

			multisampleFbo.bindFrameBuffer();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, 100000));
			waterRenderer.render(waters, camera, sun);
			multisampleFbo.unbindFrameBuffer();
			multisampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0, outputFbo);
			multisampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1, outputFbo2);
			PostProcessing.doPostProcessing(outputFbo.getColourTexture(), outputFbo2.getColourTexture());
			updateFrameBufferObjects();

			guiRenderer.render(guiTextures);
			TextMaster.render();

			DisplayManager.updateDisplay();
		}

		PostProcessing.cleanUp();
		outputFbo.cleanUp();
		outputFbo2.cleanUp();
		multisampleFbo.cleanUp();
		TextMaster.cleanUp();
		buffers.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
		music.delete();
		AudioMaster.cleanUp();
	}
}