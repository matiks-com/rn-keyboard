#include <jni.h>
#include "NitroKeyboardOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::keyboard::initialize(vm);
}
