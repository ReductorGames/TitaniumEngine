package org.reductorgames.titaniumengine.postProcessing;

import org.reductorgames.titaniumengine.shaders.ShaderProgram;

public class ContrastShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/org/reductorgames/titaniumengine/postProcessing/contrastVertex.glsl";
	private static final String FRAGMENT_FILE = "src/org/reductorgames/titaniumengine/postProcessing/contrastFragment.glsl";
	
	public ContrastShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {	
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
}