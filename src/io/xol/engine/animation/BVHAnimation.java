package io.xol.engine.animation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.xol.engine.math.lalgb.Matrix4f;
import io.xol.engine.math.lalgb.Quaternion4d;
import io.xol.engine.math.lalgb.Vector3d;
import io.xol.engine.math.lalgb.Vector3f;

//(c) 2015-2016 XolioWare Interactive
// http://chunkstories.xyz
// http://xol.io

public class BVHAnimation
{
	int frames = 0;
	float frameTime = 0f;

	Bone root;
	public List<Bone> bones = new ArrayList<Bone>();

	public static void main(String a[])
	{
		BVHAnimation test = new BVHAnimation(new File("res/models/human.bvh"));
		System.out.println(test.toString());
		
		float rotX = (float) (Math.random() * 2.0 * Math.PI);
		float rotY = (float) (Math.random() * 2.0 * Math.PI);
		float rotZ = (float) (Math.random() * 2.0 * Math.PI);

		Quaternion4d quaternionXLower = Quaternion4d.fromAxisAngle(new Vector3d(1.0, 0.0, 0.0), rotX);
		Quaternion4d quaternionYLower = Quaternion4d.fromAxisAngle(new Vector3d(0.0, 1.0, 0.0), rotY);
		Quaternion4d quaternionZLower = Quaternion4d.fromAxisAngle(new Vector3d(0.0, 0.0, 1.0), rotZ);
		Quaternion4d total = new Quaternion4d(quaternionXLower);
		total = total.mult(quaternionYLower);
		total.normalize();
		total = total.mult(quaternionZLower);
		
		Matrix4f matrix = new Matrix4f();

		//Apply rotations
		matrix.rotate(rotX, new Vector3f(1, 0, 0));
		matrix.rotate(rotY, new Vector3f(0, 1, 0));
		matrix.rotate(rotZ, new Vector3f(0, 0, 1));
		
		Matrix4f mX = new Matrix4f();
		Matrix4f mY = new Matrix4f();
		Matrix4f mZ = new Matrix4f();
		
		mX.rotate(rotX, new Vector3f(1, 0, 0));
		mY.rotate(rotY, new Vector3f(0, 1, 0));
		mZ.rotate(rotZ, new Vector3f(0, 0, 1));
		
		/*System.out.println("Old:\n"+matrix);
		System.out.println("New:\n"+Matrix4f.mul(Matrix4f.mul(mX, mY, null), mZ, null));
		
		System.out.println("mX:\n"+mX);
		System.out.println("mY:\n"+mY);
		System.out.println("mZ:\n"+mZ);*/
		
		//System.out.println(quaternionXLower);
		//System.out.println(quaternionYLower);
		//System.out.println(quaternionZLower);
		
		mX = Quaternion4d.fromAxisAngle(new Vector3d(1.0, 0.0, 0.0), rotX).toMatrix4f();
		mY = Quaternion4d.fromAxisAngle(new Vector3d(0.0, 1.0, 0.0), rotY).toMatrix4f();
		mZ = Quaternion4d.fromAxisAngle(new Vector3d(0.0, 0.0, 1.0), rotZ).toMatrix4f();

		//System.out.println("Old:\n"+matrix);
		//System.out.println("New:\n"+Matrix4f.mul(Matrix4f.mul(mX, mY, null), mZ, null));
		//System.out.println("mX:\n"+mX);
		//System.out.println("mY:\n"+mY);
		//System.out.println("mZ:\n"+mZ);
		
		//total = Matrix4f.invert(total, null);
		
		System.out.println("Qml:\n"+total.toMatrix4f());
		//System.out.println("Inv:\n"+Matrix4f.invert(total.toMatrix4f(), null));
	}

	public BVHAnimation(File file)
	{
		load(file);
	}

	public Matrix4f getTransformationForBone(String boneName, double animationTime)
	{
		Matrix4f matrix = new Matrix4f();
		if (frames == 0)
		{
			System.out.println("fack you");
			return matrix;
		}
		
		double frame = animationTime / 1000.0 / frameTime;
		
		double frameUpperBound = Math.ceil(frame);
		double frameLowerBound = Math.floor(frame);
		
		double interp = frame % 1.0;
		//Don't try to interpolate if we're on an exact frame
		if(frameLowerBound == frameUpperBound)
			interp = 0.0;
		
		int frameLower = (int)(frameLowerBound) % frames;
		int frameUpper = (int)(frameUpperBound) % frames;
		
		//if(boneName.equals("boneLegLU"))
		//	System.out.println(animationTime + " " + interp + " L"+frameLower+" U"+frameUpper);
		
		for (Bone b : bones)
			if (b.name.equals(boneName))
			{
				matrix = b.getTransformationMatrixInterpolated(frameLower, frameUpper, interp);
			}
		
		//Swaps Y and Z axises arround
		Matrix4f blender2ingame = new Matrix4f();
		blender2ingame.m11 = 0;
		blender2ingame.m22 = 0;
		blender2ingame.m12 = 1;
		blender2ingame.m21 = 1;
		
		//Rotate the matrix first to apply the transformation in blender space
		Matrix4f.mul(blender2ingame, matrix, matrix);
		
		//Mirror it
		Matrix4f mirror = new Matrix4f();
		mirror.m22 = -1;
		Matrix4f.mul(mirror, matrix, matrix);
		
		//Rotate again after so it's back the correct way arround
		Matrix4f.mul(matrix, blender2ingame, matrix);
		
		Matrix4f.mul(matrix, mirror, matrix);
		
		return matrix;
	}

