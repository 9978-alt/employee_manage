package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class WebUtil {
    static ObjectMapper objectMapper;
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z", Locale.US);
    static SimpleDateFormat sdf_new = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    static {
        objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    }

    public static <T> T readJson(HttpServletRequest request, Class<T> clazz){
        T t = null;
        try {
            BufferedReader br = request.getReader();
            StringBuilder buffer = new StringBuilder();
            String line=null;
            while ((line = br.readLine()) != null){
                buffer.append(line);
            }
            t = objectMapper.readValue(buffer.toString(),clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static void writeJson(HttpServletResponse response, Result result){
        response.setContentType("application/json;charset=utf-8");
        try {
            String info = objectMapper.writeValueAsString(result);
            response.getWriter().write(info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
