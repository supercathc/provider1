package com.feign.bean_factory_post_processor;

import com.feign.anno.HttpConfig;
import com.feign.config.HttpConsumerProperties;
import com.feign.domain.HttpDomain;
import com.feign.factorybean.HttpRequestProxyFactoryBean;
import com.feign.utils.BinderUtils;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.internal.Util;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HttpPostProcessor implements BeanClassLoaderAware, EnvironmentAware, BeanFactoryPostProcessor, ApplicationContextAware {
    private ClassLoader classLoader;

    private ConfigurableListableBeanFactory beanFactory;

    private ApplicationContext context;

    private ConfigurableEnvironment environment;

    private OkHttpClient okHttpClient;

    private final Map<String, BeanDefinition> httpClientBeanDefinitions = new HashMap<>(4);

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        this.okHttpClient = buildOkHttpClient(environment);
        postProcessBeanFactory(beanFactory, (BeanDefinitionRegistry) beanFactory);
    }

    private void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory, BeanDefinitionRegistry beanRegistry) {
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            //??????bean????????????bd
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            //??????bean???class ?????????
            String beanClassName = beanDefinition.getBeanClassName();
            // ??????@Bean ??????????????????Object??????beanClassName???null
            if (Objects.isNull(beanClassName)) {
                continue;
            }
            //???beanName??????????????????class
            Class<?> clazz = ClassUtils.resolveClassName(beanClassName, classLoader);
            //???class?????????Field ?????????annotatedWithHttpConsumer??????
            ReflectionUtils.doWithFields(clazz, this::parseElement, this::annotatedWithHttpConsumer);
        }

        for (String beanName : httpClientBeanDefinitions.keySet()) {
            if (context.containsBean(beanName)) {
                throw new IllegalArgumentException("[HttpConsumer Starter] Spring context already has a bean named " + beanName
                        + ", please change @HttpConsumer field name.");
            }
            beanRegistry.registerBeanDefinition(beanName, httpClientBeanDefinitions.get(beanName));
        }
    }

    /**
     * ???field ???httpConfig??????
     * @param field
     */
    private void parseElement(Field field) {
        //?????????field?????????
        Class<?> interfaceClass = field.getType();
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("field [" + field.getName() + "] annotated with @HttpConfig must be interface!");
        }
        HttpConfig httpConsumer = AnnotationUtils.getAnnotation(field, HttpConfig.class);
        HttpDomain httpDomain = HttpDomain.from(httpConsumer);
        // ???????????????${}
        httpDomain.setDomain(beanFactory.resolveEmbeddedValue(httpDomain.getDomain()));
        httpDomain.setPort(beanFactory.resolveEmbeddedValue(httpDomain.getPort()));
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .rootBeanDefinition(HttpRequestProxyFactoryBean.class)
                .addConstructorArgValue(interfaceClass)
                .addConstructorArgValue(httpDomain)
                .addConstructorArgValue(okHttpClient);
        BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        beanDefinition.setPrimary(true);
        beanDefinition.setAutowireCandidate(true);
        httpClientBeanDefinitions.put(field.getName(), beanDefinition);
    }
    /**
     * ????????????field???????????????httpConfig??????
     * @param field
     * @return
     */
    private boolean annotatedWithHttpConsumer(Field field) {
        return field.isAnnotationPresent(HttpConfig.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    /**
     * ??????????????????httpclient
     *
     * @param environment
     * @return
     */
    private OkHttpClient buildOkHttpClient(ConfigurableEnvironment environment) {


        //???configuration??????????????? ???????????????????????????
        HttpConsumerProperties properties = Binder.get(environment)
                .bind(HttpConsumerProperties.PREFIX, HttpConsumerProperties.class)
                .orElse(new HttpConsumerProperties());

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(properties.getCoreThreads(), properties.getMaxThreads(),
                properties.getKeepAliveTime(), TimeUnit.SECONDS, new SynchronousQueue<>(), Util.threadFactory("OkHttp Custom Dispatcher", false));
        Dispatcher dispatcher = new Dispatcher(executor);
        dispatcher.setMaxRequests(properties.getMaxRequests());
        dispatcher.setMaxRequestsPerHost(properties.getMaxRequests());
        builder.dispatcher(dispatcher);
        builder.connectTimeout(properties.getConnectTimeOut(), TimeUnit.SECONDS);
        builder.readTimeout(properties.getReadTimeOut(), TimeUnit.SECONDS);
        builder.writeTimeout(properties.getWriteTimeOut(), TimeUnit.SECONDS);
        builder.connectionPool(new ConnectionPool(properties.getMaxIdleConnections(), properties.getConnectionKeepAliveTime(), TimeUnit.SECONDS));
        return builder.build();
    }
}
