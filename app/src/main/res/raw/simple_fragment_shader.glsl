// 片段着色器
// 数据默认经度    lowp mediump highp
precision mediump float;
uniform vec4 u_Color;
void main(){
    gl_FragColor = u_Color;
}