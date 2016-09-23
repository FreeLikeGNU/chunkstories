package io.xol.chunkstories.renderer;


import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.BufferUtils;

import io.xol.engine.math.Math2;
import io.xol.engine.math.lalgb.Vector2f;
import io.xol.chunkstories.api.rendering.Primitive;
import io.xol.chunkstories.api.rendering.pipeline.PipelineConfiguration.CullingMode;
import io.xol.chunkstories.api.rendering.pipeline.ShaderInterface;
import io.xol.chunkstories.world.WorldImplementation;
import io.xol.engine.graphics.RenderingContext;
import io.xol.engine.graphics.geometry.VertexFormat;
import io.xol.engine.graphics.geometry.VerticesObject;
import io.xol.engine.graphics.textures.TexturesHandler;

//(c) 2015-2016 XolioWare Interactive
//http://chunkstories.xyz
//http://xol.io

public class WeatherEffectsRenderer
{
	Random random = new Random();
	WorldImplementation world;
	WorldRenderer worldRenderer;
	
	public WeatherEffectsRenderer(WorldImplementation world, WorldRenderer worldRenderer)
	{
		this.world = world;
		this.worldRenderer = worldRenderer;
	}
	
	//Every second regenerate the buffer with fresh vertices
	float[] raindrops = new float[110000 * 6 * 4];
	FloatBuffer raindropsData = BufferUtils.createFloatBuffer(110000 * 6 * 4);
	// Array setup : 
	int bufferOffset = 0;
	int viewX, viewY, viewZ;
	int lastX, lastY, lastZ;
	
	VerticesObject rainVerticesBuffer = new VerticesObject();
	
	private void generateRainForOneSecond(RenderingContext renderingContext)
	{
		float rainIntensity = Math.min(Math.max(0.0f, world.getWeather() - 0.5f) / 0.3f, 1.0f);
		
		bufferOffset %= 110000;
		bufferOffset += 10000;
		Vector2f view2drop = new Vector2f();
		for(int i = 0; i < 100000; i++)
		{
			// We want to always leave alone the topmost part of the array until it has gone out of view
			int location = (bufferOffset + i) % 110000;
			//Randomize location
			float rdX = viewX + (random.nextFloat() * 2.0f - 1.0f) * (int)(Math2.mix(25, 15, rainIntensity));
			float rdY = viewY + (random.nextFloat() * 2.0f - 0.5f) * (int)(Math2.mix(20, 20, rainIntensity));
			float rdZ = viewZ + (random.nextFloat() * 2.0f - 1.0f) * (int)(Math2.mix(25, 15, rainIntensity));
			//Max height it can fall to before reverting to used
			float rdMh = world.getRegionsSummariesHolder().getHeightAtWorldCoordinates((int)rdX, (int)rdZ);
			//Raindrop size, change orientation to face viewer
			view2drop.x = rdX - viewX;
			view2drop.y = rdZ - viewZ;
			view2drop.normalise();
			float mx = 0.01f * -view2drop.y;
			float mz = 0.01f * view2drop.x;
			float rainDropletSize = 0.2f;
			//Build triangle strip
			//00
			
			raindrops[location * 6 * 4 + 0 * 4 + 0] = rdX-mx;
			raindrops[location * 6 * 4 + 0 * 4 + 1] = rdY;
			raindrops[location * 6 * 4 + 0 * 4 + 2] = rdZ-mz;
			raindrops[location * 6 * 4 + 0 * 4 + 3] = rdMh;
			//01
			raindrops[location * 6 * 4 + 1 * 4 + 0] = rdX-mx;
			raindrops[location * 6 * 4 + 1 * 4 + 1] = rdY + rainDropletSize;
			raindrops[location * 6 * 4 + 1 * 4 + 2] = rdZ-mz;
			raindrops[location * 6 * 4 + 1 * 4 + 3] = rdMh + rainDropletSize;
			//10
			raindrops[location * 6 * 4 + 2 * 4 + 0] = rdX+mx;
			raindrops[location * 6 * 4 + 2 * 4 + 1] = rdY;
			raindrops[location * 6 * 4 + 2 * 4 + 2] = rdZ+mz;
			raindrops[location * 6 * 4 + 2 * 4 + 3] = rdMh;
			//11
			raindrops[location * 6 * 4 + 3 * 4 + 0] = rdX-mx;
			raindrops[location * 6 * 4 + 3 * 4 + 1] = rdY + rainDropletSize;
			raindrops[location * 6 * 4 + 3 * 4 + 2] = rdZ-mz;
			raindrops[location * 6 * 4 + 3 * 4 + 3] = rdMh + rainDropletSize;
			//01
			raindrops[location * 6 * 4 + 4 * 4 + 0] = rdX+mx;
			raindrops[location * 6 * 4 + 4 * 4 + 1] = rdY;
			raindrops[location * 6 * 4 + 4 * 4 + 2] = rdZ+mz;
			raindrops[location * 6 * 4 + 4 * 4 + 3] = rdMh;
			//00
			raindrops[location * 6 * 4 + 5 * 4 + 0] = rdX+mx;
			raindrops[location * 6 * 4 + 5 * 4 + 1] = rdY + rainDropletSize;
			raindrops[location * 6 * 4 + 5 * 4 + 2] = rdZ+mz;
			raindrops[location * 6 * 4 + 5 * 4 + 3] = rdMh + rainDropletSize;
			
		}
		raindropsData.clear();
		raindropsData.position(0);
		/*for(float[][] r : raindrops) // For each raindrop
			for(float v[] : r) // For each vertice
				for(float c : v) // For each component
				raindropsData.put(c);*/
		
		raindropsData.put(raindrops, 0, raindrops.length);
		raindropsData.flip();
		
		rainVerticesBuffer.uploadData(raindropsData);
		//glBindBuffer(GL_ARRAY_BUFFER, vboId);
		//glBufferData(GL_ARRAY_BUFFER, raindropsData, GL_STATIC_DRAW);
		lastX = viewX;
		lastY = viewY;
		lastZ = viewZ;
	}
	
