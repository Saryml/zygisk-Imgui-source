#pragma once
#include <iostream>
#include <stdio.h>
#include <string>
#include <unistd.h>
#include <stdint.h>
#include <inttypes.h>
#include <vector>
#include <map>
#include <chrono>
#include <fstream>
#include <thread>
#include <pthread.h>
#include <dirent.h>
#include <libgen.h>
#include <sys/stat.h>
#include <sys/mman.h>
#include <sys/uio.h>
#include <fcntl.h>
#include <jni.h>
#include <android/log.h>
#include <elf.h>
#include <dlfcn.h>
#if defined(__aarch64__)
#define ABI  OBFUSCATE("[x64]")
#define ARCH OBFUSCATE("arm64-v8a")
#else
#define ABI  OBFUSCATE("[x32]")
#define ARCH OBFUSCATE("armeabi-v7a")
#endif

JavaVM *g_vm;

JNIEnv* GetJNIEnv(JavaVM* vm) {
    JNIEnv* jni_env = 0;
    vm->AttachCurrentThread(&jni_env, 0);
    return jni_env;
}

jobject getJNIContext(JNIEnv *env) {
    jclass activityThreadCls = env->FindClass("android/app/ActivityThread");
    jmethodID currentActivityThread = env->GetStaticMethodID(activityThreadCls, "currentActivityThread", "()Landroid/app/ActivityThread;");
    jobject activityThreadObj = env->CallStaticObjectMethod(activityThreadCls, currentActivityThread);
    jmethodID getApplication = env->GetMethodID(activityThreadCls, "getApplication", "()Landroid/app/Application;");
    jobject context = env->CallObjectMethod(activityThreadObj, getApplication);
    return context;
}

int GetSDKVersion(JNIEnv *env) {
    jclass buildClass = env->FindClass("android/os/Build$VERSION");
    jfieldID sdkIntField = env->GetStaticFieldID(buildClass, "SDK_INT", "I");
    jint sdkInt = env->GetStaticIntField(buildClass, sdkIntField);
    return (int)sdkInt;
}

const char *GetAndroidID(JNIEnv *env, jobject context) {
    jclass contextClass = env->FindClass("android/content/Context");
    jmethodID getContentResolverMethod = env->GetMethodID(contextClass,"getContentResolver","()Landroid/content/ContentResolver;");
    jclass settingSecureClass = env->FindClass("android/provider/Settings$Secure");
    jmethodID getStringMethod = env->GetStaticMethodID(settingSecureClass,"getString", "(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;");
    auto obj = env->CallObjectMethod(context, getContentResolverMethod);
    auto str = (jstring) env->CallStaticObjectMethod(settingSecureClass, getStringMethod, obj,env->NewStringUTF("android_id"));
    return env->GetStringUTFChars(str, 0);
}

const char *GetDeviceModel(JNIEnv *env) {
    jclass buildClass = env->FindClass("android/os/Build");
    jfieldID modelId = env->GetStaticFieldID(buildClass, "MODEL","Ljava/lang/String;");
    auto str = (jstring) env->GetStaticObjectField(buildClass, modelId);
    return env->GetStringUTFChars(str, 0);
}

const char *GetDeviceBrand(JNIEnv *env) {
    jclass buildClass = env->FindClass("android/os/Build");
    jfieldID modelId = env->GetStaticFieldID(buildClass, "BRAND","Ljava/lang/String;");
    auto str = (jstring) env->GetStaticObjectField(buildClass, modelId);
    return env->GetStringUTFChars(str, 0);
}

const char *GetDeviceUniqueIdentifier(JNIEnv *env, const char *uuid) {
    jclass uuidClass = env->FindClass("java/util/UUID");
    auto len = strlen(uuid);
    jbyteArray myJByteArray = env->NewByteArray(len);
    env->SetByteArrayRegion(myJByteArray, 0, len, (jbyte *) uuid);
    jmethodID nameUUIDFromBytesMethod = env->GetStaticMethodID(uuidClass,"nameUUIDFromBytes","([B)Ljava/util/UUID;");
    jmethodID toStringMethod = env->GetMethodID(uuidClass, "toString","()Ljava/lang/String;");
    auto obj = env->CallStaticObjectMethod(uuidClass, nameUUIDFromBytesMethod, myJByteArray);
    auto str = (jstring) env->CallObjectMethod(obj, toStringMethod);
    return env->GetStringUTFChars(str, 0);
}

