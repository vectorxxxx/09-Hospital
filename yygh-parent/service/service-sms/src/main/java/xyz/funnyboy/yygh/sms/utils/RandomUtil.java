package xyz.funnyboy.yygh.sms.utils;

import java.text.DecimalFormat;
import java.util.*;

/**
 * 随机数工具类
 *
 * @author VectorX
 * @version 1.0.0
 * @date 2024/01/13
 */
public class RandomUtil
{

    private static final Random random = new Random();

    private static final DecimalFormat FOURDF = new DecimalFormat("0000");

    private static final DecimalFormat SIXDF = new DecimalFormat("000000");

    public static String getFourBitRandom() {
        return FOURDF.format(random.nextInt(10000));
    }

    public static String getSixBitRandom() {
        return SIXDF.format(random.nextInt(1000000));
    }

    /**
     * 给定数组，抽取n个数据
     *
     * @param list 列表
     * @param n    n
     * @return {@link List}<{@link Integer}>
     */
    public static List<Integer> getRandom(List<Integer> list, int n) {
        Random random = new Random();

        Map<Object, Object> hashMap = new HashMap<>();

        // 生成随机数字并存入HashMap
        for (int i = 0; i < list.size(); i++) {
            int number = random.nextInt(100) + 1;
            hashMap.put(number, i);
        }

        // 从HashMap导入数组
        Object[] robjs = hashMap
                .values()
                .toArray();

        List<Integer> r = new ArrayList<>();

        // 遍历数组并打印数据
        for (int i = 0; i < n; i++) {
            r.add(list.get((int) robjs[i]));
            System.out.print(list.get((int) robjs[i]) + "\t");
        }
        System.out.print("\n");
        return r;
    }
}

