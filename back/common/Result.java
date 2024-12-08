package common;

import lombok.Data;

@Data
public class Result<T> {
    // 泛型类/接口的定义格式 class classname/interfacename<T,...E>{}
    // 泛型方法的定义格式 权限修饰符 <T> 返回值类型 方法名(形参列表){}
    private Integer code;
    private String message;
    private T data;

    // 空参构造器
    public Result(){}

    /**
     * 根据传进来的数据构造一个result，没有提供code和message，主要用于在包装data
     * @param data  需要相应给客户端的数据
     * @param <T>   一般以map的形式传入
     * @return  返回一个只包含了data的结果
     */
    protected static <T> Result<T> build(T data){
        Result<T> result = new Result<>();
        if(data!=null){
            result.setData(data);
        }
        return result;
    }

    /**
     * 调用包装好的result根据传进来的code和message进行赋值
     * @param data  需要响应给客户端的数据
     * @param code  响应给客户端的编码
     * @param message   响应给客户端的信息
     * @param <T> 一般以map的形式传入
     * @return 返回一个完整的结果集
     */
    public static <T> Result<T> build(T data,Integer code, String message){
        Result<T> result = build(data);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 根据ResultEnum设置结果的code和message
     * @param data  需要响应给客户端的数据
     * @param resultEnum 提前准备好的枚举类
     * @param <T> 一般以map的形式传入
     * @return  返回一个完整的结果集
     */
    public static  <T> Result<T> build(T data,ResultEnum resultEnum){
        Result<T> result = build(data);
        result.setCode(resultEnum.getCode());
        result.setMessage(resultEnum.getMessage());
        return result;
    }

    /**
     * 返回一个包含数据的成功状态的结果集
     * @param data  需要响应给客户端的数据
     * @param <T> 一般以map的形式传入
     * @return 返回一个完整的结果集
     */
    public static <T> Result<T> ok(T data){
        return build(data,ResultEnum.SUCCESS);
    }

    /**
     * 设置结果集的message
     * @param message message
     * @return  一个结果集
     */
    public Result<T> message(String message){
        this.setMessage(message);
        return this;
    }

    /**
     * 设置结果集的code
     * @param code code
     * @return 一个结果集
     */
    public Result<T> code(Integer code){
        this.setCode(code);
        return this;
    }
}
