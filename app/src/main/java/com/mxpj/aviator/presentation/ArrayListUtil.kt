package com.mxpj.aviator.presentation


fun <T> ArrayList<T>.findMutable(predicate: (T) -> (Boolean)): T? {
    val iterator = this.iterator()
    while (iterator.hasNext()){
        val item = iterator.next()
        if(predicate.invoke(item)) return item
    }
    return null
}