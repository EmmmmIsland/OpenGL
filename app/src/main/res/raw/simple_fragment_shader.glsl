// 片段着色器
// 数据默认经度    lowp mediump highp
precision mediump float;
varying vec4 v_Color;
void main(){
    gl_FragColor = v_Color;
}