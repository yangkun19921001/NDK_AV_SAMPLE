cmake_minimum_required(VERSION 3.4.1)

# 打印日志
message("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA>>>")
message("当前CMake的路径是：${CMAKE_SOURCE_DIR}")
message("当前 CMAKE_ANDROID_ARCH_ABI 的路径是：${CMAKE_ANDROID_ARCH_ABI}")

# 批量引入源文件
file(GLOB allCpp *.cpp)

# 加入cpp源文件
add_library(
        native-lib
        SHARED
        # native-lib.cpp
        ${allCpp}
)

# 导入静态库
#add_library(test_a STATIC IMPORTED)
# 开始真正的导入
#set_target_properties(test_a PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/libtest.a)

# 导入动态库
add_library(test_so SHARED IMPORTED)
# 早起的cmake ANDROID_ABI == 当前CPU平台
set_target_properties(test_so PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../jniLibs/${CMAKE_ANDROID_ARCH_ABI}/libTest.so)

# 只能找系统的
find_library(
        log-lib
        log)
message("当前的log路径在哪里啊 >>>>>>>>>>>>>>>>> ${log-lib}")

# CMAKE_SOURCE_DIR == D:\NDK\CoursewareCreate\ndk_12\project\ndk12_cmake\app\src\main\cpp\CMakeLists.txt

target_link_libraries(
        native-lib
        ${log-lib}
        test_so
)