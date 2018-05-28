package org.douban.model;

import lombok.Data;

/**
 * Created by liangwenchang on 2018/5/21.
 */
@Data
public class MovieSummaryModel {
    private String movieId = null;
    private String movieName = null;
    private double scoreAVG = 0;
    private String imageURL = null;
    //导演
    private String directors = null;
    //主演
    private String casts = null;
}
