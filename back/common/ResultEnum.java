package common;

/**
 * 定义返回结果的code和message的枚举类
 */
public enum ResultEnum {
    // 在枚举类的开头声明多个对象，对象之间使用逗号隔开
    SUCCESS(200,"success"),
    INFOERROR(501,"usernameOrPasswordError"),
    NOTLOGIN(502,"notLogin"),
    ABNORMALVALUE(503,"abnormalValue"),
    INCORRECTSTATUS(504,"incorrectStatus")
    ;

    // 声明当前类的实例变量，因为是枚举类，值不会改变，用private final 修饰
    private final Integer code;
    private final String message;

    // 私有化构造器
    private ResultEnum(Integer code,String message){
        this.code = code;
        this.message = message;
    }
    // 在枚举类中不提供setter方法
    public Integer getCode(){
        return code;
    }
    public String getMessage(){
        return message;
    }

    @Override
    public String toString() {
        return "ResultEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
