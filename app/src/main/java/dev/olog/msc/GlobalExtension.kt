package dev.olog.msc

fun catchNothing(func:() -> Unit){
    try {
        func()
    } catch (ex: Exception){}
}