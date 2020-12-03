package com.baomidou.framework.aop;//package com.baomidou.framework.aop;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//
//import java.lang.reflect.Method;
//
///**
// * @author yangjing (yang.xunjing@qq.com)
// * @date 2020-07-23 23:09
// */
//@Deprecated
//public class LogAspect {
//
//    /**
//     * 日志切入点
//     */
//    private LogPoint logPoint;
//
//    /**
//     * 保存系统操作日志
//     *
//     * @param joinPoint 连接点
//     * @return 方法执行结果
//     * @throws Throwable 调用出错
//     */
//    @Around(value = "@annotation(com.baomidou.framework.annotations.Log)")
//    public Object saveLog(ProceedingJoinPoint joinPoint) throws Throwable {
//        /**
//         * 解析Log注解
//         */
//        String methodName = joinPoint.getSignature().getName();
//        Method method = currentMethod(joinPoint, methodName);
//        Log log = method.getAnnotation(Log.class);
//
//        /**
//         * 日志入库
//         */
//        if (log != null) {
//            logPoint.saveLog(joinPoint, methodName, log.value(), Integer.parseInt(log.type()));
//        }
//
//        /**
//         * 方法执行
//         */
//        return joinPoint.proceed();
//    }
//
//    /**
//     * 获取当前执行的方法
//     *
//     * @param joinPoint  连接点
//     * @param methodName 方法名称
//     * @return 方法
//     */
//    private Method currentMethod(ProceedingJoinPoint joinPoint, String methodName) {
//        /**
//         * 获取目标类的所有方法，找到当前要执行的方法
//         */
//        Method[] methods = joinPoint.getTarget().getClass().getMethods();
//        Method resultMethod = null;
//        for (Method method : methods) {
//            if (method.getName().equals(methodName)) {
//                resultMethod = method;
//                break;
//            }
//        }
//        return resultMethod;
//    }
//
//    public LogPoint getLogPoint() {
//        return logPoint;
//    }
//
//    public void setLogPoint(LogPoint logPoint) {
//        this.logPoint = logPoint;
//    }
//
//}
