#include <android/log.h>
#include <dlfcn.h>
#include <elf.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <sys/system_properties.h>
#include <unistd.h>
#include <cstdlib>
#include <string.h>
#include "fake_dlfcn.h"

#define g_LogTag "Fake_Dlfcn-LOGGER"
#define log_err(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, g_LogTag, (const char *) fmt, ##args)

#define fatal(fmt, args...) do {log_err(fmt,##args); goto err_exit; } while(0)

#ifdef __LP64__
#define Elf_Ehdr Elf64_Ehdr
#define Elf_Shdr Elf64_Shdr
#define Elf_Sym  Elf64_Sym
#else
#define Elf_Ehdr Elf32_Ehdr
#define Elf_Shdr Elf32_Shdr
#define Elf_Sym  Elf32_Sym
#endif

#if defined(__LP64__)
static const char *const kSystemLibDir = "/system/lib64/";
static const char *const kOdmLibDir = "/odm/lib64/";
static const char *const kVendorLibDir = "/vendor/lib64/";
static const char *const kApexLibDir = "/apex/com.android.runtime/lib64/";
static const char *const kApexArtNsLibDir = "/apex/com.android.art/lib64/";
#else
static const char *const kSystemLibDir = "/system/lib/";
static const char *const kOdmLibDir = "/odm/lib/";
static const char *const kVendorLibDir = "/vendor/lib/";
static const char *const kApexLibDir = "/apex/com.android.runtime/lib/";
static const char *const kApexArtNsLibDir =  "/apex/com.android.art/lib/";
#endif

struct ctx {
	void *load_addr;
	void *dynstr;
	void *dynsym;
	int nsyms;
	off_t bias;
};