std::string getPackageName(JNIEnv *env) {
	jobject context = getJNIContext(env);
    jclass contextClass = env->FindClass("android/content/Context");
    jmethodID getPackageNameId = env->GetMethodID(contextClass, "getPackageName", "()Ljava/lang/String;");
    auto str = (jstring) env->CallObjectMethod(context, getPackageNameId);
    const char* chars = env->GetStringUTFChars(str, 0);
	return std::string(chars);
}

bool deleteFile(JNIEnv *env, std::string filename) {
	std::string fullpath = "/data/data/" + getPackageName(env) + "/files/" + filename;
	return unlink(fullpath.c_str()) == 0;
}

bool removeFilesExcept(JNIEnv *env, const std::vector<std::string>& filesToKeep) {
    std::string folderPath;
    #ifdef __aarch64__
    folderPath = "/storage/emulated/0/Android/data/" + getPackageName(env) + "/files/dragon2017/assets/comlibs/arm64-v8a/";
    #else
    folderPath = "/storage/emulated/0/Android/data/" + getPackageName(env) + "/files/dragon2017/assets/comlibs/armeabi-v7a/";
    #endif
    DIR *dir;
    struct dirent *entry;
    if ((dir = opendir(folderPath.c_str())) != NULL) {
        while ((entry = readdir(dir)) != NULL) {
            std::string filename = entry->d_name;
            if (filename != "." && filename != ".." && std::find(filesToKeep.begin(), filesToKeep.end(), filename) == filesToKeep.end()) {
                std::string fullpath = folderPath + filename;
                unlink(fullpath.c_str());
            }
        }
        closedir(dir);
    }
    return true;
}

bool CopyFile(const char *in, const char *out) {
    std::ifstream src(in, std::ios::binary);
    std::ofstream dest(out, std::ios::binary);
    dest << src.rdbuf();
    return src && dest;
}

bool CheckFile(const std::string &name) {
    std::ifstream f(name.c_str());
    return f.good();
}

inline jint ShowSoftKeyboardInput(JavaVM *vm, bool open) {
    JNIEnv *env;
    vm->AttachCurrentThread(&env, nullptr);
    if (env == nullptr) return -1;
	
    jclass looperClass = env->FindClass(("android/os/Looper"));
    auto prepareMethod = env->GetStaticMethodID(looperClass, ("prepare"), ("()V"));
    env->CallStaticVoidMethod(looperClass, prepareMethod);

    jclass activityThreadClass = env->FindClass(("android/app/ActivityThread"));
    jfieldID sCurrentActivityThreadField = env->GetStaticFieldID(activityThreadClass, ("sCurrentActivityThread"), ("Landroid/app/ActivityThread;"));
    jobject sCurrentActivityThread = env->GetStaticObjectField(activityThreadClass, sCurrentActivityThreadField);

    jfieldID mInitialApplicationField = env->GetFieldID(activityThreadClass, ("mInitialApplication"), ("Landroid/app/Application;"));
    jobject mInitialApplication = env->GetObjectField(sCurrentActivityThread, mInitialApplicationField);

    jclass contextClass = env->FindClass(("android/content/Context"));
    jfieldID fieldINPUT_METHOD_SERVICE = env->GetStaticFieldID(contextClass, ("INPUT_METHOD_SERVICE"), ("Ljava/lang/String;"));
    jobject INPUT_METHOD_SERVICE = env->GetStaticObjectField(contextClass, fieldINPUT_METHOD_SERVICE);
    jmethodID getSystemServiceMethod = env->GetMethodID(contextClass, ("getSystemService"), ("(Ljava/lang/String;)Ljava/lang/Object;"));
    jobject callObjectMethod = env->CallObjectMethod(mInitialApplication, getSystemServiceMethod, INPUT_METHOD_SERVICE);

    jclass classInputMethodManager = env->FindClass(("android/view/inputmethod/InputMethodManager"));
    jmethodID toggleSoftInputId = env->GetMethodID(classInputMethodManager, ("toggleSoftInput"), ("(II)V"));

    if (open) {
        env->CallVoidMethod(callObjectMethod, toggleSoftInputId, 2, 0);
    } else {
        env->CallVoidMethod(callObjectMethod, toggleSoftInputId, 0, 0);
    }

    env->DeleteLocalRef(classInputMethodManager);
    env->DeleteLocalRef(callObjectMethod);
    env->DeleteLocalRef(contextClass);
    env->DeleteLocalRef(mInitialApplication);
    env->DeleteLocalRef(activityThreadClass);
    vm->DetachCurrentThread();

    return 0;
}

