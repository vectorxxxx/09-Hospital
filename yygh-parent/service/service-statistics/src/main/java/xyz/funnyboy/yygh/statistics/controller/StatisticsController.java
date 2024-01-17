package xyz.funnyboy.yygh.statistics.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.funnyboy.yygh.common.result.Result;
import xyz.funnyboy.yygh.order.client.OrderFeignClient;
import xyz.funnyboy.yygh.vo.order.OrderCountQueryVo;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-17 20:16:16
 */
@Api(tags = "统计管理接口")
@RestController
@RequestMapping("/admin/statistics")
public class StatisticsController
{
    @Autowired
    private OrderFeignClient orderFeignClient;

    @ApiOperation(value = "获取订单统计数据")
    @GetMapping("getCountMap")
    public Result getCountMap(
            @ApiParam(name = "orderCountQueryVo",
                      value = "查询条件",
                      required = false)
                    OrderCountQueryVo orderCountQueryVo) {
        return Result.ok(orderFeignClient.getCountMap(orderCountQueryVo));
    }

}
