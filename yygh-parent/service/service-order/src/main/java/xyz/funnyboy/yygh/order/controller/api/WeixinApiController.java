package xyz.funnyboy.yygh.order.controller.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.funnyboy.yygh.common.result.Result;
import xyz.funnyboy.yygh.order.service.PaymentService;
import xyz.funnyboy.yygh.order.service.WeixinService;

import java.util.Map;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-16 20:49:09
 */
@Api(tags = "微信支付接口")
@RestController
@RequestMapping("/api/order/weixin")
public class WeixinApiController
{
    @Autowired
    private WeixinService weixinService;

    @Autowired
    private PaymentService paymentService;

    @ApiOperation(value = "生成微信支付二维码")
    @PostMapping("createNative/{orderId}")
    public Result createNative(
            @ApiParam(name = "orderId",
                      value = "订单ID",
                      required = true)
            @PathVariable
                    Long orderId) {
        return Result.ok(weixinService.createNative(orderId));
    }

    @ApiOperation(value = "查询支付状态")
    @GetMapping("queryPayStatus/{orderId}")
    public Result queryPayStatus(
            @ApiParam(name = "orderId",
                      value = "订单ID",
                      required = true)
            @PathVariable
                    Long orderId) {
        final Map<String, String> resultMap = weixinService.queryPayStatus(orderId);
        if (resultMap == null || resultMap.isEmpty()) {
            return Result
                    .fail()
                    .message("支付失败");
        }
        if ("SUCCESS".equals(resultMap.get("trade_state"))) {
            final String outTradeNo = resultMap.get("out_trade_no");
            paymentService.paySuccess(outTradeNo, resultMap);
            return Result
                    .ok()
                    .message("支付成功");
        }
        return Result
                .ok()
                .message("支付中");
    }
}
