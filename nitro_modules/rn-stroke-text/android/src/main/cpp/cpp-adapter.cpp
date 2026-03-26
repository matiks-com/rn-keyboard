#include <jni.h>
#include "NitroRnStrokeTextOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::rnstroketext::initialize(vm);
}
