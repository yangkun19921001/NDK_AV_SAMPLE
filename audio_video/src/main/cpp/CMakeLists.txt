cmake_minimum_required(VERSION 3.4.1)

#音频渲染
set(OpenSL ${CMAKE_SOURCE_DIR}/opensl)
#视频渲染
set(OpenGL ${CMAKE_SOURCE_DIR}/gles)


#批量添加自己编写的 cpp 文件,不要把 *.h 加入进来了
file(GLOB ALL_CPP ${OpenSL}/*.cpp ${OpenGL}/*.cpp)

#添加自己编写 cpp 源文件生成动态库
add_library(audiovideo SHARED ${ALL_CPP})

#找系统中 NDK log库
find_library(log_lib
        log)

#最后才开始链接库
target_link_libraries(
        audiovideo
        #音频播放
        OpenSLES

        #视频渲染
        EGL
        GLESv2

        #添加本地库
        android

        ${log_lib}
)