std::string getClipboardText(JavaVM *vm) {
    std::string result;
    JNIEnv *env;
    vm->AttachCurrentThread(&env, 0);
    {
		auto looperClass = env->FindClass("android/os/Looper");
        auto prepareMethodID = env->GetStaticMethodID(looperClass, "prepare", "()V");
        env->CallStaticVoidMethod(looperClass, prepareMethodID);
        auto object = getJNIContext(env);
        auto ContextClass = env->FindClass("android/content/Context");
        auto getSystemServiceMethod = env->GetMethodID(ContextClass, "getSystemService", "(Ljava/lang/String;)Ljava/lang/Object;");
        auto str = env->NewStringUTF("clipboard");
        auto clipboardManager = env->CallObjectMethod(object, getSystemServiceMethod, str);
        env->DeleteLocalRef(str);
        auto ClipboardManagerClass = env->FindClass("android/content/ClipboardManager");
        auto getText = env->GetMethodID(ClipboardManagerClass, "getText", "()Ljava/lang/CharSequence;");
        auto CharSequenceClass = env->FindClass("java/lang/CharSequence");
        auto toStringMethod = env->GetMethodID(CharSequenceClass, "toString", "()Ljava/lang/String;");
        auto text = env->CallObjectMethod(clipboardManager, getText);
        if (text) {
            str = (jstring) env->CallObjectMethod(text, toStringMethod);
            result = env->GetStringUTFChars(str, 0);
            env->DeleteLocalRef(str);
            env->DeleteLocalRef(text);
        }
        env->DeleteLocalRef(CharSequenceClass);
        env->DeleteLocalRef(ClipboardManagerClass);
        env->DeleteLocalRef(clipboardManager);
        env->DeleteLocalRef(ContextClass);
    }
    vm->DetachCurrentThread();

    return result;
}

class SharedPreferences_Editor {
public:
    SharedPreferences_Editor(JNIEnv* env,const jobject joSharedPreferences_Edit):env(env),joSharedPreferences_Edit(joSharedPreferences_Edit) {
        jclass jcSharedPreferences_Editor = env->GetObjectClass(joSharedPreferences_Edit);
        jmPutBoolean=env->GetMethodID(jcSharedPreferences_Editor,"putBoolean","(Ljava/lang/String;Z)Landroid/content/SharedPreferences$Editor;");
        jmPutInt=env->GetMethodID(jcSharedPreferences_Editor,"putInt","(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;");
		jmPutFloat=env->GetMethodID(jcSharedPreferences_Editor,"putFloat","(Ljava/lang/String;F)Landroid/content/SharedPreferences$Editor;");
        jmPutString=env->GetMethodID(jcSharedPreferences_Editor,"putString","(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;");
        jmCommit=env->GetMethodID(jcSharedPreferences_Editor,"commit","()Z");
    }
    const SharedPreferences_Editor& putBoolean(const char* key,const bool value)const {
        env->CallObjectMethod(joSharedPreferences_Edit,jmPutBoolean,env->NewStringUTF(key),(jboolean)value);
        return *this;
    }
    const SharedPreferences_Editor& putInt(const char* key,const int value)const {
        env->CallObjectMethod(joSharedPreferences_Edit,jmPutInt,env->NewStringUTF(key),(jint)value);
        return *this;
    }
    const SharedPreferences_Editor& putFloat(const char* key,const float value)const {
        env->CallObjectMethod(joSharedPreferences_Edit,jmPutFloat,env->NewStringUTF(key),(jfloat)value);
        return *this;
    }
    const SharedPreferences_Editor& putString(const char* key,const char* value)const {
        env->CallObjectMethod(joSharedPreferences_Edit,jmPutString,env->NewStringUTF(key),env->NewStringUTF(value));
        return *this;
    }
    bool commit()const {
        return (bool)env->CallBooleanMethod(joSharedPreferences_Edit,jmCommit);
    }
private:
    JNIEnv* env;
    jobject joSharedPreferences_Edit;
    jmethodID jmPutBoolean;
    jmethodID jmPutInt;
    jmethodID jmPutFloat;
    jmethodID jmPutString;
    jmethodID jmCommit;
};

