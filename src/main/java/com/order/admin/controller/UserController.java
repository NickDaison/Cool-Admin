package com.order.admin.controller;

import com.order.admin.service.*;
import com.order.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: daisong
 * Date: 15-11-22
 * Time: 下午1:31
 * To change this template use File | Settings | File Templates.
*/
@Controller
public class UserController {

    @RequestMapping(value = "/admin/login.html",method = RequestMethod.GET)
    public ModelAndView login(HttpSession session){
        ModelAndView modelAndView = new ModelAndView();

        if(session.getAttribute(USERNAME) != null){
            modelAndView = index();
        } else {
            modelAndView.setViewName(login);
        }

        return modelAndView;
    }

    @RequestMapping(value = "/admin/login.html",method = RequestMethod.POST)
    public ModelAndView loginIn(HttpSession session,String username,String password){

        ModelAndView target = new ModelAndView();

        if(isValidUser(username,password)){
            session.setAttribute(USERNAME,username);
            target = index();
        } else {
            ModelMap modelMap = target.getModelMap();
            modelMap.put(ERRORMSG,"用户名或密码错误");
            target.setViewName(login);
        }
        return target;
    }

    @RequestMapping(value = "/admin/index.html",method = RequestMethod.GET)
    public ModelAndView index(){

        ModelAndView modelAndView = new ModelAndView(index);
        ModelMap modelMap = modelAndView.getModelMap();

        List<User> users = userService.findAllUsers();
        Account account = accountService.findLatestRecord();
        int totalIncome = incomeService.getTotal();

        List<Food> dishes = foodService.findAllFoods();

        Payment todayPayment = paymentService.getPaymentOfToday();

        List<FoodType> catagorys = foodTypeService.findAllFoodTypes();
        Map<String,List<Food>> map = new HashMap<String, List<Food>>();

        for (FoodType type : catagorys) {
            List<Food> foods = foodService.findFoodByType(type.getGuid());
            if (foods.size() > 0) {
                map.put(type.getName(), foods);
            }
        }


        modelMap.put(USERS, users);


        if(account != null){
            modelMap.put(REMAINDER, account.getRemainder());
        }

        modelMap.put(INCOMETOTAL, totalIncome);

        modelMap.put(DISHES, dishes);
        modelMap.put(CATAGORYS, map);

        if(todayPayment != null){
            modelMap.put(TODAYCONSUME, todayPayment.getAmount());
        }

        List<String> contentPages = new ArrayList<String>();
        contentPages.add(dashboard + JSPSUFFIX);
        contentPages.add(userManage + JSPSUFFIX);
        contentPages.add(dishManage + JSPSUFFIX);
        modelMap.put(CONTENTPAGE,contentPages);

        List<StepBean> emptyStepBean = Collections.emptyList();
        modelMap.put(STEPS,emptyStepBean);

        return modelAndView;
    }

    @RequestMapping(value = "/admin/updateUser.html",method = RequestMethod.GET)
    public ModelAndView ajaxUpdateUser(String guid){
        User currentUser = userService.findUserByGuid(guid);
        ModelAndView modelAndView = new ModelAndView(userInfo);
        ModelMap modelMap = modelAndView.getModelMap();
        modelMap.put(USER,currentUser);
        modelMap.put(ROLE, UserRole.values());

        return modelAndView;
    }

    @RequestMapping(value = "/admin/updateUser.html",method = RequestMethod.POST)
    public ModelAndView updateUser(String guid,String realName,String phoneNumber,String email,String roleText){
        User user = userService.findUserByGuid(guid);
        user.setRealName(realName);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setRole(roleText);
        userService.updateUser(user);
        return index();
    }

    //@RequestMapping(value = "/admin/ajaxValidateUsername.html",method = RequestMethod.GET)
    //public @ResponseBody Test ajaxValidateUsername(String username){
     //   User currentUser = userService.findUserByUsername(username);
//        boolean valid = false;
        //Test test = new Test();
       // if (currentUser == null){
//            valid = true;
           // test.setValid(true);
//            System.out.println(valid);
        //}else {
          //  test.setValid(false);
        //}
//        Map<String,Object> map = new HashMap<String, Object>();
//        map.put("valid", valid);
//        System.out.println(valid);
//        String json = "[{\"valid\": true}]";
//        System.out.println(json);
      //  return test;
  //  }

