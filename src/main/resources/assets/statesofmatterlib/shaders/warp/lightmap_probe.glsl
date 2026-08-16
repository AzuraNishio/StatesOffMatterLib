#version 100


//WARP_FRAGMENT(XfragColor =)
void waves(vec3 vertPos, vec3 epicenter){
    color.gb = lightmap_pos;
}//BREAK

//WARP_VERTEX(vec3 pos = Position + ChunkOffset;X)
void waves(vec3 vertPos, vec3 epicenter){
    lightmap_pos = clamp(UV0 / 256.0, vec2(0.5 / 16.0), vec2(15.5 / 16.0));
}//BREAK

//WARP_FRAGMENT(#version 150X)
void uniformAndInputsFragment(vec3 pos){
    in vec2 lightmap_pos;
}//BREAK

//WARP_VERTEX(#version 150X)
void uniformAndInputsVertex(vec3 color){
    out vec2 lightmap_pos;
}//BREAK