class SharedPreferences {
public:
    SharedPreferences(SharedPreferences const &) = delete;
    void operator=(SharedPreferences const &)= delete;
public:
    SharedPreferences(JNIEnv *env,const char* name,const bool keepReference=false) {
        this->env=env;
		jobject androidContext = getJNIContext(env);
        jclass jcContext = env->FindClass("android/content/Context");
        jclass jcSharedPreferences = env->FindClass("android/content/SharedPreferences");
        if(jcContext==nullptr || jcSharedPreferences== nullptr) {
            __android_log_print(ANDROID_LOG_DEBUG, "SharedPreferences","Cannot find classes");
        }
        jmGetBoolean=env->GetMethodID(jcSharedPreferences,"getBoolean","(Ljava/lang/String;Z)Z");
        jmGetInt=env->GetMethodID(jcSharedPreferences,"getInt","(Ljava/lang/String;I)I");
        jmGetFloat=env->GetMethodID(jcSharedPreferences,"getFloat","(Ljava/lang/String;F)F");
        jmGetString=env->GetMethodID(jcSharedPreferences,"getString","(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
        jmEdit=env->GetMethodID(jcSharedPreferences,"edit", "()Landroid/content/SharedPreferences$Editor;");
        jmethodID jmGetSharedPreferences=env->GetMethodID(jcContext,"getSharedPreferences","(Ljava/lang/String;I)Landroid/content/SharedPreferences;");
        joSharedPreferences=env->CallObjectMethod(androidContext,jmGetSharedPreferences,env->NewStringUTF(name),MODE_PRIVATE);
        if(keepReference) {
            joSharedPreferences=env->NewWeakGlobalRef(joSharedPreferences);
        }
    }
    void replaceJNI(JNIEnv* newEnv) {
        env=newEnv;
    }
private:
    JNIEnv* env;
    jobject joSharedPreferences;
    jmethodID jmGetBoolean;
    jmethodID jmGetInt;
    jmethodID jmGetFloat;
    jmethodID jmGetString;
    jmethodID jmEdit;
public:
    bool getBoolean(const char* id,bool defaultValue=false)const {
        return (bool)(env->CallBooleanMethod(joSharedPreferences,jmGetBoolean,env->NewStringUTF(id),(jboolean)defaultValue));
    }
    int getInt(const char* id,int defaultValue=0)const {
        return (int)(env->CallIntMethod(joSharedPreferences,jmGetInt,env->NewStringUTF(id),(jint)defaultValue));
    }
    float getFloat(const char* id,float defaultValue=0.0f)const {
        return (float)(env->CallFloatMethod(joSharedPreferences,jmGetFloat,env->NewStringUTF(id),(jfloat)defaultValue));
    }
    std::string getString(const char* id,const char* defaultValue="")const {
        auto value=(jstring)(env->CallObjectMethod(joSharedPreferences,jmGetString,env->NewStringUTF(id),env->NewStringUTF(defaultValue)));
        const char* valueP = env->GetStringUTFChars(value, nullptr);
        const std::string ret=std::string(valueP);
        env->ReleaseStringUTFChars(value,valueP);
        return ret;
    }
    SharedPreferences_Editor edit()const {
        jobject joSharedPreferences_Edit=env->CallObjectMethod(joSharedPreferences,jmEdit);
        SharedPreferences_Editor editor(env,joSharedPreferences_Edit);
        return editor;
    }
private:
    static constexpr const int  MODE_PRIVATE = 0; //taken directly from java, assuming this value stays constant in java
};

