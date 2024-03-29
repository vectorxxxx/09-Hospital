package xyz.funnyboy.yygh.vo.acl;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 用户查询实体
 * </p>
 *
 * @author VectorX
 * @version 1.0.0
 * @date 2024/01/06
 * @see Serializable
 */
@Data
@ApiModel(description = "用户查询实体")
public class UserQueryVo implements Serializable
{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "昵称")
    private String nickName;

}

