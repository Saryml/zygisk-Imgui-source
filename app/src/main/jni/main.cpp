#include <cstring>
#include <thread>
#include <string>
#include "hack.h"
#include "zygisk.hpp"
#include "game.h"
#include "log.h"
#include "obfuscate.h"
#include "Extra.h"

static int enable_hack;
int isGame(JNIEnv *env, jstring appDataDir);
static char *game_data_dir = NULL;

int isGame(JNIEnv *env, jstring appDataDir) {
    if (!appDataDir) return 0;
    const char *app_data_dir = env->GetStringUTFChars(appDataDir, nullptr);
    int user = 0;
    static char package_name[256];
    if (sscanf(app_data_dir, "/data/%*[^/]/%d/%s", &user, package_name) != 2) {
        if (sscanf(app_data_dir, "/data/%*[^/]/%s", package_name) != 1) {
            package_name[0] = '\0';
            LOGW("can't parse %s", app_data_dir);
            return 0;
        }
    }
    if (strcmp(package_name, pkgName) == 0) {
        LOGI("detect game: %s", package_name);
        game_data_dir = new char[strlen(app_data_dir) + 1];
        strcpy(game_data_dir, app_data_dir);
        env->ReleaseStringUTFChars(appDataDir, app_data_dir);
        return 1;
    } else {
        env->ReleaseStringUTFChars(appDataDir, app_data_dir);
        return 0;
    }
}

using zygisk::Api;
using zygisk::AppSpecializeArgs;
using zygisk::ServerSpecializeArgs;

class MyModule : public zygisk::ModuleBase {
public:
    void onLoad(Api *api, JNIEnv *env) override {
        envs = env;
    }
    void preAppSpecialize(AppSpecializeArgs *args) override {
        if (!args || !args->nice_name) {
           LOGE("Skip unknown process");
            return;
        }
        enable_hack = isGame(envs, args->app_data_dir);
    }
    void postAppSpecialize(const AppSpecializeArgs *) override {
        if (enable_hack) {
          //  int ret;
                 std::thread hack_thread(hack_prepare, game_data_dir);
            hack_thread.detach();
			 
        }
    }
private:
    JNIEnv *envs{};
};

REGISTER_ZYGISK_MODULE(MyModule)
/*

jint (JNICALL *Real_JNI_OnLoad)(JavaVM *vm, void *reserved);
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
	
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
	g_vm = vm;
	
	std::string apkPkg = getPackageName(GetJNIEnv(g_vm));
	std::string libAkSound = std::string(OBFUSCATE("libAkSoundEngine.so"));
    std::string libAkSoundOri1 = std::string(OBFUSCATE("libAkSoundEngine+.bytes"));
    std::string libAkSoundOri2 = std::string(OBFUSCATE("libAkSoundEngine+.so"));
    
    std::string localPath = std::string(OBFUSCATE("/storage/emulated/0/Android/data/")) + apkPkg + std::string(OBFUSCATE("/files/dragon2017/assets/comlibs/")) + std::string(ARCH);
    std::string rootPath = std::string(OBFUSCATE("/data/data/")) + apkPkg + std::string(OBFUSCATE("/app_libs/comlibs/")) + std::string(ARCH);
    
    std::string pathAkSound = rootPath + std::string(OBFUSCATE("/")) + libAkSound;
    std::string pathAkSoundOri1 = localPath + std::string(OBFUSCATE("/")) + libAkSoundOri1;
    std::string pathAkSoundOri2 = rootPath + std::string(OBFUSCATE("/")) + libAkSoundOri2;
    
    if (CopyFile(pathAkSoundOri1.c_str(), pathAkSoundOri2.c_str())) {
        void *handle = dlopen(pathAkSoundOri2.c_str(), RTLD_NOW);
        if (!handle) {
            handle = dlopen(pathAkSoundOri2.c_str(), RTLD_NOW);
            sleep(1);
        }
        auto Hook_JNI_OnLoad = dlsym(handle, OBFUSCATE("JNI_OnLoad"));
        if (Hook_JNI_OnLoad) {
            Real_JNI_OnLoad = decltype(Real_JNI_OnLoad)(Hook_JNI_OnLoad);
            std::thread hack_thread(hack_prepare, game_data_dir);
            hack_thread.detach();
            if (CheckFile(pathAkSoundOri2.c_str())) {
                std::remove(pathAkSound.c_str());
                std::rename(pathAkSoundOri2.c_str(), pathAkSound.c_str());
            }
            return Real_JNI_OnLoad(vm, reserved);
        }
    }
    return JNI_ERR;
}
*/
