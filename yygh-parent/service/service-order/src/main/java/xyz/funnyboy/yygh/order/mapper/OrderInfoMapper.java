package xyz.funnyboy.yygh.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.funnyboy.yygh.model.order.OrderInfo;
import xyz.funnyboy.yygh.vo.order.OrderCountQueryVo;
import xyz.funnyboy.yygh.vo.order.OrderCountVo;

import java.util.List;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-15 23:27:05
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo>
{
    /**
     * 查询订单计数
     *
     * @param orderCountQueryVo 订单计数查询 VO
     * @return {@link List}<{@link OrderCountVo}>
     */
    List<OrderCountVo> selectOrderCount(
            @Param("vo")
                    OrderCountQueryVo orderCountQueryVo);
}