	public Matrix4f getTransformationForBonePlusOffset(String boneName, double animationTime)
	{
		Matrix4f matrix = null;
		if (frames == 0)
		{
			System.out.println("Invalid bone : "+boneName + "in animation" + this);
			return new Matrix4f();
		}
		Matrix4f offsetMatrix = new Matrix4f();
		Vector3f offsetTotal = new Vector3f();
		
		//Sanity checking
		for (Bone b : bones)
			if (b.name.equals(boneName))
			{
				//Accumulate the transformation offset
				Bone kek = b;
				while (kek != null)
				{
					//Swap yz arround and negate input Y to apply blender -> ingame coordinates system transformation
					offsetTotal.x += kek.offset.x;
					offsetTotal.y += kek.offset.z; 
					offsetTotal.z += -kek.offset.y;
					kek = kek.parent;
				}
				//Negate it and build the offset matrix
				offsetTotal.negate();
				offsetMatrix.m30 += offsetTotal.x;
				offsetMatrix.m31 += offsetTotal.y;
				offsetMatrix.m32 += offsetTotal.z;
			}
		
		//Get the normal bone transformation
		matrix = getTransformationForBone(boneName, animationTime);		

		//Apply the offset matrix
		Matrix4f.mul(matrix, offsetMatrix, matrix);		
		return matrix;
	}

	private void load(File file)
	{
		bones.clear();
		try
		{
			Bone currentBone = null;
			int readingFrame = 0;
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			boolean readingMotion = false;
			boolean readingDest = false;

			while ((line = reader.readLine()) != null)
			{
				line = line.replace("\t", ""); // We don't want the tabs.
				String[] items = line.split(" ");
				if (readingMotion)
				{
					if (line.startsWith("Frames:"))
					{
						frames = Integer.parseInt(items[1]);
						root.recursiveInitMotion(frames);
					}
					else if (line.startsWith("Frame Time:"))
						frameTime = Float.parseFloat(items[2]);
					else
					{
						float[] lineData = new float[items.length];
						// Fill this shit
						for (int i = 0; i < lineData.length; i++)
							lineData[i] = Float.parseFloat(items[i]);
						// System.out.println("lineData : "+lineData.length+" / "+totalChannels);
						int index = 0;
						currentBone = root;
						while (currentBone != null)
						{
							if (!currentBone.animationDataLoaded)
							{
								// if(readingFrame == 0)
								// System.out.println("Reading "+currentBone.channels+" for bone : "+currentBone.name+" starting at index:"+index);
								for (int i = 0; i < currentBone.channels; i++)
								{
									currentBone.animationData[readingFrame][i] = lineData[index];
									index++;
								}
							}
							currentBone.animationDataLoaded = true;
							boolean hasDoneAllChilds = true;
							for (Bone c : currentBone.childs)
							{
								if (!c.animationDataLoaded)
								{
									hasDoneAllChilds = false;
									currentBone = c;
									break;
								}
							}
							if (hasDoneAllChilds)
								currentBone = currentBone.parent;
						}
						root.recursiveResetFlag();
						readingFrame++;
					}
				}
				else
				{
					if (line.equals("HIERARCHY"))
					{
						readingMotion = false;
					}
					else if (line.startsWith("ROOT"))
					{
						root = new Bone(items[1], null);
						currentBone = root;
						// Add to global bones list
						bones.add(root);
					}
					else if (line.startsWith("{"))
					{
						// ignore
					}
					else if (line.startsWith("OFFSET"))
					{
						if (readingDest)
							currentBone.dest = new Vector3f(Float.parseFloat(items[1]), Float.parseFloat(items[2]), Float.parseFloat(items[3]));
						else
							currentBone.offset = new Vector3f(Float.parseFloat(items[1]), Float.parseFloat(items[2]), Float.parseFloat(items[3]));
					}
					else if (line.startsWith("CHANNELS"))
					{
						// Currently only support for XYZ POS+ROT and ROT
						// formats.
						currentBone.channels = Integer.parseInt(items[1]);
					}
					else if (line.startsWith("JOINT"))
					{
						Bone newBone = new Bone(items[1], currentBone);
						currentBone.childs.add(newBone);
						currentBone = newBone;
						// Add to global bones list
						bones.add(newBone);
					}
					else if (line.equals("End Site"))
					{
						readingDest = true;
					}
					else if (line.startsWith("}"))
					{
						// If we were reading an end site, stop
						if (readingDest)
							readingDest = false;
						else
							// Else we point to current bone's parent
							currentBone = currentBone.parent;
					}
					else if (line.equals("MOTION"))
					{
						readingMotion = true;
					}
				}
			}
			reader.close();
		}
		catch (IOException | NullPointerException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public String toString()
	{
		String txt = "BVH ANIMATION FILE\n";
		txt += root.toString();
		txt += "Frames: " + frames + "\n";
		txt += "Frame Time: " + frameTime;
		return txt;
	}
}