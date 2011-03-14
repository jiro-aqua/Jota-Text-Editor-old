# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libuniversalchardet
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := \
universalchardet/CharDistribution.cpp \
universalchardet/JpCntx.cpp \
universalchardet/LangBulgarianModel.cpp \
universalchardet/LangCyrillicModel.cpp \
universalchardet/LangGreekModel.cpp \
universalchardet/LangHebrewModel.cpp \
universalchardet/LangHungarianModel.cpp \
universalchardet/LangThaiModel.cpp \
universalchardet/nsBig5Prober.cpp \
universalchardet/nsCharSetProber.cpp \
universalchardet/nsEUCJPProber.cpp \
universalchardet/nsEUCKRProber.cpp \
universalchardet/nsEUCTWProber.cpp \
universalchardet/nsEscCharsetProber.cpp \
universalchardet/nsEscSM.cpp \
universalchardet/nsGB2312Prober.cpp \
universalchardet/nsHebrewProber.cpp \
universalchardet/nsLatin1Prober.cpp \
universalchardet/nsMBCSGroupProber.cpp \
universalchardet/nsMBCSSM.cpp \
universalchardet/nsSBCSGroupProber.cpp \
universalchardet/nsSBCharSetProber.cpp \
universalchardet/nsSJISProber.cpp \
universalchardet/nsUTF8Prober.cpp \
universalchardet/nsUniversalDetector.cpp\
universalchardet/dll.cpp\
jni_UniversalDetector.cpp\


#TextReader.cpp \

LOCAL_CFLAGS += -O3 

LOCAL_ARM_MODE := arm
LOCAL_C_INCLUDES += $(JNI_H_INCLUDE)
LOCAL_C_INCLUDES += external/icu4c/common
LOCAL_PRELINK_MODULE := false


include $(BUILD_SHARED_LIBRARY)
