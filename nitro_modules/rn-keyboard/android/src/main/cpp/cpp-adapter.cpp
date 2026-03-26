#include <jni.h>
#include "NitroRnKeyboardOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::rnkeyboard::initialize(vm);
}
