package com.lihao.rxjavademo.card;

import io.reactivex.Observable;

/**
 * 校验工具。
 */
public class ValidationUtils {

    /**
     * 检查给定日期是否过期。
     * @param candidate 给定日期。
     * @return 检查结果。
     */
    public static boolean checkExpirationDate(CharSequence candidate) {
        return candidate.toString().matches("\\d\\d\\/\\d\\d");
    }

    /**
     * 字符串转整型数组。
     * @param str 给定的字符串。
     * @return 整型数组。
     */
    public static int[] convertFromStringToIntArray(CharSequence str) {
        int[] array = new int[str.length()];
        for (int i = 0; i < str.length(); i++){
            array[i] = Integer.parseInt(String.valueOf(str.charAt(i)));
        }
        return array;
    }

    /**
     * 信用卡卡号校验和。
     * @param digits 数字数组。
     * @return 校验结果。
     */
    public static boolean checkCardChecksum(int[] digits) {
        int sum = 0;
        int length = digits.length;
        for (int i = 0; i < length; i++) {
            int digit = digits[length - i - 1];
            if (i % 2 == 1) {
                digit *= 2;
            }
            sum += digit > 9 ? digit - 9 : digit ;
        }
        return sum %10 == 0;
    }

    public static Observable<Boolean> and(Observable<Boolean> ... os) {
        return Observable.combineLatest(os, values -> {
            for (Object valueObj : values) {
                boolean value = (boolean) valueObj;
                if (!value) {
                    return false;
                }
            }
            return true;
        });
    }

    public static Observable<Boolean> equals(Observable<Integer> ... os) {
        return Observable.combineLatest(os, values -> {
            int checkValue = ((Integer) values[0]).intValue();
            for (Object valueObj : values) {
                int value = ((Integer) valueObj).intValue();
                if (value != checkValue) {
                    return false;
                }
            }
            return true;
        });
    }
}
