package org.douban.DAL;

import org.MySqlDBManager.MySQLHelper;
import org.apache.log4j.Logger;
import org.douban.model.MovieDetailModel;
import org.douban.model.MovieMainModel;
import org.douban.model.MoviePeopleModel;
import org.douban.model.MovieSummaryModel;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liangwenchang on 2018/5/15.
 */
public class MovieDataMgr {
    private Logger logger = Logger.getLogger(this.getClass());

    public MovieDataMgr(){

    }
    //插入电影主表
    public int insertMovieMain(MovieMainModel mm ){
        if(mm == null){
            return Integer.MIN_VALUE;
        }
        String cnt = "SELECT (COALESCE(max(SerialID),0)+1) as serialid from moviemain";
        String sql = "insert into moviemain(SerialID,movietype,startCnt,count,rank,box,isNew,delta,total,title,rankdate,createtime)" +
                " values(%d,%d,%d,%d,%d,%d,'%s',%d,%d,'%s',CURDATE(),CURRENT_TIMESTAMP())";
        MySQLHelper helper = new MySQLHelper();
        ResultSet resultSet = helper.ExecuteQuery(cnt);
        int serialid = Integer.MIN_VALUE;
        try{
            if(resultSet.next()){
                serialid = resultSet.getInt("serialid");
            }
            String formatsql = String.format(sql, serialid, mm.getMovietype(), mm.getStartCnt(),
                    mm.getCount(), mm.getRank(), mm.getBox(), mm.getIsNew(), mm.getDelta(), mm.getTotal(), mm.getTitle());

            helper.ExecuteNonquery(formatsql);
        }catch (Exception e){
            logger.error(e.toString());
        }finally {
            helper.Close();
        }
        return serialid;
    }

    //插入电影概要表
    public void insertMovieSummary(int serialid, MovieSummaryModel summaryModel){
        if(summaryModel == null){
            return;
        }
        String sql = "insert into moviesummary(movieID,serialID,MovieName,ScoreAvg,directors,casts,imageURL) " +
                "values('%s',%d,'%s',%f,'%s','%s','%s')";
        MySQLHelper helper = new MySQLHelper();
        try{
            sql = String.format(sql,summaryModel.getMovieId(),serialid,summaryModel.getMovieName(),
                    summaryModel.getScoreAVG(),summaryModel.getDirectors(),summaryModel.getCasts(),
                    summaryModel.getImageURL());
            helper.ExecuteNonquery(sql);
        }catch (Exception e){
            logger.error(e.toString());
        }finally {
            helper.Close();
        }
    }

    //插入电影详细表
    public void insertMovieDetail(MovieDetailModel dm){
        if(dm == null){
            return;
        }
        String sql = "insert into moviedetail(movieID,movieName,scoreAvg,genres,countries,wishCount,collectCount," +
                "rateCount,imageUrl,MovieYear,summary) values('%s','%s',%f,'%s','%s',%d,%d,%d,'%s','%s','%s')";
        MySQLHelper helper = new MySQLHelper();
        try{
            sql = String.format(sql,dm.getMovieID(),dm.getMovieName(),dm.getScoreAvg(),dm.getGenres(),dm.getCountries(),
                dm.getWishCount(),dm.getCollectCount(),dm.getRateCount(),dm.getImageUrl(),dm.getYear(),dm.getSummary());
            helper.ExecuteNonquery(sql);
        }catch (Exception e){
            logger.error(e.toString());
        }finally {
            helper.Close();
        }
    }

    //插入影人信息
    public void insertPeopleSummary(MoviePeopleModel pm){
        if (pm == null){
            return;
        }
        String sql = "insert into peoplesummary(movieID,peopleID,peopleType,peoplename,imageURL,altURL) " +
                "values('%s','%s',%d,'%s','%s','%s')";
        MySQLHelper helper = new MySQLHelper();
        try{
            sql = String.format(sql,pm.getMovieID(),pm.getPeopleID(),pm.getPeopleType(),pm.getPeopleName(),
                    pm.getImageURL(),pm.getAltURL());
            helper.ExecuteNonquery(sql);
        }catch (Exception e){
            logger.error(e.toString());
        }finally {
            helper.Close();
        }
    }

    //获取电影概要表信息
    public List<MovieSummaryModel> GetMovieSummary(int movietype){
        String sql = "select  a.* from moviesummary a LEFT JOIN moviemain b " +
                "on a.serialID = b.SerialID where b.movietype=%d order by a.ScoreAvg desc";
        sql = String.format(sql,movietype);
        MySQLHelper helper = new MySQLHelper();
        List<MovieSummaryModel> list = new ArrayList<MovieSummaryModel>();
        try{
            ResultSet resultSet = helper.ExecuteQuery(sql);
            while (resultSet.next()){
                MovieSummaryModel movieSummaryModel = new MovieSummaryModel();
                movieSummaryModel.setMovieId(resultSet.getString("movieID"));
                movieSummaryModel.setMovieName(resultSet.getString("MovieName"));
                movieSummaryModel.setScoreAVG(resultSet.getDouble("ScoreAvg"));
                movieSummaryModel.setDirectors(resultSet.getString("directors"));
                movieSummaryModel.setCasts(resultSet.getString("casts"));
                movieSummaryModel.setImageURL(resultSet.getString("imageURL"));
                list.add(movieSummaryModel);
            }

        }catch (Exception e){
            logger.error(e.toString());
        }finally {
            helper.Close();
        }
        return list;
    }

    //获取电影详情
    public MovieDetailModel GetMovieDetail(String movieID){
        MovieDetailModel dm = new MovieDetailModel();
        String sql = "select movieID,movieName,scoreAvg,genres,countries,wishCount,collectCount,rateCount,imageUrl,MovieYear,summary " +
                "from moviedetail where movieID = '%s'";
        sql = String.format(sql,movieID);
        MySQLHelper helper = new MySQLHelper();
        try{
            ResultSet resultSet = helper.ExecuteQuery(sql);
            if (resultSet.next()){
                dm.setMovieID(resultSet.getString("movieID"));
                dm.setMovieName(resultSet.getString("movieName"));
                dm.setScoreAvg(resultSet.getDouble("scoreAvg"));
                dm.setGenres(resultSet.getString("genres"));
                dm.setCountries(resultSet.getString("countries"));
                dm.setWishCount(resultSet.getInt("wishCount"));
                dm.setCollectCount(resultSet.getInt("collectCount"));
                dm.setRateCount(resultSet.getInt("rateCount"));
                dm.setImageUrl(resultSet.getString("imageUrl"));
                dm.setYear(resultSet.getString("MovieYear"));
                dm.setSummary(resultSet.getString("summary"));
            }
            sql = "select peoplename,imageURL from peoplesummary where peopleType=0 and movieID='%s'";
            sql = String.format(sql,movieID);
            resultSet = helper.ExecuteQuery(sql);
            HashMap<String,String> castMap = new HashMap<>();
            while (resultSet.next()){
                String name = resultSet.getString("peoplename");
                String url = resultSet.getString("imageURL");
                castMap.put(name,url);
            }
            if(castMap.size() > 0){
                dm.setCastsMap(castMap);
            }
        }catch (Exception e){
            logger.error(e.toString());
        }finally {
            helper.Close();
        }
        return dm;
    }
}
