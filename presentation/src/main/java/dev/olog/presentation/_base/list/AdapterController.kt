package dev.olog.presentation._base.list

interface AdapterController <Model> {

    operator fun get(position: Int): Model

}