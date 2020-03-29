import com.android.build.gradle.internal.dsl.DefaultConfig

fun DefaultConfig.configField(
    pair: Pair<String, String>
){
    buildConfigField("String", pair.first, pair.second)
}