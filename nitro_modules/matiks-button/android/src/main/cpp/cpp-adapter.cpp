#include <jni.h>
#include "NitroMatiksButtonOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::matiksbutton::initialize(vm);
}
