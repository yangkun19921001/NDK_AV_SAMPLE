# 1.源文件在的位置。宏函数 my-dir 返回当前目录（包含 Android.mk 文件本身的目录）的路径。
# LOCAL_PATH 其实就是Android.mk文件本身的目录的路径
LOCAL_PATH := $(call my-dir)

$(info "LOCAL_PATH:======== ${LOCAL_PATH}")

# 2.清理
include $(CLEAR_VARS)

# TODO 预编译库的引入 == 提前编译好的库
LOCAL_MODULE := test

#LOCAL_SRC_FILES := libTest.so
LOCAL_SRC_FILES := test.a

# 预编译共享库的Makeifle脚本
# include $(PREBUILT_SHARED_LIBRARY)

include $(PREBUILT_STATIC_LIBRARY)

#引入其他makefile文件。CLEAR_VARS 变量指向特殊 GNU Makefile，可为您清除许多 LOCAL_XXX 变量
#不会清理 LOCAL_PATH 变量
include $(CLEAR_VARS)
# TODO end

# 3.指定库名字
#存储您要构建的模块的名称 每个模块名称必须唯一，且不含任何空格
#如果模块名称的开头已是 lib，则构建系统不会附加额外的前缀 lib；而是按原样采用模块名称，并添加 .so 扩展名。
LOCAL_MODULE := MyTestSo

#包含要构建到模块中的 C 和/或 C++ 源文件列表 以空格分开
LOCAL_SRC_FILES := test_mk.c

# TODO 开始链接进来
# 静态库的链接
LOCAL_STATIC_LIBRARIES := test
# 动态库链接
#LOCAL_SHARED_LIBRARIES := Test

# 导入 log
#LOCAL_LDLIBS := -llog
LOCAL_LDLIBS    := -lm -llog

# 4.动态库
#构建动态库BUILD_SHARED_LIBRARY 最后要动态库
include $(BUILD_SHARED_LIBRARY)