    @RequestMapping(value = "/admin/ajaxValidateEmail.html",method = RequestMethod.POST)
    public @ResponseBody boolean ajaxValidateEmail(String email){
        User currentUser = userService.findUserByEmail(email);
        boolean valid = true;
        if (currentUser != null){
            valid = false;
        }
        return valid;
    }

    @RequestMapping(value = "/admin/deleteUser.html")
    public ModelAndView deleteUser(String guid){
        User user = userService.findUserByGuid(guid);
        if(user != null){
            userService.deleteUserByGuid(guid);
        }
        return index();
    }

    @RequestMapping(value = "/admin/addUser.html", method = RequestMethod.GET)
    public ModelAndView addUser(){
        ModelAndView modelAndView = new ModelAndView();
        ModelMap modelMap = modelAndView.getModelMap();
        modelMap.put(ROLE, UserRole.values());
        modelAndView.setViewName(createUser);
        return modelAndView;
    }

    @RequestMapping(value = "/admin/addUser.html",method = RequestMethod.POST)
    public ModelAndView addUser(String username,String password,String realName,
                                String email,String phoneNumber,String roleText){
        User user = userService.findUserByUsername(username);
        if(user == null){
            User newUser = new User(username,password, realName,email,phoneNumber, roleText,new Date());
            userService.addUser(newUser);
        }
        return index(); //返回主页
    }

    @RequestMapping(value = "/admin/userManage.html",method = RequestMethod.GET)
    public ModelAndView manageUser(){

        ModelAndView modelAndView = new ModelAndView(index);
        ModelMap modelMap = modelAndView.getModelMap();

        List<String> contentPages = new ArrayList<String>();

        contentPages.add(userManage+JSPSUFFIX);

        modelMap.put(CONTENTPAGE,contentPages);

        List<StepBean> steps = new ArrayList<StepBean>();
        steps.add(StepBean.UserManage);
        modelMap.put(STEPS,steps);

        List<User> users = userService.findAllUsers();
        modelMap.put(USERS, users);

        return modelAndView;
    }



    @RequestMapping(value = "/admin/logout.html")
    public String logout(HttpSession session){
        session.removeAttribute(USERNAME);
        return login;
    }

    private boolean isValidUser(String username,String password){
        return userService.isValidateUser(username, password);
    }

    public static final String CONTENTPAGE = "contentpages";
    public static final String ROLE = "roles";
    public static final String USERS = "users";
    public static final String REMAINDER = "remainder";
    public static final String INCOMETOTAL = "sum";
    public static final String USER= "user";
    public static final String DISHES= "dishes";
    public static final String USERNAME = "username";
    public static final String ERRORMSG = "error";
    public static final String STEPS = "steps";
    public static final String CATAGORYS = "catagorys";
    public static final String JSPSUFFIX = ".jsp";
    public static final String TODAYCONSUME = "todayConsume";
    public static final String RESTAURANT  = "restaurant";
    public static final String EXPENDS = "expends";


    @Value("dashboard")
    private String dashboard;

    @Value("userManage")
    private String userManage;

    @Value("consumption")
    private String consumption;

    @Value("/admin/mainFrame")
    private String mainFrame;

    @Value("/admin/expense")
    private String expense;

    @Value("/admin/append")
    private String append;
    @Value("/admin/createUserDialog")
    private String createUser;

    @Value("/admin/userInfo")
    private String userInfo;

    @Value("/admin/login")
    private String login;

    @Value("/admin/index")
    private String index;

    @Value("dishManage")
    private String dishManage;

    @Value("dishSelect")
    private String dishSelect;

    @Autowired
    private IUserService userService;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private IIncomeService incomeService;
    @Autowired
    private IFoodService foodService;
    @Autowired
    private IPaymentService paymentService;
    @Autowired
    private IFoodTypeService foodTypeService;

//    @Autowired
//    private IDishService dishService;
//    @Autowired
//    private IExpendService expendService;
}
