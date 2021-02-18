package org.reductorgames.titaniumengine.renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;
import org.reductorgames.titaniumengine.audioEngine.AudioMaster;
import org.reductorgames.titaniumengine.fontRendering.TextMaster;
import org.reductorgames.titaniumengine.postProcessing.PostProcessing;

import java.nio.ByteBuffer;

import static org.reductorgames.titaniumengine.postProcessing.Fbo.multisampleFbo;
import static org.reductorgames.titaniumengine.postProcessing.Fbo.outputFbo;

public class DisplayManager {

	public static int recordedFPS;
	public static int fps;
	public static long lastFPS;

	private static final int WIDTH = 1280;
	private static final int HEIGHT = 720;
	private static final int FPS_CAP = 120;

	private static String OS_NAME = LWJGLUtil.getPlatformName();

	private static long lastFrameTime;
	private static float delta;

	public static void createDisplay() throws LWJGLException {
		ContextAttribs attribs = new ContextAttribs(3,3)
		.withForwardCompatible(true)
		.withProfileCore(true);
		Display.setResizable(true);
		Display.setFullscreen(true);
		Display.setVSyncEnabled(true);

		if(OS_NAME == "windows") {
			OS_NAME = "Windows";
		}

		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat().withDepthBits(24), attribs);
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0,0, WIDTH, HEIGHT);
		lastFrameTime = getCurrentTime();
	}

	public static void updateDisplay() {
		if(getCurrentTime() - lastFPS > 1000L) {
			lastFPS = getCurrentTime();
			recordedFPS = fps;
			fps = 0;
			Display.setTitle("Titanium Engine [BETA-0.5.0]  |  OS: " + OS_NAME + "  |  FPS: " + getFPS());
		}
		fps++;

		Display.sync(FPS_CAP);
		Display.update();
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			closeDisplay();
			AudioMaster.cleanUp();
			System.exit(0);
		}
	}
	
	public static float getFrameTimeSeconds() {
		return delta;
	}
	
	public static void closeDisplay() {
		Display.destroy();
	}

	public static int getFPS() {
		return recordedFPS;
	}
	
	private static long getCurrentTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}
}