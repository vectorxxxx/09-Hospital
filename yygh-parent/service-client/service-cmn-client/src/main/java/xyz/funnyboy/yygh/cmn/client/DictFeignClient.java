package xyz.funnyboy.yygh.cmn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * DictFeignClient
 *
 * @author VectorX
 * @version V1.0
 * @date 2024-01-08 20:31:00
 */
@Component
@FeignClient("service-cmn")
public interface DictFeignClient
{
    @GetMapping("/admin/cmn/dict/getName/{dictCode}/{value}")
    String getName(
            @PathVariable("dictCode")
                    String dictCode,

            @PathVariable("value")
                    String value);

    @GetMapping("/admin/cmn/dict/getName/{value}")
    String getName(
            @PathVariable("value")
                    String value);
}
