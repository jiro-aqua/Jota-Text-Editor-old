Universal Character Set Detector C Library


1. これは何ですか？

Mozillaのエンコーディング自動判別ライブラリである
「universalchardet」を、Cライブラリにしたものです。
universalchardetのオリジナルソースコードは、
http://lxr.mozilla.org/seamonkey/source/extensions/universalchardet/
で参照可能です。


2. インストール方法

Visual Studio 2005の場合は、universalchardet.slnをご利用ください。

Linuxの場合は、Makefileをご利用ください。make installで、
/usr/local/include/universalchardet.h
/usr/local/lib/libuchardet.a
/usr/local/lib/libuchardet.la
/usr/local/lib/libuchardet.so
/usr/local/lib/libuchardet.so.0
/usr/local/lib/libuchardet.so.0.0.0
がインストールされます。


3. 利用方法

universalchardet.hをご覧ください。


4. ライセンス

Mozillaと同様、
- Mozilla Public License 1.1
- GNU General Public License 2.0
- GNU Lesser General Public License 2.1
のトリプルライセンスとします。


5. 文句等

universalchardet本体に関するご質問は、Mozillaのコミュニティへどうぞ。
ライブラリ化に関することについては、

武田光平 k-tak@void.in

までどうぞ。
