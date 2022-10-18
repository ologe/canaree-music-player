cd ../exoplayer
FLAC_MODULE_PATH="$(pwd)/extensions/flac/src/main"
NDK_PATH=~/Library/Android/sdk/ndk-bundle

cd "${FLAC_MODULE_PATH}/jni" && \
  curl https://ftp.osuosl.org/pub/xiph/releases/flac/flac-1.3.2.tar.xz | tar xJ && \
  mv flac-1.3.2 flac

cd "${FLAC_MODULE_PATH}"/jni && \
  ${NDK_PATH}/ndk-build APP_ABI=all -j4