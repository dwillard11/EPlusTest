package ca.manitoulin.mtd.util;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhuiz on 2017/5/19.
 *
 * @Description
 */
public class EmailUtils {
    public static boolean validate(String email) {
        if (StringUtils.isEmpty(email)) {
            return false;
        }
        String check = "^([a-z0-9A-Z]+[_|-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        check = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";

        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(email);
        boolean isMatched = matcher.matches();
        return isMatched;
    }

    public static void main(String[] args) {
        System.out.println(validate("dffdfdf@qdq.com"));
        System.out.println(validateBatch(",,,"));
        System.out.println(validateBatch("dasd@ddd.com,,,"));
        System.out.println(validateBatch("dasd@ddd.com,dasd@ddd.com,dasd@ddd.com"));
        System.out.println(validateBatch("dasd@ddd.com,dasd@ddd.com,dasd@ddd.COM"));
        System.out.println(validateBatch("dasd@ddd.com"));
    }

    public static boolean validateBatch(String emails) {
        if (StringUtils.isEmpty(emails)) {
            return false;
        }
        Iterable<String> temp =  Splitter.on(',')
                .trimResults()
                .split(emails);
        for (String email : temp) {
            if (!validate(email)) return false;
        }
        return true;
    }
}
