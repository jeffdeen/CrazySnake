uniform mat4 uMVPMatrix;
attribute vec3 aPosition;  //顶点位置
attribute vec2 aTexCoord;    //顶点纹理坐标
varying vec2 vTextureCoord;  //用于传递给片元着色器的变量
void main()
{
   vec4 vertex = vec4(aPosition[0],aPosition[1],aPosition[2],1.0);
   gl_Position = uMVPMatrix * vertex;
   vTextureCoord = aTexCoord;
}                      