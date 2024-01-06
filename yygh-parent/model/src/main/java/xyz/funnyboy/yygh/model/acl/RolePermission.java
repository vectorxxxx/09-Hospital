package xyz.funnyboy.yygh.model.acl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import xyz.funnyboy.yygh.model.base.BaseEntity;

/**
 * <p>
 * 角色权限
 * </p>
 *
 * @author VectorX
 * @version 1.0.0
 * @date 2024/01/06
 * @see BaseEntity
 */
@Data
@ApiModel(description = "角色权限")
@TableName("acl_role_permission")
public class RolePermission extends BaseEntity
{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "roleid")
    @TableField("role_id")
    private Long roleId;

    @ApiModelProperty(value = "permissionId")
    @TableField("permission_id")
    private Long permissionId;

}

