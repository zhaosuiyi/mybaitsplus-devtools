package com.mybaitsplus.devtools.core.config;

import com.mybaitsplus.devtools.core.config.autoconfig.EnableDevTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class EnableDevToolsAutoConfig implements ImportSelector {
	
	@Override
	public String[] selectImports(AnnotationMetadata annotationMetadata) {
		
		log.info("------------------启用【DevTools】----------------");
        Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(EnableDevTools.class.getName());
        if(attributes==null){return new String[0]; }
        //获取package属性的value
        String[] basePackages = (String[]) attributes.get("packages");
        
        if(basePackages==null || basePackages.length<=0 || StringUtils.isEmpty(basePackages[0])){
        	  String basePackage = null;
              try {
                  basePackage = Class.forName(annotationMetadata.getClassName()).getPackage().getName();
              } catch (ClassNotFoundException e) {
                  e.printStackTrace();
              }
              basePackages = new String[] {basePackage};
        }

        log.info("------------------扫描DevTools包:{}----------------",Arrays.toString(basePackages));

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
        //scanner.addIncludeFilter(new AnnotationTypeFilter(Configuration.class));
        //scanner.addIncludeFilter(new AnnotationTypeFilter(Component.class));

        Set<String> classes = new HashSet<>();
        for (String basePackage : basePackages) {
            scanner.findCandidateComponents(basePackage).forEach(beanDefinition ->{
            	String beanClassName = beanDefinition.getBeanClassName();
                log.info("------------------加载:{}",beanClassName);
                classes.add(beanClassName);
            });
        }
        return classes.toArray(new String[classes.size()]);

	}
	
	/*public String[] selectImports(AnnotationMetadata annotationMetadata) {
		log.info("装配EnableSxBootAutoConfig");
		//获取EnableEcho注解的所有属性的value
        Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(EnableSxBoot.class.getName());
        if(attributes==null){return new String[0]; }
        //获取package属性的value
        String[] packages = (String[]) attributes.get("packages");
        if(packages==null || packages.length<=0 || StringUtils.isEmpty(packages[0])){
            return new String[0];
        }
        log.info("加载该包所有类到spring容器中的包名为："+ Arrays.toString(packages));
        Set<String> classNames = new HashSet<>();
        Set<String> result = new HashSet<>();
        
        for(String packageName:packages){
            classNames.addAll(ClassUtils.getClassName(packageName,true));
        }
        //将类打印到日志中
        for(String className:classNames){
        	 try {
        		 Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        		 if(clazz.isAnnotationPresent(Configuration.class)
        				 || clazz.isAnnotationPresent(Component.class)
        				 	|| clazz.isAnnotationPresent(Component.class)){
        			 log.info(className+"加载到spring容器中");
        			 result.add(className);
        		 }
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
        }
  
        String[] returnClassNames = new String[result.size()];
        returnClassNames= result.toArray(returnClassNames);
        return  returnClassNames;
	}*/
}
