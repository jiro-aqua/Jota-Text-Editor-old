/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Mozilla Universal charset detector code.
 *
 * The Initial Developer of the Original Code is
 * Netscape Communications Corporation.
 * Portions created by the Initial Developer are Copyright (C) 2001
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *          Kohei TAKETA <k-tak@void.in>
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
#ifndef ___UNIVERSALCHARDET_H___
#define ___UNIVERSALCHARDET_H___

#ifdef _WIN32
#   ifdef DLL_EXPORTS
#	define CHARDET_IMEXPORT extern _declspec(dllexport)
#   else
#	define CHARDET_IMEXPORT extern _declspec(dllimport)
#   endif
#else
#   define CHARDET_IMEXPORT extern
#endif

#include <stddef.h>

#define CHARDET_RESULT_OK		    0
#define CHARDET_RESULT_NOMEMORY		    (-1)
#define CHARDET_RESULT_INVALID_DETECTOR	    (-2)

#define CHARDET_MAX_ENCODING_NAME	    64

#define CHARDET_ENCODING_ISO_2022_JP    "ISO-2022-JP"
#define CHARDET_ENCODING_ISO_2022_CN    "ISO-2022-CN"
#define CHARDET_ENCODING_ISO_2022_KR    "ISO-2022-KR"
#define CHARDET_ENCODING_ISO_8859_5     "ISO-8859-5"
#define CHARDET_ENCODING_ISO_8859_7     "ISO-8859-7"
#define CHARDET_ENCODING_ISO_8859_8     "ISO-8859-8"
#define CHARDET_ENCODING_BIG5           "BIG5"
#define CHARDET_ENCODING_GB18030        "GB18030"
#define CHARDET_ENCODING_EUC_JP         "EUC-JP"
#define CHARDET_ENCODING_EUC_KR         "EUC-KR"
#define CHARDET_ENCODING_EUC_TW         "EUC-TW"
#define CHARDET_ENCODING_SHIFT_JIS      "SHIFT_JIS"
#define CHARDET_ENCODING_IBM855         "IBM855"
#define CHARDET_ENCODING_IBM866         "IBM866"
#define CHARDET_ENCODING_KOI8_R         "KOI8-R"
#define CHARDET_ENCODING_MACCYRILLIC    "MACCYRILLIC"
#define CHARDET_ENCODING_WINDOWS_1251   "WINDOWS-1251"
#define CHARDET_ENCODING_WINDOWS_1252   "WINDOWS-1252"
#define CHARDET_ENCODING_WINDOWS_1253   "WINDOWS-1253"
#define CHARDET_ENCODING_WINDOWS_1255   "WINDOWS-1255"
#define CHARDET_ENCODING_UTF_8          "UTF-8"
#define CHARDET_ENCODING_UTF_16BE       "UTF-16BE"
#define CHARDET_ENCODING_UTF_16LE       "UTF-16LE"
#define CHARDET_ENCODING_UTF_32BE       "UTF-32BE"
#define CHARDET_ENCODING_UTF_32LE       "UTF-32LE"
#define CHARDET_ENCODING_HZ_GB_2312     "HZ-GB-2312"
#define CHARDET_ENCODING_X_ISO_10646_UCS_4_3412 "X-ISO-10646-UCS-4-3412"
#define CHARDET_ENCODING_X_ISO_10646_UCS_4_2143 "X-ISO-10646-UCS-4-2143"

/* Unused */
#define CHARDET_ENCODING_ISO_8859_2     "ISO-8859-2"
#define CHARDET_ENCODING_WINDOWS_1250   "WINDOWS-1250"
#define CHARDET_ENCODING_TIS_620        "TIS-620"

typedef void* chardet_t;

#ifdef __cplusplus
extern "C" {
#endif

/**
 * Create an encoding detector.
 * @param pdet [out] pointer to a chardet_t variable that receives
 *             the encoding detector handle.
 * @return CHARDET_RESULT_OK if succeeded. CHARDET_RESULT_NOMEMORY otherwise.
 */
CHARDET_IMEXPORT int chardet_create(chardet_t* pdet);

/**
 * Destroy an encoding detector.
 * @param det [in] the encoding detector handle to be destroyed.
 */
CHARDET_IMEXPORT void chardet_destroy(chardet_t det);

/**
 * Feed data to an encoding detector.
 * @param det [in] the encoding detector handle
 * @param data [in] data
 * @param len [in] length of data in bytes.
 * @return CHARDET_RESULT_OK if succeeded.
 *         CHARSET_RESULT_NOMEMORY if running out of memory.
 *         CHARDET_RESULT_INVALID_DETECTOR if det was invalid.
 */
CHARDET_IMEXPORT int chardet_handle_data(chardet_t det, const char* data, unsigned int len);

/**
 * Notify an end of data to an encoding detctor.
 * @param det [in] the encoding detector handle
 * @return CHARDET_RESULT_OK if succeeded.
 *         CHARDET_RESULT_INVALID_DETECTOR if det was invalid.
 */
CHARDET_IMEXPORT int chardet_data_end(chardet_t det);

/**
 * Reset an encoding detector.
 * @param det [in] the encoding detector handle
 * @return CHARDET_RESULT_OK if succeeded.
 *         CHARDET_RESULT_INVALID_DETECTOR if det was invalid.
 */
CHARDET_IMEXPORT int chardet_reset(chardet_t det);

/**
 * Get the name of encoding that was detected.
 * @param det [in] the encoding detector handle
 * @param namebuf [in/out] pointer to a buffer that receives the name of
 *                detected encoding. A valid encoding name or an empty string
 *                will be written to namebuf. If an empty strng was written,
 *                the detector could not detect any encoding.
 *                Written strings will always be NULL-terminated.
 * @param buflen [in] length of namebuf
 * @return CHARDET_RESULT_OK if succeeded.
 *         CHARDET_RESULT_NOMEMORY if namebuf was too small to store
 *         the entire encoding name.
 *         CHARDET_RESULT_INVALID_DETECTOR if det was invalid.
 */
CHARDET_IMEXPORT int chardet_get_charset(chardet_t det, char* namebuf, unsigned int buflen);


#ifdef __cplusplus
};
#endif

#endif
