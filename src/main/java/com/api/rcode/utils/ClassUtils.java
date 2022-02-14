package com.api.rcode.utils;

import com.api.rcode.mvc.HandlerManger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author ：河神
 * @date ：Created in 2022/2/11 11:27 上午
 */
public class ClassUtils {


    public static List<Class<?>> getAllClass(String basePack, Predicate<Class<?>> predicate) throws ClassNotFoundException {

        //先把包名转换为路径,首先得到项目的classpath
        String classpath = Objects.requireNonNull(HandlerManger.class.getResource("/")).getPath();

        //然后把我们的包名basPach转换为路径名
        basePack = basePack.replace(".", File.separator);
        //然后把classpath和basePack合并
        String searchPath = classpath + basePack;

        List<String> classPaths = new ArrayList<>();
        doPath(new File(searchPath),classPaths);

        //这个时候我们已经得到了指定包下所有的类的绝对路径了。我们现在利用这些绝对路径和java的反射机制得到他们的类对象
        List<Class<?>> classes = new ArrayList<>(classPaths.size());
        for (String s : classPaths) {
            //把 D:\work\code\20170401\search-class\target\classes\com\baibin\search\a\A.class 这样的绝对路径转换为全类名com.baibin.search.a.A
            s = s.replace(classpath.replace("/","\\")
                            .replaceFirst("\\\\",""),"")
                    .replace("\\",".")
                    .replace(".class","")
                    .replace(classpath,"")
                    .replace("/",".");

            Class<?> cls = Class.forName(s);
            if (!predicate.test(cls)){
                continue;
            }
            classes.add(cls);
        }
        return classes;
    }


    /**
     * 该方法会得到所有的类，将类的绝对路径写入到classPaths中
     */
    private static void doPath(File file,List<String> classPaths) {
        if (file.isDirectory()) {//文件夹
            //文件夹我们就递归
            File[] files = file.listFiles();
            if (files==null){
                return;
            }
            for (File f1 : files) {
                doPath(f1,classPaths);
            }
        } else {//标准文件
            //标准文件我们就判断是否是class文件
            if (file.getName().endsWith(".class")) {
                //如果是class文件我们就放入我们的集合中。
                classPaths.add(file.getPath());
            }
        }
    }



}
