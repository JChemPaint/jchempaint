#include <stdio.h>
#include <stdlib.h>

#include <jni.h>

#include "setclipboard.h"
#include "setclipboard_swift.h"


JNIEXPORT void JNICALL Java_org_openscience_jchempaint_OsxClipboard_setClipboard(
  JNIEnv *env, jclass cls, jbyteArray pdfData, jbyteArray svgData, jbyteArray pngData, jstring smi) {

  int pdfDataLen = (*env)->GetArrayLength(env, pdfData);
  unsigned char* pdfDataPtr = (unsigned char*)malloc(pdfDataLen);
  (*env)->GetByteArrayRegion(env, pdfData, 0, pdfDataLen, (jbyte*)pdfDataPtr);

  int svgDataLen = (*env)->GetArrayLength(env, svgData);
  unsigned char* svgDataPtr = (unsigned char*)malloc(svgDataLen);
  (*env)->GetByteArrayRegion(env, svgData, 0, svgDataLen, (jbyte*)svgDataPtr);

  int pngDataLen = (*env)->GetArrayLength(env, pngData);
  unsigned char* pngDataPtr = (unsigned char*)malloc(pngDataLen);
  (*env)->GetByteArrayRegion(env, pngData, 0, pngDataLen, (jbyte*)pngDataPtr);

  const char *nativeSmi = (*env)->GetStringUTFChars(env, smi, 0);

  setClipboard(pdfDataPtr, pdfDataLen,
               svgDataPtr, svgDataLen,
               pngDataPtr, pngDataLen,
               nativeSmi);
  (*env)->ReleaseStringUTFChars(env, smi, nativeSmi);
  free(pdfDataPtr);
  free(svgDataPtr);
  free(pngDataPtr);
}