extern "C" {
	static int fake_dlclose(void *handle) {
		if (handle) {
			struct ctx *ctx = (struct ctx *)handle;
			if (ctx->dynsym) free(ctx->dynsym);
			if (ctx->dynstr) free(ctx->dynstr);
			free(ctx);
		}
		return 0;
	}
	
	static void *fake_dlopen_with_path(const char *libpath, int flags) {
		FILE *maps;
		char buff[256], name[256];
		struct ctx *ctx = 0;
		off_t load_addr, size;
		int k, fd = -1, found = 0;
		char *shoff;
		Elf_Ehdr *elf = (Elf_Ehdr *) MAP_FAILED;
		
		maps = fopen("/proc/self/maps", "r");
		
		if (!maps)
			fatal("failed to open maps");
			
		while (!found && fgets(buff, sizeof(buff), maps)) {
			if (strstr(buff, libpath) && (strstr(buff, "r-xp") || strstr(buff, "r--p"))) found = 1;
		}
		
		fclose(maps);
		
		if (!found) 
			fatal("%s not found in my userspace", libpath);
			
		if (sscanf(buff, "%lx-%*lx %*s %*s %*s %*s %s", &load_addr, name) != 2)
			fatal("failed to read load address for %s", libpath);
			
		__android_log_print(ANDROID_LOG_INFO, g_LogTag, "%s loaded in Android at 0x%08lx", libpath, load_addr);
		
		libpath = name;
		
		fd = open(libpath, O_RDONLY);
		if (fd < 0)
			fatal("failed to open %s", libpath);
			
		size = lseek(fd, 0, SEEK_END);
		if (size <= 0) 
			fatal("lseek() failed for %s", libpath);
			
		elf = (Elf_Ehdr *) mmap(0, size, PROT_READ, MAP_SHARED, fd, 0);
		close(fd);
		fd = -1;
		
		if (elf == MAP_FAILED)
			fatal("mmap() failed for %s", libpath);
			
		ctx = (struct ctx *) calloc(1, sizeof(struct ctx));
		if (!ctx)
			fatal("no memory for %s", libpath);
			
		ctx->load_addr = (void *) load_addr;
		shoff = ((char *) elf) + elf->e_shoff;
		
		for (k = 0; k < elf->e_shnum; k++, shoff += elf->e_shentsize) {
			Elf_Shdr *sh = (Elf_Shdr *) shoff;
			__android_log_print(ANDROID_LOG_DEBUG, g_LogTag, "%s: k=%d shdr=%p type=%x", __func__, k, sh, sh->sh_type);
			
			switch (sh->sh_type) {
			case SHT_DYNSYM:
				if (ctx->dynsym)
					fatal("%s: duplicate DYNSYM sections", libpath);
					ctx->dynsym = malloc(sh->sh_size);
				if (!ctx->dynsym)
					fatal("%s: no memory for .dynsym", libpath);
					memcpy(ctx->dynsym, ((char *) elf) + sh->sh_offset, sh->sh_size);
					ctx->nsyms = (sh->sh_size / sizeof(Elf_Sym));
					break;
			case SHT_STRTAB:
				if (ctx->dynstr) break;
					ctx->dynstr = malloc(sh->sh_size);
				if (!ctx->dynstr)
					fatal("%s: no memory for .dynstr", libpath);
					memcpy(ctx->dynstr, ((char *) elf) + sh->sh_offset, sh->sh_size);
					break;
			case SHT_PROGBITS:
				if (!ctx->dynstr || !ctx->dynsym)
					break;
				ctx->bias = (off_t) sh->sh_addr - (off_t) sh->sh_offset;
				k = elf->e_shnum;
                break;
			}
		}
		munmap(elf, size);
		elf = 0;
		
		if (!ctx->dynstr || !ctx->dynsym)
			fatal("dynamic sections not found in %s", libpath);
		#undef fatal
		
		__android_log_print(ANDROID_LOG_DEBUG, g_LogTag, "%s: ok, dynsym = %p, dynstr = %p", libpath, ctx->dynsym, ctx->dynstr);
		return ctx;
		
		err_exit:
		if (fd >= 0) close(fd);
		if (elf != MAP_FAILED) munmap(elf, size);
		fake_dlclose(ctx);
		return 0;
	}
	
	static void *fake_dlopen(const char *filename, int flags) {
		if (strlen(filename) > 0 && filename[0] == '/') {
			return fake_dlopen_with_path(filename, flags);
		} else {
			char buf[512] = {0};
			void *handle = NULL;
			
			strcpy(buf, kSystemLibDir);
			strcat(buf, filename);
			
			handle = fake_dlopen_with_path(buf, flags);
			if (handle) {
				return handle;
			}
			
			memset(buf, 0, sizeof(buf));
			strcpy(buf, kApexLibDir);
			strcat(buf, filename);
			handle = fake_dlopen_with_path(buf, flags);
			if (handle) {
				return handle;
			}
			
			memset(buf, 0, sizeof(buf));
			strcpy(buf, kApexArtNsLibDir);
			strcat(buf, filename);
			handle = fake_dlopen_with_path(buf, flags);
			if (handle) {
				return handle;
			}
			
			memset(buf, 0, sizeof(buf));
			strcpy(buf, kOdmLibDir);
			strcat(buf, filename);
			handle = fake_dlopen_with_path(buf, flags);
			if (handle) {
				return handle;
			}
			
			memset(buf, 0, sizeof(buf));
			strcpy(buf, kVendorLibDir);
			strcat(buf, filename);
			handle = fake_dlopen_with_path(buf, flags);
			if (handle) {
				return handle;
			}
			return fake_dlopen_with_path(filename, flags);
		}
	}
	
	static void *fake_dlsym(void *handle, const char *name) {
		int k;
		struct ctx *ctx = (struct ctx *) handle;
		Elf_Sym *sym = (Elf_Sym *) ctx->dynsym;
		char *strings = (char *) ctx->dynstr;
		
		for (k = 0; k < ctx->nsyms; k++, sym++)
		if (strcmp(strings + sym->st_name, name) == 0) {
			void *ret = (char *) ctx->load_addr + sym->st_value - ctx->bias;
			__android_log_print(ANDROID_LOG_INFO, g_LogTag, "%s found at %p", name, ret);
			return ret;
		}
		return 0;
	}
	
	static const char *fake_dlerror() {
		return NULL;
	}
	
	static int SDK_INT = -1;
	static int get_sdk_level() {
		if (SDK_INT > 0) {
			return SDK_INT;
		}
		char sdk[PROP_VALUE_MAX] = {0};
		__system_property_get("ro.build.version.sdk", sdk);
		SDK_INT = atoi(sdk);
		return SDK_INT;
	}
	
	int dlclose_ex(void *handle) {
		if (get_sdk_level() >= 24) {
			return fake_dlclose(handle);
		} else {
			return dlclose(handle);
		}
	}
	
	void *dlopen_ex(const char *filename, int flags) {
		__android_log_print(ANDROID_LOG_INFO, g_LogTag, "dlopen: %s", filename);
		if (get_sdk_level() >= 24) {
			return fake_dlopen(filename, flags);
		} else {
			return dlopen(filename, flags);
		}
	}
	
	void *dlsym_ex(void *handle, const char *symbol) {
		if (get_sdk_level() >= 24) {
			return fake_dlsym(handle, symbol);
		} else {
			return dlsym(handle, symbol);
		}
	}
	
	const char *dlerror_ex() {
		if (get_sdk_level() >= 24) {
			return fake_dlerror();
		} else {
			return dlerror();
		}
	}
}
