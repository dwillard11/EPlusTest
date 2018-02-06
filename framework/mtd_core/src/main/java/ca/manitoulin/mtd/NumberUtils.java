package ca.manitoulin.mtd;


import com.google.common.base.Throwables;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by zhuiz on 2017/4/9.
 *
 * @Description
 */
public class NumberUtils {
    public static final String CURRENCY_PATTERN_DEFAULT = "#,###.00";
    private static final Logger log = Logger.getLogger(NumberUtils.class);

    public static BigDecimal keep(String number, int scale) {

        if (isEmpty(number))
            return BigDecimal.ZERO.setScale(scale, BigDecimal.ROUND_HALF_UP);
        Double d;
        try {
            d = Double.parseDouble(number);
        } catch (NumberFormatException e) {
            log.error(Throwables.getStackTraceAsString(e));
            return BigDecimal.ZERO.setScale(scale, BigDecimal.ROUND_HALF_UP);
        }
        return new BigDecimal(d).setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

    public static boolean upToDecimal(String str, int scale) {
        if (scale < 1 || isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))([.]\\d{0," + scale + "})?$");
        return pattern.matcher(str).matches();

    }

    public static String percentFormat(double d, int scale) {
        NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMaximumFractionDigits(scale);
        return percent.format(d);
    }

    public static String currencyFormat(double d, String pattern) {
        if (StringUtils.isEmpty(pattern)) pattern = CURRENCY_PATTERN_DEFAULT;
        try {
            DecimalFormat formatter = new DecimalFormat(pattern);
            return formatter.format(d);
        } catch (Exception e) {
            log.error("currency format error->value:" + d + " pattern:" + pattern);
            return null;
        }

    }
    public static void main(String[] args) {


        System.out.println(currencyFormat(3.33d, "#,###.00"));
        System.out.println(percentFormat(3.33d, 0));
    }


}
