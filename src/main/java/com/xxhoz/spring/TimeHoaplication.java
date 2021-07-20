package com.xxhoz.spring;

import com.xxhoz.spring.annotation.Autowired;
import com.xxhoz.spring.annotation.ComponentScan;
import com.xxhoz.spring.annotation.Scope;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.concurrent.ConcurrentHashMap;

public class TimeHoaplication {

    ConcurrentHashMap beanSourceMap = new ConcurrentHashMap<String, BeanDefinition>();

    ConcurrentHashMap beansMap = new ConcurrentHashMap<String, Object>();       // 二级缓存
    ConcurrentHashMap beanTempMap = new ConcurrentHashMap<String, Object>();    // 一级缓存

    private Class config;
    public TimeHoaplication(Class config){
        this.config = config;

        ComponentScan annotation = (ComponentScan) config.getDeclaredAnnotation(ComponentScan.class);
        if (annotation == null || annotation.value()=="") {
            throw new RuntimeException("Component注解错误");
        }
        String value = annotation.value();    // 扫描路径

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(value);
        // 获取扫描包下的文件
        String decode = null;
        try {
            decode = URLDecoder.decode(resource.getFile(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        File file = new File(decode);
        if (!file.isDirectory()) {
            throw new RuntimeException("Component注解错误");
        }
        File[] files = file.listFiles();

        // 将类装载为 BeanDefinition
        String s = value.split("/")[0];
        // 遍历扫描目录  TODO 递归待实现
        for (File f : files) {
            String path = f.getPath();
            //System.out.println("扫描到类:");
            //System.out.println(f.getPath());


            path = path.substring(path.indexOf(s), path.indexOf(".class"))
                    .replace("\\",".");
            // 类class
            Class<?> beanClass = null;
            try {
                beanClass = classLoader.loadClass(path);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            BeanDefinition beanDefinition = new BeanDefinition();
            Scope scopeAnnotation = beanClass.getDeclaredAnnotation(Scope.class);
            String scope;
            if (scopeAnnotation != null ) {
                scope = scopeAnnotation.value();
            }else {
                scope = "singleton";
            }
            beanDefinition.setBean(beanClass);
            beanDefinition.setScope(scope);
            beanSourceMap.put(beanClass.getSimpleName().toUpperCase(),beanDefinition);

        }
        //System.out.println(beanSourceMap);
    }

    private Object CreateBean(String beanName) throws InstantiationException, IllegalAccessException {
        BeanDefinition beanDefinition = (BeanDefinition)beanSourceMap.get(beanName);
        String scope = beanDefinition.getScope();
        // 创建出对象
        Class bean = beanDefinition.getBean();

        Object o = bean.newInstance();

        // 二级缓存
        if (!scope.equals("prototype")) {
            beanName = beanName.toUpperCase();
            beanTempMap.put(beanName,o);
        }

        // 依赖注入
        Field[] Fields = bean.getDeclaredFields();
        for (Field field : Fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                String b = field.getName().toUpperCase();
                field.setAccessible(true);
                //Object bean1 = getBean(b);
                Object bean1 = beansMap.get(b);
                if (bean1 == null) {
                    bean1 = beanTempMap.get(b);
                    if (bean1 == null) {
                        bean1 = getBean(b);
                    }
                }
                field.set(o,bean1);
            }
        }
        // 构造完成移动到一级缓存
        if (!scope.equals("prototype")) {
            beanName = beanName.toUpperCase();
            Object remove = beanTempMap.remove(beanName);
            beansMap.put(beanName,remove);
        }
        return o;
    }


    /**
     * 创建指定Bean
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) {

        beanName = beanName.toUpperCase();
        if (beanName.isEmpty()) { throw new RuntimeException("未指定Bean"); }

        BeanDefinition beanDefinition = (BeanDefinition)beanSourceMap.get(beanName);
        if (beanDefinition == null) { throw new RuntimeException("未找到指定Bean");}
        Object bean = null;
        try {
            if (beanDefinition.getScope().equals("prototype")) {
                bean = CreateBean(beanName);
            }else {
                bean = beansMap.get(beanName);
                if ( bean == null) {
                    bean = CreateBean(beanName);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return bean;
    }
}
