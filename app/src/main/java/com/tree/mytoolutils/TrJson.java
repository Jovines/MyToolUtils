package com.tree.mytoolutils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class TrJson {

    private static TrJson trJson;
    private final int FIELD = 1;
    private final int OBJECT = 2;
    private final int ARRAY = 3;
    Type parameterType;

    Context context;



    private TrJson(Context context) {
        this.context = context;
    }

    public static TrJson with(Context context) {
        if (trJson == null) {
            synchronized (TrJson.class){
                if (trJson == null) {
                    trJson = new TrJson(context);
                }
            }
        }
        return trJson;
    }

    public static TrJson getTrJson() {
        return trJson;
    }

    public <T> T factoryBean(String json, T mT) throws JSONException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        JSONObject jsonObject = new JSONObject(json);//总的JSONobject
        Class mClass = mT.getClass();//总的Class
        Field fields[] = mClass.getDeclaredFields();
        fieldsTransfer(mT,fields,mClass,jsonObject);
        return mT;
    }

    public <T> T factoryBean(String json, Type type) throws JSONException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type rawType = parameterizedType.getRawType();
        parameterType = parameterizedType.getActualTypeArguments()[0];
        JSONObject jsonObject = new JSONObject(json);//总的JSONobject
        T mT = (T) Class.forName(((Class)rawType).getName()).newInstance();
        Class mClass = (Class)rawType;//总的Class
        Field fields[] = mClass.getDeclaredFields();
        fieldsTransfer(mT,fields,mClass,jsonObject);
        return mT;
    }

    //遍历属性数组通过属性类型来调用函数
    private void fieldsTransfer(Object mObject, Field fields[] , Class mClass, JSONObject jsonObject) throws JSONException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        for (Field f : fields) {
            if (judge(f.getName(),jsonObject) == FIELD){
                handleField(mObject,f,mClass,jsonObject);
            }else if (judge(f.getName(),jsonObject) == OBJECT){
                handleObject(mObject,f,mClass,jsonObject);
            }else if (judge(f.getName(),jsonObject) == ARRAY){
                handleArray(mObject,f,mClass,jsonObject);
            }
        }
    }

    //通过属性名判断是否为一个对象
    private int judge(String fieldName,JSONObject jsonObject) throws JSONException {
        char name[] = jsonObject.getString(fieldName).toCharArray();
        if (name.length>0){
            switch (name[0]) {
                case '{':
                    return OBJECT;
                case '[':
                    return ARRAY;
                default:
                    return FIELD;
            }
        }
        return FIELD;
    }

    int i = 0;

    //处理属性
    private void handleField(Object mObject,Field mF,Class mClass,JSONObject jsonObject) throws NoSuchMethodException, JSONException, InvocationTargetException, IllegalAccessException {
        Method setMethod = mClass.getDeclaredMethod(findSetMethodName(mF.getName()),mF.getType());
//        Class<?> parameterType = (setMethod.getParameterTypes())[0];
            if ("string".equalsIgnoreCase(mF.getType().getSimpleName())) {
                setMethod.invoke(mObject, jsonObject.get(mF.getName()));
            } else if ("int".equalsIgnoreCase(mF.getType().getSimpleName())
                    || "interger".equalsIgnoreCase(mF.getType().getSimpleName())) {
                setMethod.invoke(mObject, Integer.parseInt(jsonObject.get(mF.getName()).toString()));
            } else if ("double".equalsIgnoreCase(mF.getType().getSimpleName())) {
                setMethod.invoke(mObject, Double.parseDouble(jsonObject.get(mF.getName()).toString()));
            } else if ("boolean".equalsIgnoreCase(mF.getType().getSimpleName())) {
                setMethod.invoke(mObject, Boolean.parseBoolean(jsonObject.get(mF.getName()).toString()));
            }
    }

    int j = 0;
    //处理对象
    private void handleObject(Object mObject, Field mF, Class mClass, JSONObject mJsonObject) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, JSONException, InstantiationException, ClassNotFoundException {
        Method setMethod = mClass.getDeclaredMethod(findSetMethodName(mF.getName()), mF.getType());
        JSONObject jsonObject = mJsonObject.getJSONObject(mF.getName());//获得当前对象的JSONobject
        Object object = (Object)(mF.getType() == Object.class?Class.forName(((Class)parameterType).getName()).newInstance():mF.getType().newInstance());//获得当前的对象
        Class nClass = object.getClass()==Class.class?(Class) parameterType:object.getClass();//获得当前的对象的class对象
        Field fields[] = nClass.getDeclaredFields();//获得当前的类中的属性
        fieldsTransfer(object,fields,nClass,jsonObject);//进行处理
        setMethod.invoke(mObject,object);//设置当前对象至外部对象
    }

    //处理数组
    private void handleArray(Object mObject, Field mF, Class mClass, JSONObject mJsonObject) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, JSONException, InstantiationException, ClassNotFoundException {
        Method setMethod = mClass.getDeclaredMethod(findSetMethodName(mF.getName()), mF.getType());
        JSONArray jsonArray = mJsonObject.getJSONArray(mF.getName());//获取当前的JsonArray
        Type genericType = mF.getGenericType();//获得属性类型
        Class<?> genericClazz = null;
        if(genericType instanceof ParameterizedType){
            ParameterizedType pt = (ParameterizedType) genericType;
            genericClazz = (Class<?>)pt.getActualTypeArguments()[0];//得到泛型里的class类型对象
        }
        List<Object> beans = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = (JSONObject)jsonArray.get(i);
            assert genericClazz != null;
            Object object = genericClazz.newInstance();
            Field fields[] = genericClazz.getDeclaredFields();
            fieldsTransfer(object,fields,genericClazz,jsonObject);
            beans.add(object);
        }
        setMethod.invoke(mObject,beans);
    }

    //通过属性名来找到设置方法名
    private String findSetMethodName(String fieldName) {
        return "set" + transform(fieldName);
    }


    //首字母大写
    private String transform(String word){
        char abc[] = word.toCharArray();
        if (abc[0]>= 97 && abc[0]<= 122){  //防止格式不正确的属性
            abc[0] -= 32;
            return String.valueOf(abc);
        }
     return word;
    }

    public static class TypeToken<T> {
        final Type type;

        @SuppressWarnings("unchecked")
        protected TypeToken() {
            this.type = getSuperclassTypeParameter(getClass());
        }

        Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class) {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            assert parameterized != null;
            return parameterized.getActualTypeArguments()[0];
        }

        public final Type getType() {
            return type;
        }
    }
}
