package org.douban.model;

import lombok.Data;
import org.Utils.Common;

/**
 * Created by liangwenchang on 2018/5/22.
 */
//主演信息
@Data
public class MoviePeopleModel {
    private String movieID = null;
    private int movietype = -1;
    private String peopleID = null;
    //类型：1->导演，0->主演
    private int peopleType = -1;
    private String peopleName = null;
    private String imageURL = null;
    //影人条目（可看影人详细信息）
    private String altURL = null;
}
