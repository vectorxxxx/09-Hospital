package xyz.funnyboy.yygh.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import xyz.funnyboy.yygh.vo.order.OrderCountQueryVo;

import java.util.Map;

/**
 * @author VectorX
 * @version V1.0
 * @date 2024-01-17 20:10:24
 */
@Component
@FeignClient(value = "service-order")
public interface OrderFeignClient
{
    @PostMapping("/api/order/orderInfo/inner/getCountMap")
    Map<String, Object> getCountMap(
            @RequestBody
                    OrderCountQueryVo orderCountQueryVo);
}
