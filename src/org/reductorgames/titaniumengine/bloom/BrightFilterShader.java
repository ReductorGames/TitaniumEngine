package org.reductorgames.titaniumengine.bloom;

import org.reductorgames.titaniumengine.shaders.ShaderProgram;

public class BrightFilterShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "src/org/reductorgames/titaniumengine/bloom/simpleVertex.glsl";
	private static final String FRAGMENT_FILE = "src/org/reductorgames/titaniumengine/bloom/brightFilterFragment.glsl";
	
	public BrightFilterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
}