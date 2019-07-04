package com.xingen.systemutils.calculation;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author HeXinGen
 * date 2018/12/24.
 */
public class CalculationUtils {

    /**
     * 保留两位小数，不四舍五入
     *
     * @param d
     * @return
     */
    private double formatDoubleWithoutReserved(double d) {
        BigDecimal bg = new BigDecimal(d).setScale(2, RoundingMode.DOWN);
        return bg.doubleValue();
    }
}
