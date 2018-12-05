package com.igniubi.common.page;

/**
 * 维护分页信息
 * <p>
 *
 * @author 徐擂
 * @version 1.0.0
 * @date 2018-12-5
 */
public class PagerHelper {

    protected static final ThreadLocal<PagerInfo> LOCAL_PAGE = new ThreadLocal();

    public PagerHelper() {
    }

    protected static void setLocalPage(PagerInfo page) {
        LOCAL_PAGE.set(page);
    }

    public static <T> PagerInfo<T> getLocalPage() {
        return (PagerInfo)LOCAL_PAGE.get();
    }

    public static void clearPage() {
        LOCAL_PAGE.remove();
    }

    public static <E> PagerInfo<E> startPage(PagerInfo pagerInfo) {
        if (pagerInfo == null) {
            pagerInfo = new PagerInfo();
        }

        setLocalPage(pagerInfo);
        return pagerInfo;
    }
}
