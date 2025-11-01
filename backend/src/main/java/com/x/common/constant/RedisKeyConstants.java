package com.x.common.constant;

public class RedisKeyConstants {
    private RedisKeyConstants() {} // 私有构造，防止实例化

    public static final String LOCK_PREFIX = "lock:";
    public static final String STORE_PREFIX = "store:";

    public  static  final  String INFO =  "info:";

    // 具体业务锁
    public static final String LOCK_REGISTER = LOCK_PREFIX + "register";

    public static final String LOCK_LIKE = LOCK_PREFIX + "like";

    public static final String STORE_ARTICLEIDS = STORE_PREFIX+"articleIds";

    public static final String STORE_POSTIDS = STORE_PREFIX+"postIds";

    public  static  final  String POST_COUNT="postCount";
    public static final String USER_COUNT="userCount";
    public static final String ARTICLE_COUNT="articleCount";

    public static final String CATEGORY_COUNT="categoryCount";

    public static final String MY_POST_COUNT="myPostCount";

    public static final String CATEGORY_INFO=INFO+"category";

    public static final String ARTICLE_LIKEIDS="article:likeIds";

    public static final String COMMENT_LIKEIDS="comment:likeIds";


    //stream
    public static final String LIKE_STREAM = "like:stream";
    public static final String LIKE_CONSUMER_GROUP = "like-group";
    public static final String LIKE_CONSUMER_NAME = "like-consumer-1";


    public static final int COUNT_TTL=3; //3天

    public static  final int LIKE_TTL=3;  //3天

}
