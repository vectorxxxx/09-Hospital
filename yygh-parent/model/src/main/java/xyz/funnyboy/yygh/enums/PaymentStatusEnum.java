package xyz.funnyboy.yygh.enums;

/**
 * 付款状态枚举
 *
 * @author VectorX
 * @version 1.0.0
 * @date 2024/01/06
 * @see Enum
 */
public enum PaymentStatusEnum
{
    UNPAID(1, "支付中"),

    PAID(2, "已支付");
    //REFUND(-1,"已退款");

    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    PaymentStatusEnum(Integer status, String name) {
        this.status = status;
        this.name = name;
    }

}
