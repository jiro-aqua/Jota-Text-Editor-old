#include "org_mozilla_universalchardet_UniversalDetector.h"
#include "universalchardet/universalchardet.h"

/*
 * Class:     org_mozilla_universalchardet_UniversalDetector
 * Method:    chardet_create
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_org_mozilla_universalchardet_UniversalDetector_chardet_1create
  (JNIEnv *env, jclass jclazz)
{
	chardet_t ret = NULL;
	int result = chardet_create(&ret);
	if ( result == CHARDET_RESULT_OK ){
		return (jlong)(ret);
	}
	return 0;
}

/*
 * Class:     org_mozilla_universalchardet_UniversalDetector
 * Method:    chardet_destroy
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_mozilla_universalchardet_UniversalDetector_chardet_1destroy
  (JNIEnv *env, jclass jclazz, jlong det)
{
	chardet_destroy((chardet_t) det);
}

/*
 * Class:     org_mozilla_universalchardet_UniversalDetector
 * Method:    chardet_handle_data
 * Signature: (J[BJ)I
 */
JNIEXPORT jint JNICALL Java_org_mozilla_universalchardet_UniversalDetector_chardet_1handle_1data
  (JNIEnv *env , jclass jclazz , jlong det, jbyteArray data , jint offset , jint len)
{
	jint ret;
	jbyte *ndata = (jbyte*)env->GetPrimitiveArrayCritical(data, 0);
	if ( ndata != 0 ){
		
		ret = chardet_handle_data((chardet_t)det, (const char*)ndata+offset , len);

		env->ReleasePrimitiveArrayCritical( data, ndata, JNI_ABORT);
		return ret;
	}
	return -1;
}

/*
 * Class:     org_mozilla_universalchardet_UniversalDetector
 * Method:    chardet_data_end
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_mozilla_universalchardet_UniversalDetector_chardet_1data_1end
  (JNIEnv *env, jclass jclazz, jlong det)
{
	return chardet_data_end((chardet_t) det);
	
}

/*
 * Class:     org_mozilla_universalchardet_UniversalDetector
 * Method:    chardet_reset
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_mozilla_universalchardet_UniversalDetector_chardet_1reset
  (JNIEnv *env, jclass jclazz, jlong det)
{
	return chardet_reset((chardet_t) det);
}

/*
 * Class:     org_mozilla_universalchardet_UniversalDetector
 * Method:    chardet_get_charset
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_mozilla_universalchardet_UniversalDetector_chardet_1get_1charset
  (JNIEnv *env, jclass jclazz, jlong det)
{
	char	namebuf[256];
	
	int result = chardet_get_charset((chardet_t) det, namebuf, sizeof(namebuf) );
	if ( result == CHARDET_RESULT_OK ){
		return env->NewStringUTF( namebuf);
	}
	return 0;
}


