package org.douban.model;

import lombok.Data;

import java.util.HashMap;

/**
 * Created by liangwenchang on 2018/5/22.
 */
@Data
public class MovieDetailModel {
    private String movieID = null;
    private int movietype = -1;
    private String movieName    = null;
    private String genres       = null;
    private String countries    = null;
    private String imageUrl     = null;
    private String year         = null;
    private String summary      = null;
    private double scoreAvg  = (double)0;
    //想看数
    private int wishCount    = -1;
    //收藏数
    private int collectCount = -1;
    //评分数
    private int rateCount    = -1;
    //演员：<名字，头像url>
    private HashMap<String,String> castsMap = null;

}
