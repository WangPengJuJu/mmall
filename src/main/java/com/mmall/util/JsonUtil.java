package com.mmall.util;

import com.google.common.collect.Lists;
import com.mmall.pojo.TestPojo;
import com.mmall.pojo.User;
import com.sun.xml.internal.ws.developer.Serialization;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();
    static{
        //对象的所有字段全部列入
        objectMapper.setSerializationInclusion(Inclusion.NON_EMPTY);

        //取消默认转换timestamps形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);

        //忽略空bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);

        //所有日期格式统一为以下日期样式，即YYYY-MM-DD HH:MM:SS
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));

        //忽略在json字符串存在，但是在java对象中不存在对应属性的情况,防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }
    public static <T> String obj2String(T obj){
        if(obj == null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj:objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("parse obj to string error",e);
            return null;
        }
    }

    public static <T> String obj2StringPretty(T obj){
        if(obj == null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj:objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("parse obj to string error",e);
            return null;
        }
    }

    public static <T> T string2Obj(String str,Class<T> clazz){
        if(StringUtils.isEmpty(str) || clazz == null){
            return null;
        }
        try {
            return clazz.equals(String.class)?(T)str:objectMapper.readValue(str,clazz);
        } catch (IOException e) {
            log.warn("parse string to obj error",e);
            return null;

        }
    }

    public static <T> T string2Obj(String str, TypeReference<T> typeReference){
        if(StringUtils.isEmpty(str) || typeReference == null){
            return null;
        }
        try {
            return (T)(typeReference.getType().equals(String.class)?str:objectMapper.readValue(str,typeReference));
        } catch (IOException e) {
            log.warn("parse string to obj error",e);
            return null;
        }
    }

    public static <T> T string2Obj(String str, Class<?> collectionClass,Class<?> ...elementClasses){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClasses);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (IOException e) {
            log.warn("parse string to obj error",e);
            return null;
        }
    }

    public static void main(String[] args) {
//     TestPojo obj = new TestPojo();
//     obj.setId("1");
//     obj.setName("wpj");
        String JsonObj = "{\"id\":\"1\",\"name\":\"wpj\",\"age\":12}";
        TestPojo o = JsonUtil.string2Obj(JsonObj,TestPojo.class);
//        User u2 = new User();
//        u2.setId(2);
//        u2.setUsername("wpj2");
//        String str = JsonUtil.obj2String(u1);
//        String strPretty = JsonUtil.obj2StringPretty(u1);
//        List<User> userList = Lists.newArrayList();
//        userList.add(u1);
//        userList.add(u2);
//        String userListStr = JsonUtil.obj2StringPretty(userList);
//        log.info("str:{}",str);
//        log.info("strPretty:{}",strPretty);
//        log.info("userListStr:{}",userListStr);
//        List<User> userListResult = JsonUtil.string2Obj(userListStr, new TypeReference<List<User>>(){});
//        List<User> userListResult2 = JsonUtil.string2Obj(userListStr,List.class,User.class);
//        User user1 = JsonUtil.string2Obj(str,User.class);
        System.out.println("end");
    }
}
