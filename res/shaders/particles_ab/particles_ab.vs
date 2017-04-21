#version 150

varying vec4 texcoord;
varying vec2 lightMapCoords;

//Lighthing
uniform float sunIntensity;

// The normal we're going to pass to the fragment shader.
varying vec3 varyingNormal;
// The vertex we're going to pass to the fragment shader.
varying vec4 varyingVertex;

varying float fogI;

uniform float time;
varying vec3 eye;
uniform vec3 camPos;
uniform vec3 objectPosition;

uniform float vegetation;
varying float chunkFade;
uniform float viewDistance;

varying vec4 modelview;

attribute vec4 particlesPositionIn;
attribute vec2 textureCoordinatesIn;

uniform float areTextureCoordinatesSupplied;

uniform mat4 projectionMatrix;
uniform mat4 projectionMatrixInv;

uniform mat4 modelViewMatrix;
uniform mat4 modelViewMatrixInv;

uniform mat3 normalMatrix;
uniform mat3 normalMatrixInv;

uniform float billboardSize;

//Fog
uniform float fogStartDistance;
uniform float fogEndDistance;

const vec2 vertices[] = vec2[]( vec2(1.0 ,  1.0),
								vec2(-1.0,  1.0),
								vec2(-1.0, -1.0),
								vec2(-1.0, -1.0),
								vec2(1.0 ,  1.0),
								vec2(1.0,  -1.0)
								);

void main(){
	//Usual variable passing
	
	vec2 proceduralVertex = vertices[gl_VertexID % 6];
	
	if(areTextureCoordinatesSupplied < 0.5)
		texcoord = vec4(proceduralVertex*0.5+0.5, 0, 0);
	else
		texcoord = vec4(textureCoordinatesIn, 0, 0);
	
	vec4 v = particlesPositionIn;
	
	varyingVertex = v;
	
	//Compute lightmap coords
	lightMapCoords = vec2(0.0, 1.0);
	
	modelview = modelViewMatrix * v;
	
	modelview += vec4(proceduralVertex*billboardSize, 0.0, 0.0);
	
	vec4 clochard = projectionMatrix * modelview;
	
	gl_Position = clochard;
}