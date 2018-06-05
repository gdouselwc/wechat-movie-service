package org.controler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.douban.BLL.MovieMgr;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class Main {
    @RequestMapping(method= RequestMethod.POST,value = "/test")
    public @ResponseBody
    String test(){
        System.out.println("test############");
        System.out.println("test");
        return "test";
    }

    @RequestMapping(method=RequestMethod.GET,value = "/BeingFilms",
            produces = "text/html;charset=UTF-8")
    public @ResponseBody
    String GetBeingFilms(){
        System.out.println("GetBeingFilms>>>>>>>>>>>>>>>>");
        String result = "";
        MovieMgr mm = new MovieMgr();
        result = mm.GetMovieSummary(0);
        return result;
    }

    @RequestMapping(method = RequestMethod.GET,value = "/MovieDetail",
            produces = "text/html;charset=UTF-8")
    public @ResponseBody
    String GetMovieDetail(@RequestParam("movieId")String movieID){
        System.out.println("GetMovieDetail&&&&&&&&&&&&&");
        String result = "";
        MovieMgr mm = new MovieMgr();
        result = mm.GetMovieDetail(movieID);
        return result;
    }
    @RequestMapping(method = RequestMethod.GET,value = "/CommingFilms",
        produces = "text/html;charset=UTF-8")
    public @ResponseBody
    String GetCommingFilms(){
        System.out.println("GetCommingFilms>>>>>>>>>>>>>>>>");
        String result = "";
        MovieMgr mm = new MovieMgr();
        result = mm.GetMovieSummary(1);
        return result;
    }
}
