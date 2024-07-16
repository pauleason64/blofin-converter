package com.peason;

import org.springframework.context.ApplicationContext;

//@Component
public class SpringContext {

    static ApplicationContext appContext=null;

    /**
     * Returns the Spring managed bean instance of the given class type if it exists.
     * Returns null otherwise.
     * @param beanClass
     * @return
     */
    public static <T extends Object> T getBean(Class<T> beanClass) {
        return appContext.getBean(beanClass);
    }

    public static ApplicationContext getContext() {
        return appContext;
    }

    public  void setApplicationContext(ApplicationContext context) { //throws BeansException

        SpringContext.appContext = context;
    }
}