package xyz.funnyboy.yygh.order.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.yygh.common.result.Result;
import xyz.funnyboy.yygh.common.utils.AuthContextHolder;
import xyz.funnyboy.yygh.enums.OrderStatusEnum;
import xyz.funnyboy.yygh.model.order.OrderInfo;
import xyz.funnyboy.yygh.order.service.OrderService;
import xyz.funnyboy.yygh.vo.order.OrderCountQueryVo;
import xyz.funnyboy.yygh.vo.order.OrderQueryVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 订单接口
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-15 23:29:31
 */
@Api(tags = "订单接口")
@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderApiController
{
    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "创建订单")
    @PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
    public Result submitOrder(
            @ApiParam(name = "scheduleId",
                      value = "排班ID",
                      required = true)
            @PathVariable
                    String scheduleId,

            @ApiParam(name = "patientId",
                      value = "就诊人ID",
                      required = true)
            @PathVariable
                    Long patientId) {
        return Result.ok(orderService.saveOrder(scheduleId, patientId));
    }

    @ApiOperation(value = "订单列表（条件查询带分页）")
    @GetMapping("auth/{page}/{limit}")
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
                    OrderQueryVo orderQueryVo,

            HttpServletRequest request) {
        // 设置当前用户ID
        orderQueryVo.setUserId(AuthContextHolder.getUserId(request));
        // 分页查询
        final Page<OrderInfo> pageParam = new Page<>(page, limit);
        return Result.ok(orderService.selectPage(pageParam, orderQueryVo));
    }

    @ApiOperation(value = "获取订单详情")
    @GetMapping("auth/getOrder/{orderId}")
    public Result getOrder(
            @ApiParam(name = "orderId",
                      value = "订单ID",
                      required = true)
            @PathVariable("orderId")
                    String orderId) {
        return Result.ok(orderService.getOrderInfo(orderId));
    }

    @ApiOperation(value = "获取订单状态")
    @GetMapping("auth/getStatusList")
    public Result getOrderStatus() {
        return Result.ok(OrderStatusEnum.getStatusList());
    }

    @ApiOperation(value = "取消预约")
    @PostMapping("auth/cancelOrder/{orderId}")
    public Result cancelOrder(
            @ApiParam(name = "orderId",
                      value = "订单ID",
                      required = true)
            @PathVariable("orderId")
                    Long orderId) {
        return Result.ok(orderService.cancelOrder(orderId));
    }

    @ApiOperation(value = "获取订单统计数据")
    @PostMapping("inner/getCountMap")
    public Map<String, Object> getCountMap(
            @ApiParam(name = "orderCountQueryVo",
                      value = "查询条件",
                      required = false)
            @RequestBody
                    OrderCountQueryVo orderCountQueryVo) {
        return orderService.getCountMap(orderCountQueryVo);
    }
}
