Universal Character Set Detector C Library


1. What is it?

A port to C of "universalchardet", that is the encoding detector
library of Mozilla.

The original code of universalchardet is available at
http://lxr.mozilla.org/seamonkey/source/extensions/universalchardet/


2. Installation

To build an DLL on Windows, use universalchardet.sln (VS2005 Solution File).

To install the library to an Linux system, use Makefile.
"make && make install" will install files below.
/usr/local/include/universalchardet.h
/usr/local/lib/libuchardet.a
/usr/local/lib/libuchardet.la
/usr/local/lib/libuchardet.so
/usr/local/lib/libuchardet.so.0
/usr/local/lib/libuchardet.so.0.0.0


3. API

See universalchardet.h.


4. License

The library is subject to the Mozilla Public License Version 1.1.
Alternatively, the library may be used under the terms of either
the GNU General Public License Version 2 or later, or the GNU
Lesser General Public License 2.1 or later.


5. Contacts

If you have any question about the original source code,
please consult with the Mozilla team. If you have a question about
DLLization of universalchardet, feel free to email to
k-tak@void.in (Kohei TAKETA).

