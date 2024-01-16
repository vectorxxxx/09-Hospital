package xyz.funnyboy.yygh.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.funnyboy.yygh.common.result.Result;
import xyz.funnyboy.yygh.enums.OrderStatusEnum;
import xyz.funnyboy.yygh.model.order.OrderInfo;
import xyz.funnyboy.yygh.order.service.OrderService;
import xyz.funnyboy.yygh.vo.order.OrderQueryVo;

/**
 * 订单接口
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-15 23:29:31
 */
@Api(tags = "订单接口")
@RestController
@RequestMapping("/admin/order/orderInfo")
public class OrderController
{
    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "订单列表（条件查询带分页）")
    @GetMapping("{page}/{limit}")
    public Result list(
            @ApiParam(name = "page",
                      value = "页码",
                      required = true)
            @PathVariable
                    int page,

            @ApiParam(name = "limit",
                      value = "每页数量",
                      required = true)
            @PathVariable
                    int limit,

            @ApiParam(name = "orderQueryVo",
                      value = "查询条件",
                      required = false)
                    OrderQueryVo orderQueryVo) {
        final Page<OrderInfo> pageParam = new Page<>(page, limit);
        return Result.ok(orderService.selectPage(pageParam, orderQueryVo));
    }

    @ApiOperation(value = "获取订单状态")
    @GetMapping("getStatusList")
    public Result getOrderStatus() {
        return Result.ok(OrderStatusEnum.getStatusList());
    }

    @ApiOperation(value = "获取订单")
    @GetMapping("show/{orderId}")
    public Result get(
            @ApiParam(name = "orderId",
                      value = "订单ID",
                      required = true)
            @PathVariable("orderId")
                    Long orderId) {
        return Result.ok(orderService.show(orderId));
    }
}
