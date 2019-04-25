package papd.pingan.com.lywcharles;


import java.io.File;
import java.io.FileOutputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class MyClass {
    private static String JAR_DIR = "C:\\Users\\11870\\Desktop\\Charles\\lywcharles\\src\\main\\java\\papd\\pingan\\com\\lywcharles\\";
    private static String JAR_NAME = "charles.jar";
    private static String PKGNAME = "com.xk72.charles";
    private static String CLASSNAME = "License";
//    private static String OUT_PATH = "C:\\Users\\11870\\Desktop\\Charles\\lywcharles\\libs\\";

    public static void main(String[] args) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                observableEmitter.onNext(JAR_DIR + JAR_NAME);
            }
        }).map(new Function<String, byte[]>() {
            @Override
            public byte[] apply(String jarPath) throws Exception {
                ClassPool classPool = ClassPool.getDefault();
                classPool.insertClassPath(jarPath);
                CtClass ctClass = classPool.get(PKGNAME + "." + CLASSNAME);
//                CtMethod method = ctClass.getDeclaredMethod("a", null);
                CtMethod[] methods = ctClass.getDeclaredMethods("a");
                for (CtMethod method : methods) {
                    CtClass[] parameterTypes = method.getParameterTypes();
                    if (method.getReturnType().getName().equals("boolean") && parameterTypes != null && parameterTypes.length == 0) {
                        System.out.println(method.getReturnType().getName());
                        method.setBody("{return true;}");
                    }
                }
                CtMethod[] ctMethods = ctClass.getDeclaredMethods("b");
                for (CtMethod ctMethod : ctMethods) {
                    CtClass[] parameterTypes = ctMethod.getParameterTypes();
                    if (ctMethod.getReturnType().getName().equals("java.lang.String") && parameterTypes != null && parameterTypes.length == 0) {
                        System.out.println("java.lang.String");
                        ctMethod.setBody("{return \" this is lyw \";}");
                    }
                }
                return ctClass.toBytecode();
            }
        }).map(new Function<byte[], String>() {
            @Override
            public String apply(byte[] bytes) throws Exception {
                String classPath = PKGNAME.replace(".", "/") + "/";
                File file = new File(JAR_DIR + classPath + CLASSNAME + ".class");
                System.out.println(classPath + CLASSNAME + ".class");
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(bytes);
                return file.getAbsolutePath();
            }
        }).map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) throws Exception {
                String classPath = PKGNAME.replace(".", "/") + "/";
                Process process = Runtime.getRuntime().exec("jar uvf " + JAR_NAME + " " + classPath + CLASSNAME + ".class",null,new File(JAR_DIR));
                System.out.println("jar uvf " + JAR_NAME + " " + classPath + CLASSNAME + ".class");
                return process.waitFor();
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println("status" + integer + Thread.currentThread().getName());
            }
        });
    }
}
