package xyz.funnyboy.yygh.model.acl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import xyz.funnyboy.yygh.model.base.BaseEntity;

/**
 * <p>
 * 用户
 * </p>
 *
 * @author VectorX
 * @version 1.0.0
 * @date 2024/01/06
 * @see BaseEntity
 */
@Data
@ApiModel(description = "用户")
@TableName("acl_user")
public class User extends BaseEntity
{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户名")
    @TableField("username")
    private String username;

    @ApiModelProperty(value = "密码")
    @TableField("password")
    private String password;

    @ApiModelProperty(value = "昵称")
    @TableField("nick_name")
    private String nickName;

    @ApiModelProperty(value = "用户头像")
    @TableField("salt")
    private String salt;

    @ApiModelProperty(value = "用户签名")
    @TableField("token")
    private String token;

}



