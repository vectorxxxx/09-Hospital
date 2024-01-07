package xyz.funnyboy.yygh.model.cmn;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import xyz.funnyboy.yygh.model.base.BaseEntity;

/**
 * <p>
 * Dict
 * </p>
 *
 * @author VectorX
 * @version 1.0.0
 * @date 2024/01/06
 */
@Data
@ApiModel(description = "数据字典")
@TableName("dict")
public class Dict extends BaseEntity
{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "上级id")
    @TableField("parent_id")
    private Long parentId;

    @ApiModelProperty(value = "名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "值")
    @TableField("value")
    private String value;

    @ApiModelProperty(value = "编码")
    @TableField("dict_code")
    private String dictCode;

    @ApiModelProperty(value = "是否包含子节点")
    @TableField(exist = false)
    private boolean hasChildren;

}
