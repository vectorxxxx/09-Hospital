package xyz.funnyboy.yygh.model.hosp;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * RegisterRule
 * </p>
 *
 * @author VectorX
 * @version 1.0.0
 * @date 2024/01/06
 */
@Data
@ApiModel(description = "预约规则")
@Document("BookingRule")
public class BookingRule
{

    @ApiModelProperty(value = "预约周期")
    private Integer cycle;

    @ApiModelProperty(value = "放号时间")
    private String releaseTime;

    @ApiModelProperty(value = "停挂时间")
    private String stopTime;

    @ApiModelProperty(value = "退号截止天数（如：就诊前一天为-1，当天为0）")
    private Integer quitDay;

    @ApiModelProperty(value = "退号时间")
    private String quitTime;

    @ApiModelProperty(value = "预约规则")
    private List<String> rule;

    /**
     * Map转换为Hospital对象时，预约规则bookingRule为一个对象属性，rule为一个数组属性，因此在转换时我们要重新对应的set方法，不然转换不会成功
     *
     * @param rule 规则
     */
    public void setRule(String rule) {
        if (!StringUtils.isEmpty(rule)) {
            this.rule = JSONArray.parseArray(rule, String.class);
        }
    }

}

