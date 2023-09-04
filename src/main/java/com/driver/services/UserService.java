package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        return userRepository.save(user).getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        List<WebSeries> webSeriesList = webSeriesRepository.findAll();
        int CountOfWebSeries=0;
        User user = userRepository.findById(userId).get(); //get userId
        SubscriptionType usersubscriptionType = user.getSubscription().getSubscriptionType();//get subs type of user
        int userAge = user.getAge(); //get user age

        for(WebSeries webSeries : webSeriesList){
            if(webSeries.getAgeLimit() <= userAge){
                if(usersubscriptionType == SubscriptionType.ELITE)
                    CountOfWebSeries++;
                else if(usersubscriptionType == SubscriptionType.PRO && webSeries.getSubscriptionType() == SubscriptionType.PRO || webSeries.getSubscriptionType()==SubscriptionType.BASIC)
                    CountOfWebSeries++;
                else if(usersubscriptionType == SubscriptionType.BASIC && webSeries.getSubscriptionType()==SubscriptionType.BASIC)
                    CountOfWebSeries++;
            }
        }

        return CountOfWebSeries;
    }


}
