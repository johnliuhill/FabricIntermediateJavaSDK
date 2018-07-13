/***********************************************************************
 *
 * @program: hft01
 * @description: SDK通用工具类
 *
 * @author: John Liu
 * @create: 2018/06/23 21:51
 *
 ***********************************************************************/


package cn.flt.dev.FabricIntermediateJavaSDK;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class Helper {

    /**************************************************************
     * parseDateFormat
     * 将日期转换为字符串
     *
     * @param date                  date日期
     * @return java.lang.String     日期字符串
     *
     * @author : John Liu
     */
    static String parseDateFormat(Date date) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);
            return sdf.format(date);
        } catch (Exception ex) {
            return "";
        }
    }

}
