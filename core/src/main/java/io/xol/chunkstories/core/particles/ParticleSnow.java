package io.xol.chunkstories.core.particles;

import io.xol.chunkstories.api.math.vector.dp.Vector3dm;
import io.xol.chunkstories.api.particles.ParticleDataWithVelocity;
import io.xol.chunkstories.api.particles.ParticleType;
import io.xol.chunkstories.api.particles.ParticleTypeHandler;
import io.xol.chunkstories.api.particles.ParticlesRenderer;
import io.xol.chunkstories.api.rendering.RenderingInterface;
import io.xol.chunkstories.api.world.World;
import io.xol.chunkstories.world.WorldImplementation;

//(c) 2015-2017 XolioWare Interactive
//http://chunkstories.xyz
//http://xol.io

public class ParticleSnow extends ParticleTypeHandler
{
	public ParticleSnow(ParticleType type) {
		super(type);
	}

	public class SnowData extends ParticleData implements ParticleDataWithVelocity {
		
		int hp = 60 * 2; // 5s
		Vector3dm vel = new Vector3dm((Math.random() * 0.5 - 0.25) * 0.5, -Math.random() * 0.15 - 0.10, (Math.random() * 0.5 - 0.25) * 0.5);
		
		public SnowData(float x, float y, float z)
		{
			super(x, y, z);
		}
		
		public void setVelocity(Vector3dm vel)
		{
			this.vel = vel;
		}
	}

	@Override
	public ParticleData createNew(World world, float x, float y, float z)
	{
		return new SnowData(x, y, z);
	}

	@Override
	public void forEach_Physics(World world, ParticleData data)
	{
		SnowData b = (SnowData) data;
		
		b.setX((float) (b.getX() + b.vel.getX()));
		b.setY((float) (b.getY() + b.vel.getY()));
		b.setZ((float) (b.getZ() + b.vel.getZ()));
		
		if (((WorldImplementation) world).checkCollisionPoint(b.getX(), b.getY(), b.getZ()))
		{
			b.hp--;
			b.vel.set(0d, 0d, 0d);
		}
		
		// 60th square of 0.5
		//b.vel.scale(0.98581402);
		//if(b.vel.length() < 0.1/60.0)
		//	b.vel.zero();
		
		if(b.hp < 0 || b.getY() < 0)
			b.destroy();
	}
	
	@Override
	public ParticleTypeRenderer getRenderer(ParticlesRenderer particlesRenderer) {
		return new ParticleTypeRenderer(particlesRenderer) {
			@Override
			public void forEach_Rendering(RenderingInterface renderingContext, ParticleData data)
			{
				
			}

			@Override
			public void destroy() {
				
			}
			
		};
	}
}