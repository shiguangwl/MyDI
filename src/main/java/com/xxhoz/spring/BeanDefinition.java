package com.xxhoz.spring;

public class BeanDefinition {

    Class bean;
    String scope;

    public BeanDefinition() {
    }

    public BeanDefinition(Class bean, String scope) {
        this.bean = bean;
        this.scope = scope;
    }

    public Class getBean() {
        return bean;
    }

    public void setBean(Class bean) {
        this.bean = bean;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
