cd ../exoplayer
FFMPEG_MODULE_PATH="$(pwd)/extensions/ffmpeg/src/main"
NDK_PATH=~/Library/Android/sdk/ndk-bundle

HOST_PLATFORM="darwin-x86_64"

cd "${FFMPEG_MODULE_PATH}/jni" && \
  git clone git://source.ffmpeg.org/ffmpeg && \
  cd ffmpeg && \
  git checkout release/4.2 && \
  FFMPEG_PATH="$(pwd)"

ENABLED_DECODERS=(vorbis opus flac alac mp3)

cd "${FFMPEG_MODULE_PATH}/jni" && \
  ln -s "$FFMPEG_PATH" ffmpeg

cd "${FFMPEG_MODULE_PATH}/jni" && \
  ./build_ffmpeg.sh \
  "${FFMPEG_MODULE_PATH}" "${NDK_PATH}" "${HOST_PLATFORM}" "${ENABLED_DECODERS[@]}"