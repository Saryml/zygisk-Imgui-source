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