	long lastRender = 0L;
	
	// Rain falls at ~10m/s, so we prepare in advance 10 meters of rain to fall until we add some more on top
	public void renderEffects(RenderingContext renderingContext)
	{
		viewX = (int) -renderingContext.getCamera().pos.getX();
		viewY = (int) -renderingContext.getCamera().pos.getY();
		viewZ = (int) -renderingContext.getCamera().pos.getZ();
		if(world.getWeather() > 0.5)
			renderRain(renderingContext);
	}

	//int vboId = -1;
	
	private void renderRain(RenderingContext renderingContext)
	{
		//if(vboId == -1)
		//	vboId = glGenBuffers();
		
		ShaderInterface weatherShader = renderingContext.useShader("weather");
		//weatherShader.use(true);
		if((System.currentTimeMillis() - lastRender) >= 1000 || Math.abs(viewX - lastX) > 10  || Math.abs(viewZ - lastZ) > 10)
		{
			generateRainForOneSecond(renderingContext);
			lastRender = System.currentTimeMillis();
		}
		
		/*glDisable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
		glDisable(GL_ALPHA_TEST);
		glDisable(GL_BLEND);
		glDepthFunc(GL_LEQUAL);*/
		
		renderingContext.setCullingMode(CullingMode.DISABLED);
		
		renderingContext.getCamera().setupShader(weatherShader);
		//renderingContext.setVertexAttributePointerLocation(vertexIn, 3, GL_FLOAT, false, 0, 0);
		weatherShader.setUniform1f("time", (System.currentTimeMillis() - lastRender) / 1000f);
		
		renderingContext.bindTexture2D("lightmap", TexturesHandler.getTexture("environement/lightcolors.png"));
		//weatherShader.setUniformSampler(0, "lightmap", TexturesHandler.getTexture("environement/lightcolors.png"));
		weatherShader.setUniform1f("sunTime", worldRenderer.getSky().time);
		//raindropsData.flip();
		//glBindBuffer(GL_ARRAY_BUFFER, vboId);
		
		renderingContext.bindAttribute("vertexIn", rainVerticesBuffer.asAttributeSource(VertexFormat.FLOAT, 4));
		//renderingContext.setVertexAttributePointerLocation("vertexIn", 4, GL_FLOAT, false, 0, 0L, rainVerticesBuffer);
		
		float rainIntensity = Math.min(Math.max(0.0f, world.getWeather() - 0.5f) / 0.3f, 1.0f);
		
		//System.out.println("rainIntensity"+rainIntensity);
		
		renderingContext.draw(Primitive.TRIANGLE, 0, 2000 + (int)(9000 * rainIntensity));
		//GLCalls.drawArrays(GL_TRIANGLES, 0, 2000 + (int)(9000 * rainIntensity));
		//glDisable(GL_BLEND);
		
		renderingContext.setCullingMode(CullingMode.COUNTERCLOCKWISE);
	}
	
	public void destroy()
	{
		rainVerticesBuffer.destroy();
		//if(vboId != -1)
		//	glDeleteBuffers(vboId);
	}
}
