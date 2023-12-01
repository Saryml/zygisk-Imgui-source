LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

# ============================================================================#
LOCAL_MODULE            := libdobby
LOCAL_SRC_FILES         := Dobby/libraries/$(TARGET_ARCH_ABI)/libdobby.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/dobby/
include $(PREBUILT_STATIC_LIBRARY)

# ============================================================================#

include $(CLEAR_VARS)

LOCAL_MODULE            := libAkSoundEngine+
LOCAL_CFLAGS            := -Wno-error=format-security -fvisibility=hidden -ffunction-sections -fdata-sections -w
LOCAL_CFLAGS            += -fno-rtti -fno-exceptions -fpermissive
LOCAL_CPPFLAGS          := -Wno-error=format-security -fvisibility=hidden -ffunction-sections -fdata-sections -w -Werror -s -std=c++17
LOCAL_CPPFLAGS          += -Wno-error=c++11-narrowing -fms-extensions -fno-rtti -fno-exceptions -fpermissive
LOCAL_LDFLAGS           += -Wl,--gc-sections,--strip-all, -llog
LOCAL_ARM_MODE          := arm
LOCAL_LDLIBS            := -llog -landroid -lEGL -lGLESv3 -lGLESv2 -lGLESv1_CM -lz

LOCAL_C_INCLUDES        += $(LOCAL_PATH)

LOCAL_SRC_FILES := imgui/imgui.cpp \
imgui/imgui_demo.cpp \
imgui/imgui_draw.cpp \
imgui/imgui_tables.cpp \
imgui/imgui_widgets.cpp \
imgui/backends/imgui_impl_android.cpp \
imgui/backends/imgui_impl_opengl3.cpp \
struct/MonoString.cpp \
xDL/xdl.c \
xDL/xdl_iterate.c \
xDL/xdl_linker.c \
xDL/xdl_lzma.c \
xDL/xdl_util.c \
KittyMemory/KittyArm64.cpp \
KittyMemory/KittyMemory.cpp \
KittyMemory/KittyScanner.cpp \
KittyMemory/KittyUtils.cpp \
KittyMemory/MemoryPatch.cpp \
KittyMemory/MemoryBackup.cpp \
fake_dlfcn.cpp \
Il2Cpp.cpp \
utils.cpp \
hack.cpp \
Main.cpp

LOCAL_STATIC_LIBRARIES  := libdobby
LOCAL_CPP_FEATURES     := exceptions

include $(BUILD_SHARED_LIBRARY)
# ============================================================================ #
