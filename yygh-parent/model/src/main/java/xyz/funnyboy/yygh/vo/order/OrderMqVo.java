package xyz.funnyboy.yygh.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import xyz.funnyboy.yygh.vo.sms.SmsVo;

@Data
@ApiModel(description = "OrderMqVo")
public class OrderMqVo
{

    @ApiModelProperty(value = "可预约数")
    private Integer reservedNumber;

    @ApiModelProperty(value = "剩余预约数")
    private Integer availableNumber;

    @ApiModelProperty(value = "排班id")
    private String scheduleId;

    @ApiModelProperty(value = "短信实体")
    private SmsVo smsVo;

}

