cmake_minimum_required(VERSION 3.4.1)

#定义 ffmpeg、rtmp 、yk_player 目录
set(FFMPEG ${CMAKE_SOURCE_DIR}/ffmpeg)
set(RTMP ${CMAKE_SOURCE_DIR}/librtmp)
set(YK_PLAYER ${CMAKE_SOURCE_DIR}/player)

#指定 ffmpeg 头文件目录
include_directories(${FFMPEG}/include)

#指定 ffmpeg 静态库文件目录
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${FFMPEG}/libs/${CMAKE_ANDROID_ARCH_ABI}")

#指定 rtmp 静态库文件目录
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${RTMP}/libs/${CMAKE_ANDROID_ARCH_ABI}")

#批量添加自己编写的 cpp 文件,不要把 *.h 加入进来了
file(GLOB ALL_CPP ${YK_PLAYER}/*.cpp)

#添加自己编写 cpp 源文件生成动态库
add_library(YK_PLAYER SHARED ${ALL_CPP})

#找系统中 NDK log库
find_library(log_lib
        log)

#最后才开始链接库
target_link_libraries(
        #自己编写的 cpp 打成 so 库包
        YK_PLAYER
        # 写了此命令不用在乎添加 ffmpeg lib 顺序问题导致应用崩溃
        -Wl,--start-group
        avcodec avfilter avformat avutil swresample swscale
        -Wl,--end-group
        z
        #拉流库
        rtmp
        #本地库
        android
        #音频播放
        OpenSLES
        ${log_lib}
        )
