attribute vec3 aPosition;
uniform mat4 uMVPMatrix;
void main()
{
   vec4 vertex = vec4(aPosition[0],aPosition[1],aPosition[2],1.0);
   gl_Position = uMVPMatrix * vertex;
}                      