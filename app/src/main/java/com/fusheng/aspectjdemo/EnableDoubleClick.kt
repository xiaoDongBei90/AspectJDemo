package com.fusheng.aspectjdemo
/**
 * @Author: lixiaowei
 * @CreateDate: 2020/12/1 2:36 PM
 * @Description:允许短时间内多次点击的注解
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR)
annotation class EnableDoubleClick {
}