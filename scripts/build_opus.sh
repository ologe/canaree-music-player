cd ../ExoPlayer
OPUS_MODULE_PATH="$(pwd)/extensions/opus/src/main"
NDK_PATH=~/Library/Android/sdk/ndk-bundle

cd "${OPUS_MODULE_PATH}/jni" && \
  git clone https://gitlab.xiph.org/xiph/opus.git libopus

cd ${OPUS_MODULE_PATH}/jni && ./convert_android_asm.sh

cd "${OPUS_MODULE_PATH}"/jni && \
  ${NDK_PATH}/ndk-build APP_ABI=all -j4