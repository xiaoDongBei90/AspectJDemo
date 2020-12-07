package com.fusheng.aspectjdemo

import android.util.Log
import android.view.View
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut

/**
 * @Author: lixiaowei
 * @CreateDate: 2020/12/1 2:12 PM
 * @Description:切面控制点击事件重复问题，如果想让某个点击事件可以重复，
 * 可以在方法上添加@EnableDoubleClick注解
 */
@Aspect
class SingleClickAop {
    val TAG = "SingleClickAop"
    private val MIN_CLICK_DELAY_TIME = 600
    private var isDoubleClick: Boolean? = false
    private var preInvokerStr: String? = null
    private var clickViewKey: Int = -1

    companion object {
        //切点为OnClickListener.onClick方法的匹配表达式
        private const val CLICK_POINTCUTS = "execution(* android.view.View.OnClickListener.onClick(..))"

        //切点为lambda表达式的匹配表达式
        private const val CLICK_IN_LAMBDA_POINTCUTS = "execution(void *..lambda$*(..))"
    }

    @Pointcut(CLICK_POINTCUTS)
    fun onClickPointcuts() {
    }

    @Pointcut(CLICK_IN_LAMBDA_POINTCUTS)
    fun onClickInLambdaPointcuts() {
    }

    @Before("execution(@com.fusheng.aspectjdemo.EnableDoubleClick * *(..))")
    fun beforeEnableDoubleClick(joinPoint: JoinPoint) {
        isDoubleClick = true
    }

    @Around("onClickPointcuts()||onClickInLambdaPointcuts()")
    fun onClickListener(joinPoint: ProceedingJoinPoint) {
        if (isDoubleClick == true) {
            //如果发现有EnableDoubleClick注解标注，那么直接直接执行方法，之后返回
            isDoubleClick = false
            joinPoint.proceed()
            return
        }
        val args = joinPoint.args
        val view: View? = getViewFromArgs(args)
        clickViewKey = view?.id ?: -1
        Log.d(TAG, "clickViewKey----${clickViewKey}----")
        if (view == null) {
            //点击了非控件类型的元素，执行
            joinPoint.proceed()
            return
        }
        val lastClickTime = view.getTag(clickViewKey) as Long?
        val enableClick = canClick(lastClickTime ?: 0)//是否超过限制时间
        Log.d(TAG, "enableClick----${enableClick}----${lastClickTime}----")
        if (enableClick) {
            joinPoint.proceed()
            view.setTag(clickViewKey, System.currentTimeMillis())
            preInvokerStr = joinPoint.`this`?.toString() ?: ""
        } else {
            val isDiffInvoker = preInvokerStr != joinPoint.`this`?.toString() ?: ""//是否是相同的目标，防止OnClickListener嵌套引起的第二层onClick方法不执行问题
            //Log.d(TAG, "isSameTarget----${isDiffInvoker}----${preInvokerStr}----${joinPoint.`this`?.toString() ?: ""}----")
            if (isDiffInvoker) {
                joinPoint.proceed()
            }
        }
    }

    /**
     * 获取参数中的view
     */
    private fun getViewFromArgs(args: Array<Any>?): View? {
        if (args.isNullOrEmpty()) {
            return null
        }
        for (e in args) {
            if (e is View) {
                return e
            }
        }
        return null
    }

    /**
     * 判断是否达到可以点击的时间间隔
     * @param lastClickTime
     */
    private fun canClick(lastClickTime: Long): Boolean {
        Log.d(TAG, "currentTimeMillis----${lastClickTime}----${System.currentTimeMillis() - lastClickTime}----")
        return System.currentTimeMillis() - lastClickTime >= MIN_CLICK_DELAY_TIME
    }
}