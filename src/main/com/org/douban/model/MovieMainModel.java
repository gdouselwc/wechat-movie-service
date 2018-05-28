package org.douban.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
/**
 * Created by liangwenchang on 2018/5/16.
 */
@Data
public class MovieMainModel {
    private int Movietype = -1;
    private int startCnt = -1;
    private int count = -1;
    private int rank = -1;
    private int box = -1;
    private char isNew;
    private int delta = -1;
    private int total = -1;
    private String title = null;
    private String rankdate = null;
}
