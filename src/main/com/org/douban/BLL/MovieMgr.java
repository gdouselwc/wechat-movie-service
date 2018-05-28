package org.douban.BLL;

import org.Utils.Common;
import org.apache.log4j.Logger;
import org.douban.DAL.MovieDataMgr;
import org.douban.Web.WebRequest;
import org.douban.model.MoviePeopleModel;
import org.douban.model.MovieDetailModel;
import org.douban.model.MovieMainModel;
import org.douban.model.MovieSummaryModel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by liangwenchang on 2018/5/15.
 */
public class MovieMgr {
    private static Logger logger = Logger.getLogger(MovieMgr.class);
    //正在热映
    private String BeingFilmUrl = null;
    //即将上映
    private String ComingSoomUrl = null;
    //Top250
    private String Top250Url = null;
    //口碑榜
    private String PraiseUrl = null;
     // 北美票房榜
    private String USBoxUrl = null;
     // 新片榜
    private String NewMovieUrl = null;
    //电影详情
    private String MovieDetail = null;

    public MovieMgr(){
        parseConfig();
    }
    private void parseConfig(){
        InputStream in = getClass().getClassLoader().getResourceAsStream("config.properties");
        Properties ppt = new Properties();
        try{
            ppt.load(in);
            BeingFilmUrl = ppt.getProperty("BeingFilmUrl");
            ComingSoomUrl = ppt.getProperty("ComingSoomUrl");
            Top250Url = ppt.getProperty("Top250Url");
            PraiseUrl = ppt.getProperty("PraiseUrl");
            USBoxUrl = ppt.getProperty("USBoxUrl");
            NewMovieUrl = ppt.getProperty("NewMovieUrl");
            MovieDetail = ppt.getProperty("MovieDetail");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //http请求豆瓣电影信息
    //type  0：正在热映
    //      1：即将上映
    //      2：top250
    //      3：口碑榜
    //      4：北美票房榜
    //      5：新片榜'

    public void GetMovieInfo(int type) {
        String url = "";
        switch (type){
            case Common.BEINGFILMS :
                url = this.BeingFilmUrl;
                break;
            case Common.COMMINGFILMS:
                url = this.ComingSoomUrl;
                break;
            case Common.TOP250FILMS:
                url = this.Top250Url;
                break;
            case Common.PRAISEFILMS:
                url = this.PraiseUrl;
                break;
            case Common.USBOXFILMS:
                url = this.USBoxUrl;
                break;
            case Common.NEWFILMS:
                url = this.NewMovieUrl;
                break;
            default:
                break;

        }
        if (url == "") return;
        //请求数据
        String result = new WebRequest().sendGet(url);
        JSONObject jsonObject = new JSONObject(result);

        //TDOD 事务操作

        /*处理主表*/
        List<MovieSummaryModel> listSM  = new ArrayList<MovieSummaryModel>();
        List<String> movieIDs = new ArrayList<>();
        int serialID = DealMovieMain(type,jsonObject,listSM,movieIDs);
        if(Integer.MIN_VALUE == serialID){
            logger.error("SerialID 获取错误");
            return;
        }
        /*处理概要表*/
        DealMovieSummary(serialID,listSM);
        /*处理详情表*/
        List<MoviePeopleModel> list = new ArrayList<>();
        list = DealMovieDetail(movieIDs);
        MovieDataMgr dm = new MovieDataMgr();
        for (MoviePeopleModel pm : list){
            dm.insertPeopleSummary(pm);
        }
    }

    //主表
    private int DealMovieMain(int type,JSONObject jsonObject,List<MovieSummaryModel> listSM,
                              List<String> movieIDs){
        MovieMainModel mainModel = new MovieMainModel();
        mainModel.setMovietype(type);
        switch (type){
            //top250,正在热映，即将上映
            case Common.BEINGFILMS:
            case Common.COMMINGFILMS:
            case Common.TOP250FILMS:{
                mainModel.setCount(jsonObject.getInt("count"));
                mainModel.setStartCnt(jsonObject.getInt("start"));
                mainModel.setTotal(jsonObject.getInt("total"));
                mainModel.setTitle(jsonObject.getString("title"));
                JSONArray subject = jsonObject.getJSONArray("subjects");
                subject.forEach((Object item) -> {
                    MovieSummaryModel summaryModel = new MovieSummaryModel();
                    summaryModel.setMovieName(((JSONObject)item).getString("title"));
                    summaryModel.setScoreAVG(((JSONObject)item).getJSONObject("rating").getDouble("average"));
                    String id = ((JSONObject)item).getString("id");
                    summaryModel.setMovieId(id);
                    movieIDs.add(id);
                    summaryModel.setImageURL(((JSONObject)item).getJSONObject("images").getString("small"));
                    String directors = "";
                    String casts = "";
                    //主演
                    for(Object o : ((JSONObject)item).getJSONArray("casts")){
                        casts += ((JSONObject)o).getString("name") + "/";
                    }
                    if(casts != ""){
                        casts = casts.substring(0,casts.lastIndexOf('/'));
                    }
                    summaryModel.setCasts(casts);
                    //导演
                    for (Object o : ((JSONObject)item).getJSONArray("directors")){
                        directors += ((JSONObject) o).getString("name") + "/";
                    }
                    if(directors != ""){
                        directors = directors.substring(0,directors.lastIndexOf('/')-1);
                    }
                    summaryModel.setDirectors(directors);
                    listSM.add(summaryModel);
                });
                break;
            }
            //口碑 TODO need_permission
            case Common.PRAISEFILMS:{
                mainModel.setTitle(jsonObject.getString("title"));
                String subject = jsonObject.getString("subjects");
                JSONObject jtmp = new JSONObject(subject);
                mainModel.setRank(jtmp.getInt("rank"));
                mainModel.setDelta(jtmp.getInt("delta"));
                break;
            }
            //北美
            case Common.USBOXFILMS:{
                mainModel.setTitle(jsonObject.getString("title"));
                mainModel.setRankdate(jsonObject.getString("date"));
                String subject = jsonObject.getString("subjects");
                JSONObject jtmp = new JSONObject(subject);
                mainModel.setRank(jtmp.getInt("rank"));
                mainModel.setBox(jtmp.getInt("box"));
                mainModel.setIsNew(jtmp.getString("new")=="true"?'1':'0');
                break;
            }
            //新片 TODO need_permission
            case Common.NEWFILMS:{
                mainModel.setTitle(jsonObject.getString("title"));
                break;
            }
        }
        MovieDataMgr dm = new MovieDataMgr();
        return dm.insertMovieMain(mainModel);
    }

    //电影概要表
    private void DealMovieSummary(int serialid,List<MovieSummaryModel> listSM){
        if (serialid == Integer.MIN_VALUE){
            logger.debug("serialid获取失败");
            return;
        }
        MovieDataMgr dm = new MovieDataMgr();
        for(MovieSummaryModel sm : listSM){
            dm.insertMovieSummary(serialid,sm);
        }
    }

    //电影详细信息
    private List<MoviePeopleModel> DealMovieDetail(List<String> movieIDs){
        List<MovieDetailModel> list = new ArrayList<>();
        List<MoviePeopleModel> castsList = new ArrayList<>();
        for(String id : movieIDs){
            MovieDetailModel detailModel = new MovieDetailModel();
            String url = this.MovieDetail + id;
            String result = new WebRequest().sendGet(url);
            JSONObject jsonObject = new JSONObject(result);
            System.out.println(result);
            try {
                detailModel.setScoreAvg(jsonObject.getJSONObject("rating").getDouble("average"));
                detailModel.setRateCount(jsonObject.getInt("ratings_count"));
                detailModel.setCollectCount(jsonObject.getInt("collect_count"));
                detailModel.setWishCount(jsonObject.getInt("wish_count"));
                detailModel.setMovieID(id);
                detailModel.setMovieName(jsonObject.getString("title"));
                JSONArray tmp = jsonObject.getJSONArray("countries");
                String countries = "";
                for(int i = 0; i < tmp.length();++i){
                    countries += tmp.get(i).toString() + ',';
                }
                if(countries != ""){
                    countries.substring(0,countries.lastIndexOf(','));
                }
                detailModel.setCountries(countries);
                tmp = jsonObject.getJSONArray("genres");
                String genres = "";
                for(int i = 0; i < tmp.length();++i){
                    genres += tmp.get(i).toString() + ',';
                }
                if(genres != ""){
                    genres.substring(0,genres.lastIndexOf(','));
                }
                detailModel.setGenres(genres);
                detailModel.setImageUrl(jsonObject.getJSONObject("images").getString("small"));
                detailModel.setSummary(jsonObject.getString("summary"));
                detailModel.setYear(jsonObject.getString("year"));
                list.add(detailModel);

                //影人条目
                //导演
                JSONArray director = jsonObject.getJSONArray("directors");
                director.forEach(item -> {
                    MoviePeopleModel peopleModel = new MoviePeopleModel();
                    peopleModel.setMovieID(id);
                    peopleModel.setPeopleID(((JSONObject)item).getString("id"));
                    peopleModel.setPeopleName(((JSONObject)item).getString("name"));
                    peopleModel.setAltURL(((JSONObject)item).getString("alt"));
                    peopleModel.setImageURL(((JSONObject)item).getJSONObject("avatars").getString("small"));
                    peopleModel.setPeopleType(Common.DIRECTOR);
                    castsList.add(peopleModel);
                });
                //主演
                JSONArray casts = jsonObject.getJSONArray("casts");
                casts.forEach(item ->{
                    MoviePeopleModel peopleModel = new MoviePeopleModel();
                    peopleModel.setMovieID(id);
                    peopleModel.setPeopleID(((JSONObject)item).getString("id"));
                    peopleModel.setPeopleName(((JSONObject)item).getString("name"));
                    peopleModel.setAltURL(((JSONObject)item).getString("alt"));
                    peopleModel.setImageURL(((JSONObject)item).getJSONObject("avatars").getString("small"));
                    peopleModel.setPeopleType(Common.CAST);
                    castsList.add(peopleModel);
                });
            }catch (Exception e){
                logger.error(e.toString());
            }
        }
        //插入数据库
        MovieDataMgr dm = new MovieDataMgr();
        for (MovieDetailModel mm : list){
            dm.insertMovieDetail(mm);
        }
        return castsList;
    }

    //获取电影概要
    //返回值是json格式
    public String GetMovieSummary(int type){
        List<MovieSummaryModel> summaryModels = new ArrayList<>();
        MovieDataMgr dm = new MovieDataMgr();
        summaryModels = dm.GetMovieSummary(type);
        JSONArray jsonArray = new JSONArray();
        for (MovieSummaryModel sm : summaryModels){
            JSONObject obj = new JSONObject();
            obj.put("movieid",sm.getMovieId());
            obj.put("moviename",sm.getMovieName());
            obj.put("scoreavg",sm.getScoreAVG());
            obj.put("directors",sm.getDirectors());
            obj.put("casts",sm.getCasts());
            obj.put("imageurl",sm.getImageURL());
            jsonArray.put(obj);
        }
        return jsonArray.toString();
    }

    public String GetMovieDetail(String movieID){
        JSONObject jsonObject = new JSONObject();
        MovieDataMgr dm = new MovieDataMgr();
        MovieDetailModel detailModel = dm.GetMovieDetail(movieID);
        jsonObject.put("movieID",detailModel.getMovieID());
        jsonObject.put("movieName",detailModel.getMovieName());
        jsonObject.put("scoreAvg",detailModel.getScoreAvg());
        jsonObject.put("genres",detailModel.getGenres());
        jsonObject.put("countries",detailModel.getCountries());
        jsonObject.put("wishCount",detailModel.getWishCount());
        jsonObject.put("collectCount",detailModel.getCollectCount());
        jsonObject.put("rateCount",detailModel.getRateCount());
        jsonObject.put("imageUrl",detailModel.getImageUrl());
        jsonObject.put("MovieYear",detailModel.getYear());
        jsonObject.put("summary",detailModel.getSummary());
        if(detailModel.getCastsMap().size() > 0) {
            JSONArray array = new JSONArray();
            for (String key : detailModel.getCastsMap().keySet()) {
                JSONObject tmp = new JSONObject();
                tmp.put("name",key);
                tmp.put("imageURL", detailModel.getCastsMap().get(key));
                array.put(tmp);
            }
            jsonObject.put("casts",array);
        }

        System.out.println("git test");
        return jsonObject.toString();
    }
    //test
    public static void main(String[] args){
        MovieMgr mm = new MovieMgr();
        //mm.GetMovieInfo(0);
        //String str = mm.GetMovieSummary(0);
        //System.out.println(str);
        List<String> ids = new ArrayList<>();
        ids.add("24773958");
        //mm.DealMovieDetail(ids);
        mm.GetMovieDetail("24773958");
    }
}
