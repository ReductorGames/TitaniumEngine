package org.reductorgames.titaniumengine.postProcessing;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import org.reductorgames.titaniumengine.bloom.BrightFilter;
import org.reductorgames.titaniumengine.bloom.CombineFilter;
import org.reductorgames.titaniumengine.gaussianBlur.HorizontalBlur;
import org.reductorgames.titaniumengine.gaussianBlur.VerticalBlur;
import org.reductorgames.titaniumengine.models.RawModel;
import org.reductorgames.titaniumengine.renderEngine.Loader;


public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;
	private static ContrastChanger contrastChanger;
	private static BrightFilter brightFilter;
	private static HorizontalBlur hBlur;
	private static VerticalBlur vBlur;
	private static CombineFilter combineFilter;

	public static void init(Loader loader) throws Exception {
		quad = loader.loadToVAO(POSITIONS, 2);
		contrastChanger = new ContrastChanger();
		brightFilter = new BrightFilter(Display.getWidth() / 2, Display.getHeight() / 2);
		hBlur = new HorizontalBlur(Display.getWidth() / 5, Display.getHeight() / 5);
		vBlur = new VerticalBlur(Display.getWidth() / 5, Display.getHeight() / 5);
		combineFilter = new CombineFilter();
		updateFbos();
	}

	public static void updateFbos() throws Exception {
		cleanUp();
		contrastChanger = new ContrastChanger();
		hBlur = new HorizontalBlur(Display.getWidth() / 8, Display.getHeight() / 8);
		vBlur = new VerticalBlur(Display.getWidth() / 8, Display.getHeight() / 8);
		combineFilter = new CombineFilter();
	}

	public static void doPostProcessing(int colourTexture, int brightTexture) {
		start();
		//brightFilter.render(colourTexture);
		hBlur.render(brightTexture);
		vBlur.render(hBlur.getOutputTexture());
		combineFilter.render(colourTexture, vBlur.getOutputTexture());
		end();
	}
	
	public static void cleanUp() {
		contrastChanger.cleanUp();
		vBlur.cleanUp();
		hBlur.cleanUp();
		combineFilter.cleanUp();
	}
	
	private static void start() {
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	private static void end() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
}