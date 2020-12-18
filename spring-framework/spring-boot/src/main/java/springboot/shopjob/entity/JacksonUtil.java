package springboot.shopjob.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author zhaizhipeng
 * json 转换工具类
 */
public class JacksonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public static String toJsonString(Object obj) {
        if (obj == null){
            return "";
        }
        return objectMapper.writeValueAsString(obj);
    }

    @SneakyThrows
    public static <T> T parseObj(String str, TypeReference typeReference){
        if (StringUtils.isEmpty(str) || typeReference == null){
            return null;
        }
        try {
            return typeReference.getType().equals(String.class) ? (T) str : (T) objectMapper.readValue(str, typeReference);
        } catch (IOException e) {
            return null;
        }
    }

    @SneakyThrows
    public static <T> T parseObj(String str, Class<T> clazz) {
        return objectMapper.readValue(str, clazz);
    }


}
